package dat250.poll_backend;

import dat250.poll_backend.manager.PollManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.UnifiedJedis;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean(destroyMethod = "close")
    public UnifiedJedis jedis() {
        return new UnifiedJedis("redis://localhost:6379");
    }

    @Bean
    public CacheService cacheService(UnifiedJedis jedis) {
        return new CacheService(jedis, 60); // TTL = 60 seconds
    }

    @Bean
    public PollManager pollManager() {
        PollManager m = new PollManager();
        m.seedSampleData();
        return m;
    }
}
