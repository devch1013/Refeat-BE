package com.audrey.refeat.domain.project.entity;

import com.audrey.refeat.domain.chat.entity.enums.Language;
import com.audrey.refeat.domain.project.dto.response.AiFileUploadResponseDto;
import com.audrey.refeat.domain.project.entity.enums.DocumentStatus;
import com.audrey.refeat.domain.project.entity.enums.DocumentType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Document {
    @Id
    private UUID id;
    @Column
    private String name;
    @Column(length = 1024)
    private String link;
    @Column(length = 1024)
    private String originLink;
    @Column
    @Enumerated(EnumType.STRING)
    private DocumentType type;
    @Column(columnDefinition = "TEXT")
    private String summary;
    @Column(columnDefinition = "TINYINT(3)")
    private DocumentStatus saveDone;
    @Column(columnDefinition = "TINYINT(3)")
    private DocumentStatus summaryDone;
    @Column(columnDefinition = "TINYINT(3)")
    private DocumentStatus embeddingDone;
    @Column
    private Boolean isDeleted;
    @Column(length = 1024)
    private String favicon;
    @Column
    private LocalDateTime createdAt;
    @Column
    @Enumerated(EnumType.STRING)
    private Language lang;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project")
    private Project project;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private List<ColumnValue> columnValues;


    @Builder
    public Document(UUID id, String name, String link, DocumentType type, String summary, String favicon, Project project, String originLink, Language lang) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.originLink = originLink;
        this.type = type;
        this.summary = summary;
        this.favicon = favicon;
        this.createdAt = LocalDateTime.now();
        this.project = project;
        this.lang = lang;
        this.summaryDone = DocumentStatus.YET;
        this.embeddingDone = DocumentStatus.YET;
        this.isDeleted = false;

    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateAiResponse(AiFileUploadResponseDto aiFileUploadResponseDto) {
        if (
                type == DocumentType.WEB
                        && (aiFileUploadResponseDto.favicon() != null)
                        && (aiFileUploadResponseDto.favicon().length() <= 1000
                )) {
            this.favicon = aiFileUploadResponseDto.favicon();

        }
        if (!Objects.equals(aiFileUploadResponseDto.title(), this.id.toString())) {
            this.name = aiFileUploadResponseDto.title();
        }
    }

    public void delete(){
        this.isDeleted = true;
    }

    public String getFavicon(String s3Endpoint){
        return s3Endpoint + favicon;
    }

    public String getFavicon(){
        return favicon;
    }


}
