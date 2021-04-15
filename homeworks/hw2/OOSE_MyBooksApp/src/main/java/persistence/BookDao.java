package persistence;

import exception.DaoException;
import model.Book;

import java.util.List;

public interface BookDao {
    /* Adds a book to the database. Returns its primary key.
     * Throws an exception of query fails to be executed. */
    int add(Book book) throws DaoException;

    /* Returns a list of all books in the database.
     * Throws an exception of query fails to be executed. */
    List<Book> listAll() throws DaoException;

    /* Deletes a book from the database. Returns true if book
     * was deleted by query execution, false otherwise.
     * Throws an exception of query fails to be executed. */
    boolean delete(Book book) throws DaoException;

    /* Updates a book in the database. Returns true if book
     * was updated by query execution, false otherwise.
     * Throws an exception of query fails to be executed. */
    boolean update(Book book) throws DaoException;
}
