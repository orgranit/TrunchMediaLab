package com.example.trunch;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by or on 4/3/2015.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Id",
        "name",
        "tags",
        "image"
})
public class Restaurant {

    @JsonProperty("Id")
    private String Id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("tags")
    private List<String> tags = new ArrayList<>();
    @JsonProperty("image")
    private String image;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();
    @JsonIgnore
    /**
     *
     * @return
     * The Id
     */
    @JsonProperty("Id")
    public String getId() {
        return Id;
    }

    /**
     *
     * @param Id
     * The Id
     */
    @JsonProperty("Id")
    public void setId(String Id) {
        this.Id = Id;
    }

    public Restaurant withId(String Id) {
        this.Id = Id;
        return this;
    }

    /**
     *
     * @return
     * The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Restaurant withName(String name) {
        this.name = name;
        return this;
    }

    /**
     *
     * @return
     * The tags
     */
    @JsonProperty("tags")
    public List<String> getTags() {
        List<String> reTags = new  ArrayList<>();
        reTags.addAll(tags);
        return reTags;
    }
    /**
     *
     * @param tags
     * The tags
     */
    @JsonProperty("tags")
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Restaurant withTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    /**
     *
     * @return
     * The image
     */
    @JsonProperty("image")
    public String getImage() {
        return image;
    }

    /**
     *
     * @param image
     * The image
     */
    @JsonProperty("image")
    public void setImage(String image) {
        this.image = image;
    }

    public Restaurant withImage(String image) {
        this.image = image;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Restaurant withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    public Restaurant(){
    }
}
