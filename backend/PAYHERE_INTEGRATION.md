# PayHere Payment Gateway Integration

## Overview
This document describes the PayHere payment gateway integration for the MoveMinds fitness application.

## Configuration

### Environment Variables
The following environment variables can be set to configure PayHere:

```properties
# PayHere Configuration
PAYHERE_MERCHANT_ID=1232127
PAYHERE_MERCHANT_SECRET=123456789
PAYHERE_SANDBOX_URL=https://sandbox.payhere.lk/pay/checkout
PAYHERE_LIVE_URL=https://www.payhere.lk/pay/checkout
PAYHERE_RETURN_URL=http://localhost:5173/payment/success
PAYHERE_CANCEL_URL=http://localhost:5173/payment/cancel
PAYHERE_NOTIFY_URL=http://localhost:8081/api/payment/payhere/notify
PAYHERE_SANDBOX_MODE=true
```

### Default Configuration
- **Merchant ID**: 1232127 (PayHere Sandbox)
- **Sandbox Mode**: Enabled by default
- **Currency**: LKR (Sri Lankan Rupees)

## API Endpoints

### 1. Create Payment Request
**POST** `/api/payment/payhere/create`

Creates a PayHere payment request and returns payment form data.

**Request Body:**
```json
{
  "programId": 1,
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "0771234567",
  "customerAddress": "123 Main St",
  "customerCity": "Colombo",
  "customerCountry": "Sri Lanka",
  "amount": 1000.00,
  "currency": "LKR",
  "orderId": "MM_1234567890_1",
  "itemName": "Fitness Program",
  "itemDescription": "Complete fitness program"
}
```

**Response:**
```json
{
  "status": "success",
  "message": "Payment request created successfully",
  "paymentUrl": "https://sandbox.payhere.lk/pay/checkout",
  "orderId": "MM_1234567890_1",
  "merchantId": "1232127",
  "merchantSecret": "123456789",
  "amount": "1000.00",
  "currency": "LKR",
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "0771234567",
  "customerAddress": "123 Main St",
  "customerCity": "Colombo",
  "customerCountry": "Sri Lanka",
  "itemName": "Fitness Program",
  "itemDescription": "Complete fitness program",
  "returnUrl": "http://localhost:5173/payment/success",
  "cancelUrl": "http://localhost:5173/payment/cancel",
  "notifyUrl": "http://localhost:8081/api/payment/payhere/notify",
  "hash": "ABC123DEF456..."
}
```

### 2. Payment Notification Handler
**POST** `/api/payment/payhere/notify`

Handles PayHere payment notifications (webhook).

**Request Body:**
```json
{
  "merchant_id": "1232127",
  "order_id": "MM_1234567890_1",
  "payment_id": "1234567890",
  "payhere_amount": "1000.00",
  "payhere_currency": "LKR",
  "status_code": "2",
  "md5sig": "ABC123DEF456...",
  "method": "VISA",
  "status_message": "Successfully completed the payment",
  "card_holder_name": "John Doe",
  "card_no": "************1234",
  "card_expiry": "12/25"
}
```

### 3. Complete Payment
**POST** `/api/payment/payhere/complete`

Completes the payment process and enrolls the user in the program.

**Parameters:**
- `orderId`: The order ID from PayHere
- `programId`: The program ID to enroll in

## Frontend Integration

### Payment Modal
The payment modal (`PaymentModal.tsx`) handles the payment flow:

1. Collects customer information
2. Creates payment request via API
3. Redirects to PayHere payment page
4. Handles success/cancel redirects

### Payment Pages
- **Success Page**: `/payment/success` - Handles successful payments
- **Cancel Page**: `/payment/cancel` - Handles cancelled payments

## Security Features

### Hash Verification
All PayHere notifications are verified using MD5 hash:
```
hash = MD5(merchant_id + order_id + amount + currency + merchant_secret)
```

### Authentication
- Payment creation requires user authentication
- Only authenticated users can complete payments
- User enrollment is verified before payment processing

## Testing

### Sandbox Testing
Use the following test card details for sandbox testing:

**Visa Test Card:**
- Card Number: 4916217501611292
- Expiry: 12/25
- CVV: 123
- Name: Test User

**Mastercard Test Card:**
- Card Number: 5123456789012346
- Expiry: 12/25
- CVV: 123
- Name: Test User

### Test Scenarios
1. **Successful Payment**: Use valid test card details
2. **Failed Payment**: Use invalid card details
3. **Cancelled Payment**: Cancel during payment process
4. **Duplicate Enrollment**: Try to enroll in same program twice

## Error Handling

### Common Error Scenarios
1. **Invalid Program**: Program not found
2. **Already Enrolled**: User already enrolled in program
3. **Invalid Hash**: Payment notification verification failed
4. **Network Issues**: API communication problems

### Error Responses
All errors return appropriate HTTP status codes and error messages:
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Authentication required
- `404 Not Found`: Program not found
- `409 Conflict`: Already enrolled
- `500 Internal Server Error`: Server errors

## Production Deployment

### Environment Setup
1. Update `PAYHERE_SANDBOX_MODE=false`
2. Set production PayHere merchant credentials
3. Update URLs to production domains
4. Configure SSL certificates
5. Set up proper logging and monitoring

### Security Considerations
1. Use HTTPS for all payment-related endpoints
2. Validate all input data
3. Implement rate limiting
4. Monitor payment notifications
5. Keep merchant credentials secure

## Troubleshooting

### Common Issues
1. **Hash Verification Failed**: Check merchant secret configuration
2. **Payment Not Processing**: Verify PayHere URLs and merchant ID
3. **User Not Enrolled**: Check payment completion endpoint
4. **Frontend Redirect Issues**: Verify return/cancel URLs

### Debug Mode
Enable debug logging by setting:
```properties
logging.level.com.java.moveminds.services.impl.PayHereServiceImpl=DEBUG
logging.level.com.java.moveminds.controllers.PaymentController=DEBUG
```

## Support
For PayHere-specific issues, refer to:
- [PayHere Documentation](https://www.payhere.lk/developers)
- [PayHere Sandbox](https://sandbox.payhere.lk/)
- [PayHere Support](https://www.payhere.lk/support)
