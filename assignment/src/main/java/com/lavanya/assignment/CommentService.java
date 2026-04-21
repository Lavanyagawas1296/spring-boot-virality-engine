package com.lavanya.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BotGuardService botGuardService;

    @Autowired
    private ViralityService viralityService;

    public Comment addComment(Long postId, Long authorId, String authorType,
                              String content, Long parentCommentId) {

        // 1. Check post exists
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new RuntimeException("Post not found");
        }

        // 2. Calculate depth level FIRST
        int depthLevel = 0;
        if (parentCommentId != null) {
            Comment parentComment = commentRepository.findById(parentCommentId).orElse(null);
            if (parentComment == null) {
                throw new RuntimeException("Parent comment not found");
            }
            depthLevel = parentComment.getDepthLevel() + 1;
        }

        // 3. NOW apply bot locks (depthLevel is ready)
        if ("BOT".equals(authorType)) {

            if (botGuardService.isBotLimitReached(postId)) {
                throw new RuntimeException("BOT_LIMIT_REACHED");
            }

            if (botGuardService.isDepthLimitReached(depthLevel)) {
                throw new RuntimeException("DEPTH_LIMIT_REACHED");
            }

            if ("USER".equals(post.getAuthorType())) {
                Long humanId = post.getAuthorId();
                if (botGuardService.isCooldownActive(authorId, humanId)) {
                    throw new RuntimeException("COOLDOWN_ACTIVE");
                }
            }
        }

        // 4. Save comment
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(authorId);
        comment.setAuthorType(authorType);
        comment.setContent(content);
        comment.setDepthLevel(depthLevel);
        comment.setParentCommentId(parentCommentId);

        // 5. Update virality score
        if ("USER".equals(authorType)) {
            viralityService.addHumanComment(postId);
        } else if ("BOT".equals(authorType)) {
            viralityService.addBotReply(postId);
        }

        // Notify post owner if a bot commented
        if ("BOT".equals(authorType) && "USER".equals(post.getAuthorType())) {
            notificationService.handleBotNotification(post.getAuthorId(), "Bot_" + authorId);
        }

        return commentRepository.save(comment);
    }
}