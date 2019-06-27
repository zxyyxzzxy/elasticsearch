package com.zxy.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;

import com.zxy.model.Book;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Created by ZhouXinyu on 2019/6/24 15:05.
 */
@RequestMapping("book/novel")
@RestController
public class BookController {

    @Autowired
    private TransportClient client;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("add")
    public ResponseEntity<String> add(Book book) {

        try {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("title", book.getTitle())
                    .field("author", book.getAuthor())
                    .field("word_count", book.getWordCount())
                    .field("publish_date", book.getPublishDate())
                    .endObject();

            IndexResponse result = this.client.prepareIndex("book", "novel")
                    .setSource(builder)
                    .get();

            return new ResponseEntity<>(result.getId(), HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @GetMapping("get/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String id) {
        GetResponse result = this.client.prepareGet("book", "novel", id).get();

        if (!result.isExists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result.getSource(), HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<String> update(Book book) {
        UpdateRequest request = new UpdateRequest("book", "novel", book.getId());
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();

            builder.startObject()
            .field("title", book.getTitle())
            .field("author", book.getAuthor())
            .field("word_count", book.getWordCount())
            .field("publish_date", book.getPublishDate())
            .endObject();

            request.doc(builder);

            UpdateResponse result = this.client.update(request).get();
            return new ResponseEntity<>(result.getResult().toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("delete")
    public ResponseEntity<String> delete(
            @RequestParam(name = "id") String id
    ) {
        DeleteResponse result = this.client.prepareDelete("book", "novel", id).get();
        return new ResponseEntity<>(result.getResult().toString(), HttpStatus.OK);
    }

    @PostMapping("query")
    public ResponseEntity<List<Map<String, Object>>> query(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "0") Integer gtWordCount,
            @RequestParam(defaultValue = "0") Integer ltWordCount
    ) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (title != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", title));
        }

        if (author != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("author", author));
        }

        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("word_count");

        rangeQuery.from(gtWordCount);
        if (ltWordCount > 0) {
            rangeQuery.to(ltWordCount);
        }
        boolQueryBuilder.filter(rangeQuery);

        SearchRequestBuilder builder = this.client.prepareSearch("book")
                .setTypes("novel")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                .setFrom(0)
                .setSize(10);

        System.out.println(builder);

        SearchResponse response = builder.get();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hitFields : response.getHits()) {
            result.add(hitFields.getSourceAsMap());
        }

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

}