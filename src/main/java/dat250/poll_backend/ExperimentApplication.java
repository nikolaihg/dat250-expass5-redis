package dat250.poll_backend;

import dat250.poll_backend.domain.Poll;
import dat250.poll_backend.domain.User;

import dat250.poll_backend.domain.VoteOption;
import redis.clients.jedis.UnifiedJedis;

import java.time.Instant;
import java.util.Map;

public class ExperimentApplication {
    public static void main(String[] args) {
        UnifiedJedis jedis = new UnifiedJedis("redis://localhost:6379");

        try {
            System.out.println("Connected to Redis: " + jedis.ping());

            // Experiment 1: Track logged-in users
            trackLoggedInUsers(jedis);

            // Experiment 2: Store poll vote counts
            storeVoteCounts(jedis);

        } finally {
            jedis.close();
        }
    }
    public static void trackLoggedInUsers(UnifiedJedis jedis) {
        System.out.println("\n=== Experiment 1: Logged-in Users ===");

        String key = "logged_in_users";
        jedis.del(key);

        User alice = new User("alice", "alice@example.com");
        User bob = new User("bob", "bob@example.com");
        User eve = new User("eve", "eve@example.com");

        System.out.println("Initial: " + jedis.smembers(key));

        jedis.sadd(key, alice.getUsername());
        System.out.println("Alice logs in: " + jedis.smembers(key));

        jedis.sadd(key, bob.getUsername());
        System.out.println("Bob logs in: " + jedis.smembers(key));

        jedis.srem(key, alice.getUsername());
        System.out.println("Alice logs out: " + jedis.smembers(key));

        jedis.sadd(key, eve.getUsername());
        System.out.println("Eve logs in: " + jedis.smembers(key));

    }
    public static void storeVoteCounts(UnifiedJedis jedis) {
        System.out.println("\n=== Experiment 2: Poll Vote Counts ===");

        Poll poll = new Poll("Pineapple on Pizza?", Instant.now(), Instant.now().plusSeconds(86400));

        VoteOption option1 = new VoteOption("Yes, yammy!", 0);
        VoteOption option2 = new VoteOption("Mamma mia, nooooo!", 1);
        VoteOption option3 = new VoteOption("I do not really care...", 2);

        poll.getVoteOptions().add(option1);
        poll.getVoteOptions().add(option2);
        poll.getVoteOptions().add(option3);

        // Store in Redis
        String key = "poll:votes:" + poll.getId();
        jedis.del(key);

        jedis.hset(key, "0", "269");
        jedis.hset(key, "1", "268");
        jedis.hset(key, "2", "42");

        System.out.println("\nInitial vote counts:");
        Map<String, String> votes = jedis.hgetAll(key);
        votes.forEach((option, count) ->
                System.out.println("  Option " + option + ": " + count + " votes")
        );

        // Simulate voting
        System.out.println("\nSimulating votes...");
        jedis.hincrBy(key, "0", 1);
        jedis.hincrBy(key, "1", 3);
        jedis.hincrBy(key, "2", 1);

        System.out.println("\nUpdated vote counts:");
        votes = jedis.hgetAll(key);
        votes.forEach((option, count) ->
                System.out.println("  Option " + option + ": " + count + " votes")
        );

        // Set expiration
        jedis.expire(key, 300);
        System.out.println("\nCache TTL: " + jedis.ttl(key) + " seconds");
    }
}
