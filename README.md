# Team 25 - Knights

## Team Information
- **Team Number**: 25
- **Team Name**: Knights
- **Members**:
  - Tommy Parisi - Project Manager (Phase 3)  - Contact Email: tjp225@lehigh.edu
  - Joseph Sanchez - Backend Developer (Phase 3) - Contact Email: jrs225@lehigh.edu
  - Adivi Karawat - Mobile Developer (Phase 3) - Contact Email: adk225@lehigh.edu
  - Eric Osterman - Admin Developer (Phase 3) - Contact Email: ejo223@lehigh.edu
  - Sehyoun Jang - Web Developer (Phase 3)  - Contact Email: sej324@lehigh.edu

## URLs
- **Git Repository**: <https://bitbucket.org/cse216-2023fa-team-25/cse216-2023fa-team-25/src/master/>
- **Trello Board**: <https://trello.com/invite/b/EnJtgGYY/ATTI0772a2683b64851fa36b0f707982fa087EC7746E/cse216>
- **Elephant SQL**: <https://api.elephantsql.com/console/8ce5b366-8e87-4040-afec-37897ecb4725/details>
- **Backend URL**: <https://team-knights.dokku.cse.lehigh.edu/>

## Release Description

### Initial Setup (Version 0.1.0)
- Repository has been set up with the essential directories and branches.
- README file has been created and updated with team information, key URLs, and initial project description.


### Tagged Release
1. mobile_v1 (sprint 6):
    * The mobile app features two main pages, the Home Page and Idea Submission Page. The Home Page consists of a title, a small description, a list view of Idea formats, and a 'say your piece' button. Idea formats are ideas that are collected from the database and formatted so that it contains the content, like count, and two buttons to either like or dislike. The 'say your piece' buttons, if tapped, brings you to the Idea Submission Page. This page consists of a text field, a 'submit' button, and a 'go back home' button. Users can write their idea into the text field and when ready to submit and share with the world, they click the submit button to post their data onto the database. Lastly, the 'go back home' button naturally sends the user back to the Home Page.
    * The app currently does not allow the user to effectively see their like/dislike be updated on a certain post without having to refresh. 
    The app does not allow the user to refresh without doing hot reload or closing the app.

1. web (sprint 12)
    * The web app allows for viewing and posting Ideas and comments on Ideas, which include text content, an optional link, and an optional file attachment. Profile pages can be viewed, and self profile content edited.

## Build & Run Instructions

### Mobile
#### To deploy UI
1. The mobile app can currently only be ran locally through an emulator. Therefore, the initial step is to have an Android Emulator (API 33 and above) set up 
2. Open up the source code on an IDE and open flutter/knights/lib/main.dart
3. Ensure the emulator is up and running, and then proceed to click **run and debug** (or f5) on your IDE (in main.dart)
4. Wait until the app loads onto your emulator
5. Once app is loaded you can proceed to try out all the features explained in the tagged release!

#### To use tests
1. Navigate to flutter/knights directory in terminal
2. Run command to test a specific file: flutter test tests/<test_file_name>

### Web
#### to deploy UI
    * ensure that CORS is enabled through dokku
    * once this is checked, navigate to knights-web-app/
    * then in the terminal type: ng serve --open

#### to run unit tests
    * navigate to knights-web-app
    * run command: ng test

### Backend

#### Locally
1. From the backend maven project root, run mvn exec:java with the `PORT` and `DATABASE_URL` environment variables specified
    * `PORT=8998 CLIENT_ID=1019349198762-463i1tt2naq9ipll3f9ade5u7nli7gju.apps.googleusercontent.com DATABASE_URL=postgres://pfdcoetq:VMXXrjrJtMXqzP6JwpjnapwpOVpk6e9o@peanut.db.elephantsql.com/pfdcoetq MEMCACHIER_USERNAME=B24B55 MEMCACHIER_PASSWORD=FDCD8702A87D0542C2EC23142008D739 MEMCACHIER_SERVERS=mc4.dev.ec2.memcachier.com:11211 CORS_ENABLED=TRUE GOOGLE_APPLICATION_CREDENTIALS="src\main\java\edu\lehigh\cse216\knights\backend\credentials\the-knights-d575548961a0.json" mvn exec:java`
    * The address is `http://localhost:PORT/`

#### On Dokku
1. Ensure your ssh key is set up on Dokku
2. Use `config:set` to edit and setup the environment variables
    * For example, `ssh -i ~/.ssh/id_ed25519 -t dokku@dokku.cse.lehigh.edu 'config:set team-knights CORS_ENABLED=false'`
    * `config:export` will show the environment variables
3. From the *local* machine (not on dokku), run the following to use `ps:start`:
    * `ssh -i ~/.ssh/id_ed25519 -t dokku@dokku.cse.lehigh.edu 'ps:start team-knights'`
    * The address is `https://team-knights.dokku.cse.lehigh.edu/`
4. To see continuous logs, run the following after starting:
    * `ssh -i ~/.ssh/id_ed25519 -t dokku@dokku.cse.lehigh.edu 'logs team-knights -t'`

### Admin Build & Run Instructions
- compile java code through mvn package and then run the POSTGRES command with the environment variables
    * `DATABASE_URL=postgres://pfdcoetq:VMXXrjrJtMXqzP6JwpjnapwpOVpk6e9o@peanut.db.elephantsql.com/pfdcoetq mvn exec:java`
    * Optionally set `FILEPATH=path/to/sample/data/directory` if not running from the maven project root
- Follow CLI instructions to ineract with the database
- pgAdmin or ElephantSQL are useful tools to verify results

## Developer Documentation

### Artifacts
[Artifacts List](docs/README-phase3.md)

### Mobile
[Mobile Dartdocs](docs/mobile_artifacts/api/index.html)

### Web
[Web JSDocs](docs/web_artifacts/index.html)

### Backend API
[Backend Javadoc](docs/backend-apidocs/site/apidocs/index.html)

### Admin
[Admin Javadoc](docs/admin-apidocs/site/apidocs/index.html)

