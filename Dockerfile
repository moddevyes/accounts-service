FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
EXPOSE 8001
ARG JAR_FILE=build/libs/accounts-service-0.0.1.jar
ARG JAR_FILE
COPY ${JAR_FILE} accounts-service-0.0.1.jar
ENTRYPOINT ["java","-jar","accounts-service-0.0.1.jar"]


# BUILD
# docker build -t accounts-service:latest .

# IMAGE built and in docker

# docker images
# REPOSITORY                    TAG             IMAGE ID       CREATED         SIZE
# accounts-service             latest          6977a8e7c2a4   4 minutes ago   386MB

# NETWORK for MySQL and Container
# docker network create shipments-netw

# RUN the container
# docker run -p8001:8001 accounts-service:latest

# INSPECT
# docker inspect message-server
# docker stop message-server
# docker rm message-server

