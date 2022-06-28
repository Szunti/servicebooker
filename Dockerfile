FROM openjdk:11-jre-slim AS builder
WORKDIR /opt/app/
COPY target/servicebooker-*.jar servicebooker.jar
RUN ["java", "-Djarmode=layertools", "-jar", "servicebooker.jar", "extract"]

FROM openjdk:11-jre-slim
WORKDIR /opt/servicebooker/
COPY --from=builder /opt/app/dependencies/ ./
COPY --from=builder /opt/app/spring-boot-loader/ ./
COPY --from=builder /opt/app/snapshot-dependencies/ ./
COPY --from=builder /opt/app/application/ ./
EXPOSE 8080/tcp
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]