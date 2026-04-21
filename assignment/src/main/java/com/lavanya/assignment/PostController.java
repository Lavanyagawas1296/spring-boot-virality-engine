package com.lavanya.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private ViralityService viralityService;

    @GetMapping("/{postId}/virality")
    public ResponseEntity<String> getViralityScore(@PathVariable Long postId) {
        String score = viralityService.getViralityScore(postId);
        return ResponseEntity.ok("Virality Score: " + score);
    }

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post savedPost = postService.createPost(
                post.getAuthorId(),
                post.getAuthorType(),
                post.getContent()
        );

        return ResponseEntity.ok(savedPost);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Post> likePost(@PathVariable Long postId) {
        Post updatedPost = postService.likePost(postId);
        return ResponseEntity.ok(updatedPost);
    }
}