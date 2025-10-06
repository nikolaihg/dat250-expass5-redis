package dat250.poll_backend.controllers;

import dat250.poll_backend.CacheService;
import dat250.poll_backend.domain.Poll;
import dat250.poll_backend.manager.PollManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/polls")
public class PollController {
    private final PollManager manager;
    private final CacheService cacheService;

    public static record CreatePollRequest(UUID creatorId, String question, List<String> options) {}

    public PollController(PollManager manager, CacheService cacheService) {
        this.manager = manager;
        this.cacheService = cacheService;
    }

    @PostMapping
    public ResponseEntity<Poll> create(@RequestBody CreatePollRequest r) {
        if (r.question == null || r.options == null || r.options.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Poll p = manager.createPoll(r.creatorId(), r.question(), r.options());
        return ResponseEntity.ok(p);
    }

    @GetMapping
    public Collection<Poll> list() {
        return manager.listPolls();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Poll> get(@PathVariable UUID id) {
        return manager.getPoll(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/counts")
    public ResponseEntity<Map<String, Long>> counts(@PathVariable UUID id) {
        var cached = cacheService.get(id);
        if (cached != null) {
            Map<String, Long> result = cached.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> String.valueOf(e.getKey()),
                            Map.Entry::getValue
                    ));
            return ResponseEntity.ok(result);
        }

        // if not cached, compute and set initial counts
        Map<Integer, Long> counts = manager.aggregateCounts(id);
        cacheService.set(id, counts);
        Map<String, Long> out = new TreeMap<>();
        counts.forEach((k, v) -> out.put(String.valueOf(k), v));
        return ResponseEntity.ok(out);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        manager.deletePoll(id);
        cacheService.invalidate(id);
        return ResponseEntity.noContent().build();
    }
}
