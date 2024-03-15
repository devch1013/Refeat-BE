package com.audrey.refeat.domain.chat.entity.dao;

import com.audrey.refeat.domain.chat.entity.Chat;
import com.audrey.refeat.domain.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("select c from Chat c where c.project = ?1 order by c.createdAt DESC")
    Page<Chat> findByProjectOrderByCreated_atDesc(Project project, Pageable pageable);

    @Query("select c from Chat c where c.project = ?1 order by c.createdAt ASC")
    List<Chat> findByProjectOrderByCreated_atAsc(Project project, Pageable pageable);

}