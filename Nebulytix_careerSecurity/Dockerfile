FROM maven:3.9.11-eclipse-temurin-17 AS build
ADD . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17.0.16_8-jre-ubi9-minimal AS runtime
RUN adduser -m -d  /usr/share/backed -s /bin/bash pavan
USER pavan
WORKDIR /usr/share/backed
COPY --from=build /target/*.war Nebulytix.war
EXPOSE 5054/tcp
CMD ["java", "-jar", "Nebulytix.war"]