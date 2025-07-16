# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a full-stack ERP system called "ERPoor" (凱亞 ERP) with a Vue.js/Quasar frontend and Spring Boot backend. The system manages inventory, sales orders, and customer data for small to medium businesses.

## Frontend Architecture

### Technology Stack
- **Framework**: Vue 3 with Composition API
- **UI Framework**: Quasar Framework v2.16.0
- **State Management**: Pinia (stores in `src/stores/`)
- **Routing**: Vue Router 4
- **Build Tool**: Vite (via Quasar CLI)
- **Language**: TypeScript with strict mode
- **Internationalization**: Vue i18n (Chinese Traditional as default)

### Key Directories
- `src/components/` - Reusable Vue components (forms, etc.)
- `src/pages/` - Page-level components for routes
- `src/layouts/` - Layout components (MainLayout is primary)
- `src/stores/` - Pinia stores for state management
- `src/boot/` - Bootstrap files (axios, i18n setup)
- `src/router/` - Vue Router configuration

### State Management
- Uses Pinia for global state
- Event bus (`src/utils/eventBus.ts`) for component communication
- Local storage for user preferences (theme, user info)

### Authentication
- JWT-based authentication with Spring Security backend
- User state managed in MainLayout component
- Protected routes use `meta: { requiresAuth: true }`

## Backend Architecture

### Technology Stack
- **Framework**: Spring Boot 3.5.0
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA
- **Security**: Spring Security with JWT
- **Build Tool**: Maven
- **Java Version**: 17

### Key Modules
- `account/` - Authentication, user management, JWT handling
- `dashboard/` - Dashboard data and analytics
- `inventory/` - Product and inventory management
- `saleOrder/` - Sales order processing
- `util/` - Common utilities and responses

### Database
- PostgreSQL database with JPA entities
- Database views for complex queries (inventory, alerts)
- Initialization script: `initialize.sql`

## Development Commands

### Frontend (run from `/Frontend` directory)
```bash
# Install dependencies
npm install

# Start development server (runs on port 80)
npm run dev
# or
quasar dev

# Build for production
npm run build
# or
quasar build

# Lint code
npm run lint

# Format code
npm run format

# Type check
# No explicit typecheck script - handled by Vite plugin
```

### Backend (run from `/Backend` directory)
```bash
# Run application
./mvnw spring-boot:run

# Build
./mvnw clean install

# Run tests
./mvnw test
```

## API Integration

### Frontend API Configuration
- Base API configuration in `src/boot/axios.ts`
- Backend runs on different port than frontend
- CORS configured in Spring Boot

### Key API Endpoints
- `/auth/login` - User authentication
- `/saleOrder` - Sales order management
- `/saleOrder/products` - Product options for orders
- `/dashboard` - Dashboard data

## Development Notes

### Frontend Conventions
- Uses Composition API with `<script setup>`
- TypeScript interfaces defined inline or in component files
- Quasar components with responsive design (mobile-first)
- Form validation using Quasar's built-in rules
- Notifications using Quasar's `$q.notify()`

### UI/UX Patterns
- Material Design icons and components
- Responsive grid layouts with Quasar classes
- Modern gradient backgrounds and smooth animations
- Dark/light theme support with system preference detection
- Chinese Traditional as primary language

### Form Handling
- Complex forms like `ShippingForm.vue` handle multiple product items
- Real-time calculations for totals and pricing
- Form validation with visual feedback
- Event bus for cross-component communication

### State Patterns
- User preferences stored in localStorage
- Theme toggling with system-wide dark mode
- Form state reset patterns for dialogs
- Loading states for async operations

## Testing

### Frontend
- No test framework currently configured
- Test script returns success (placeholder)

### Backend
- JUnit tests in `src/test/`
- Controller tests for API endpoints
- Security configuration tests

## Configuration Files

### Frontend
- `quasar.config.ts` - Quasar framework configuration
- `eslint.config.js` - ESLint configuration with Vue and TypeScript
- `tsconfig.json` - TypeScript configuration
- `package.json` - Dependencies and scripts

### Backend
- `pom.xml` - Maven dependencies and build configuration
- `application.properties` - Spring Boot application settings
- `initialize.sql` - Database initialization script

## Common Development Tasks

### Adding New Components
1. Create component in `src/components/`
2. Import in layout or page where needed
3. Follow existing patterns for props, events, and styling

### Adding New Pages
1. Create page component in `src/pages/`
2. Add route in `src/router/routes.ts`
3. Update navigation in `MainLayout.vue` if needed

### Adding New API Endpoints
1. Create controller in backend module
2. Add service layer if needed
3. Update frontend API calls in components

### Database Changes
1. Update entity classes in backend
2. Modify `initialize.sql` for new tables/views
3. Update repository interfaces as needed

## Security Considerations

- JWT tokens handled securely in frontend
- CORS properly configured for development
- Input validation on both frontend and backend
- SQL injection prevention through JPA
- XSS prevention through Vue's template system