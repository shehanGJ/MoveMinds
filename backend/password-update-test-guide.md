# Password Update Test Guide

## 🔧 Issue Fixed

**Problem**: Frontend was sending `currentPassword` but backend expected `oldPassword`

**Solution**: Updated frontend API client to map field names correctly

## 🧪 Testing Steps

### 1. Start Both Services

**Backend:**
```bash
cd backend
mvn spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm run dev
```

### 2. Test Password Update

1. **Login to the application**:
   - Go to http://localhost:8080/login
   - Use valid credentials

2. **Navigate to Profile**:
   - Go to http://localhost:8080/profile
   - Click "Change Password"

3. **Fill out the form**:
   - Current Password: Your current password
   - New Password: Must be 8+ characters with uppercase, lowercase, and number
   - Confirm Password: Same as new password

4. **Submit and verify**:
   - Click "Update Password"
   - Should see success message
   - Try logging out and back in with new password

## 🔍 What Was Fixed

### Frontend API Client (`frontend/src/lib/api.ts`)
```typescript
// Before (causing 400 error)
updatePassword: (data: { currentPassword: string; newPassword: string }) =>
  api.patch('/user/password', data),

// After (fixed)
updatePassword: (data: { currentPassword: string; newPassword: string }) =>
  api.patch('/user/password', {
    oldPassword: data.currentPassword,  // ← Field name mapping
    newPassword: data.newPassword
  }),
```

### Frontend Validation (`frontend/src/pages/Profile.tsx`)
```typescript
// Enhanced password validation
newPassword: z.string()
  .min(8, "Password must be at least 8 characters")
  .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/, "Password must contain uppercase, lowercase, and number"),
```

### Error Handling
- Added better error message display
- Console logging for debugging
- Proper error response handling

## 🚨 Common Issues & Solutions

### "400 Bad Request"
- **Cause**: Field name mismatch (now fixed)
- **Solution**: Use the updated API client

### "401 Unauthorized"
- **Cause**: JWT token expired
- **Solution**: Login again to get fresh token

### "The old password is incorrect"
- **Cause**: Wrong current password entered
- **Solution**: Enter the correct current password

### "Password must contain uppercase, lowercase, and number"
- **Cause**: New password doesn't meet requirements
- **Solution**: Use a password like "NewTest123!"

## ✅ Expected Results

1. **Form Validation**: Real-time validation as you type
2. **API Call**: Successful PATCH request to `/user/password`
3. **Success Message**: "Password updated successfully"
4. **Login Test**: Can login with new password
5. **Error Handling**: Clear error messages for any issues

## 🔧 Backend Endpoint Details

**Endpoint**: `PATCH /user/password`
**Headers**: `Authorization: Bearer <jwt-token>`
**Body**:
```json
{
  "oldPassword": "current-password",
  "newPassword": "new-password"
}
```

**Response**:
- **Success**: `200 OK` with "Password changed successfully!"
- **Error**: `400 Bad Request` with error message
