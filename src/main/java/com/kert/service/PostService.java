package com.kert.service;

import com.kert.model.Post;
import com.kert.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
       return postRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    public List<Post> getPostsByTag(String tag) {
        return postRepository.findByTag(tag);
    }

    @Transactional
    public Post updatePost(Long id, Post postDetails) {
        Post post = getPostById(id);
        post.setTitle(postDetails.getTitle());
        post.setTag(postDetails.getTag());
        post.setContent(postDetails.getContent());
        return postRepository.save(post);
    }
    
    @Transactional
    public void deletePost(Long id) {
        Post post = getPostById(id);
        postRepository.delete(post);
    }
}
