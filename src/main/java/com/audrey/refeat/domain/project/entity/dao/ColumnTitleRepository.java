package com.audrey.refeat.domain.project.entity.dao;

import com.audrey.refeat.domain.project.entity.ColumnTitle;
import com.audrey.refeat.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ColumnTitleRepository extends JpaRepository<ColumnTitle, Long> {
    @Query("select c from ColumnTitle c where c.project = ?1 and c.deleted = false order by c.createdAt")
    List<ColumnTitle> findByProjectAndDeletedFalseOrderByCreatedAtAsc(Project project);
    @Query("select count(c) from ColumnTitle c where c.deleted = false and c.project = ?1")
    long countByDeletedFalseAndProject(Project project);
}
