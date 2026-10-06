# AI-Powered Resume Builder & Job Matcher Platform (Backend)

A production-ready Spring Boot backend for an **AI-Powered Resume Builder & Job Matcher Platform**. It provides secure JWT-based authentication, MongoDB Atlas integration, Resume management, Job postings, and LLM-powered semantic matching of resumes against job descriptions using Google Gemini and OpenAI.

---

## 🛠️ Technology Stack

- **Language:** Java 17 / 21
- **Framework:** Spring Boot 3.3.4
- **Database:** MongoDB / MongoDB Atlas (Spring Data MongoDB)
- **Security:** Spring Security 6, JJWT (io.jsonwebtoken 0.12.6), BCrypt
- **Validation:** Jakarta Validation (`spring-boot-starter-validation`)
- **AI Integrations:** Google Gemini API (`gemini-1.5-flash` / `gemini-2.5-flash`) & OpenAI API (`gpt-4o-mini` / `gpt-4o`)
- **Build Tool:** Apache Maven
- **Utilities:** Lombok, Jackson

---

## 📁 Project Structure

```
src/main/java/com/example/resumebuilder/
├── ResumeBuilderApplication.java       # Spring Boot Application Entry Point
│
├── controller/                         # REST API Controllers
│   ├── AuthController.java             # /api/auth (Register, Login, Me)
│   ├── ResumeController.java           # /api/resumes (CRUD + Ownership)
│   ├── JobController.java              # /api/jobs (Job Postings CRUD)
│   ├── AiController.java               # /api/ai/match (AI Matchmaker)
│   └── HealthController.java           # /api/health (Service Health)
│
├── model/                              # MongoDB Entities & Documents
│   ├── User.java                       # User entity (Unique email, hashed passwords)
│   ├── Resume.java                     # Resume entity with nested sub-documents
│   ├── Job.java                        # Job postings entity
│   ├── PersonalInfo.java               # Nested: Personal & contact info
│   ├── Education.java                  # Nested: Education details
│   ├── Experience.java                 # Nested: Work history & roles
│   ├── Project.java                    # Nested: Portfolio projects & links
│   └── dto/                            # Data Transfer Objects (DTOs)
│       ├── RegisterRequest.java
│       ├── LoginRequest.java
│       ├── AuthResponse.java
│       ├── UserDto.java
│       ├── ResumeRequest.java
│       ├── ResumeResponse.java
│       ├── JobRequest.java
│       ├── JobResponse.java
│       ├── AiMatchRequest.java
│       ├── AiMatchResponse.java
│       └── ErrorResponse.java
│
├── repository/                         # Spring Data MongoDB Repositories
│   ├── UserRepository.java             # findByEmail, existsByEmail
│   ├── ResumeRepository.java           # findByUserId, findByIdAndUserId
│   └── JobRepository.java              # findAllByOrderByPostedDateDesc
│
├── service/                            # Core Business Logic & AI Abstraction
│   ├── AuthService.java                # Registration, Login, Token generation
│   ├── ResumeService.java              # Resume CRUD + strict ownership verification
│   ├── JobService.java                 # Job management logic
│   ├── AiMatchingService.java          # Context building and matching pipeline
│   ├── LlmService.java                 # AI LLM Provider Interface
│   └── impl/
│       ├── GeminiLlmService.java       # Google Gemini 1.5 Flash implementation
│       ├── OpenAiLlmService.java       # OpenAI GPT-4o-mini implementation
│       └── LlmServiceDelegator.java    # Dynamic provider dispatcher
│
├── config/                             # Security & App Configuration
│   ├── SecurityConfig.java             # Spring Security filter chain & permissions
│   ├── JwtTokenProvider.java           # JWT signing, claims parsing & validation
│   ├── JwtAuthenticationFilter.java    # OncePerRequestFilter for Bearer tokens
│   ├── UserPrincipal.java              # UserDetails implementation
│   ├── CustomUserDetailsService.java   # Spring Security user loading
│   ├── CorsConfig.java                 # Configurable CORS for React/Vite frontend
│   └── MongoConfig.java                # MongoDB Auditing & index configuration
│
└── exception/                          # Exception Handling
    ├── GlobalExceptionHandler.java     # @RestControllerAdvice with structured JSON
    ├── ResourceNotFoundException.java  # HTTP 404
    ├── BadRequestException.java        # HTTP 400
    ├── ForbiddenException.java         # HTTP 403
    └── AiServiceException.java         # HTTP 503 / safe AI error responses
```

---

## 🔒 Security & Ownership Architecture

1. **Password Protection:** Passwords are encrypted using strong BCrypt hashing with salt.
2. **Stateless JWT:** Bearer tokens are signed with HMAC-SHA256 and verified per-request.
3. **Ownership Validation:**
   - A user cannot read, update, or delete resumes belonging to another user.
   - User identity is extracted directly from the authenticated JWT `SecurityContext`, preventing user ID spoofing.
   - Any unauthorized access attempt throws `ForbiddenException` (HTTP 403).

---

## ⚙️ Environment Configuration

Create a `.env` or set environment variables before running:

| Variable | Description | Default / Example |
| :--- | :--- | :--- |
| `PORT` | Backend Server Port | `8080` |
| `MONGODB_URI` | MongoDB Atlas Connection String | `mongodb+srv://user:pass@cluster.mongodb.net/` |
| `MONGODB_DATABASE` | Database Name | `resume_builder` |
| `JWT_SECRET` | Secret key for signing JWT tokens (min 32 chars) | `replace_with_secure_secret` |
| `JWT_EXPIRATION` | Token expiration in milliseconds | `86400000` (24 hours) |
| `FRONTEND_URL` | Allowed CORS Frontend URL | `http://localhost:5173` |
| `AI_PROVIDER` | Active LLM Provider (`gemini` or `openai`) | `gemini` |
| `GEMINI_API_KEY` | Google Gemini API Key | `AIzaSy...` |
| `GEMINI_MODEL` | Gemini Model Name | `gemini-1.5-flash` |
| `OPENAI_API_KEY` | OpenAI API Key (if `AI_PROVIDER=openai`) | `sk-...` |
| `OPENAI_MODEL` | OpenAI Model Name | `gpt-4o-mini` |

---

## 🚀 Getting Started

### Prerequisites
- **Java 17+** (or Java 21)
- **Maven 3.8+**
- **MongoDB Atlas** cluster URL (or local MongoDB instance)

### Build the Project
```bash
mvn clean package
```

### Run Tests
```bash
mvn test
```

### Start the Application
```bash
# Using Maven
mvn spring-boot:run

# Or running the packaged JAR
java -jar target/resume-builder-0.0.1-SNAPSHOT.jar
```

The API will be available at: `http://localhost:8080/api`

---

## 📚 API Endpoints Documentation

### 1. Authentication (`/api/auth`)

#### Register User
`POST /api/auth/register` (Public)
```json
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "securePassword123"
}
```
**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": "660c1d2e...",
    "name": "Jane Doe",
    "email": "jane@example.com",
    "roles": ["ROLE_USER"],
    "createdAt": "2026-10-06T09:00:00Z"
  }
}
```

#### Login User
`POST /api/auth/login` (Public)
```json
{
  "email": "jane@example.com",
  "password": "securePassword123"
}
```
**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": "660c1d2e...",
    "name": "Jane Doe",
    "email": "jane@example.com",
    "roles": ["ROLE_USER"]
  }
}
```

---

### 2. Resumes (`/api/resumes`) - *Requires Bearer Token*

#### Create Resume
`POST /api/resumes`
```json
{
  "title": "Senior Full Stack Engineer",
  "personalInfo": {
    "fullName": "Jane Doe",
    "email": "jane@example.com",
    "phone": "+1 555-0199",
    "location": "San Francisco, CA",
    "linkedin": "https://linkedin.com/in/janedoe",
    "github": "https://github.com/janedoe"
  },
  "summary": "Passionate Software Engineer with 5+ years of experience building scalable backend architectures.",
  "skills": ["Java", "Spring Boot", "MongoDB", "React", "Docker", "AWS", "REST APIs"],
  "experience": [
    {
      "company": "Tech Innovations Inc",
      "role": "Senior Java Developer",
      "startDate": "2022-01",
      "endDate": "Present",
      "current": true,
      "description": "Led the migration to Spring Boot microservices, improving throughput by 40%."
    }
  ],
  "education": [
    {
      "institution": "University of California, Berkeley",
      "degree": "Bachelor of Science",
      "fieldOfStudy": "Computer Science",
      "startYear": "2016",
      "endYear": "2020"
    }
  ],
  "projects": [
    {
      "title": "AI Resume Matcher",
      "techStack": ["Spring Boot", "MongoDB", "Gemini API", "React"],
      "description": "An automated candidate evaluation tool using generative AI.",
      "link": "https://github.com/janedoe/resume-matcher"
    }
  ]
}
```

#### Get Resumes for User
`GET /api/resumes/user/{userId}`
*Returns list of resumes. Only accessible if `userId` matches the authenticated user.*

#### Get Resume by ID
`GET /api/resumes/{resumeId}`
*Returns single resume. Only accessible by the resume owner.*

#### Update Resume
`PUT /api/resumes/{resumeId}`
*Updates resume fields. Only accessible by the resume owner.*

#### Delete Resume
`DELETE /api/resumes/{resumeId}`
*Deletes resume. Only accessible by the resume owner. Returns 204 No Content.*

---

### 3. Jobs (`/api/jobs`)

#### Create Job Posting
`POST /api/jobs` (*Authenticated*)
```json
{
  "title": "Senior Backend Engineer",
  "company": "CloudScale Systems",
  "description": "We are seeking a senior backend engineer proficient in Java, Spring Boot, MongoDB, and AWS cloud deployments.",
  "requiredSkills": ["Java", "Spring Boot", "MongoDB", "AWS", "Docker"],
  "location": "Remote",
  "jobType": "Full-Time",
  "salaryRange": "$140,000 - $180,000"
}
```

#### List All Jobs
`GET /api/jobs` (*Public*)

#### Get Job Details
`GET /api/jobs/{jobId}` (*Public*)

#### Update Job
`PUT /api/jobs/{jobId}` (*Authenticated - Creator*)

#### Delete Job
`DELETE /api/jobs/{jobId}` (*Authenticated - Creator*)

---

### 4. AI Matching (`/api/ai/match`) - *Requires Bearer Token*

#### Match Resume with Job
`POST /api/ai/match`
```json
{
  "resumeId": "660c1d2e...",
  "jobDescription": "Looking for a Senior Backend Engineer with 4+ years in Java 17+, Spring Boot, Docker, Kubernetes, and AWS. Must have experience designing high-throughput REST APIs and MongoDB databases."
}
```

**Response (200 OK):**
```json
{
  "matchScore": 88,
  "matchedSkills": [
    "Java",
    "Spring Boot",
    "MongoDB",
    "Docker",
    "AWS",
    "REST APIs"
  ],
  "missingSkills": [
    "Kubernetes"
  ],
  "aiRecommendations": [
    "Highlight experience with container orchestration (Kubernetes) or related CI/CD deployment pipelines.",
    "Add specific performance metrics (e.g. TPS, latency reduction) in your role at Tech Innovations."
  ]
}
```

---

## 🛡️ Error Response Schema

All errors follow a unified, secure format (no leaked stack traces or sensitive credentials):

```json
{
  "timestamp": "2026-10-06T09:15:00.000Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied: You do not have permission to access this resume",
  "path": "/api/resumes/660c1d2e..."
}
```

Validation failure format:
```json
{
  "timestamp": "2026-10-06T09:15:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "path": "/api/auth/register",
  "validationErrors": {
    "email": "Please provide a valid email address",
    "password": "Password must be at least 6 characters long"
  }
}
```
