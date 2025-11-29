# Project

This is a basic Java project using Maven for dependency management.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Project Structure

```
.
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── example/
│                   └── App.java
├── pom.xml
└── README.md
```

## Building the Project

To build the project, run:

```bash
mvn clean install
```

## Running the Application

To run the application, use:

```bash
mvn exec:java -Dexec.mainClass="com.example.App"
```

## Testing

To run the tests:

```bash
mvn test
``` 