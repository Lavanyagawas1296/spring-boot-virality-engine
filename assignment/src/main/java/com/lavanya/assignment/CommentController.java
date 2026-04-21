package com.lavanya.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId,
                                        @RequestBody Map<String, String> body) {
        Long authorId = Long.parseLong(body.get("authorId"));
        String authorType = body.get("authorType");
        String content = body.get("content");

        Long parentCommentId = null;
        if (body.get("parentCommentId") != null) {
            parentCommentId = Long.parseLong(body.get("parentCommentId"));
        }

        try {
            Comment saved = commentService.addComment(postId, authorId, authorType,
                    content, parentCommentId);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.status(429).body(e.getMessage());
        }
    }
}