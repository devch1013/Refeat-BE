package com.audrey.refeat.domain.project.entity;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "refeat_ai", createIndex = false)
@Mapping(mappingPath = "elasticsearch/es-mappings.json")
@Setting(settingPath = "elasticsearch/es-settings.json")
public class DocumentES {
    @Id
    private String id;

    @Field(type = FieldType.Keyword, name = "project_id")
    private String projectId;

    @Field(type = FieldType.Text, name = "file_path", index = true)
    private String filePath;

    @Field(type = FieldType.Keyword, name = "file_uuid")
    private String fileUuid;

    @Field(type = FieldType.Text, name = "title")
    private String title;

//    @Field(type = FieldType.Text, name = "full_text")
//    private String fullText;

    @Field(type = FieldType.Date, name = "init_date", format = DateFormat.date_hour_minute_second)
    private LocalDateTime initDate;

    @Field(type = FieldType.Date, name = "updated_date", format = DateFormat.date_hour_minute_second)
    private LocalDateTime updatedDate;

    @Field(type = FieldType.Text, name = "summary")
    private String summary;

    @Field(type = FieldType.Text, name = "chunk_list_by_text_rank")
    private String chunkListByTextRank;

    @Field(type = FieldType.Nested, name = "contents")
    private List<Content> contents;

    // getters and setters

    // Inner class representing the "contents" nested field
    public static class Content {
//        @Field(type = FieldType.Text, name = "content")
//        private String content;

        @Field(type = FieldType.Object, name = "bbox")
        private Bbox bbox;
//
//        @Field(type = FieldType.Dense_Vector, name = "content_embedding", dims = 3072)
//        private float[] contentEmbedding;

        @Field(type = FieldType.Integer, name = "page")
        private int page;

        @Field(type = FieldType.Integer, name = "token_num")
        private int tokenNum;

        // getters and setters
    }

    // Inner class representing the "bbox" field within "contents"
    public static class Bbox {
        @Field(type = FieldType.Integer, name = "left_x")
        private int leftX;

        @Field(type = FieldType.Integer, name = "top_y")
        private int topY;

        @Field(type = FieldType.Integer, name = "right_x")
        private int rightX;

        @Field(type = FieldType.Integer, name = "bottom_y")
        private int bottomY;

        // getters and setters
    }
}
