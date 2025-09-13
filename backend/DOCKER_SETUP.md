# 🐳 Docker Setup Guide

## 📋 Prerequisites
- Docker Desktop installed and running
- Docker Compose v2.0+

## 🚀 Quick Start

### 1. Create Environment File
Create a `.env` file in the backend directory with the following content:

```bash
# Server Configuration
NODE_ENV=production
PORT=3000

# Database Configuration
MONGODB_URI=mongodb://app_user:app_password@mongodb:27017/usermanagement?authSource=usermanagement

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-here-change-this-in-production

# Google OAuth Configuration
GOOGLE_CLIENT_ID=your-google-client-id-here
GOOGLE_CLIENT_SECRET=your-google-client-secret-here
GOOGLE_REDIRECT_URI=http://localhost:3001/auth/google/callback

# MongoDB Root Credentials (for Docker Compose)
MONGO_ROOT_USERNAME=admin
MONGO_ROOT_PASSWORD=password
MONGO_DATABASE=usermanagement
```

### 2. Build and Start Services
```bash
# Build and start all services
docker-compose up --build

# Or run in detached mode
docker-compose up --build -d
```

### 3. Development Mode
For development with hot reload:
```bash
# Uses docker-compose.override.yml automatically
docker-compose up --build
```

## 🏗️ Services

### MongoDB Database
- **Container**: `cpen321-mongodb`
- **Port**: `27017`
- **Database**: `usermanagement`
- **User**: `app_user` / `app_password`
- **Admin**: `admin` / `password`

### API Server
- **Container**: `cpen321-api`
- **Port**: `3001` (mapped from internal 3000)
- **Environment**: Production or Development
- **Dependencies**: MongoDB

## 🔧 Useful Commands

### Start Services
```bash
# Start all services
docker-compose up

# Start in background
docker-compose up -d

# Start with rebuild
docker-compose up --build
```

### Stop Services
```bash
# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ deletes data)
docker-compose down -v
```

### View Logs
```bash
# View all logs
docker-compose logs

# View specific service logs
docker-compose logs api
docker-compose logs mongodb

# Follow logs in real-time
docker-compose logs -f api
```

### Database Management
```bash
# Connect to MongoDB
docker-compose exec mongodb mongosh -u admin -p password

# Backup database
docker-compose exec mongodb mongodump --db usermanagement --out /backup

# Restore database
docker-compose exec mongodb mongorestore --db usermanagement /backup/usermanagement
```

## 🐛 Troubleshooting

### Port Conflicts
If you get port conflicts:
```bash
# Check what's using the ports
lsof -ti:3001
lsof -ti:27017

# Kill processes using the ports
kill $(lsof -ti:3001)
kill $(lsof -ti:27017)
```

### Container Issues
```bash
# Rebuild specific service
docker-compose build api

# Remove and recreate containers
docker-compose down
docker-compose up --build

# Clean up everything
docker-compose down -v --rmi all
```

### Database Issues
```bash
# Reset database (⚠️ deletes all data)
docker-compose down -v
docker-compose up --build

# Check database connection
docker-compose exec api node -e "console.log('Testing DB connection...')"
```

## 📁 File Structure
```
backend/
├── docker-compose.yml          # Main compose file
├── docker-compose.override.yml # Development overrides
├── Dockerfile                  # API container definition
├── mongo-init.js              # Database initialization
├── .env                       # Environment variables (create this)
└── DOCKER_SETUP.md           # This file
```

## 🔐 Security Notes

1. **Change default passwords** in production
2. **Use strong JWT secrets** (generate with `openssl rand -base64 32`)
3. **Set up proper Google OAuth credentials**
4. **Use environment-specific configurations**
5. **Enable MongoDB authentication** in production

## 🌐 Network Configuration

- **Network**: `cpen321-network`
- **API Access**: `http://localhost:3001`
- **MongoDB Access**: `localhost:27017`
- **Internal Communication**: Services communicate via service names

## 📊 Monitoring

### Health Checks
```bash
# Check API health
curl http://localhost:3001/api/health

# Check MongoDB
docker-compose exec mongodb mongosh --eval "db.adminCommand('ping')"
```

### Resource Usage
```bash
# View resource usage
docker stats

# View specific container stats
docker stats cpen321-api cpen321-mongodb
```
