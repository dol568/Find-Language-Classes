package com.springbootangularcourses.springbootbackend.repository;

import com.springbootangularcourses.springbootbackend.domain.UserLanguageClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLanguageClassRepository extends JpaRepository<UserLanguageClass, Long> {
}
