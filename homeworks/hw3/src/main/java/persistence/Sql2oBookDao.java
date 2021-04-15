package persistence;

import exception.DaoException;
import model.Book;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.sql.SQLException;
import java.util.List;

public class Sql2oBookDao implements BookDao {
    private final Sql2o sql2o;

    public Sql2oBookDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public int add(Book book) throws DaoException {
        try (Connection con = sql2o.open()) {
            String query = "INSERT INTO Books (id, title, isbn, publisher, year, authorId)" +
                    "VALUES (NULL, :title, :isbn, :publisher, :year, :authorId)";
            int id = (int) con.createQuery(query, true)
                    .bind(book)
                    .executeUpdate().getKey();
            book.setId(id);
            return id;
        }
        catch (Sql2oException ex) {
            throw new DaoException();
        }
    }

    @Override
    public List<Book> listAll() {
        String sql = "SELECT * FROM Books";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql).executeAndFetch(Book.class);
        }
        catch (Sql2oException ex) {
            throw new DaoException();
        }
    }
    // TODO: Add "delete" method from hw2 here; feel free to add more methods
    @Override
    public boolean delete(Book book) throws DaoException {
        return deleteByIsbn(book.getIsbn());
    }

    // Delete a book given its ISBN.
    public boolean deleteByIsbn(String isbn) throws DaoException {
        String sql = "DELETE FROM Books WHERE isbn='" + isbn + "'";
        try (Connection con = sql2o.open()) {
            con.createQuery("PRAGMA foreign_keys = ON").executeUpdate();
            int changes = con.createQuery(sql).executeUpdate().getResult();
            return changes > 0;
        }
        catch (Sql2oException e) {
            throw new DaoException();
        }
    }

    @Override
    public boolean update(Book book) throws DaoException {
        String up = "UPDATE Books SET " +
                "title = :title, " +
                "publisher = :publisher, " +
                "year = :year, " +
                "authorId = :authorId, " +
                "WHERE isbn = :isbn";
        try (Connection con = sql2o.open()) {
            con.createQuery("PRAGMA foreign_keys = ON").executeUpdate();
            int changes = con.createQuery(up)
                    .bind(book)
                    .executeUpdate().getResult();
            return changes > 0;
        }
        catch (Sql2oException e) {
            throw new DaoException();
        }
    }

    public void dropCreateTable() {
        String sql_drop_books = "DROP TABLE IF EXISTS Books";

        String sql = "CREATE TABLE IF NOT EXISTS Books (id INTEGER PRIMARY KEY, title VARCHAR(200) NOT NULL, " +
                "isbn VARCHAR(14) NOT NULL UNIQUE, publisher VARCHAR(14), year INTEGER, " +
                "authorId INTEGER NOT NULL, FOREIGN KEY(authorId) REFERENCES Authors(id) " +
                "ON UPDATE CASCADE ON DELETE CASCADE);";

        try (Connection con = sql2o.open()) {
            con.createQuery(sql_drop_books).executeUpdate();
            con.createQuery(sql).executeUpdate();
        }
    }
}