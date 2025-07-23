FROM icr.io/appcafe/open-liberty:full-java21-openj9-ubi-minimal

COPY --chown=1001:0 /src/main/liberty/config /config
COPY --chown=1001:0 libs/mariadb-java-client-3.5.2.jar /config/resources/

RUN features.sh

COPY --chown=1001:0 target/*.war /config/apps

RUN configure.sh