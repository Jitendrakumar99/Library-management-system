# Library Management System

A full-stack Library Management System built with Angular and Spring Boot, featuring JWT authentication, role-based access control, and comprehensive book management capabilities.

## Features

### User Module
- ✅ User registration and authentication with JWT security
- ✅ Role-based access: Admin and User (Student) roles
- ✅ Admin can manage users
- ✅ Users can view and update their profiles
- ✅ Password encryption using BCrypt

### Book Module
- ✅ Admin can add, edit, delete, and view books
- ✅ Book categorization for easier navigation
- ✅ Users can browse and search available books
- ✅ Dynamic book availability tracking
- ✅ Book details: title, author, genre, quantity, available copies

### Category Module
- ✅ Admin manages book categories (CRUD operations)
- ✅ Users can view book categories
- ✅ Category-based book organization

### Book Issue Module
- ✅ Admin manages issued books (track borrowers and dates)
- ✅ Users can request to borrow or return books
- ✅ Book availability updates dynamically on issue/return
- ✅ Automatic fine calculation for overdue books

### Feedback Module
- ✅ Users can submit and manage feedback on books
- ✅ Admin can review all feedback
- ✅ Rating system (1-5 stars)

### Security
- ✅ JWT-based authentication for secure login and role validation
- ✅ Route protection based on user roles in Angular
- ✅ Password encryption using BCrypt
- ✅ CORS configuration for cross-origin requests

### UI Features
- ✅ Responsive dashboards for admin and users
- ✅ Interactive forms with validation
- ✅ Material Design UI
- ✅ Real-time book status updates
- ✅ Modern and intuitive user interface

## Technology Stack

### Backend
- **Spring Boot 3.5.5** - Java framework
- **Spring Security** - Authentication and authorization
- **JPA/Hibernate** - Database ORM
- **MySQL** - Database
- **JWT** - Token-based authentication
- **Maven** - Dependency management

### Frontend
- **Angular 20** - Frontend framework
- **Angular Material** - UI components
- **RxJS** - Reactive programming
- **TypeScript** - Programming language

## Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- MySQL 8.0 or higher
- Maven 3.6+
- npm or yarn

## Installation & Setup

### 1. Database Setup

Create a MySQL database:
```sql
CREATE DATABASE librarydb1;
```

Update database credentials in `library-management-system/src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=your_password
```

### 2. Backend Setup

Navigate to the backend directory:
```bash
cd library-management-system
```

Build the project:
```bash
mvn clean install
```

Run the application:
```bash
mvn spring-boot:run
```

The backend server will start on `http://localhost:8086`

**Default Admin Credentials:**
- Username: `admin`
- Password: `admin123`

### 3. Frontend Setup

Navigate to the frontend directory:
```bash
cd frontend
```

Install dependencies:
```bash
npm install
```

Run the development server:
```bash
npm start
# or
ng serve
```

The frontend will be available on `http://localhost:4200`

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/auth/validate` - Validate JWT token

### Books
- `GET /api/books` - Get all books
- `GET /api/books/{id}` - Get book by ID
- `POST /api/books` - Create book (Admin only)
- `PUT /api/books/{id}` - Update book (Admin only)
- `DELETE /api/books/{id}` - Delete book (Admin only)
- `GET /api/books/search` - Search books
- `GET /api/books/available` - Get available books

### Categories
- `GET /api/categories` - Get all categories
- `GET /api/categories/{id}` - Get category by ID
- `POST /api/categories` - Create category (Admin only)
- `PUT /api/categories/{id}` - Update category (Admin only)
- `DELETE /api/categories/{id}` - Delete category (Admin only)

### Issues
- `GET /api/issues` - Get all issues (Admin only)
- `GET /api/issues/{id}` - Get issue by ID
- `GET /api/issues/student/{studentId}` - Get issues by student
- `GET /api/issues/active` - Get active issues (Admin only)
- `GET /api/issues/returned` - Get returned issues (Admin only)
- `POST /api/issues` - Create issue (Admin only)
- `POST /api/issues/{id}/return` - Return a book (Admin only)

### Feedback
- `GET /api/feedback` - Get all feedback
- `GET /api/feedback/{id}` - Get feedback by ID
- `GET /api/feedback/book/{bookId}` - Get feedback by book
- `GET /api/feedback/user/{userId}` - Get feedback by user
- `POST /api/feedback` - Create feedback (Student/Admin)
- `PUT /api/feedback/{id}` - Update feedback (Student/Admin)
- `DELETE /api/feedback/{id}` - Delete feedback (Admin only)

### Users
- `GET /api/users` - Get all users (Admin only)
- `GET /api/users/students` - Get all students (Admin only)
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (Admin only)

## Project Structure

```
├── frontend/                          # Angular frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/           # UI components
│   │   │   │   ├── login/
│   │   │   │   ├── register/
│   │   │   │   ├── admin/
│   │   │   │   │   └── admin-dashboard/
│   │   │   │   └── student/
│   │   │   │       └── student-dashboard/
│   │   │   ├── guards/              # Route guards
│   │   │   ├── interceptors/        # HTTP interceptors
│   │   │   ├── models/              # TypeScript models
│   │   │   └── services/            # API services
│   │   └── styles.css
│   └── package.json
│
├── library-management-system/         # Spring Boot backend
│   ├── src/main/
│   │   ├── java/
│   │   │   └── com/example/demo/
│   │   │       ├── config/           # Configuration
│   │   │       ├── controller/       # REST controllers
│   │   │       ├── dto/             # Data Transfer Objects
│   │   │       ├── filter/          # JWT filter
│   │   │       ├── model/           # Entity models
│   │   │       ├── repository/      # JPA repositories
│   │   │       ├── service/         # Business logic
│   │   │       └── util/            # JWT utilities
│   │   └── resources/
│   │       └── application.properties
│   └── pom.xml
│
└── README.md
```

## Usage

### Admin Features
1. Login with admin credentials
2. Manage books: Add, edit, delete books
3. Manage categories: Create and organize book categories
4. Issue books: Assign books to students
5. Return books: Mark books as returned
6. View all issues: Monitor all book transactions
7. View feedback: Review student feedback

### Student Features
1. Register for a new account or login
2. Browse available books
3. View borrowed books
4. Check fines
5. Submit feedback on books
6. View personal borrowing history

## Development Notes

### JWT Configuration
JWT secret is configured in `application.properties`. In production, use a strong, randomly generated secret key.

### CORS
Currently configured to allow requests from `http://localhost:4200`. Update for production deployments.

### Database
The application uses Hibernate auto-update (`ddl-auto=update`). In production, use migration tools like Flyway or Liquibase.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For issues and questions, please open an issue on the GitHub repository.





