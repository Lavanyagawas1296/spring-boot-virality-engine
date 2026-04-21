package com.lavanya.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post createPost(Long authorId, String authorType, String content) {

        if (authorType == null) {
            throw new RuntimeException("authorType is required");
        }

        if (content == null || content.isEmpty()) {
            throw new RuntimeException("Content cannot be empty");
        }

        Post post = new Post();
        post.setAuthorId(authorId);
        post.setAuthorType(authorType.toUpperCase());
        post.setContent(content);

        return postRepository.save(post);
    }

    @Autowired
    private ViralityService viralityService;

    public Post likePost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new RuntimeException("Post not found");
        }
        post.setLikeCount(post.getLikeCount() + 1);
        viralityService.addHumanLike(postId); // +20 in Redis
        return postRepository.save(post);
    }
}