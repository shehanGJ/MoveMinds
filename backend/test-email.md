# Email Service Test Guide

## 🧪 Testing Steps

### 1. Set Up Your Email Credentials

Create a `.env` file in the `backend` directory:

```bash
# For Gmail
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_FROM=noreply@moveminds.com
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
APP_EMAIL_ENABLED=true
```

### 2. Start the Backend

```bash
cd backend
mvn spring-boot:run
```

### 3. Test Signup (Sends Email)

```bash
curl -X POST http://localhost:8081/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "your-test-email@gmail.com",
    "password": "Test123!",
    "firstName": "Test",
    "lastName": "User",
    "cityId": 1
  }'
```

### 4. Check Logs

```bash
tail -f backend/logs/moveminds.log | grep -i email
```

### 5. Expected Log Messages

✅ **Success Messages:**
- "Sending an email to activate your account"
- "Email sent successfully to: your-test-email@gmail.com"

❌ **Error Messages:**
- "Email sending disabled - skipping activation email"
- "Error sending email: Authentication failed"
- "Error sending email: Connection timeout"

## 🔧 Troubleshooting

### Gmail Setup:
1. Enable 2-Factor Authentication
2. Generate App Password: Google Account → Security → 2-Step Verification → App passwords
3. Use App Password (not regular password)

### Common Issues:
- **Authentication Failed**: Wrong username/password
- **Connection Timeout**: Check internet/firewall
- **SSL Issues**: Verify SSL settings
- **Email Not Sending**: Check `app.email.enabled=true`

## 📧 Email Template Preview

The new email template includes:
- Professional MoveMinds branding
- Clear activation button
- Responsive design
- Security notice
- Professional footer
