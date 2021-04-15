package persistence;

import exception.DaoException;
import model.Author;
import java.util.List;
import org.sql2o.*;

public class Sql2oAuthorDao implements AuthorDao {

    private final Sql2o sql2o;

    public Sql2oAuthorDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }


    @Override
    public int add(Author author) throws DaoException {
        try (Connection con = sql2o.open()) {
            String query = "INSERT INTO Authors (name, numOfBooks, nationality)" +
                    "VALUES (:name, :numOfBooks, :nationality)";
            int id = (int) con.createQuery(query, true)
                    .bind(author)
                    .executeUpdate().getKey();
            author.setId(id);
            return id;
        }
        catch (Sql2oException ex) {
            throw new DaoException();
        }
    }

    @Override
    public List<Author> listAll() {
        String sql = "SELECT * FROM Authors";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql).executeAndFetch(Author.class);
        }
        catch (Sql2oException ex) {
            throw new DaoException();
        }
    }

    // TODO: Add "delete" method from hw2 here; feel free to add more methods
    @Override
    public boolean delete(Author author) throws DaoException {
        return deleteByName(author.getName());
    }

    public boolean deleteByName(String name) throws DaoException {
        String sql = "DELETE FROM Authors WHERE name='" + name + "'";
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
    public boolean update(Author author) {
        String up = "UPDATE Authors SET " +
                "numOfBooks = :numOfBooks, " +
                "nationality = :nationality " +
                "WHERE name = :name";
        try (Connection con = sql2o.open()) {
            con.createQuery("PRAGMA foreign_keys = ON").executeUpdate();
            int changes = con.createQuery(up)
                    .bind(author)
                    .executeUpdate().getResult();
            return changes > 0;
        }
        catch (Sql2oException e) {
            throw new DaoException();
        }
    }

    public void dropCreateTable() {
        String sql_drop_authors = "DROP TABLE IF EXISTS Authors";

        String sql = "CREATE TABLE IF NOT EXISTS Authors (id INTEGER PRIMARY KEY, name VARCHAR(100) NOT NULL UNIQUE," +
                " numOfBooks INTEGER, nationality VARCHAR(30));";

        try (Connection con = sql2o.open()) {
            con.createQuery(sql_drop_authors).executeUpdate();
            con.createQuery(sql).executeUpdate();
        }
    }
}
