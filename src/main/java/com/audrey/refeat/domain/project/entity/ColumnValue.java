package com.audrey.refeat.domain.project.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ColumnValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4096)
    private String columnDescription;

    @Column
    private Boolean isDone;

    @Column
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "title")
    private ColumnTitle title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document")
    private Document document;

    @Builder
    public ColumnValue(String columnDescription, Boolean isDone, ColumnTitle title, Document document) {
        this.columnDescription = columnDescription;
        this.isDone = isDone;
        this.createdAt = LocalDateTime.now();
        this.title = title;
        this.document = document;
    }

    public void updateColumnValue(String columnDescription) {
        this.columnDescription = columnDescription;
    }
}
