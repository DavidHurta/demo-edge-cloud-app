# TERM PROJECT


Author: David Hurta

To deploy the application, an internet connection is required
for obtaining container images. Subsequently, `docker` and 
`docker-compose` commands are needed.


To build an `edge-cloud:latest` image, run:
```shell
docker build --file Dockerfile --tag edge-cloud:latest .
```

To run the edge cloud application, run:
```shell
docker-compose -f hack/docker-compose.yml up -d
```

This will create a Kafka UI application available at `http://localhost:8080/`.
