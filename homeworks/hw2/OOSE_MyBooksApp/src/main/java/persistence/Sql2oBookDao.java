package persistence;

import exception.DaoException;
import model.Author;
import model.Book;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import org.sqlite.SQLiteException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Sql2oBookDao implements BookDao{

    private final Sql2o sql2o;

    public Sql2oBookDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public int add(Book book) throws DaoException {
        try (Connection con = sql2o.open()) {
            int authorId = book.getAuthor().getId();
            String query = "INSERT INTO Books (title, isbn, publisher, year, authorId)" +
                    "VALUES (:title, :isbn, :publisher, :year, "+ authorId + ")";
            int id = (int) con.createQuery(query, true)
                    .bind(book)
                    .executeUpdate().getKey();
            book.setId(id);
            return id;
        }
        catch (Sql2oException e) {
            throw new DaoException();
        }
    }

    @Override
    public List<Book> listAll() {
        List<Book> listBooks = new ArrayList<>();
        String sql = "SELECT * FROM Books";
        try (Connection con = sql2o.open()) {
            List<Map<String,Object>> bookTable = con.createQuery(sql).executeAndFetchTable().asList();
            for (Map<String,Object> book : bookTable) {
                // Note that "authorid" is lowercase in the database.
                String sql_author = "SELECT * FROM Authors WHERE id=" + book.get("authorid") + "";
                List<Author> authors = con.createQuery(sql_author).executeAndFetch(Author.class);

                listBooks.add(new Book((String) book.get("title"), (String) book.get("isbn"),
                        (String) book.get("publisher"), (int) book.get("year"), authors.get(0)));
            }
            return listBooks;
        }
        catch (Sql2oException e) {
            throw new DaoException();
        }
    }

    @Override
    public boolean delete(Book book) throws DaoException {
        String sql = "DELETE FROM Books WHERE isbn='" + book.getIsbn() + "'";
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
                "authorId = " + book.getAuthor().getId() + " " +
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
}
