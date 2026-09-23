
# pre built image container
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

# download all Maven dependencies -B batch mode
RUN mvn dependency:go-offline -B

COPY src ./src

# build the JAR
RUN mvn package -DskipTests -B

# Stage 2 Runtime
#use jre only image (no compiler, no maven) -> make image smaller
FROM eclipse-temurin:17-jre

WORKDIR /app

# What: Copy ONLY the built JAR from Stage 1
# Why:  The entire Maven + source code from Stage 1 is now GONE.
#       Only the JAR survives. This is the "multi-stage" benefit.
# How:  --from=build refers to the stage named "build" above.
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080


# What: The command that runs when the container starts
# Why:  This IS your app. Container starts → this command runs → Tomcat starts.
# How:  Exec form ["java", "-jar", "app.jar"] = no shell, signals work properly.
ENTRYPOINT ["java", "-jar", "app.jar"]





