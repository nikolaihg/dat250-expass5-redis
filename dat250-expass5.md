# HVL DAT250 Experiment Assignment 5
- **Name:** Nikolai Hansen Gangstø
- **User:** nigan4342
- **Date:** October 2025

## Code
Code link: [nikolaihg/dat250-expass5-redis](https://github.com/nikolaihg/dat250-expass5-redis)

## Report

### Step 1: Install
- Created a redis docker container and started it
- tried creating users:
```bash
127.0.0.1:6379> ping
PONG
127.0.0.1:6379> SET user bob
OK
127.0.0.1:6379> GET user
"bob"
127.0.0.1:6379> set user alice
OK
127.0.0.1:6379> get user
"alice"
```
- tried `expire`;
```bash
127.0.0.1:6379> keys *
(empty array)
127.0.0.1:6379> set user bob
OK
127.0.0.1:6379> keys *
1) "user"
127.0.0.1:6379> expire user 5
(integer) 1
127.0.0.1:6379> ttl user
(integer) 4
127.0.0.1:6379> ttl user
(integer) -2
127.0.0.1:6379> get user
(nil)
```

### Step 2: Warm Up
- Started the CLI and did the two warm up tasks:

#### Use case 1
Can keep track of logged in users with: `SADD`, `SREM`, `SMEMBERS`.
- operations:
  1) Initial state: no user is logged in : `SMEMBERS logged_in`
  2) User "alice" logs in : `SADD logged_in alice`
  3) User "bob" logs in : `SADD logged_in bob`
  4) User "alice" logs off : `SREM logged_in alice`
  5) User "eve" logs in :  `SADD logged_in eve`
- output: 
```bash
127.0.0.1:6379> SMEMBERS logged_in
(empty array)
127.0.0.1:6379> clear
127.0.0.1:6379> SMEMBERS logged_in
(empty array)
127.0.0.1:6379> SADD logged_in alice
(integer) 1
127.0.0.1:6379> SADD logged_in bob
(integer) 1
127.0.0.1:6379> SMEMBERS logged_in
1) "alice"
2) "bob"
127.0.0.1:6379> SREM logged_in alice
(integer) 1
127.0.0.1:6379> SMEMBERS logged_in
1) "bob"
127.0.0.1:6379> SADD logged_in eve
(integer) 1
127.0.0.1:6379> SMEMBERS logged_in
1) "bob"
2) "eve"
```

#### Use case 2
Can use hashes (`HSET`) to store the poll with operations: `HSET`, `HGET`, `HGETALL`, `HINCRBY`:
- Contructing poll:
```bash
127.0.0.1:6379> HSET poll:03ebcb7b-bd69-440b-924e-f5b7d664af7b title "Pineapple on Pizza?"
(integer) 1
127.0.0.1:6379> HSET poll:03ebcb7b-bd69-440b-924e-f5b7d664af7b option:0:caption "Yes, yammy!"
(integer) 1
127.0.0.1:6379> HSET poll:03ebcb7b-bd69-440b-924e-f5b7d664af7b option:0:votes 269
(integer) 1
127.0.0.1:6379> HSET poll:03ebcb7b-bd69-440b-924e-f5b7d664af7b option:1:caption "Mamma mia, nooooo!"
(integer) 1
127.0.0.1:6379> HSET poll:03ebcb7b-bd69-440b-924e-f5b7d664af7b option:1:votes 268
(integer) 1
127.0.0.1:6379> HSET poll:03ebcb7b-bd69-440b-924e-f5b7d664af7b option:2:caption "I do not really care ..."
(integer) 1
127.0.0.1:6379> HSET poll:03ebcb7b-bd69-440b-924e-f5b7d664af7b option:3:votes 42
(integer) 1
```
- Increment vote:
```bash
127.0.0.1:6379> HGETALL poll:03ebcb7b-bd69-440b-924e-f5b7d664af7b
 1) "title"
 2) "Pineapple on Pizza?"
 3) "option:0:caption"
 4) "Yes, yammy!"
 5) "option:0:votes"
 6) "269"
 7) "option:1:caption"
 8) "Mamma mia, nooooo!"
 9) "option:1:votes"
10) "268"
11) "option:2:caption"
12) "I do not really care ..."
13) "option:3:votes"
14) "42"
127.0.0.1:6379> HINCRBY poll:03ebcb7b-bd69-440b-924e-f5b7d664af7b option:0:votes 1
(integer) 270
127.0.0.1:6379> HINCRBY poll:03ebcb7b-bd69-440b-924e-f5b7d664af7b option:1:votes 1
(integer) 269
127.0.0.1:6379> HINCRBY poll:03ebcb7b-bd69-440b-924e-f5b7d664af7b option:2:votes 1
(integer) 43
127.0.0.1:6379> HGETALL poll:03ebcb7b-bd69-440b-924e-f5b7d664af7b
 1) "title"
 2) "Pineapple on Pizza?"
 3) "option:0:caption"
 4) "Yes, yammy!"
 5) "option:0:votes"
 6) "270"
 7) "option:1:caption"
 8) "Mamma mia, nooooo!"
 9) "option:1:votes"
10) "269"
11) "option:2:caption"
12) "I do not really care ..."
13) "option:2:votes"
14) "43"
```
### Step 3: Implementing Cache
Started a new gradle project, and based the java objects on previous expass assignments.
- `\domain` contains the domain model and objects.
- `ExperimentApplication.java` is the entrypoint.

Started by only making the redis part of the experiment (no rest) to keep it simple. Made two functions in `Application.java`:
#### `trackLoggedInUsers(jedis);`
Output (same as using `redis-cli`):
```bash
=== Experiment 1: Logged-in Users ===
Initial: []
Alice logs in: [alice]
Bob logs in: [bob, alice]
Alice logs out: [bob]
Eve logs in: [bob, eve]
```
#### `cacheVoteCounts(jedis);`
```bash
=== Experiment 2: Poll Vote Counts ===

Initial vote counts:
  Option 0: 269 votes
  Option 1: 268 votes
  Option 2: 42 votes

Simulating votes...

Updated vote counts:
  Option 0: 270 votes
  Option 1: 271 votes
  Option 2: 43 votes

Cache TTL: 300 seconds
```
### View keys in redis
Actually checking that it worked using the `redis-cli`: 
```bash
127.0.0.1:6379> keys *
1) "logged_in_users"
2) "poll:votes:56d00e33-f427-4eb6-9655-5193fbfbc89d"
```

### Step 4: Adding REST functionality
- Moved both of the previous experiments to their own file, so they can be run without rest integration.
- Started on the rest integration based on expass 2/3.
- Created a pollmanager: `\manager\PollManager.java`
- `Application.java` as entrypoint for spring.
- `CacheService`, redis cache.
- `\controllers` rest controllers (based on previous expasses).
  - `UserController.java`
  - `PollController.java`
  - `VoteController.java`
- There is no JPA or Database every object is in memory (`ConcurrentHashMap`)
- Cache key & data type:
  - Key format: poll:{pollId}:counts (Redis HASH)
  - TTL = 60 seconds.

### Step 5: Testing with curl
#### REST api
- Show users
```bash
nikolai@LAPTOP-6UOFH1KM:~/documents/dat250/dat250-expass5-redis (main)$ curl -s http://localhost:8080/users | jq .
[
  {
    "id": "26aceab4-f919-45b5-b387-88217259cefa",
    "username": "carol",
    "email": "carol@example.com",
    "createdPolls": [],
    "votes": []
  },
  {
    "id": "2387bb92-3cfe-4e13-80a0-453a1e36ae27",
    "username": "alice",
    "email": "alice@example.com",
    "createdPolls": [],
    "votes": []
  },
  {
    "id": "b42e7d05-5743-4a28-a3b8-a9432b084ccf",
    "username": "bob",
    "email": "bob@example.com",
    "createdPolls": [],
    "votes": []
  }
] 
```
- Show polls
```bash
nikolai@LAPTOP-6UOFH1KM:~/documents/dat250/dat250-expass5-redis (main)$ curl -s http://localhost:8080/polls | jq .
[
  {
    "id": "bfe1f24f-52e2-4b80-a677-dfc0f6c727e6",
    "question": "Pineapple or Banana?",
    "publishedAt": "2025-10-06T12:26:53.717023252Z",
    "validUntil": "2025-10-13T12:26:53.717024819Z",
    "voteOptions": [
      {
        "id": "9ea28938-4bfc-4d9c-9665-0bf069903618",
        "caption": "Pineapple",
        "presentationOrder": 1
      },
      {
        "id": "085a3849-f8eb-45a7-997b-8940d8d37a32",
        "caption": "Banana",
        "presentationOrder": 2
      }
    ]
  }
]
```
- Vote (alice votes for pineapple):
```bash
nikolai@LAPTOP-6UOFH1KM:~/documents/dat250/dat250-expass5-redis (main)$ curl -X POST http://localhost:8080/polls/bfe1f24f-52e2-4b80-a677-dfc0f6c727e6/vote   -H "Content-Type: application/json"   -d '{"userId":"2387bb92-3cfe-4e13-80a0-453a1e36ae27", "presentationOrder":1}'
vote recorded (cache updated)n
```
#### In redis:
Poll before and after voting twice on option 2.
```bash
127.0.0.1:6379> keys *
1) "poll:bfe1f24f-52e2-4b80-a677-dfc0f6c727e6:counts"
127.0.0.1:6379> HGETALL poll:bfe1f24f-52e2-4b80-a677-dfc0f6c727e6:counts
1) "1"
2) "1"
127.0.0.1:6379> HGETALL poll:bfe1f24f-52e2-4b80-a677-dfc0f6c727e6:counts
1) "1"
2) "3"
```

### Technical Problems
When running the java application I got these errors in the terminal, but the program still seems to work fine. I assume these are from either jedis og jackson that needs loggin so i added slf4j as a dependecy to the project, and the errors stopped showing up.
```bash
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```
After adding springboot I had to remove this dependecy again to get it to run, but the error did not show up again.