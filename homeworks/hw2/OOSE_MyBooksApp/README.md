# MyBooksApp

This is a simple application we build during lectures in fall 2020 OOSE class together to practice with various concepts and technologies. This 
is a web app conforming to Client-Server Architecture where user(s) can store/access their favorite books and authors. The app
will store data in a database and its backend functionalities are implemented as RESTful API end-points.

In this homework, we inplemented `add()` and `listAll()` for the `author` class and added the new `BookDao` interface, which is implemented by the `sql2oBookDao` class. We also implemented the update and delete methods for both of the `sql2oBookDao` and `sql2oAuthorDao` class. The update and delete methods for `sql2oBookDao` work by finding the isbn of the book in the database. Similarly, those of the `sql2oAuthorDao` class work by targeting the author by his/her name. We also added one test for each of the methods mentioned above. The assumption is that an author's name and a book's isbn won't be changed easily. (We don't consider the situation if an author changes his/her name and we still want to keep it in our database.)

One assumption we made is that when an author is deleted from the database. All books from the author will be deleted from the book database also. To test this cascade, we have one additional `testDeleteAuthorCascade()` test. In addition, we have `testAuthorOpenDBException()` and `testBookOpenDBException()` to confirm that the exception is correctly thrown when we fail to connect to a database.

Github: https://github.com/jhu-oose/2020-fall-group-unicorns-of-love
