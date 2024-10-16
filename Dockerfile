FROM eclipse-temurin:21-jdk-ubi9-minimal
RUN mkdir /app
COPY target/microservicio-gateway-0.0.1-SNAPSHOT.jar app/microservicio-gateway-0.0.1-SNAPSHOT.jar
#CMD ["java","-jar","app/microservicio-gateway-0.0.1-SNAPSHOT.jar","--spring.profiles.active=docker"]
CMD ["java","-jar","app/microservicio-gateway-0.0.1-SNAPSHOT.jar","--spring.profiles.active=kubernete"]
# docker build --tag=microservicio-gateway:latest .
# docker run -p8090:8090 -d --net=docker-network --name=microservicio-gateway  microservicio-gateway:latest