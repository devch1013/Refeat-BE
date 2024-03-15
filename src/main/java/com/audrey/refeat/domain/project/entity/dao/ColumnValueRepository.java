package com.audrey.refeat.domain.project.entity.dao;

import com.audrey.refeat.domain.project.entity.ColumnTitle;
import com.audrey.refeat.domain.project.entity.ColumnValue;
import com.audrey.refeat.domain.project.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ColumnValueRepository extends JpaRepository<ColumnValue, Long> {
    @Query("select c from ColumnValue c where c.title = ?1 and c.document = ?2")
    Optional<ColumnValue> findByTitleAndDocument(ColumnTitle title, Document document);

    @Query("select c from ColumnValue c where c.title = ?1")
    List<ColumnValue> findByTitle(ColumnTitle title);



}
