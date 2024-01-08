# Welcome to CampusCrafter

CampusCrafter is a dynamic web platform built in Java, aiming to elevate the educational 
journey for students, educators, and administrators.

## What's Inside

### Who Can Do What
- **Students**: Dive into courses and submit assignments hassle-free.
- **Teachers**: Craft courses, manage assignments, and evaluate submissions.
- **Admins**: Oversee the system and handle user accounts with ease.

### The Core Components
1. **Course Details**: Covering titles, descriptions, start dates, credits, and more.
2. **Assignments**: From content and deadlines to submission formats and scores.
3. **Grades**: Recording student performance, feedback, and submission dates.
4. **User Profiles**: Managing user credentials, roles, and login details.

## Key Functionalities

- **Students**: Browse courses, submit assignments.
- **Teachers**: Create assignments, courses, grade submissions.
- **Admins**: Manage user accounts, access all data.

## Running Locally

Before testing endpoints through Swagger, ensure you've set up the project locally, and it's running on your device. You can start the project and access it locally through your browser.

## APIs at a Glance

### Courses
- Fetch all, single, create, update, or delete courses.
- `/api/courses`, `/api/courses/{id}` - do it all here!

### Assignments
- Manage assignments per course, create, update, or delete them.
- `/api/courses/{courseId}/assignments`, `/api/assignments/{id}` - easy peasy!

### Grades
- Post grades, view them - simple for teachers and admins.
- `/api/assignments/{assignmentId}/grades`, `/api/students/{studentId}/grades` - grades made easy!

### User Profiles
- Access and update user profiles, create or delete users.
- `/api/users/{userId}`, `/api/users` - manage profiles seamlessly!

## Authentication and Security

- Secure login and registration processes.
- Utilize JWT or session-based authentication.
- Role-based access control (RBAC) for user types.

## Swagger for Endpoint Testing

- Access all endpoints through Swagger for testing: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Tech Landscape

- **Java Framework**: Spring
- **Database**: MongoDB
- **Build Tool**: Gradle
- **Documentation Tools**: Swagger
- **Languages**: Java