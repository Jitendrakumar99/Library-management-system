# Library Management System - Quick Start Guide

## Prerequisites Check

Ensure you have:
- ✅ Java 17+ installed
- ✅ Node.js 18+ installed  
- ✅ MySQL 8.0+ running
- ✅ Maven installed

## Step-by-Step Setup

### 1. Database Setup (5 minutes)

1. Open MySQL command line or MySQL Workbench
2. Create the database:
```sql
CREATE DATABASE librarydb1;
```

3. Verify your MySQL credentials in `library-management-system/src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

### 2. Backend Setup (10 minutes)

1. Open a terminal in the project root
2. Navigate to backend:
```bash
cd library-management-system
```

3. Clean and build (first time only):
```bash
mvn clean install
```

4. Run the Spring Boot application:
```bash
mvn spring-boot:run
```

5. Wait for "Started LibraryManagementSystemApplication" message
6. Backend is now running on: http://localhost:8086

**Default Admin Login:**
- Username: `admin`
- Password: `admin123`

### 3. Frontend Setup (5 minutes)

1. Open a NEW terminal (keep backend running)
2. Navigate to frontend:
```bash
cd frontend
```

3. Install dependencies (first time only):
```bash
npm install
```

4. Start the Angular dev server:
```bash
npm start
# or
ng serve
```

5. Frontend will open automatically on: http://localhost:4200

### 4. First Access (2 minutes)

1. Open browser to http://localhost:4200
2. You'll see the login page
3. Login as admin:
   - Username: `admin`
   - Password: `admin123`
4. You're now in the admin dashboard!

## Testing the System

### As Admin:
1. ✅ Add a book: Click "Add Book" button
2. ✅ Issue a book to a student
3. ✅ View all active issues
4. ✅ Return a book

### As Student:
1. Register a new account: Click "Register" on login page
2. Login with your new credentials
3. ✅ Browse available books
4. ✅ View your borrowed books
5. ✅ Check your fines

## Common Issues & Solutions

### Backend won't start
**Issue:** Port 8086 already in use
**Solution:** 
```bash
# Find and kill the process
netstat -ano | findstr :8086
taskkill /PID <pid> /F
```

### MySQL connection error
**Issue:** "Communications link failure"
**Solution:** 
1. Ensure MySQL service is running
2. Check credentials in application.properties
3. Verify database exists

### Frontend compilation errors
**Issue:** "Module not found"
**Solution:**
```bash
cd frontend
rm -rf node_modules
npm install
```

### CORS errors in browser console
**Issue:** API requests blocked
**Solution:** 
1. Verify backend is running on port 8086
2. Check application.properties has CORS config
3. Restart backend server

### JWT errors
**Issue:** "Invalid token" or authentication fails
**Solution:**
1. Clear browser localStorage
2. Login again
3. Check backend logs for JWT errors

## Production Deployment Notes

### Backend:
1. Change `spring.jpa.hibernate.ddl-auto=update` to `none`
2. Use proper database migrations (Flyway/Liquibase)
3. Generate strong JWT secret (minimum 32 characters)
4. Enable HTTPS
5. Configure proper CORS origins
6. Set strong admin password

### Frontend:
1. Update API URL from `localhost:8086` to production URL
2. Build for production:
```bash
cd frontend
ng build --configuration production
```
3. Deploy to web server (Nginx, Apache, etc.)

## Architecture Overview

```
Browser (localhost:4200)
        ↓
Angular App (Frontend)
        ↓ HTTP/HTTPS
Spring Boot REST API (localhost:8086)
        ↓ JPA/Hibernate
MySQL Database (localhost:3306)
        ↓
Tables: users, books, issues, categories, feedback
```

## API Testing

Test the backend directly using:
- Postman
- curl
- Browser DevTools Network tab

Example curl test:
```bash
# Login
curl -X POST http://localhost:8086/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Save the token from response, then:
curl -X GET http://localhost:8086/api/books \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Next Steps

### Recommended Enhancements:
1. Add book images
2. Implement email notifications for overdue books
3. Add advanced search filters
4. Create reports/dashboards
5. Add book reservations queue
6. Implement fine payment integration
7. Add audit logging
8. Create mobile responsive views

### Code Organization:
- All backend logic: `library-management-system/src/main/java`
- All frontend components: `frontend/src/app`
- Database config: `application.properties`
- API endpoints documented in main README.md

## Support

For issues or questions:
1. Check this document first
2. Review main README.md
3. Check backend logs: Console where Spring Boot is running
4. Check browser console: F12 → Console tab
5. Verify all services are running (MySQL, Backend, Frontend)

## Quick Commands Reference

```bash
# Backend
cd library-management-system
mvn spring-boot:run          # Run application
mvn clean install            # Build
mvn test                     # Run tests

# Frontend
cd frontend
npm start                    # Run dev server
npm run build                # Build for production
npm test                     # Run tests
ng generate component name   # Generate new component

# Database
mysql -u root -p             # Connect to MySQL
USE librarydb1;              # Switch to database
SHOW TABLES;                 # List all tables
```

---

**You're all set! Happy coding! 🎉**





