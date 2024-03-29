package com.springbootangularcourses.springbootbackend.repository;

import com.springbootangularcourses.springbootbackend.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findCommentsByLanguageClass_Id(Long id, Pageable pageable);
}
