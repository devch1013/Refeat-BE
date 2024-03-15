package com.audrey.refeat.domain.project.entity.dao;

import com.audrey.refeat.domain.project.entity.DocumentES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface DocumentESRepository extends ElasticsearchRepository<DocumentES, String> {

    List<DocumentES> findByProjectId(String projectId);
}
