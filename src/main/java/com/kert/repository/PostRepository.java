package com.kert.repository;

import com.kert.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
  Page<Post> findByTitleContainingOrTagContaining(String title, String tag, Pageable pageable);
}