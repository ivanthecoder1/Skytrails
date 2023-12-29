# Skytrails
A social media application for users to login via Google, and share posts with other users.

## Features
- Login through email via Google OAuth 2.0
- Profile Customization
- Create posts
- Like or dislike other posts
- Comment on other posts and edit / delete a comment
- Attach a file and/or link to a post or comment
- Admin CLI for invalidating/validating posts, directly interact with database, etc
- **Tech Stack:** React.js, TypeScript, HTML/CSS, Java, Maven, Flutter, ElephantSQL, Vitest/Jest, JUnit, DartDocs, Dokku 

## Website Look
| Desktop View  | Mobile View |
| ------------- | ------------- |
|<img width="700" src="https://github.com/ivanthecoder1/Skytrails/assets/56855196/b87d976e-938e-4e7c-a846-b0c581fad5e5">| <img width="220" src="https://github.com/ivanthecoder1/Skytrails/assets/56855196/0f484456-8ce6-4ec6-badd-db24b75b0cc7"> |

## How to run back end
- CD to backend folder 
- Run mvn clean; mvn package
- Run POSTGRES_IP=[server] POSTGRES_PORT=[port number] POSTGRES_USER=[user] POSTGRES_PASS=[password] mvn exec:java

## How to run web front end
- CD to web/skytrail folder
- Run npm install to get dependencies
- Run npm run dev to start the web application

## How to run mobile front end
- CD to skytrail folder within mobile folder
- Run flutter pub get to install dependencies
- Have an emulator set up to Pixel API 33 and run it first
- Then run flutter run in the terminal to run mobile app

## Unit Testing 
- Backend: run mvn package within backend folder to view tests
- Web: run "npm run test" within web/skytrail and wait for test output
- Mobile: CD to test directory within mobile folder, and run flutter test main_test.dart

