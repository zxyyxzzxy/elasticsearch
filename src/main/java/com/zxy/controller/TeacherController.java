package com.zxy.controller;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhouXinyu on 2019/7/29 15:36.
 */

@RequestMapping("teacher2")
@RestController
public class TeacherController {
    @Autowired
    private TransportClient client;

    @RequestMapping("{index}/query")
    public ResponseEntity<List<Map<String, Object>>> query(
            @PathVariable String index,
            @RequestParam(required = false, defaultValue = "0") Integer phrase,
            @RequestParam Map<String, String> paramMap
    ) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        Integer page = getPageInteger(paramMap, "page");
        Integer pageSize = getPageInteger(paramMap, "pageSize");

        setMatchPhraseQueryParam(boolQueryBuilder, paramMap, phrase);

        SearchRequestBuilder builder = this.client.prepareSearch("t_plan_idx")
                .setTypes("doc")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                .setFrom((page - 1) * pageSize)
                .setSize(pageSize);

        System.out.println(builder);

        SearchResponse response = builder.get();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hitFields : response.getHits()) {
            result.add(hitFields.getSourceAsMap());
        }

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    public void setMatchPhraseQueryParam(BoolQueryBuilder boolQueryBuilder, Map<String, String> paramMap, Integer phrase) {
        paramMap.forEach((k, v) -> {
            if (!"page".equals(k) && !"pageSize".equals(k) && !"phrase".equals(k)) {
                if (phrase == 1) {
                    boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(k, v));
                } else {
                    boolQueryBuilder.must(QueryBuilders.matchQuery(k, v).operator(Operator.AND));
                }
            }
        });
    }

    public Integer getPageInteger(Map<String, String> paramMap, String pageInfo) {
        Integer pageInteger = 1;
        if ("page".equals(pageInfo)) {
            try {
                pageInteger = Integer.parseInt(paramMap.get("page"));
            } catch (Exception e) {
                pageInteger = 1;
            }
        } else if ("pageSize".equals(pageInfo)) {
            try {
                pageInteger = Integer.parseInt(paramMap.get("pageSize"));
            } catch (Exception e) {
                pageInteger = 10;
            }
        }
        return pageInteger;
    }
}
