package dat250.poll_backend.controllers;

import dat250.poll_backend.CacheService;
import dat250.poll_backend.manager.PollManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/polls/{pollId}/vote")
public class VoteController {
    private final PollManager manager;
    private final CacheService cache;

    public static record VoteRequest(UUID userId, int presentationOrder) {}

    public VoteController(PollManager manager, CacheService cache) {
        this.manager = manager;
        this.cache = cache;
    }

    @PostMapping
    public ResponseEntity<String> vote(@PathVariable UUID pollId, @RequestBody VoteRequest r) {
        // old vote (if any) to handle decrement
        Integer oldOrder = manager.getExistingVoteOrder(pollId, r.userId());
        boolean ok = manager.vote(pollId, r.userId(), r.presentationOrder());
        if (!ok) return ResponseEntity.badRequest().body("invalid poll or user");

        // adjust cache via HINCRBY differential update
        if (oldOrder != null && oldOrder != r.presentationOrder()) {
            cache.decrement(pollId, oldOrder);
        }
        cache.increment(pollId, r.presentationOrder());

        return ResponseEntity.ok("vote recorded (cache updated)");
    }
}
