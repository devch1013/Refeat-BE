package com.audrey.refeat.domain.project.entity;

import com.audrey.refeat.domain.user.entity.UserInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String thumbnail;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;
    @Column
    private Boolean isDeleted;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ColumnTitle> columnTitles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private UserInfo user;

    @Builder
    public Project(UserInfo user) {
        this.name = "Untitled";
        this.description = "";
        this.thumbnail = "https://d1ko5ecn45u2gs.cloudfront.net/images/project_empty.png";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.user = user;
        this.isDeleted = false;
    }

    public void updateName(String name) {
        this.name = name;
    }
    public void updateTime() {
        this.updatedAt = LocalDateTime.now();
    }

    public List<ColumnTitle> getColumnTitles() {
        return columnTitles;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
