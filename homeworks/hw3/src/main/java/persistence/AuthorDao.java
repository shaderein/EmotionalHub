package persistence;

import exception.DaoException;
import model.Author;
import model.Book;

import java.util.List;

public interface AuthorDao {
    /* Adds an author to the database. Returns its primary key.
     * Throws an exception of query fails to be executed. */
    int add(Author author) throws DaoException;

    /* Returns a list of all authors in the database.
     * Throws an exception of query fails to be executed. */
    List<Author> listAll() throws DaoException;

    /* Deletes an author from the database. Returns true if author
     * was deleted by query execution, false otherwise.
     * Throws an exception of query fails to be executed. */
    boolean delete(Author author) throws DaoException;

    /* Updates an author in the database. Returns true if author
     * was updated by query execution, false otherwise.
     * Throws an exception of query fails to be executed. */
    boolean update(Author author) throws DaoException;
}