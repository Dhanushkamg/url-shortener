# QuickLink

A modern, high-performance URL shortener application built with a Spring Boot backend and a React (Vite) frontend.

QuickLink provides a seamless experience for shortening, sharing, and tracking links, featuring a premium glassmorphism UI.

## Features

- **URL Shortening**: Convert long, unwieldy URLs into compact, easy-to-share links.
- **Custom Aliases**: Create personalized, memorable short links (e.g., `quicklink.com/my-campaign`).
- **Click Analytics**: Track click events, including timestamps.
- **QR Code Generation**: Automatically generates downloadable QR codes for every shortened link.
- **Rate Limiting**: Built-in API rate limiting using Bucket4j to prevent abuse.
- **Modern UI**: A responsive, animated frontend featuring premium glassmorphism aesthetics.
- **Robust Backend**: Built on Spring Boot 3 with Flyway database migrations and robust error handling.

## Tech Stack

### Backend
- **Java 17** & **Spring Boot 3**
- **Spring Data JPA** / Hibernate
- **H2 Database** (configured for easy local development, easily swappable to PostgreSQL)
- **Flyway** (Database Migrations)
- **Bucket4j** (Rate Limiting)
- **Testcontainers** & **Mockito** (Testing)

### Frontend
- **React 18**
- **Vite** (Build tool)
- **qrcode.react** (QR Code rendering)
- **Vanilla CSS** (Custom Design System)

## Getting Started

### Prerequisites
- Java 17 or higher
- Node.js (v18+)

### 1. Running the Backend

The backend uses Maven wrapper, so you don't need Maven installed globally. By default, it runs with an in-memory H2 database.

```bash
# Navigate to the project root
cd "URL Shortener"

# Run the Spring Boot application
./mvnw spring-boot:run
```
The backend API will start on `http://localhost:8080`.

### 2. Running the Frontend

The frontend uses Vite with a configured proxy to route API requests to the backend.

```bash
# Open a new terminal tab and navigate to the frontend directory
cd frontend

# Install dependencies
npm install

# Start the development server
npm run dev
```
The frontend application will start on `http://localhost:5173`.

## API Endpoints

- `POST /api/urls`: Create a new shortened URL.
  - Body: `{ "originalUrl": "https://...", "customCode": "optional-alias" }`
- `GET /{shortCode}`: Redirects the user to the original URL and records a click event.

## Architecture Highlights
- **DTO Layer**: Strict separation between internal entities and external API contracts.
- **Global Exception Handling**: Standardized JSON error responses via `@RestControllerAdvice`.
- **Scheduled Cleanup**: Background tasks to prune expired URLs automatically.
