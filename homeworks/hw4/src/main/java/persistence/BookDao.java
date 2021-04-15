package persistence;

import exception.DaoException;

import model.Book;

import java.util.List;

//An interface for Book Data Acess Objects
public interface BookDao {
    int add(Book author) throws DaoException;
    List<Book> listAll() throws DaoException;
    boolean update(Book book) throws DaoException;
    boolean delete(Book book) throws DaoException;
}
