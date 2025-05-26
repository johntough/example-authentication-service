FROM openjdk:19-jdk

# Create a custom user with UID 1234 and GID 1234
RUN groupadd -g 1234 customgroup && \
    useradd -m -u 1234 -g customgroup customuser

RUN mkdir -p /data && chown customuser:customgroup /data

# Switch to the custom user
USER customuser

WORKDIR /app

COPY target/example-authentication-service-0.0.1-SNAPSHOT.jar /app/example-authentication-service-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/example-authentication-service-0.0.1-SNAPSHOT.jar"]