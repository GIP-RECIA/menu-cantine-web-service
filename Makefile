.PHONY: run run-mock run-unit

run:
	mvn spring-boot:run

run-mock:
	mvn spring-boot:run -Dspring-boot.run.profiles=mock

run-unit:
	mvn spring-boot:run -Dspring-boot.run.profiles=unit