package com.lavanya.assignment;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository repo;

//    @PostMapping
//    public ResponseEntity<Post> createPost(@RequestBody Post post) {
//        Post savedPost = postService.createPost(
//                post.getAuthorId(),
//                post.getAuthorType(),
//                post.getContent()
//        );
//        return ResponseEntity.ok(savedPost);
//    }

    @GetMapping
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        repo.deleteById(id);
        return "User removed successfully";
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        User existingUser = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setUsername(user.getUsername());

        return repo.save(existingUser);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
