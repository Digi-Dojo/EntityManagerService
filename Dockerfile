FROM gradle:7.4.2-jdk17 AS base

USER gradle

WORKDIR /usr/src/digidojo

COPY --chown=gradle:gradle ./DigiDojoSharedModel ./DigiDojoSharedModel

COPY --chown=gradle:gradle ./EntityManagerService ./EntityManagerService

FROM base AS builder

WORKDIR /usr/src/digidojo/EntityManagerService

RUN gradle clean bootJar

FROM eclipse-temurin:17-jdk AS runner

WORKDIR /digidojo

ENV PORT=8200

EXPOSE ${PORT}

COPY --from=builder /usr/src/digidojo/EntityManagerService/build/libs/*.jar ./service.jar

ENTRYPOINT ["java", "-jar", "/digidojo/service.jar"]