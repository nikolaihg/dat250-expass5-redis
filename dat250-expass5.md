# HVL DAT250 Experiment Assignment 5
- **Name:** Nikolai Hansen Gangstø
- **User:** nigan4342
- **Date:** October 2025

## Code
Code link: [GitHub Repository](https://github.com/nikolaihg/dat250-expass4)

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
- `Application.java` is the entrypoint
- `\manager\PollManager.java` manages the polls

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

### Step 4: Rest
Since the previous step worked, you now could add the REST

### Technical Problems
When running the java application I got these errors in the terminal, but the program still seems to work fine. I assume these are from either jedis og jackson that needs loggin so i added slf4j as a dependecy to the project, and the errors stopped showing up.
```bash
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```