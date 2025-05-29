# Example Authentication Service

## Overview
This is a Spring Boot application that uses **Spring Security** and **OpenID Connect (OIDC)** for user authentication. The app uses both Google and Microsoft as identity providers.

## 🔐 Authentication Overview

The app integrates with **Google** & **Microsoft** to authenticate users. It uses Spring Security's OAuth2 and OIDC support to handle login, session management, and user identity retrieval.

Upon successful authentication with Google/Microsoft, the application:

1. Loads the authenticated user's identity using a custom `CustomOidcUserService`.
2. Checks whether the user already exists in the application database.
3. If not found, a new user is created and stored.
4. Returns an authenticated Spring Security `OidcUser` with appropriate roles and attributes.

### 🛡️ Default Role Assignment
All new users successfully authenticated are assigned the default role:

```java
newUser.setRoles(Set.of("ROLE_USER"));
```

## 🧩 Technologies Used
- Java
- Spring Boot
- Spring Security
- OAuth2 / OIDC
- Google Identity Platform
- SQLite / JPA
- Thymeleaf 

## 📦 Key Components

### `CustomOidcUserService`

This class extends `OidcUserService` to customize how authenticated users are handled. It:

- Extracts the user's information (email, name, Google/Microsoft subject ID) from the `OidcUserRequest`.
- Checks for an existing user in the application's database (`UserRepository`).
- Creates and saves a new user if one does not already exist.
- Assigns a default role (`ROLE_USER`) to new users.
- Wraps the user details into a `DefaultOidcUser` with proper authorities.

## 🚀 Build & Deploy
This project is built with **Maven**, containerized using **Docker**, and run with **Docker Compose**.

✅ Prerequisites
- Java 19
- Maven
- Docker
- Docker Compose

### 🛠 Build the Project
To build the application JAR using Maven:

```code
mvn clean package
```

This will produce a JAR at:

```code
target/example-authentication-service-0.0.1-SNAPSHOT.jar
```

### 🐳 Run with Docker Compose
Start the application and its environment with:
```code
docker-compose up --build
```
This will:

- Build the Docker image
- Mount a persistent SQLite database in a named volume (sqlite-data)
- Expose the app at http://localhost:8080

## 🔧 Identity Provider (IdP) Setup
To enable authentication using Google or Microsoft, you must first register your application with each provider and obtain the necessary credentials.

### Google Identity Platform Setup
Follow the instructions at [Google - OpenID Connect](https://developers.google.com/identity/openid-connect/openid-connect) to enable your app to use OpenID Connect (OIDC) authentication with Google. 

###  Microsoft Entra ID Setup
Follow the instructions at [Microsoft - OpenID Connect](https://learn.microsoft.com/en-us/power-pages/security/authentication/openid-settings#create-an-app-registration-in-azure) to register an application in Microsoft Entra ID to use OpenID Connect (OIDC) authentication with Microsoft.

### 🔑 OAuth2 Environment Configuration
You need to create a .env file at `environment-variables/.env` with your Google / Microsoft credentials as per the IdP Setup:

```code
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
MICROSOFT_CLIENT_ID=your-microsoft-client-id
MICROSOFT_CLIENT_SECRET=your-microsoft-client-secret
MICROSOFT_TENANT_ID=your-microsoft-tenant-id
```

These are injected into the app via environment variables and used in application.yaml

### 🗂 Data Persistence
The SQLite database is mounted to the container at /data/auth.sqlite and is persisted using a Docker volume:

```code
volumes:
- sqlite-data:/data
```
So your user data will persist across container restarts.

## 🧪 Running Tests
Run the unit and integration tests using:
```code
mvn test
```

## 🌐 Accessing the App
Once running, navigate to:

```code
http://localhost:8080
```
### ➡️ 🔐 Sign In
Upon clicking the Sign-In buttons you'll be redirected to either Google or Microsoft Sign-In,. On successful login, a new user is created (if one doesn't already exist), and stored in the SQLite DB with the default role: `ROLE_USER`.
An `OidcUser` user is created and stored in the `SecurityContext` with the appropriate roles created as per application logic, `ROLE_USER` as default.

### ➡️ 🚪👋 Sign Out
After successful sign in you'll see a Sign-Out button.
When you trigger logout `/logout`, Spring Security clears the local security context and invalidates the HTTP session in the application. By default, Spring Security does **NOT** log the user out from the Identity Provider (IdP). Therefore, the user remains logged into Google / Microsoft (IdP).