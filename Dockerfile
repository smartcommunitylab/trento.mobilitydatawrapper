FROM maven:3.3.3-jdk-8 AS mvn
WORKDIR /tmp
COPY ./pom.xml /tmp/mobility/pom.xml
COPY ./src /tmp/mobility/src
WORKDIR /tmp/mobility
RUN mvn clean install -Dmaven.test.skip=true

FROM openjdk:8-jdk-alpine
ENV FOLDER=/tmp/mobility/target
ENV APP=trento.mobilitydatawrapper-1.0.jar
ARG USER=mobility
ARG USER_ID=3004
ARG USER_GROUP=mobility
ARG USER_GROUP_ID=3004
ARG USER_HOME=/home/${USER}

RUN  addgroup -g ${USER_GROUP_ID} ${USER_GROUP}; \
     adduser -u ${USER_ID} -D -g '' -h ${USER_HOME} -G ${USER_GROUP} ${USER} ;

WORKDIR  /home/${USER}/app
RUN chown ${USER}:${USER_GROUP} /home/${USER}/app
COPY --from=mvn --chown=mobility:mobility ${FOLDER}/${APP} /home/${USER}/app/mobilitydatawrapper.jar

USER mobility
CMD ["java", "-jar", "mobilitydatawrapper.jar"]