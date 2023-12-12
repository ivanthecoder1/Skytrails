# Skytrails
A social media application 

## Features
- Users can login through email via Google OAuth 2.0
- Users are able to edit their user profile
- Users can create posts
- Users can like or dislike other posts
- Users can comment on other posts and edit / delete a comment
- Users can attach a file and/or link to a post or comment
- Admin CLI for invalidating/validating posts, directly interact with database, etc
- **Tech Stack:** React.js, TypeScript, HTML/CSS, Java, Maven, Flutter, ElephantSQL, Vitest/Jest, JUnit, DartDocs, Dokku 

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

