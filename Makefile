.PHONY: test run run-mock run-unit

test:
	./mvnw test

run:
	./mvnw spring-boot:run

run-mock:
	./mvnw spring-boot:run -Dspring-boot.run.profiles=mock

run-unit:
	./mvnw spring-boot:run -Dspring-boot.run.profiles=unit
