package com.zxy.model;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Created by ZhouXinyu on 2019/6/24 15:32.
 */
public class Book {

  private String id;
  private String title;
  private String author;
  private Integer wordCount;
  private String publishDate;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Integer getWordCount() {
    return wordCount;
  }

  public void setWordCount(Integer wordCount) {
    this.wordCount = wordCount;
  }

  public String getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(String publishDate) {
    this.publishDate = publishDate;
  }
}