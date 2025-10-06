package dat250.poll_backend.manager;

import dat250.poll_backend.domain.Poll;
import dat250.poll_backend.domain.User;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PollManager {
    private final Map<UUID, User> users = new ConcurrentHashMap<>();
    private final Map<UUID, Poll> polls = new ConcurrentHashMap<>();

    private final Map<UUID, Map<UUID, Integer>> votes = new ConcurrentHashMap<>();

    public User createUser(String username, String email) {
        User u = new User(username, email);
        if (u.getId() == null) u.setId(UUID.randomUUID());
        users.put(u.getId(), u);
        return u;
    }

    public Collection<User> listUsers() {
        return users.values();
    }

    public Optional<User> getUser(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    public Poll createPoll(UUID creatorId, String question, List<String> optionCaptions) {
        Poll p = new Poll();
        p.setId(UUID.randomUUID());
        p.setQuestion(question);
        p.setPublishedAt(Instant.now());
        p.setValidUntil(Instant.now().plusSeconds(7 * 24 * 3600L));

        List<dat250.poll_backend.domain.VoteOption> opts = new ArrayList<>();
        for (int i = 0; i < optionCaptions.size(); i++) {
            dat250.poll_backend.domain.VoteOption vo = new dat250.poll_backend.domain.VoteOption();
            vo.setId(UUID.randomUUID());
            vo.setCaption(optionCaptions.get(i));
            vo.setPresentationOrder(i + 1);
            opts.add(vo);
        }
        p.setVoteOptions(opts);

        if (creatorId != null && users.containsKey(creatorId)) {
            p.setCreator(users.get(creatorId));
        }
        polls.put(p.getId(), p);
        return p;
    }


    public Collection<Poll> listPolls() {
        return polls.values();
    }

    public Optional<Poll> getPoll(UUID id) {
        return Optional.ofNullable(polls.get(id));
    }

    public boolean vote(UUID pollId, UUID userId, int presentationOrder) {
        if (!polls.containsKey(pollId) || !users.containsKey(userId)) return false;
        votes.putIfAbsent(pollId, new ConcurrentHashMap<>());
        votes.get(pollId).put(userId, presentationOrder);
        return true;
    }

    public Map<Integer, Long> aggregateCounts(UUID pollId) {
        Map<Integer, Long> result = new TreeMap<>();
        var poll = polls.get(pollId);
        if (poll == null) return result;
        for (var o : poll.getVoteOptions()) {
            result.put(o.getPresentationOrder(), 0L);
        }
        var pv = votes.getOrDefault(pollId, Collections.emptyMap());
        for (Integer order : pv.values()) {
            result.put(order, result.getOrDefault(order, 0L) + 1L);
        }
        return result;
    }

    public void deletePoll(UUID pollId) {
        polls.remove(pollId);
        votes.remove(pollId);
    }

    public Integer getExistingVoteOrder(UUID pollId, UUID userId) {
        var pollVotes = votes.get(pollId);
        if (pollVotes == null) return null;
        return pollVotes.get(userId);
    }

    // For testing convenience: create sample data
    public void seedSampleData() {
        if (!users.isEmpty() || !polls.isEmpty()) return;
        var u1 = createUser("alice", "alice@example.com");
        var u2 = createUser("bob", "bob@example.com");
        var u3 = createUser("carol", "carol@example.com");

        var p = createPoll(u1.getId(), "Pineapple or Banana?", List.of("Pineapple", "Banana"));
        vote(p.getId(), u2.getId(), 1); // bob -> Pineapple
        vote(p.getId(), u3.getId(), 2); // carol -> Banana
    }
}
