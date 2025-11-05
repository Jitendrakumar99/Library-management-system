# Email Notification Setup Guide

## 📧 Configure Email Notifications

Email notifications are sent when:
- A book is issued to a student
- A student's account is approved
- Book reminders and overdue alerts (future feature)

## 🔧 Setup Steps

### Step 1: Configure Email in `application.properties`

Open the file: `library-management-system/src/main/resources/application.properties`

Add these properties at the end of the file (uncomment and fill in your details):

```properties
# Email Configuration (SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password-here
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

### Step 2: For Gmail Users

#### Option A: Use Gmail App Password (Recommended)

1. **Enable 2-Step Verification:**
   - Go to: https://myaccount.google.com/security
   - Enable "2-Step Verification" if not already enabled

2. **Generate App Password:**
   - Go to: https://myaccount.google.com/apppasswords
   - Select "Mail" as the app
   - Select "Other (Custom name)" as device
   - Enter "Library Management System"
   - Click "Generate"
   - Copy the 16-character password (no spaces)

3. **Add to application.properties:**
   ```properties
   spring.mail.username=your-email@gmail.com
   spring.mail.password=xxxx xxxx xxxx xxxx  # Use the generated App Password
   ```

#### Option B: Use OAuth2 (Advanced)

For production, consider using OAuth2 instead of App Passwords.

### Step 3: For Other Email Providers

#### Outlook/Office 365:
```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=your-email@outlook.com
spring.mail.password=your-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

#### Yahoo Mail:
```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
spring.mail.username=your-email@yahoo.com
spring.mail.password=your-app-password  # Yahoo also requires App Password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

#### Custom SMTP Server:
```properties
spring.mail.host=your-smtp-server.com
spring.mail.port=587
spring.mail.username=your-email@domain.com
spring.mail.password=your-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Step 4: Enable Email Auto-Configuration

After adding email properties, **update** `LibraryManagementSystemApplication.java`:

Remove or comment out the email exclusion:

```java
@SpringBootApplication
// Remove this line: exclude = {MailSenderAutoConfiguration.class}
public class LibraryManagementSystemApplication {
    // ...
}
```

### Step 5: Restart the Application

After configuring email properties:
1. Stop the Spring Boot application (Ctrl+C)
2. Restart: `mvn spring-boot:run`

### Step 6: Test Email Notifications

1. Issue a book to a student (must have email in profile)
2. Check the student's email inbox
3. Check application logs for email status

## ✅ Verification

When email is configured correctly, you should see in logs:
- No "Email service not configured" messages
- Successful email sending messages (or email service exceptions if there are issues)

If email is NOT configured, the system will:
- Log email details to console instead of sending
- Continue working normally (emails just won't be sent)

## 🔒 Security Notes

1. **Never commit email passwords to Git:**
   - Use environment variables or secrets management in production
   - Add `application.properties` to `.gitignore` if it contains passwords

2. **For Production:**
   - Use environment variables:
     ```properties
     spring.mail.username=${MAIL_USERNAME}
     spring.mail.password=${MAIL_PASSWORD}
     ```
   - Or use Spring Cloud Config, AWS Secrets Manager, etc.

## 🐛 Troubleshooting

### Error: "Authentication failed"
- **Gmail:** Make sure you're using App Password, not regular password
- **Gmail:** Ensure 2-Step Verification is enabled
- Check username and password are correct

### Error: "Connection timeout"
- Check firewall settings
- Verify SMTP host and port are correct
- Some networks block SMTP ports

### Emails not sending but no errors
- Check spam/junk folder
- Verify recipient email address is correct
- Check application logs for detailed error messages

## 📝 Example Configuration

Here's a complete example for Gmail:

```properties
# Email Configuration (SMTP) - Gmail Example
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=library.system@gmail.com
spring.mail.password=abcd efgh ijkl mnop
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000
```

Replace `library.system@gmail.com` and `abcd efgh ijkl mnop` with your actual email and App Password.

