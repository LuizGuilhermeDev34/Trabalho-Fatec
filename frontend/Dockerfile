# ─────────────────────────────────────────────────────
# Stage 1: Build do frontend (Node.js)
# ─────────────────────────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm ci --silent

COPY . .
RUN npm run build

# ─────────────────────────────────────────────────────
# Stage 2: Servir com Nginx
# ─────────────────────────────────────────────────────
FROM nginx:1.25-alpine

# Remove config padrão e copia a nossa
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copia os arquivos buildados
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
