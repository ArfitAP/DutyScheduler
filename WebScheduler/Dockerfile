# Stage 1 — Build Angular
FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npx ng build WebScheduler --configuration production

# Stage 2 — Serve with Nginx
FROM nginx:alpine

COPY --from=build /app/dist/web-scheduler /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]