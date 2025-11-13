# Javatheque - Spring Boot REST API

A REST API to manage a personal movie library, built with Spring Boot, Spring Data JPA, MySQL, and TMDB (The Movie Database) API integration.

## Features

- **User Authentication**: Registration, login and logout with session management
- **Library Management**: Each user has their own movie library (Vidéothèque)
- **Movie Management**: Add, update, delete and search movies
- **TMDB Integration**: Search and automatically retrieve movie information from TMDB with pagination
- **REST API**: Documented RESTful endpoints
- **Retro Gaming UI**: Pixel art black & white theme inspired by classic arcade games

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
git clone <repository-url>
cd javatheque
```

### 2. Configure environment variables

Create a `.env` file at the root of the project:

```bash
# Copy the example file
cp .env.example .env
```

Then edit the `.env` file with your settings:

```env
# MySQL Database Configuration
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=javatheque
MYSQL_USER=javatheque
MYSQL_PASSWORD=your_secure_password

# Spring Datasource Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/javatheque?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root

# TMDB API Configuration
# Get your API key at: https://www.themoviedb.org/settings/api
TMDB_API_KEY=your_tmdb_api_key_here

# Server Configuration
SERVER_PORT=8080

# Spring Profile (dev, prod)
SPRING_PROFILES_ACTIVE=dev
```

⚠️ **Important**: 
- The `.env` file is ignored by git to protect your credentials
- Get your free TMDB API key at https://www.themoviedb.org/settings/api

### 3. Run the application

**Using Docker Compose**

```bash
# Build and start all services
docker-compose up -d

# Check logs
docker-compose logs -f app

# Stop services
docker-compose down
```

The application will be accessible at:
- **Application**: http://localhost:8080
- **API**: http://localhost:8080/api

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

### 📚 Vidéothèque (Library)

| Method | Endpoint | Description | Parameters |
|---------|----------|-------------|------------|
| `GET` | `/api/library` | Get all movies from your library | - |
| `GET` | `/api/library/search` | Search in your library | `?search=matrix` |


## User Interface

The application includes a **retro gaming pixel art** web interface with a black & white theme inspired by classic arcade games:

- **Home Page**: http://localhost:8080/
- **Login**: http://localhost:8080/login.html
- **Register**: http://localhost:8080/register.html
- **Vidéothèque (Library)**: http://localhost:8080/library.html (authentication required)
- **Search Movies**: http://localhost:8080/search.html (authentication required)

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

## Environment Variables

All sensitive configuration is managed through environment variables in the `.env` file:

| Variable | Description | Example | Required |
|----------|-------------|---------|----------|
| `MYSQL_ROOT_PASSWORD` | MySQL root password | `root` | Yes |
| `MYSQL_DATABASE` | Database name | `javatheque` | Yes |
| `MYSQL_USER` | MySQL user | `javatheque` | Yes |
| `MYSQL_PASSWORD` | MySQL password | `javatheque123` | Yes |
| `TMDB_API_KEY` | TMDB API key | `your_api_key` | Yes |
| `SERVER_PORT` | Application port | `8080` | No (default: 8080) |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `dev` or `prod` | No (default: dev) |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins (comma-separated) | `https://myapp.com,http://localhost:3000` | No (default: localhost) |
| `SESSION_COOKIE_SECURE` | Enable secure cookies (HTTPS only) | `true` or `false` | No (default: false) |

## Production Deployment

When deploying to production, make sure to configure the following environment variables:

### Required for Production

1. **CORS Configuration**: Add your production domain to allowed origins

```bash
# Example for production with HTTPS
CORS_ALLOWED_ORIGINS=https://javatheque.yourdomain.com

# Or multiple domains (comma-separated, no spaces)
CORS_ALLOWED_ORIGINS=https://javatheque.yourdomain.com,https://app.yourdomain.com
```

2. **Secure Cookies**: Enable secure cookies for HTTPS

```bash
SESSION_COOKIE_SECURE=true
```

### Example Production Configuration

```bash
# Production .env file
MYSQL_ROOT_PASSWORD=your_secure_root_password
MYSQL_DATABASE=javatheque
MYSQL_USER=javatheque
MYSQL_PASSWORD=your_secure_password
TMDB_API_KEY=your_tmdb_api_key
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod

# CORS - Add your production domain
CORS_ALLOWED_ORIGINS=https://javatheque.yourdomain.com

# Enable secure cookies for HTTPS
SESSION_COOKIE_SECURE=true
```

### Docker Deployment

If you're using Docker, pass environment variables to your container:

```bash
docker run -d \
  -e MYSQL_ROOT_PASSWORD=secure_password \
  -e TMDB_API_KEY=your_api_key \
  -e CORS_ALLOWED_ORIGINS=https://javatheque.yourdomain.com \
  -e SESSION_COOKIE_SECURE=true \
  -p 8080:8080 \
  your-image-name
```

Or add them to your `docker-compose.yml`:

```yaml
services:
  app:
    environment:
      - CORS_ALLOWED_ORIGINS=https://javatheque.yourdomain.com
      - SESSION_COOKIE_SECURE=true
```

## Author

**Corentin** - 2024

## License

This project is private and not licensed for public use.

