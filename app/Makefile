run-dist:
	./build/install/app/bin/app

start:
	./gradlew run

build:
	./gradlew clean build

clean:
	./gradlew clean

test:
	./gradlew clean
	./gradlew test

report:
	./gradlew jacocoTestReport

lint:
	./gradlew checkstyleMain checkstyleTest

build-run: build run

.PHONY: build