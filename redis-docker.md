## Redis Setup

### Quick Start
```bash
# Start Redis container
docker run -d --name redis -p 6379:6379 redis:latest
# Check if container is running
docker ps
```

### Container Management
```bash
# Start existing container
docker start redis

# Stop container
docker stop redis

# Remove container (data will be lost)
docker rm redis

# View container logs
docker logs redis
```

### Redis Access
```bash
# Connect to Redis CLI
docker exec -it redis redis-cli

# Test connection
docker exec -it redis redis-cli ping
```

### Port
- **6379**: Redis server

### Persistent Data (Optional)
To persist data across container restarts, add a volume mount:
```bash
docker run -d --name redis \
  -p 6379:6379 \
  -v redis-data:/data \
  redis:latest
```
