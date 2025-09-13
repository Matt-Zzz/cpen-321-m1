# Docker Setup for Backend

This document explains how to run the backend application using Docker.

## Prerequisites

- Docker and Docker Compose installed on your system
- Environment variables configured

## Quick Start

1. **Copy environment file:**
   ```bash
   cp env.example .env
   ```

2. **Edit the `.env` file** with your actual values:
   - Set a strong `JWT_SECRET`
   - Add your `GOOGLE_CLIENT_ID`
   - Update MongoDB credentials if needed

3. **Build and run with Docker Compose:**
   ```bash
   docker-compose up --build
   ```

4. **Access the application:**
   - Backend API: http://localhost:3000
   - MongoDB: localhost:27017

## Manual Docker Commands

### Build the Docker image:
```bash
docker build -t backend-app .
```

### Run the container:
```bash
docker run -p 3000:3000 \
  -e MONGODB_URI="your-mongodb-uri" \
  -e JWT_SECRET="your-jwt-secret" \
  -e GOOGLE_CLIENT_ID="your-google-client-id" \
  -v $(pwd)/uploads:/app/uploads \
  backend-app
```

## Docker Compose Services

### Backend Service
- **Image**: Built from local Dockerfile
- **Port**: 3000
- **Environment**: Production
- **Volumes**: Uploads directory mounted
- **Dependencies**: MongoDB

### MongoDB Service
- **Image**: mongo:7.0
- **Port**: 27017
- **Volumes**: Persistent data storage
- **Initialization**: Custom script for user creation and indexes

## Environment Variables

| Variable | Description | Required | Default |
|----------|-------------|----------|---------|
| `NODE_ENV` | Environment mode | No | production |
| `PORT` | Server port | No | 3000 |
| `MONGODB_URI` | MongoDB connection string | Yes | - |
| `JWT_SECRET` | JWT signing secret | Yes | - |
| `GOOGLE_CLIENT_ID` | Google OAuth client ID | Yes | - |
| `MONGO_ROOT_USERNAME` | MongoDB root username | No | admin |
| `MONGO_ROOT_PASSWORD` | MongoDB root password | No | password |
| `MONGO_DATABASE` | Application database name | No | usermanagement |

## Health Check

The Docker container includes a health check that:
- Runs every 30 seconds
- Times out after 3 seconds
- Waits 5 seconds before starting
- Retries 3 times before marking as unhealthy
- Checks the `/api/health` endpoint

## Security Features

- **Non-root user**: Application runs as `nodejs` user (UID 1001)
- **Signal handling**: Uses `dumb-init` for proper signal handling
- **Minimal base image**: Uses Alpine Linux for smaller attack surface
- **Production dependencies only**: Final image excludes dev dependencies

## Development vs Production

### Development
```bash
# Run with hot reload
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
```

### Production
```bash
# Run with production settings
docker-compose up -d
```

## Troubleshooting

### Container won't start
1. Check environment variables are set correctly
2. Verify MongoDB is accessible
3. Check container logs: `docker-compose logs backend`

### Database connection issues
1. Ensure MongoDB service is running: `docker-compose ps`
2. Check MongoDB logs: `docker-compose logs mongodb`
3. Verify connection string format

### Permission issues
1. Check uploads directory permissions
2. Ensure Docker has access to mounted volumes

### Health check failing
1. Verify the application is responding on port 3000
2. Check if `/api/health` endpoint exists
3. Review application logs for errors

## Useful Commands

```bash
# View logs
docker-compose logs -f backend

# Execute commands in running container
docker-compose exec backend sh

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Rebuild without cache
docker-compose build --no-cache

# Scale services
docker-compose up --scale backend=3
```

## Production Deployment

For production deployment:

1. **Use a reverse proxy** (nginx, traefik)
2. **Set up SSL/TLS** certificates
3. **Use secrets management** for sensitive data
4. **Configure logging** aggregation
5. **Set up monitoring** and alerting
6. **Use container orchestration** (Kubernetes, Docker Swarm)

## Multi-stage Build Benefits

The Dockerfile uses a multi-stage build to:
- Keep the final image small (only production dependencies)
- Improve build caching
- Separate build and runtime environments
- Reduce attack surface
