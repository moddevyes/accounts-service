FROM eclipse-temurin:17-jdk-alpine
EXPOSE 8001
RUN mkdir /opt/commerceapps
ARG JAR_FILE=build/libs/accounts-service-0.0.1.jar
COPY ${JAR_FILE} /opt/commerceapps/accounts-service-0.0.1.jar
ENTRYPOINT ["java","-jar","/opt/commerceapps/accounts-service-0.0.1.jar"]