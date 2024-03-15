package com.audrey.refeat.domain.project.entity.dao;

import com.audrey.refeat.domain.project.entity.Document;
import com.audrey.refeat.domain.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    @Query("select d from Document d where d.project = ?1 and d.isDeleted = false order by d.createdAt DESC")
    Page<Document> findByProjectOrderByCreatedAtDescPaging(Project project, Pageable pageable);

    @Query("select (count(d) > 0) from Document d where d.project = ?1 and d.isDeleted = false")
    boolean existsByProject(Project project);

    @Query("select d from Document d where d.project = ?1 and d.isDeleted = false")
    List<Document> findByProject(Project project);



    @Query("select d from Document d where d.project = ?1 and d.isDeleted = false order by d.createdAt DESC")
    List<Document> findByProjectOrderByCreatedAtDesc(Project project);

    @Query("""
            select d from Document d
            where d.project = ?1 and d.embeddingDone = 1 and d.summaryDone = 1 and d.isDeleted = false
            order by d.createdAt DESC""")
    List<Document> findByProjectAndEmbeddingDoneTrueAndSummaryDoneTrueOrderByCreatedAtDesc(Project project);



    @Query("select d from Document d where d.id in ?1 and d.isDeleted = false")
    List<Document> findByIds(List<UUID> uuidList);
}
