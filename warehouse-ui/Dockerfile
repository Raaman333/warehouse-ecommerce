# ─── Stage 1: Build ──────────────────────────────────────────────────────────
FROM node:22-alpine AS builder
WORKDIR /app

# Install dependencies (layer-cached)
COPY package.json package-lock.json ./
RUN npm ci --silent

# Build production bundle
COPY . .
RUN npm run build

# ─── Stage 2: Serve via Nginx ────────────────────────────────────────────────
FROM nginx:1.27-alpine AS runtime

# Custom nginx config for SPA routing (sends unknown paths to index.html)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy Vite build output
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
