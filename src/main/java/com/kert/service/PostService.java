package com.kert.service;

import com.kert.model.Post;
import com.kert.model.User;
import com.kert.repository.PostRepository;
import com.kert.repository.UserRepository;
import com.kert.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public Post createPost(Post post, HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        Long userId = jwtTokenProvider.getUserIdFromJWT(token);
        User user = userRepository.findById(userId).orElseThrow();

        post.setUser(user);

        return postRepository.save(post);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Page<Post> searchPosts(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (search != null && !search.isEmpty()) {
            return postRepository.findByTitleContainingOrTagContaining(search, search, pageable);
        } else {
            return postRepository.findAll(pageable);
        }
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    @Transactional
    public Post updatePost(Long id, Post postDetails) {
        Post post = getPostById(id);
        post.setTitle(postDetails.getTitle());
        post.setTag(postDetails.getTag());
        post.setDescription(postDetails.getDescription());
        post.setContent(postDetails.getContent());
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = getPostById(id);
        postRepository.delete(post);
    }
}
