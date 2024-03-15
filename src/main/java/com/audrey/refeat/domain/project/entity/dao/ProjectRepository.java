package com.audrey.refeat.domain.project.entity.dao;

import com.audrey.refeat.domain.project.entity.Project;
import com.audrey.refeat.domain.user.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long>{
    @Query("select p from Project p where p.user = ?1 order by p.createdAt DESC")
    List<Project> findByUserOrderByCreatedAtDesc(UserInfo user, Pageable pageable);

    @Query("select p from Project p where p.user.id = ?1 and p.isDeleted = false order by p.createdAt DESC")
    Page<Project> findByUserAndIsDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("select p from Project p where p.user.id = ?1 and p.isDeleted = false order by p.updatedAt DESC")
    Page<Project> findByUser_IdAndIsDeletedFalseOrderByUpdatedAtDesc(Long id, Pageable pageable);



}
