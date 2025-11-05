# Library Management System - Project Overview

## 1. Project Description
The **Library Management System** is a comprehensive full-stack web application built using **Spring Boot** (backend) and **Angular** (frontend). This system provides a complete solution for managing library operations including book management, user management, book issuing/returning, activity logging, feedback system, and contact form management. The application features a modern, responsive user interface with role-based access control for administrators and students.

---

## 2. Technology Stack

### Backend Technologies
- **Spring Boot 3.5.5** - Main framework for building the RESTful API backend
- **Java 17** - Programming language
- **Spring Data JPA** - For database operations and ORM
- **Hibernate** - ORM implementation
- **MySQL Database** - Relational database for data persistence
- **Spring Security** - For authentication and authorization
- **JWT (JSON Web Tokens)** - For stateless authentication (jjwt 0.12.3)
- **Spring Mail** - For email notifications
- **Maven** - Dependency management and build automation

### Frontend Technologies
- **Angular 20** - Modern frontend framework (separate application)
- **Angular Material** - UI component library for professional design
- **TypeScript** - Programming language for Angular
- **RxJS** - Reactive programming for API calls
- **HTML5/CSS3** - Modern styling with responsive design
- **SCSS** - Enhanced CSS with theming support

---

## 3. Project Architecture

The project follows **Microservices-style architecture** with complete separation of frontend and backend:

### Backend Architecture (Spring Boot)
- **Layered Architecture** (Controller → Service → Repository → Database)
- **RESTful API** design for all endpoints
- **JWT-based stateless authentication**

### Frontend Architecture (Angular)
- **Component-based architecture**
- **Service layer** for API communication
- **Route guards** for authentication and authorization
- **Reactive forms** with validation
- **Responsive design** for all screen sizes

---

## 4. Project Structure

### Backend Structure
```
library-management-system/
├── src/main/java/com/example/demo/
│   ├── config/              - Configuration classes (Security, CORS)
│   ├── controller/          - REST API controllers
│   ├── dto/                 - Data Transfer Objects
│   ├── filter/              - JWT authentication filter
│   ├── model/               - JPA Entity classes
│   ├── repository/          - Data access layer (JPA repositories)
│   ├── service/             - Business logic layer
│   └── util/                - Utility classes (JWT utilities)
└── src/main/resources/
    └── application.properties - Configuration file
```

### Frontend Structure
```
frontend/
├── src/app/
│   ├── components/          - Angular components
│   │   ├── home/           - Landing page
│   │   ├── about/          - About Us page
│   │   ├── contact/        - Contact form page
│   │   ├── login/          - Login component
│   │   ├── register/       - Registration component
│   │   ├── navbar/         - Navigation bar
│   │   ├── admin/          - Admin dashboard components
│   │   ├── student/        - Student dashboard components
│   │   └── shared/         - Shared components (sidebar)
│   ├── services/           - API service layer
│   ├── guards/             - Route guards
│   ├── interceptors/       - HTTP interceptors (JWT)
│   ├── models/             - TypeScript models/interfaces
│   └── app.routes.ts       - Application routing
└── src/styles.css          - Global styles
```

---

## 5. Main Packages and Components

### Backend Packages

#### **config/** Package
- **SecurityConfig.java** - Configures Spring Security, JWT authentication, CORS policies, and access control rules for all endpoints

#### **model/** Package (Entity Classes)
- **User.java** - User entity with roles (ADMIN/STUDENT), status (PENDING/APPROVED/REJECTED), email, roll number
- **Book.java** - Book entity with title, author, genre, quantity, availability, category, and image URL
- **Issue.java** - Book issue/return tracking with dates, fine calculation, and status
- **Category.java** - Book category classification
- **ActivityLog.java** - System activity tracking and audit logs
- **Feedback.java** - User feedback and ratings for books
- **Contact.java** - Contact form submissions from users

#### **repository/** Package (Data Access Layer)
- **UserRepository.java** - User data operations
- **BookRepository.java** - Book data operations
- **IssueRepository.java** - Issue/return data operations
- **CategoryRepository.java** - Category data operations
- **ActivityLogRepository.java** - Activity log data operations
- **FeedbackRepository.java** - Feedback data operations
- **ContactRepository.java** - Contact message data operations

#### **service/** Package (Business Logic Layer)
- **UserService.java** - User management logic (registration, approval, statistics)
- **BookService.java** - Book management logic (CRUD operations)
- **IssueService.java** - Book issuing and returning logic with fine calculation
- **CategoryService.java** - Category management logic
- **ActivityLogService.java** - Activity logging and tracking
- **FeedbackService.java** - Feedback management
- **EmailService.java** - Email notification functionality
- **ContactService.java** - Contact message management
- **ReportService.java** - Statistics and reporting functionality
- **CustomUserDetailsService.java** - Spring Security user details service

#### **controller/** Package (REST API Endpoints)
All controllers are REST API endpoints consumed by Angular frontend:
- **AuthController.java** - Authentication APIs (`/api/auth/**`)
  - `POST /api/auth/login` - User login
  - `POST /api/auth/register` - User registration
  
- **BookRestController.java** - Book management APIs (`/api/books/**`)
  - Full CRUD operations for books
  
- **UserRestController.java** - User management APIs (`/api/user/**`, `/api/users/**`)
  - User profile, statistics, approval management
  
- **IssueRestController.java** - Issue management APIs (`/api/issue/**`, `/api/issues/**`)
  - Book request, issue, return operations
  
- **CategoryRestController.java** - Category APIs (`/api/categories/**`)
  - Category CRUD operations
  
- **ActivityLogRestController.java** - Activity log APIs (`/api/activity-logs/**`)
  - System activity tracking
  
- **FeedbackRestController.java** - Feedback APIs (`/api/feedback/**`)
  - Feedback submission and retrieval
  
- **ReportsRestController.java** - Reporting APIs (`/api/reports/**`)
  - Statistics, analytics, and reports
  
- **ContactRestController.java** - Contact form APIs (`/api/contact/**`)
  - Contact form submission and retrieval (admin only)

#### **dto/** Package (Data Transfer Objects)
- **AuthRequest.java** - Login request DTO
- **AuthResponse.java** - Login response with JWT token
- **RegisterRequest.java** - User registration DTO
- **ContactRequest.java** - Contact form submission DTO
- **ApiResponse.java** - Standard API response wrapper
- **UserStatistics.java** - User statistics DTO

---

## 6. Frontend Components

### Public Pages
1. **Home Component** (`/`)
   - Landing page with hero section
   - Features showcase
   - About section
   - Call-to-action buttons

2. **About Us Component** (`/about`)
   - Mission and vision statements
   - Features grid
   - Team information
   - Statistics display

3. **Contact Component** (`/contact`)
   - Contact form with validation
   - Contact information display
   - FAQ section
   - Email sending functionality

4. **Login Component** (`/login`)
   - User authentication
   - JWT token management
   - Form validation

5. **Register Component** (`/register`)
   - New user registration
   - Form validation
   - Success/error messages

### Admin Dashboard (`/admin`)
**Sidebar Navigation:**
- Dashboard (Overview with statistics)
- Books Management
- Users Management
- Book Requests (with badge count)
- User Approval (with badge count)
- Issue Book
- Activity Logs
- Reports & Analytics
- Contact Messages (with badge count)

**Features:**
- View system statistics (total books, users, active issues, overdue books, fines)
- Manage books (add, edit, delete, seed sample data)
- Manage users (view all users with statistics, approve/reject, delete)
- Approve/reject book requests
- Approve/reject user registrations
- Issue books directly to students
- View complete activity logs
- View reports (most issued books, top readers, overdue books, monthly activity)
- View and manage contact form submissions
- Badge notifications for pending requests and messages

### Student Dashboard (`/student`)
**Sidebar Navigation:**
- Dashboard (Overview with summary)
- My Books (Active issued books)
- Available Books (Browse and request)
- Request History (All requests with status)

**Features:**
- View personal statistics (active issues, returned books, total fines)
- View currently issued books
- Browse available books
- Request books from library
- View complete request history
- See fine details for overdue books

### Shared Components
- **Navbar Component** - Fixed navigation bar with mobile menu
- **Sidebar Component** - Responsive sidebar navigation for dashboards

---

## 7. Key Features

### Authentication & Authorization
- ✅ **JWT-based Authentication** - Stateless authentication for API endpoints
- ✅ **Role-based Access Control** - Admin and Student roles with different permissions
- ✅ **User Registration** - Students can register with admin approval workflow
- ✅ **Password Encryption** - BCrypt password hashing
- ✅ **Route Guards** - Angular guards protect routes based on authentication and roles

### Book Management
- ✅ Add, update, delete books
- ✅ Book categorization
- ✅ Track book quantity and availability
- ✅ Book image support
- ✅ Search and filter books
- ✅ Seed sample books functionality

### Issue & Return Management
- ✅ Request books (with approval workflow)
- ✅ Issue books to students
- ✅ Return book functionality
- ✅ Automatic fine calculation for late returns (₹10 per day)
- ✅ Track issue/return history
- ✅ Status tracking (PENDING, APPROVED, REJECTED, RETURNED)

### User Management
- ✅ Student registration with approval system
- ✅ Admin approval/rejection of student accounts
- ✅ User profile management
- ✅ User statistics (total issued, active, returned, fines)
- ✅ Email notifications for account status changes
- ✅ User deletion with validation

### Activity Logging
- ✅ Comprehensive activity tracking
- ✅ Logs all major operations (book issued, returned, added, deleted, user approved, etc.)
- ✅ Audit trail for system activities
- ✅ User and book association in logs

### Feedback System
- ✅ Students can provide feedback on books
- ✅ Rating system (1-5 stars)
- ✅ Comment functionality
- ✅ View feedback by book or user

### Contact Form System
- ✅ Public contact form for inquiries
- ✅ Form validation
- ✅ Email notifications to admin
- ✅ Confirmation emails to users
- ✅ Database storage of all submissions
- ✅ Admin can view all contact messages
- ✅ Admin can reply via email
- ✅ Admin can delete messages

### Email Notifications
- ✅ Email notifications for user account approval/rejection
- ✅ Email notifications for book issue confirmations
- ✅ Email reminders for due dates
- ✅ Overdue book alerts
- ✅ Contact form submission notifications
- ✅ SMTP configuration support (Gmail, Outlook, Yahoo)

### Reporting & Analytics
- ✅ System statistics dashboard
- ✅ User statistics with detailed metrics
- ✅ Most issued books report
- ✅ Top readers report
- ✅ Monthly activity tracking
- ✅ Overdue books report
- ✅ Fine tracking and reporting

### UI/UX Features
- ✅ **Responsive Design** - Works on desktop, tablet, and mobile devices
- ✅ **Modern Material Design** - Professional UI with Angular Material
- ✅ **Fixed Navigation Bar** - Always visible at top
- ✅ **Sidebar Navigation** - Collapsible sidebar for dashboards
- ✅ **Mobile Menu** - Hamburger menu for mobile devices
- ✅ **Smooth Animations** - Fade-in transitions and hover effects
- ✅ **Empty States** - Helpful messages when no data
- ✅ **Loading States** - Visual feedback during API calls
- ✅ **Toast Notifications** - Success/error messages
- ✅ **Form Validation** - Real-time validation feedback
- ✅ **Badge Counts** - Visual indicators for pending items

---

## 8. Database Schema

### Main Tables
1. **users** - User accounts (Admin/Student)
   - Fields: id, username, password, email, rollNumber, role, status, createdAt
   
2. **books** - Book information
   - Fields: id, title, author, genre, quantity, available, category_id, imageUrl
   
3. **categories** - Book categories
   - Fields: id, name, description
   
4. **issues** - Book issue/return records
   - Fields: id, student_id, book_id, issueDate, returnDate, fine, status, returned
   
5. **activity_logs** - System activity audit trail
   - Fields: id, actionType, description, user_id, book_id, createdAt
   
6. **feedback** - User feedback and ratings
   - Fields: id, user_id, book_id, rating, comment, createdAt
   
7. **contacts** - Contact form submissions
   - Fields: id, name, email, subject, message, createdAt

### Relationships
- **User** ↔ **Issue** (One-to-Many) - One user can have multiple issues
- **Book** ↔ **Issue** (One-to-Many) - One book can be issued multiple times
- **Category** ↔ **Book** (One-to-Many) - One category can have multiple books
- **User** ↔ **ActivityLog** (One-to-Many) - User activity tracking
- **Book** ↔ **ActivityLog** (One-to-Many) - Book activity tracking
- **User** ↔ **Feedback** (One-to-Many) - User can give multiple feedbacks
- **Book** ↔ **Feedback** (One-to-Many) - Book can have multiple feedbacks

---

## 9. Security Configuration

### Authentication Flow
1. User logs in via `/api/auth/login`
2. System validates credentials and generates JWT token
3. JWT token is stored in localStorage (Angular)
4. Token is included in subsequent API requests (Authorization: Bearer token)
5. `JwtAuthenticationFilter` validates token on each request
6. User role is extracted from token for authorization

### Access Control
- **Public Endpoints**: 
  - `/api/auth/**` - Authentication endpoints
  - `/api/contact/**` - Contact form endpoints
  - `/api/categories` - View categories (public)
  
- **Admin Only**: 
  - `/api/admin/**` - Admin-specific operations
  - `/api/users/**` - User management
  - `/api/contact/all` - View all contact messages
  - `/api/reports/**` - Reports and analytics
  
- **Authenticated Users**: 
  - `/api/user/**` - User profile operations
  - `/api/student/**` - Student operations
  - `/api/issue/**` - Issue operations
  - `/api/feedback/**` - Feedback operations
  
- **CORS Enabled**: Configured for `http://localhost:4200` (Angular frontend)

---

## 10. API Endpoints Summary

### Authentication APIs
- `POST /api/auth/login` - User login (returns JWT token)
- `POST /api/auth/register` - User registration

### Book APIs
- `GET /api/books` - Get all books
- `GET /api/books/{id}` - Get book by ID
- `GET /api/books/available` - Get available books
- `POST /api/books` - Add new book (Admin)
- `PUT /api/books/{id}` - Update book (Admin)
- `DELETE /api/books/{id}` - Delete book (Admin)
- `POST /api/books/seed` - Seed sample books (Admin)

### Issue APIs
- `GET /api/issues` - Get all issues (Admin)
- `GET /api/issues/active` - Get active issues (Admin)
- `GET /api/issues/student/{studentId}` - Get issues by student
- `GET /api/issues/pending` - Get pending requests (Admin)
- `POST /api/issues/request` - Request a book (Student)
- `POST /api/issues/{id}/approve` - Approve request (Admin)
- `POST /api/issues/{id}/reject` - Reject request (Admin)
- `POST /api/issues/{id}/return` - Return a book

### User APIs
- `GET /api/users` - Get all users (Admin)
- `GET /api/users/students` - Get all students (Admin)
- `GET /api/users/pending` - Get pending users (Admin)
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/{id}/statistics` - Get user statistics
- `GET /api/users/statistics` - Get all users with statistics (Admin)
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (Admin)
- `POST /api/users/{id}/approve` - Approve user (Admin)
- `POST /api/users/{id}/reject` - Reject user (Admin)

### Category APIs
- `GET /api/categories` - Get all categories
- `GET /api/categories/{id}` - Get category by ID
- `POST /api/categories` - Add category (Admin)
- `PUT /api/categories/{id}` - Update category (Admin)
- `DELETE /api/categories/{id}` - Delete category (Admin)

### Activity Log APIs
- `GET /api/activity-logs` - Get all activity logs (Admin)

### Feedback APIs
- `GET /api/feedback` - Get all feedback
- `GET /api/feedback/book/{bookId}` - Get feedback by book
- `GET /api/feedback/user/{userId}` - Get feedback by user
- `POST /api/feedback` - Submit feedback (Student/Admin)

### Reports APIs
- `GET /api/reports/statistics` - Get system statistics (Admin)
- `GET /api/reports/most-issued` - Get most issued books (Admin)
- `GET /api/reports/top-readers` - Get top readers (Admin)
- `GET /api/reports/monthly-activity` - Get monthly activity (Admin)
- `GET /api/reports/overdue` - Get overdue books (Admin)

### Contact APIs
- `POST /api/contact/send` - Submit contact form (Public)
- `GET /api/contact/all` - Get all contact messages (Admin)
- `DELETE /api/contact/{id}` - Delete contact message (Admin)

---

## 11. Frontend Pages and Routes

### Public Routes
- `/` - Home/Landing page
- `/about` - About Us page
- `/contact` - Contact form page
- `/login` - Login page
- `/register` - Registration page

### Protected Routes (Require Authentication)
- `/admin` - Admin dashboard (Admin only)
  - Query parameters: `?view=dashboard`, `?view=books`, `?view=users`, etc.
- `/student` - Student dashboard (Student only)
  - Query parameters: `?view=dashboard`, `?view=my-books`, `?view=available-books`, `?view=request-history`

### Route Guards
- **authGuard** - Requires authentication
- **adminGuard** - Requires admin role
- **studentGuard** - Requires student role

---

## 12. Responsive Design

### Breakpoints
- **Desktop**: > 1024px - Full layout with sidebar
- **Tablet**: 768px - 1024px - Collapsed sidebar, adjusted layouts
- **Mobile**: 480px - 768px - Mobile menu, single column layouts
- **Small Mobile**: < 480px - Compact design, icon-only navigation

### Responsive Features
- ✅ Fixed navbar that stays at top
- ✅ Collapsible sidebar (icon-only on mobile)
- ✅ Mobile hamburger menu
- ✅ Responsive tables with horizontal scroll
- ✅ Flexible grid layouts
- ✅ Touch-friendly buttons and links
- ✅ Optimized font sizes and spacing

---

## 13. Configuration Files

### application.properties
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/librarydb
spring.datasource.username=root
spring.datasource.password=your_password

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Server Port
server.port=8086

# JWT Configuration
jwt.secret=YourSuperSecretKeyThatShouldBeAtLeast32CharactersLongForHS256Algorithm
jwt.expiration=86400000

# Email Configuration (Optional)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Default Admin Credentials
- Username: `admin`
- Password: `admin123`

---

## 14. Key Technical Highlights

1. **Full-Stack Separation** - Complete separation of frontend (Angular) and backend (Spring Boot)
2. **Stateless Authentication** - JWT-based authentication for scalable API access
3. **RESTful API Design** - Clean REST API endpoints following best practices
4. **Role-based Security** - Fine-grained access control with Spring Security
5. **Activity Audit Trail** - Complete logging of all system activities
6. **Email Integration** - Automated email notifications for important events
7. **Fine Calculation** - Automatic fine calculation for late book returns (₹10/day)
8. **Approval Workflow** - User registration and book request approval system
9. **Responsive Design** - Mobile-first responsive design for all devices
10. **Modern UI/UX** - Material Design components with smooth animations
11. **Contact Management** - Complete contact form system with email notifications
12. **Comprehensive Reporting** - Statistics and analytics for administrators
13. **CORS Support** - Configured for frontend-backend communication
14. **Form Validation** - Client-side and server-side validation
15. **Error Handling** - Proper error handling with user-friendly messages

---

## 15. Development Tools & Setup

### Prerequisites
- **Java 17+** - Required for Spring Boot
- **Node.js 18+** - Required for Angular
- **MySQL 8.0+** - Database server
- **Maven 3.6+** - Build tool
- **npm or yarn** - Package manager for frontend

### IDE Support
- Compatible with IntelliJ IDEA, Eclipse, VS Code
- Angular Language Service for VS Code

### Ports
- **Backend**: 8086 (configurable in application.properties)
- **Frontend**: 4200 (default Angular dev server)

---

## 16. Project Structure Summary

```
Library Management System/
├── library-management-system/          # Spring Boot Backend
│   ├── src/main/java/com/example/demo/
│   │   ├── config/                    # Configuration
│   │   ├── controller/                # REST Controllers
│   │   ├── dto/                       # Data Transfer Objects
│   │   ├── filter/                    # JWT Filter
│   │   ├── model/                     # JPA Entities
│   │   ├── repository/                # Data Access Layer
│   │   ├── service/                   # Business Logic
│   │   └── util/                      # Utilities
│   ├── src/main/resources/
│   │   └── application.properties     # Configuration
│   └── pom.xml                        # Maven Dependencies
│
├── frontend/                           # Angular Frontend
│   ├── src/app/
│   │   ├── components/                # Angular Components
│   │   │   ├── home/                 # Landing page
│   │   │   ├── about/                # About Us
│   │   │   ├── contact/              # Contact form
│   │   │   ├── login/                # Login
│   │   │   ├── register/             # Registration
│   │   │   ├── navbar/               # Navigation bar
│   │   │   ├── admin/                # Admin dashboard
│   │   │   ├── student/              # Student dashboard
│   │   │   └── shared/               # Shared components
│   │   ├── services/                  # API Services
│   │   ├── guards/                    # Route Guards
│   │   ├── interceptors/              # HTTP Interceptors
│   │   ├── models/                    # TypeScript Models
│   │   └── app.routes.ts              # Routing
│   ├── src/styles.css                 # Global Styles
│   └── package.json                   # Dependencies
│
└── PROJECT_OVERVIEW.md                 # This file
```

---

## 17. Features Summary

### Admin Features
✅ Complete book management (CRUD operations)
✅ User management with approval system
✅ Book request approval/rejection
✅ Issue books directly to students
✅ View all active issues and returns
✅ Activity log monitoring
✅ Comprehensive reports and analytics
✅ Contact message management
✅ System statistics dashboard
✅ Email notifications management

### Student Features
✅ Browse available books
✅ Request books from library
✅ View personal book history
✅ Track active issues and fines
✅ View request status
✅ Submit feedback on books
✅ View personal statistics

### Public Features
✅ User registration
✅ Home page with information
✅ About Us page
✅ Contact form submission
✅ Responsive navigation

---

## 18. Technologies and Skills Demonstrated

This project demonstrates proficiency in:

### Backend
- ✅ Spring Boot framework and RESTful API development
- ✅ Spring Security and JWT authentication
- ✅ Spring Data JPA and Hibernate ORM
- ✅ MySQL database design and management
- ✅ Email service integration
- ✅ Layered architecture patterns
- ✅ Exception handling and validation

### Frontend
- ✅ Angular framework and component architecture
- ✅ TypeScript programming
- ✅ Angular Material UI components
- ✅ Reactive forms and validation
- ✅ HTTP client and RxJS observables
- ✅ Route guards and navigation
- ✅ Responsive web design
- ✅ CSS/SCSS styling and animations

### Full-Stack
- ✅ RESTful API integration
- ✅ JWT token management
- ✅ CORS configuration
- ✅ Error handling across layers
- ✅ State management
- ✅ Authentication flow

---

## 19. How to Run the Project

### Backend Setup
1. Ensure MySQL is running and create database `librarydb`
2. Update database credentials in `application.properties`
3. Navigate to `library-management-system/` directory
4. Run: `mvn spring-boot:run`
5. Backend starts on `http://localhost:8086`

### Frontend Setup
1. Navigate to `frontend/` directory
2. Install dependencies: `npm install`
3. Run: `ng serve` or `npm start`
4. Frontend starts on `http://localhost:4200`

### Access the Application
1. Open browser to `http://localhost:4200`
2. View home page, about, or contact pages
3. Register as new student or login as admin
4. Admin credentials: `admin` / `admin123`

---

## 20. Recent Updates and Improvements

### UI/UX Enhancements
- ✅ Modern Material Design implementation
- ✅ Fully responsive design for all devices
- ✅ Fixed navigation bar with mobile menu
- ✅ Sidebar navigation for dashboards
- ✅ Smooth animations and transitions
- ✅ Professional color scheme and styling

### New Features
- ✅ Contact form system with database storage
- ✅ About Us page with comprehensive information
- ✅ Home landing page with hero section
- ✅ Contact message management for admins
- ✅ Badge notifications for pending items
- ✅ Dynamic page titles based on current view

### Technical Improvements
- ✅ Removed Thymeleaf (pure REST API backend)
- ✅ Complete Angular frontend implementation
- ✅ Query parameter-based navigation
- ✅ Optimized responsive breakpoints
- ✅ Enhanced error handling and user feedback

---

## 21. Project Highlights for Presentation

### Architecture
- **Clean Architecture** - Separation of concerns with layered structure
- **RESTful API** - Standard REST endpoints for all operations
- **Microservices-ready** - Frontend and backend completely decoupled

### Security
- **JWT Authentication** - Stateless, scalable authentication
- **Role-based Access Control** - Fine-grained permissions
- **Password Encryption** - BCrypt hashing
- **CORS Protection** - Configured for secure cross-origin requests

### User Experience
- **Responsive Design** - Works seamlessly on all devices
- **Modern UI** - Professional Material Design interface
- **Intuitive Navigation** - Easy-to-use sidebar and navbar
- **Real-time Feedback** - Toast notifications and loading states

### Data Management
- **Comprehensive Logging** - Complete audit trail
- **Statistics & Reports** - Detailed analytics
- **Email Notifications** - Automated communications
- **Fine Calculation** - Automatic overdue fine tracking

---

This project represents a complete, production-ready library management system with modern technologies, best practices, and comprehensive features for both administrators and students.
