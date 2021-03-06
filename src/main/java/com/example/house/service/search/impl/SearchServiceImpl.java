package com.example.house.service.search.impl;

import java.io.IOException;
import java.util.*;

//import com.example.house.base.HouseSort;
//import com.example.house.base.RentValueBlock;
import com.example.house.base.ServiceMultiResult;
import com.example.house.base.ServiceResult;
import com.example.house.domain.House;
import com.example.house.domain.HouseDetail;
import com.example.house.domain.HouseTag;
import com.example.house.dto.HouseBucketDTO;
import com.example.house.form.RentSearch;
import com.example.house.mapper.HouseDetailMapper;
import com.example.house.mapper.HouseMapper;
import com.example.house.mapper.HouseTagMapper;
import com.example.house.service.search.HouseIndexKey;
import com.example.house.service.search.HouseIndexMessage;
import com.example.house.service.search.HouseIndexTemplate;
import com.example.house.service.search.ISearchService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.gson.JsonSerializer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements ISearchService {
    private static final Logger logger = LoggerFactory.getLogger(ISearchService.class);
    private static final String INDEX_NAME = "house";
    private static final String INDEX_TOPIC = "house";
    private static final String INDEX_TYPE = "house";

    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private HouseDetailMapper houseDetailMapper;
    @Autowired
    private HouseTagMapper houseTagMapper;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = INDEX_TOPIC)
    private void handleMessage(String content) {
        try {
            HouseIndexMessage houseIndexMessage
                    = objectMapper.readValue(content, HouseIndexMessage.class);
            switch (houseIndexMessage.getOperation()) {
                case HouseIndexMessage.INDEX -> this.createOrUpdateIndex(houseIndexMessage);
                case HouseIndexMessage.REMOVE -> this.removeIndex(houseIndexMessage);
                default -> logger.warn("not support message content");
            }
        } catch (JsonProcessingException e) {
            logger.error("cannot parse json for " + content, e);
        }
    }

    private  void removeIndex(HouseIndexMessage houseIndexMessage){
       Long houseId = houseIndexMessage.getHouseId();
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(INDEX_NAME);
        deleteByQueryRequest.setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId));
        BulkByScrollResponse bulkByScrollResponse = null;
        try {
            restHighLevelClient.deleteByQuery(deleteByQueryRequest,RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long delete = bulkByScrollResponse.getDeleted();
        logger.debug("Delete total" + delete);

        if(delete <= 0){
            logger.warn("Did not remove data from es for response:" + bulkByScrollResponse);
            //????????????????????????
            this.remove(houseId, houseIndexMessage.getRetry()+1);
        }
    }

    private void index(Long houseId, int retry) {
        if(retry > HouseIndexMessage.MAX_RETRY)
            logger.error("retry index times over 3 for house");
        HouseIndexMessage houseIndexMessage = new HouseIndexMessage(houseId, HouseIndexMessage.INDEX, retry);
        try {
            kafkaTemplate.send(INDEX_TOPIC,objectMapper.writeValueAsString(houseIndexMessage));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void remove(Long houseId, int retry) {
        if(retry > HouseIndexMessage.MAX_RETRY)
            logger.error("Retry remove over 3 for house:" + houseId + "Please check it!");
        HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.REMOVE, retry);
        try {
            this.kafkaTemplate.send(INDEX_TOPIC,objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            logger.error("Cannot encode json for" + message, e);
        }
    }


    private void createOrUpdateIndex(HouseIndexMessage houseIndexMessage){
        Long houseId = houseIndexMessage.getHouseId();
        House house = houseMapper.findOne(houseId);
        if(house == null){
            logger.error("Index house {} does not exist!", houseId);
            this.index(houseId, houseIndexMessage.getRetry()+1);
        }
        HouseIndexTemplate houseIndexTemplate = new HouseIndexTemplate();
        modelMapper.map(house, houseIndexTemplate);

        HouseDetail houseDetail = houseDetailMapper.findByHouseId(houseId);
        if(houseDetail == null){logger.error("Index house {} does not exist!", houseId);}

        modelMapper.map(houseDetail,houseIndexTemplate);
        List<HouseTag> houseTags = houseTagMapper.findAllByHouseId(houseId);
        if(houseTags != null && !houseTags.isEmpty()){
            List<String> houseTagsString = new ArrayList<>();
            houseTags.forEach(houseTag -> houseTagsString.add(houseTag.getName()));
            houseIndexTemplate.setTags(houseTagsString);
        }

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        QueryBuilder queryBuilder = QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID,houseId);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        long totalHit = searchResponse.getHits().getTotalHits().value;
        boolean success;
        if(totalHit == 0){
            success = create(houseIndexTemplate);
        } else if(totalHit == 1){
            String esId = searchResponse.getHits().getAt(0).getId();
            success = update(esId,houseIndexTemplate);
        } else {
            success = deleteAndCreate(totalHit, houseIndexTemplate);
        }
        if(success){
            logger.debug("");
        }
    }




    private boolean create(HouseIndexTemplate houseIndexTemplate) {
        try {
            IndexRequest indexRequest = new IndexRequest(INDEX_NAME)
                    .source(objectMapper.writeValueAsBytes(houseIndexTemplate), XContentType.JSON);
            IndexResponse response =
                    restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            if (response.status() == RestStatus.CREATED)
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean update(String esId, HouseIndexTemplate houseIndexTemplate) {
        try {
            UpdateRequest updateRequest = new UpdateRequest(INDEX_NAME, esId) //esId?????????????????????ID
                    .doc(objectMapper.writeValueAsBytes(houseIndexTemplate), XContentType.JSON);
            UpdateResponse updateResponse
                    = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

            logger.debug("Create index with house: " + houseIndexTemplate.getHouseId());
            if (updateResponse.status() == RestStatus.OK)
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean deleteAndCreate(long totalHit, HouseIndexTemplate houseIndexTemplate) {
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(INDEX_NAME);
        deleteByQueryRequest.setQuery(
                QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseIndexTemplate.getHouseId()));
        BulkByScrollResponse bulkByScrollResponse = null;
        try {
            bulkByScrollResponse =
                    restHighLevelClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long delete = bulkByScrollResponse.getDeleted();
        if (delete != totalHit)
            return false;
        else return create(houseIndexTemplate);
    }



    @Override
    public void index(Long houseId) {
        this.index(houseId,0);
    }

    @Override
    public void remove(Long houseId) {
        this.remove(houseId,0);
    }


    @Override
    public ServiceMultiResult<Long> query(RentSearch rentSearch) {
        return null;
    }

    @Override
    public ServiceMultiResult<HouseBucketDTO> mapAggregate(String cityEnName) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, cityEnName));

        AggregationBuilder aggBuilder = AggregationBuilders.terms(HouseIndexKey.AGG_REGION)
                .field(HouseIndexKey.REGION_EN_NAME); //group by

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQuery);//select cityEnName from xxx group by region=bj
        sourceBuilder.aggregation(aggBuilder);

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<HouseBucketDTO> buckets = new ArrayList<>();
        if (searchResponse.status() != RestStatus.OK) {
            logger.warn("Aggregate status is not ok for " + searchRequest);
            return new ServiceMultiResult<>(0, buckets);
        }

        Terms terms = searchResponse.getAggregations().get(HouseIndexKey.AGG_REGION);
        for (Terms.Bucket bucket : terms.getBuckets()) {
            buckets.add(new HouseBucketDTO(bucket.getKeyAsString(), bucket.getDocCount()));
        }

        return new ServiceMultiResult<>(searchResponse.getHits().getTotalHits().value, buckets);
    }
}
