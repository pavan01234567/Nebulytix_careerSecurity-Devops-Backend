FROM node:18-alpine AS builder
ADD . /app
WORKDIR /app 
RUN npm install && \
    npm run build 

FROM nginx:1.29.5-alpine AS runtime
LABEL Project="Nebulytix-Technologies-task-Devops-Frontend"
LABEL author="Pavan Kumar"
COPY --from=builder /app/dist /usr/share/nginx/html
RUN adduser -m -h /usr/share/demo -s /bin/sh Pavan
USER Pavan
EXPOSE 80
