# ---- Builder ----
FROM node:20-alpine AS builder
WORKDIR /app
COPY backend/package*.json ./
RUN npm ci
COPY backend/tsconfig.json ./
COPY backend/src ./src
RUN NODE_OPTIONS="--max-old-space-size=4096" npm run build
    
# ---- Runtime ----
FROM node:20-alpine AS runtime
ENV NODE_ENV=production
WORKDIR /app
COPY backend/package*.json ./
RUN npm ci --omit=dev
COPY --from=builder /app/dist ./dist

# Optional: if you rely on PORT at runtime
ARG PORT=3000
ENV PORT=${PORT}
EXPOSE ${PORT}

CMD ["node", "dist/index.js"]