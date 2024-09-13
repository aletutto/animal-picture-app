FROM openjdk:17-jdk-alpine
RUN addgroup -S aletutto && adduser -S aletutto -G aletutto
USER aletutto:aletutto
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","de/aletutto/animal/picture/app/AnimalPictureApplication"]