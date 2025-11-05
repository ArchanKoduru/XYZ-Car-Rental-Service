# XYZ Car Rental Service API

**XYZ Car Rental Company has an in-house software system to manage car rentals.**  
This API allows you to create and manage bookings, validate driving licenses, and check car pricing.

---

## Features

* Create and view car bookings
* Validate driving license and car segment
* Enum validation for car segments (`SMALL`, `MEDIUM`, `LARGE`, `EXTRA_LARGE`)
* Stub services for car pricing and driving license verification
* Consistent JSON error responses

---

## Base URL

```
http://localhost:8080/api/v1
```

---

## Endpoints

### 1. Create Booking

```
POST /bookings
Content-Type: application/json
```

**Request Body:**

```json
{
  "drivingLicenseNumber": "DL123",
  "age": 28,
  "startDate": "2025-11-05",
  "endDate": "2025-11-10",
  "carSegment": "MEDIUM"
}
```

**Success Response (201 Created):**

```json
{
  "bookingId": 1,
  "drivingLicenseNumber": "DL123",
  "customerName": "John Doe",
  "age": 28,
  "startDate": "2025-11-05",
  "endDate": "2025-11-10",
  "carSegment": "MEDIUM",
  "rentalPrice": 244.64
}
```

---

### Error Cases (Sample Requests)

#### 1. Invalid Driving License

**Request Body:**

```json
{
  "drivingLicenseNumber": "DL_UNKNOWN",
  "age": 28,
  "startDate": "2025-11-05",
  "endDate": "2025-11-10",
  "carSegment": "MEDIUM"
}
```

**Expected Response (404 Not Found):**

```json
{
  "error": "Driving license not found"
}
```

---

#### 2. Invalid Car Segment

**Request Body:**

```json
{
  "drivingLicenseNumber": "DL123",
  "age": 28,
  "startDate": "2025-11-05",
  "endDate": "2025-11-10",
  "carSegment": "MEDUM"
}
```

**Expected Response (400 Bad Request):**

```json
{
  "error": "Invalid value 'MEDUM' for field 'carSegment'. Acceptable values: [LARGE, EXTRA_LARGE, MEDIUM, SMALL]"
}
```

---

#### 3. Missing Required Field (drivingLicenseNumber)

**Request Body:**

```json
{
  "age": 28,
  "startDate": "2025-11-05",
  "endDate": "2025-11-10",
  "carSegment": "MEDIUM"
}
```

**Expected Response (400 Bad Request):**

```json
{
  "error": "drivingLicenseNumber must not be blank"
}
```

---

#### 4. Booking Dates Invalid (End Date Before Start Date)

**Request Body:**

```json
{
  "drivingLicenseNumber": "DL123",
  "age": 28,
  "startDate": "2025-11-10",
  "endDate": "2025-11-05",
  "carSegment": "MEDIUM"
}
```

**Expected Response (400 Bad Request):**

```json
{
  "error": "Reservation end date must be after start date."
}
```

---

### 2. Get Booking by ID

```
GET /bookings/{id}
```

**Success Response (200 OK):**

```json
{
  "bookingId": 1,
  "drivingLicenseNumber": "DL123",
  "customerName": "John Doe",
  "age": 28,
  "startDate": "2025-11-05",
  "endDate": "2025-11-10",
  "carSegment": "MEDIUM",
  "rentalPrice": 244.64
}
```

**Error Response (404 Not Found):**

```json
{
  "error": "Booking not found for id 999"
}
```

---

## Error Response Format

All errors return:

```json
{
  "error": "Error message here"
}
```

| HTTP Status               | Example Error Message                                         |
| ------------------------- | ------------------------------------------------------------- |
| 400 Bad Request           | Invalid input / Missing required fields / Invalid enum values |
| 404 Not Found             | Driving license not found / Booking not found                 |
| 503 Service Unavailable   | Car pricing service unavailable                               |
| 500 Internal Server Error | Internal server error                                         |

---

## Car Segments

Allowed values:

* `SMALL`
* `MEDIUM`
* `LARGE`
* `EXTRA_LARGE`

---

## Postman Collection (Sample Requests)

1. **Create Booking (Valid)**
2. **Create Booking (Invalid License)**
3. **Create Booking (Invalid Car Segment)**
4. **Create Booking (Invalid Dates / Missing Fields)**
5. **Get Booking by ID (Valid & Invalid)**

> You can import these JSON requests into Postman and test the API endpoints.

---

## Running the Service with Docker Compose

1. **Build Docker Image**

```bash
./mvnw clean package -DskipTests
docker build -t car-rental-service:latest .
```

2. **Run with Docker Compose**

Make sure you have a `docker-compose.yml` in your project root with proper service definitions.

```bash
docker-compose up
```

3. **Stop Services**

```bash
docker-compose down
```

4. **Access API**

```
http://localhost:8080/api/v1/bookings
```

---

## Notes

* This service uses stub clients for driving license and car pricing services.
* All validation and error handling follows a consistent JSON format (`{"error": "..."}`).
