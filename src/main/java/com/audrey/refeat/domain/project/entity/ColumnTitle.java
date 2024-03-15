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
public class ColumnTitle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String title;
    @Column
    private String query;
    @Column
    private Boolean general;
    @Column
    private Boolean custom;
    @Column
    private LocalDateTime createdAt;
    @Column
    private Boolean deleted;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project")
    private Project project;

    @Builder
    public ColumnTitle(String title, String query, Project project, Boolean general, Boolean custom) {
        this.title = title;
        this.query = query;
        this.createdAt = LocalDateTime.now();
        this.project = project;
        this.general = general;
        this.custom = custom;
        this.deleted = false;
    }

    public void delete() {
        this.deleted = true;
    }
}
