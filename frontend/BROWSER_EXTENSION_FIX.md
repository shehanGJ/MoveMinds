# Browser Extension Error Fix

## Problem
When clicking "Pay with PayHere", you may see these errors in the browser console:
```
Error: Invalid frameId for foreground frameId: 0
Unchecked runtime.lastError: No window with id: 2128781357.
```

## Root Cause
These errors are caused by browser extensions (likely ad blockers, password managers, or other extensions) trying to access popup windows that no longer exist. This is NOT related to the PayHere payment integration.

## Solutions

### Solution 1: Test in Incognito Mode (Recommended)
1. Open Chrome/Edge in **Incognito/Private mode**
2. Navigate to your application
3. Login and test the payment flow
4. Extensions are disabled in incognito mode by default

### Solution 2: Disable Extensions Temporarily
1. Go to `chrome://extensions/` (or `edge://extensions/`)
2. Disable all extensions temporarily
3. Test the payment flow
4. Re-enable extensions after testing

### Solution 3: Use a Different Browser
1. Test in Firefox, Safari, or another browser
2. This will eliminate extension conflicts

### Solution 4: Filter Console Errors
1. In Chrome DevTools Console
2. Click the filter icon
3. Uncheck "Errors" to hide extension errors
4. This won't fix the issue but will clean up the console

## Verification Steps

### Test PayHere Payment Flow:
1. **Login** as a user in the frontend
2. **Navigate** to Programs page
3. **Click "Enroll Now"** on any program
4. **Fill payment form** with:
   - Full Name: Test User
   - Email: test@example.com
   - Phone: 0771234567
   - Address: 123 Test Street
   - City: Colombo
   - Country: Sri Lanka
5. **Click "Pay with PayHere"**
6. **Expected Result**: Redirect to PayHere payment page

### PayHere Test Cards:
- **Visa**: 4916217501611292 (Exp: 12/25, CVV: 123)
- **Mastercard**: 5123456789012346 (Exp: 12/25, CVV: 123)

## Troubleshooting

### If Payment Still Fails:
1. Check browser console for actual PayHere errors (not extension errors)
2. Verify backend is running on port 8081
3. Check network tab for failed API calls
4. Ensure user is properly authenticated

### Common Issues:
- **401 Unauthorized**: User not logged in
- **403 Forbidden**: Security configuration issue
- **500 Internal Server Error**: Backend application issue
- **CORS Error**: Frontend-backend communication issue

## Note
The browser extension errors are harmless and don't affect the payment functionality. Focus on testing the actual payment flow rather than these console errors.
