package dat250.poll_backend.controllers;

import dat250.poll_backend.domain.User;
import dat250.poll_backend.manager.PollManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final PollManager manager;

    public UserController(PollManager manager) {
        this.manager = manager;
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User u) {
        User created = manager.createUser(u.getUsername(), u.getEmail());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public Collection<User> list() {
        return manager.listUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable UUID id) {
        return manager.getUser(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
