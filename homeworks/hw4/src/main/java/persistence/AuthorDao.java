package persistence;

import exception.DaoException;
import model.Author;

import java.util.List;

// An interface for Author Data Access Object
public interface AuthorDao {
    int add(Author author) throws DaoException;
    List<Author> listAll() throws DaoException;
    boolean update(Author author) throws DaoException;
    boolean delete(Author author) throws DaoException;
}
