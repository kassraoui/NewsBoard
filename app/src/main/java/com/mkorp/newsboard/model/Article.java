package com.mkorp.newsboard.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

public class Article implements ArticleItem{
    public static final int ARTICLE_TYPE = 0;
    private final Source source;
    private final String title;
    private final String description;
    private final String url;
    private final String urlToImage;
    private final String author;
    @JsonIgnore
    private final String content;
    private final Date publishedAt;

    @JsonCreator
    public Article(@JsonProperty("source") Source source, @JsonProperty("title") String title, @JsonProperty("description") String description, @JsonProperty("url") String url, @JsonProperty("urlToImage") String urlToImage, @JsonProperty("author") String author, @JsonProperty("content") String content, @JsonProperty("publishedAt") Date publishedAt) {
        this.author = author;
        this.content = content;
        this.source = new Source(source.getId(), source.getName(), getDomainFromUrl(url));
        this.title = cleanTitle(title);
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }

    private String cleanTitle(String title)
    {
        int lastIndex = title.lastIndexOf("-");
        if(lastIndex<=0)
            return title;
        return title.substring(0, lastIndex - 1);
    }
    private String getDomainFromUrl(String url)
    {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Source getSource() {
        return source;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public String getAuthor() {
        return author;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int getItemType() {
        return ARTICLE_TYPE;
    }
}
