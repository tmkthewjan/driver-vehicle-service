# Driver & Vehicle Service - Implementation Plan

**Owner:** Member 2  
**Service:** Driver & Vehicle Service  
**Project:** RideLink (IT3130 Application Development)

---

## 1. Service Responsibility

The Driver & Vehicle Service manages all driver operational data within the RideLink ride-sharing platform. It is responsible for:

- Driver operational profiles (linked to accounts managed by Account Service)
- Vehicle registration and details
- Driver availability status tracking
- Driver service area management
- Simulated current location tracking
- Retrieval of eligible available drivers for ride requests

This service does **not** own user accounts, authentication, ride lifecycle, or fare/payment data.

---

## 2. Main Entities

### Driver
| Field              | Type     | Description                              |
|--------------------|----------|------------------------------------------|
| id                 | String   | MongoDB-generated unique identifier      |
| accountId          | String   | Reference to Account Service user ID     |
| firstName          | String   | Driver's first name                      |
| lastName           | String   | Driver's last name                       |
| phoneNumber        | String   | Driver's contact number                  |
| status             | Enum     | ACTIVE, INACTIVE, SUSPENDED              |
| availabilityStatus | Enum     | AVAILABLE, UNAVAILABLE, ON_RIDE          |
| serviceArea        | String   | Area name where the driver operates      |
| currentLatitude    | Double   | Simulated latitude (-90 to 90)           |
| currentLongitude   | Double   | Simulated longitude (-180 to 180)        |
| createdAt          | DateTime | Record creation timestamp                |
| updatedAt          | DateTime | Last update timestamp                    |

### Vehicle
| Field              | Type     | Description                              |
|--------------------|----------|------------------------------------------|
| id                 | String   | MongoDB-generated unique identifier      |
| driverId           | String   | Reference to owning driver               |
| registrationNumber | String   | Vehicle registration/number plate        |
| make               | String   | Vehicle manufacturer                     |
| model              | String   | Vehicle model name                       |
| vehicleType        | Enum     | SEDAN, SUV, VAN, BIKE                    |
| color              | String   | Vehicle color                            |
| capacity           | Integer  | Passenger capacity                       |
| isActive           | Boolean  | Whether vehicle is currently active      |
| createdAt          | DateTime | Record creation timestamp                |
| updatedAt          | DateTime | Last update timestamp                    |

---

## 3. API Endpoints

### Driver Profile
| Method | Endpoint                              | Description                    |
|--------|---------------------------------------|--------------------------------|
| POST   | `/api/drivers`                        | Register driver profile        |
| GET    | `/api/drivers/{driverId}`             | Get driver profile             |
| PUT    | `/api/drivers/{driverId}`             | Update driver profile          |
| PATCH  | `/api/drivers/{driverId}/status`      | Update driver status           |
| PATCH  | `/api/drivers/{driverId}/availability`| Update availability            |
| PATCH  | `/api/drivers/{driverId}/location`    | Update simulated location      |
| PATCH  | `/api/drivers/{driverId}/service-area`| Update service area            |

### Vehicle
| Method | Endpoint                                    | Description              |
|--------|---------------------------------------------|--------------------------|
| POST   | `/api/drivers/{driverId}/vehicle`           | Register vehicle         |
| GET    | `/api/drivers/{driverId}/vehicle`           | Get driver's vehicle     |
| PUT    | `/api/drivers/{driverId}/vehicle`           | Update vehicle           |
| PATCH  | `/api/drivers/{driverId}/vehicle/status`    | Activate/deactivate      |

### Eligible Drivers
| Method | Endpoint              | Description                       |
|--------|-----------------------|-----------------------------------|
| GET    | `/api/drivers/eligible` | Find eligible drivers for a ride |

Query parameters: `latitude`, `longitude`, `radius` (km, default 10), `vehicleType` (optional)

---

## 4. Persistence Approach

- **Database:** MongoDB (as configured in `pom.xml`)
- **ODM:** Spring Data MongoDB
- **Collections:** `drivers`, `vehicles`
- **Data ownership:** This service exclusively owns its database. No other service accesses it directly.

---

## 5. Validation Rules

### Driver
- `accountId` — required, not blank
- `firstName` — required, not blank
- `lastName` — required, not blank
- `phoneNumber` — required, not blank
- `status` — must be a valid enum value (ACTIVE, INACTIVE, SUSPENDED)
- `availabilityStatus` — must be a valid enum value
- `currentLatitude` — between -90 and 90 (when provided)
- `currentLongitude` — between -180 and 180 (when provided)

### Vehicle
- `registrationNumber` — required, not blank
- `make` — required, not blank
- `model` — required, not blank
- `vehicleType` — required, valid enum
- `color` — required, not blank
- `capacity` — required, minimum 1
- Driver must exist

### Business Rules
- INACTIVE/SUSPENDED drivers cannot be set to AVAILABLE
- ON_RIDE drivers are not returned as eligible
- Drivers without an active vehicle are not eligible

---

## 6. Authentication / Authorization Approach

- **Account Service** (Member 1) owns authentication and JWT token issuance
- This service will validate incoming JWT tokens
- Until Account Service is available, auth middleware will be structured but permissive
- Role-based access: DRIVER role for driver operations, ADMIN for administrative actions
- No hardcoded secrets; JWT secret loaded from environment variables

---

## 7. Interservice Communication Plan

### Inbound (other services call this service)
- **Ride Management Service → GET /api/drivers/eligible**: Find eligible drivers for a ride request
- Communication: synchronous REST (HTTP)

### Outbound (this service calls other services)
- **Account Service**: Potentially validate accountId during driver registration (future integration)
- Communication: synchronous REST via RestTemplate/WebClient

### Rationale
REST is chosen because:
1. The eligible-driver query is synchronous request-response
2. Simple to implement and debug
3. Matches the assignment's REST-first approach
4. All services expose REST APIs already

---

## 8. Testing Plan

| Area                | Test Cases                                              |
|---------------------|---------------------------------------------------------|
| Driver Service      | Create, retrieve, update, status changes, validation    |
| Vehicle Service     | Create, retrieve, update, activate/deactivate           |
| Availability        | Valid transitions, invalid transitions, business rules  |
| Location            | Valid coordinates, invalid coordinates                  |
| Eligibility         | Active+available+vehicle, exclusions, radius filtering  |
| Error Handling      | Not found, invalid input, duplicates                   |

- **Framework:** JUnit 5 + Mockito
- **Approach:** Unit tests for service layer logic; mock repositories

---

## 9. Future Integration Points

1. **Account Service integration**: Validate accountId on driver registration
2. **Ride Management integration**: 
   - Eligible driver query (already planned)
   - Driver availability update when ride is assigned (ON_RIDE) or completed (AVAILABLE)
3. **Event-driven updates**: Could add async events for driver status changes (future enhancement)

---

## 10. Technology Stack Summary

| Component       | Technology                    |
|-----------------|-------------------------------|
| Language        | Java 17                       |
| Framework       | Spring Boot 4.1.1             |
| Database        | MongoDB                       |
| Build           | Maven 3.9.16                  |
| Validation      | Jakarta Bean Validation       |
| API Docs        | SpringDoc OpenAPI (Swagger)   |
| Testing         | JUnit 5, Mockito              |
| Utilities       | Lombok                        |
