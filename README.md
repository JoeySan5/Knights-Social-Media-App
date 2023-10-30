# Team 25 - Knights

## Team Information
- **Team Number**: 25
- **Team Name**: Knights
- **Members**:
  - Tommy Parisi - Admin Developer - Contact Email: tjp225@lehigh.edu
  - Joseph Sanchez - Web Developer - Contact Email: jrs225@lehigh.edu
  - Adivi Karawat - Project Manager - Contact Email: adk225@lehigh.edu
  - Eric Osterman - Mobile Developer - Contact Email: ejo223@lehigh.edu
  - Sehyoun Jang - Backend Manager - Contact Email: sej324@lehigh.edu

## URLs
- **Git Repository**: [https://bitbucket.org/cse216-2023fa-team-25/cse216-2023fa-team-25/src/master/](https://bitbucket.org/cse216-2023fa-team-25/cse216-2023fa-team-25/src/master/)
- **Trello Board**: [https://trello.com/invite/b/EnJtgGYY/ATTI0772a2683b64851fa36b0f707982fa087EC7746E/cse216](https://trello.com/invite/b/EnJtgGYY/ATTI0772a2683b64851fa36b0f707982fa087EC7746E/cse216)
- **Backend URL**: [https://team-knights.dokku.cse.lehigh.edu/](https://team-knights.dokku.cse.lehigh.edu/)

## Release Description

### Initial Setup (Version 0.1.0)
- Repository has been set up with the essential directories and branches.
- README file has been created and updated with team information, key URLs, and initial project description.
- No core functionalities have been implemented yet. Future updates and functionalities will be documented in the subsequent releases.

### Tagged Release


## Build & Run Instructions

### Locally
1. From the backend maven project root, run mvn exec:java with the `PORT` and `DATABASE_URL` environment variables specified
    * `PORT=8998 DATABASE_URL=postgres://pfdcoetq:VMXXrjrJtMXqzP6JwpjnapwpOVpk6e9o@peanut.db.elephantsql.com/pfdcoetq mvn exec:java`
    * The address is `http://localhost:PORT/`

### On Dokku
1. Ensure your ssh key is set up on Dokku
2. Use `config:set` to edit and setup the environment variables
    * For example, `ssh -i ~/.ssh/id_ed25519 -t dokku@dokku.cse.lehigh.edu 'config:set team-knights CORS_ENABLED=false'`
    * `config:export` will show the environment variables
3. From the *local* machine (not on dokku), run the following to use `ps:start`:
    * `ssh -i ~/.ssh/id_ed25519 -t dokku@dokku.cse.lehigh.edu 'ps:start team-knights'`
    * The address is `https://team-knights.dokku.cse.lehigh.edu/`
4. To see continuous logs, run the following after starting:
    * `ssh -i ~/.ssh/id_ed25519 -t dokku@dokku.cse.lehigh.edu 'logs team-knights -t'`
