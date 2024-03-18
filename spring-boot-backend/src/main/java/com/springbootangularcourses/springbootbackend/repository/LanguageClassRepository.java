package com.springbootangularcourses.springbootbackend.repository;

import com.springbootangularcourses.springbootbackend.domain.LanguageClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LanguageClassRepository extends JpaRepository<LanguageClass, Long> {
    Optional<LanguageClass> findById(Long classId);
    List<LanguageClass> findByOrderByTimeAsc();
}