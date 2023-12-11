# Trento Mobility Data Service Wrapper

## Requirements

- Java 8

## Java Build / Run 

Build with Maven:
``
mvn clean package -Dmaven.test.skip=true
``

Run standalone Java app:
``
java -jar target/trento.mobilitydatawrapper-1.0.jar
``
The server is started and listening on port 8080

## Docker

Build image 

``docker build -t smartcommunitylab/trento.mobilitydatawrapper . ``

Run image 

``docker run -p 8080:8080 smartcommunitylab/trento.mobilitydatawrapper``

## Environment Variables
- `DB_PARKINGS_URL` DB url of the parking Database
- `DB_PARKINGS_USERNAME` username for the parking Database
- `DB_PARKINGS_PASSWORD` password for the parking Database
- `DB_TRAFFIC_URL` DB url for the traffic Database
- `DB_TRAFFIC_USERNAME` username for the traffic Database
- `DB_TRAFFIC_PASSWORD` password for the traffic Database