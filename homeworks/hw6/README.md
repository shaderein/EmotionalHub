# MyBooksApp

This is a simple application we build during lectures in fall 2020 OOSE class together to practice with various concepts and technologies. This 
is a web app conforming to Client-Server Architecture where user(s) can store their favorite books and authors. The app
will store data in a database and its backend functionalities are implemented as RESTful API end-points.

## HW6

The MyBooksApp is deployed using Heroku at https://hidden-shore-35596.herokuapp.com/. 
The default app name is "hidden-shore-35596".

commands for deployment:
* chmod +x gradlew
* ./gradlew build jar
* ./gradlew build deployHeroku

CI with Heroku was built into the project repo.