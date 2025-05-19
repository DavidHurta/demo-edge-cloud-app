# Demonstration Kafka Edge-Cloud Application

To deploy the application, an internet connection is required
for obtaining the container images. Subsequently, [`docker`](https://docs.docker.com/engine/install/ubuntu/) and 
[`docker compose`](https://docs.docker.com/compose/install/linux/#install-using-the-repository) commands are needed.

To build an `edge-cloud:latest` image, run:
```shell
$ docker build --file Dockerfile --tag edge-cloud:latest .
```

To run the edge cloud application, run:
```shell
$ docker compose up -d
```

This will create a Kafka UI application available at `http://localhost:8080/`.

To stop the application, run:

```shell
$ docker compose down
```

Used tools in development and their reported versions:

| Tool           | Version |
|----------------|---------|
| docker         | 28.0.4  |
| docker compose | v2.34.0 |
