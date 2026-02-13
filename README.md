# Movie App 

A modern Android application built with Jetpack Compose that displays popular movies from The Movie Database (TMDB) API. The app features infinite scrolling, movie details, search functionality, and follows Clean Architecture principles with MVVM pattern.

## Features

- Movie List: Browse popular movies with infinite scrolling
- **Movie Details**: View comprehensive information about each movie including:
  - Movie poster and backdrop images
  - Rating, votes, runtime, and release date
  - Genres and production companies
  - Overview and tagline
- **Search**: Real-time movie search with debounced autocomplete
- **Modern UI**: Beautiful Material Design 3 interface with dark/light theme support
- **White Label Support**: Easy customization with dynamic color theming

## Architecture

The app follows **Clean Architecture** principles with clear separation of concerns.


### Architecture Layers

1. **Presentation Layer** (presentation module)
   - Jetpack Compose UI screens
   - ViewModels handling UI state
   - Design system following Atomic Design
   - Navigation components

2. **Domain Layer** (domain module)
   - Pure Kotlin business logic
   - Use cases (Single Responsibility Principle)
   - Domain models
   - Repository interfaces

3. **Data Layer** (data module)
   - Retrofit API service
   - DTOs and data mapping
   - Repository implementations
   - Network configuration

## Tech Stack

### Core Technologies
- **100% Kotlin**: Modern, concise, and safe
- **Jetpack Compose**: Declarative UI framework
- **Coroutines & Flow**: Asynchronous programming
- **Material Design 3**: Modern UI components with dynamic colors

### Architecture & Design Patterns
- **Clean Architecture**: Separation of concerns across layers
- **MVVM**: Model-View-ViewModel pattern
- **Repository Pattern**: Data abstraction layer
- **SOLID Principles**: Maintainable and scalable code
- **Atomic Design**: Component-based design system

### Dependency Injection
- **Hilt (Dagger)**: Type-safe dependency injection
- **KSP**: Kotlin Symbol Processing for code generation

### Networking
- **Retrofit**: Type-safe HTTP client for REST API
- **OkHttp**: HTTP client with logging interceptor
- **Gson**: JSON serialization/deserialization

### Image Loading
- **Coil**: Kotlin-first image loading library

### Navigation
- **Navigation Component**: Type-safe navigation with Compose

### Testing
- **JUnit**: Unit testing framework
- **MockK**: Mocking library for Kotlin
- **Turbine**: Flow testing library

### Code Quality
- **Detekt**: Static code analysis
- **ProGuard**: Code obfuscation and optimization
- **KDoc**: Comprehensive documentation

## Design System

The app follows **Atomic Design** principles:

- **Atoms**: Basic reusable components (`LoadingIndicator`)
- **Molecules**: Simple component combinations (`ErrorMessage`)
- **Organisms**: Complex components (`MovieCard`)
- **Templates/Pages**: Full screens (`MovieListScreen`, `MovieDetailsScreen`)

## Security

- API key stored securely in `local.properties` (not hardcoded)
- Exposed via BuildConfig in the data module
- ProGuard rules configured for release builds

## 📦 Setup

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11 or higher
- Android SDK (API 24+)

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd Movie-App
```

2. Add your TMDB API key to `local.properties`:
```properties
tmdbApiKey=your_api_key_here
```

3. Sync and build the project:
```bash
./gradlew build
```

4. Run the app on an emulator or device

## Testing

### Unit Tests
```bash
./gradlew test
```

### Code Quality Check
```bash
./gradlew detekt
```

## Project Structure Highlights

### Clean Architecture Benefits
- **Testability**: Each layer can be tested independently
- **Maintainability**: Clear separation of concerns
- **Scalability**: Easy to add new features
- **Reusability**: Domain logic is framework-independent

### SOLID Principles Implementation
- **Single Responsibility**: Each class has one reason to change
- **Open/Closed**: Open for extension, closed for modification
- **Liskov Substitution**: Interfaces allow substitution
- **Interface Segregation**: Focused interfaces
- **Dependency Inversion**: High-level modules don't depend on low-level modules

## 🚀 Future Enhancements

### Planned Features
- **Favorites**: Save favorite movies locally
- **Offline Support**: Cache movies for offline viewing
- **Movie Trailers**: Play trailers using YouTube API
- **Reviews**: Display user reviews and ratings
- **Cast & Crew**: Show movie cast and crew information
- **Similar Movies**: Recommendations based on selected movie
- **Filtering**: Filter movies by genre, year, rating
- **Pagination Improvements**: Better infinite scroll handling
- **Error Handling**: More robust error handling and retry mechanisms
- **Accessibility**: Enhanced accessibility features
- **Localization**: Multi-language support
- **Dark Mode Toggle**: Manual theme switching
- **Biometric Authentication**: Secure app access

### Technical Improvements
- **Room Database**: Local data persistence
- **WorkManager**: Background tasks for data sync
- **Paging 3**: Improved pagination handling
- **Compose Animation**: Enhanced UI animations
- **CI/CD**: Automated testing and deployment
- **Performance Monitoring**: Firebase Performance or similar
- **Crash Reporting**: Firebase Crashlytics integration
- **Analytics**: User behavior tracking

## 🎯 What Makes This App Stand Out

### Code Quality
- ✅ **Clean Architecture**: Proper separation of concerns
- ✅ **SOLID Principles**: Maintainable and scalable codebase
- ✅ **100% Kotlin**: Modern language features
- ✅ **Comprehensive Testing**: Unit tests
- ✅ **Code Documentation**: KDoc comments throughout

### Architecture Excellence
- ✅ **Multi-Module Structure**: Clear module boundaries
- ✅ **Dependency Injection**: Hilt for type-safe DI
- ✅ **Repository Pattern**: Data abstraction layer
- ✅ **Use Cases**: Single responsibility business logic

### User Experience
- ✅ **Material Design 3**: Modern UI
- ✅ **Infinite Scrolling**: Smooth pagination
- ✅ **Search with Autocomplete**: Real-time search
- ✅ **Error Handling**: User-friendly error messages
- ✅ **Loading States**: Clear loading indicators

### Developer Experience
- ✅ **Type-Safe Navigation**: Compile-time navigation safety
- ✅ **Design System**: Reusable components
- ✅ **White Label Support**: Easy customization
- ✅ **Code Quality Tools**: Detekt, ProGuard configured

**Note**: This app uses The Movie Database (TMDB) API. Make sure to add your API key to `local.properties` before running the app.

