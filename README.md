# Javatheque - Spring Boot REST API

A REST API to manage a personal movie library, built with Spring Boot, Spring Data JPA, MySQL, and TMDB (The Movie Database) API integration.

## Features

- **User Authentication**: Registration, login and logout with session management
- **Library Management**: Each user has their own movie library
- **Movie Management**: Add, update, delete and search movies
- **TMDB Integration**: Search and automatically retrieve movie information from TMDB
- **REST API**: Documented RESTful endpoints

## Technologies

- **Spring Boot 4.0.0-RC2** (Java 21)
- **Spring Data JPA**: Data access with Hibernate
- **MySQL**: Relational database
- **Liquibase**: Database migration management
- **Spring Security**: Security and authentication
- **Spring RestClient**: HTTP client for TMDB API
- **Lombok**: Boilerplate code reduction
- **Gson**: JSON parsing for TMDB
- **Gradle**: Dependency and build management

## Project Architecture

The project follows a layered architecture respecting Spring conventions:

```
src/main/java/fr/corentin/javatheque/
├── configuration/      # Spring Configurations (Security, RestClient)
├── controller/         # REST Controllers (API endpoints)
├── dto/               # Data Transfer Objects
├── model/             # JPA Entities
├── repository/        # Spring Data JPA Repositories
└── service/           # Business Logic
```

## Prerequisites

- **Java 21** or higher
- **MySQL 8.0** or higher
- **Gradle** (included via wrapper)
- **TMDB API Key** (free at https://www.themoviedb.org/)

## Installation and Configuration

### 1. Clone the project

```bash
cd javatheque
```

### 2. Configure MySQL

Create a MySQL database:

```sql
CREATE DATABASE javatheque CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configure the application

Copy the example configuration file:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Then edit `src/main/resources/application.properties` with your settings:

```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/javatheque?createDatabaseIfNotExist=true
spring.datasource.username=your_username
spring.datasource.password=your_password

# TMDB API Configuration
tmdb.api.key=your_tmdb_api_key
```

⚠️ **Important**: The `application.properties` file is ignored by git to protect your credentials.

### 4. Configure Docker (Optional)

Copy the Docker example files:

```bash
cp Dockerfile.example Dockerfile
cp docker-compose.yml.example docker-compose.yml
```

Then edit `docker-compose.yml` with your credentials:
- Replace `your_root_password` with a secure root password
- Replace `your_mysql_user` with your MySQL username
- Replace `your_mysql_password` with your MySQL password
- Replace `your_tmdb_api_key_here` with your TMDB API key

⚠️ **Important**: Docker files are ignored by git to protect your credentials.

### 5. Run the application

**Using Gradle (local):**
```bash
./gradlew bootRun
```

**Or using Docker Compose:**
```bash
docker-compose up -d
```

The application will be accessible at:
- **Application**: http://localhost:8080
- **phpMyAdmin**: http://localhost:8081 (only with Docker)

## API Endpoints

### 🔐 Authentication

| Method | Endpoint | Description | Request Body |
|---------|----------|-------------|------------------|
| `POST` | `/api/auth/register` | Create a new account | `{ "lastname": "Doe", "firstname": "John", "email": "john@example.com", "password": "password123" }` |
| `POST` | `/api/auth/login` | Log in | `{ "email": "john@example.com", "password": "password123" }` |
| `POST` | `/api/auth/logout` | Log out | - |
| `GET` | `/api/auth/me` | Get current user info | - |

### 🎬 Movies

| Method | Endpoint | Description | Parameters / Body |
|---------|----------|-------------|-------------------|
| `GET` | `/api/films/search` | Search movies on TMDB | `?title=Matrix&language=en-US&page=1` |
| `POST` | `/api/films` | Add a movie to library | `{ "tmdbId": 603, "lang": "en", "support": "Blu-ray", "rate": 9.5, "opinion": "Excellent!" }` |
| `GET` | `/api/films/{filmId}` | Get movie details | `{filmId}` = Movie ID |
| `PUT` | `/api/films/{filmId}` | Update a movie | `{ "lang": "en", "support": "4K UHD", "rate": 10.0, "opinion": "Masterpiece!" }` |
| `DELETE` | `/api/films/{filmId}` | Delete a movie | `{filmId}` = Movie ID |

### 📚 Library

| Method | Endpoint | Description | Parameters |
|---------|----------|-------------|------------|
| `GET` | `/api/library` | Get all movies from your library | - |
| `GET` | `/api/library/search` | Search in your library | `?search=matrix` |

### 📋 HTTP Response Codes

| Code | Status | Description |
|------|---------------|-------------|
| `200` | OK | Request successful |
| `201` | Created | Resource created successfully |
| `204` | No Content | Deletion successful |
| `400` | Bad Request | Invalid data |
| `401` | Unauthorized | Not authenticated |
| `403` | Forbidden | Access denied |
| `404` | Not Found | Resource not found |
| `500` | Internal Server Error | Server error |

## Database Structure

The database schema is managed by Liquibase and created automatically on startup:

- **users**: Application users
- **libraries**: Libraries (one per user)
- **films**: Movies in libraries
- **film_actors**: Association table for actors (ElementCollection)

## User Interface

The application includes a modern and responsive web interface accessible via:
- **Home Page**: http://localhost:8080/
- **Login**: http://localhost:8080/login.html
- **Register**: http://localhost:8080/register.html
- **Library**: http://localhost:8080/library.html (authentication required)
- **Search**: http://localhost:8080/search.html (authentication required)

## DTOs Architecture

DTOs are organized in classes with inner classes for better clarity:

```java
UserDTO.Register    // Registration
UserDTO.Login       // Login
UserDTO.Response    // User response

FilmDTO.Request         // Add/Update movie
FilmDTO.Response        // Movie response
FilmDTO.SearchRequest   // TMDB search

ResponseDTO.Success // Success response
ResponseDTO.Error   // Error response
```

