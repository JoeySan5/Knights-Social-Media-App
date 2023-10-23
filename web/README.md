# Web

# to deploy UI
    * ensure that CORS is enabled through dokku
    * once this is checked, navigate to Web/ directory
    * run the command: sh deploy.sh
        * this compiles, runs, and creates the necessary folders to run
    * once loaded, navigate to the correct https location, there should be 3 different ones

# to run unit tests
    * navigate to Web/ directory
    * run command: sh deploy.sh
    * once this compiles, select one of the three http locations provided
    * navigate to that url but add '/spec_runner.html' to the end of the url
    * verify that tests work

# Documentation
    * link to index.html produced from JSDocs [JSDocs html](Web/out/index.html)