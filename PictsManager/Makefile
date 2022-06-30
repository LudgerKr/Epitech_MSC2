##
## EPITECH PROJECT, 2020
## Makefile
## File description:
## toto
##

install:
	cd backend && mvn -U clean install -DskipTests

dev:
	cd backend && mvn spring-boot:run

prod_log:
	docker-compose build && docker-compose up

prod:
	docker-compose build && docker-compose up -d

delete_docker:
	docker stop docker_java_spring_boot && \
	docker stop docker_mysql && \
	docker stop docker_apk && \
	docker rm docker_java_spring_boot && \
	docker rm docker_mysql && \
	docker rm docker_apk
