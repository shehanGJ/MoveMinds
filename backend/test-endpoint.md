# Test the Program Stats Endpoint

To test if the program stats endpoint is working, you can use the following curl command:

```bash
# First, get a JWT token by logging in
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "instructor1", "password": "password123"}'

# Then use the token to call the stats endpoint
curl -X GET http://localhost:8080/instructor/programs/1/stats \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

Expected response:
```json
{
  "programId": 1,
  "programName": "Morning Cardio Blast",
  "totalStudents": 3,
  "activeStudents": 3,
  "totalEnrollments": 3,
  "activeEnrollments": 3,
  "completedEnrollments": 0
}
```

If this works, then the backend is functioning correctly and the issue is in the frontend.
