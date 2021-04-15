package persistence;

import exception.DaoException;
import model.Author;
import model.Book;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Sql2oBookDao implements BookDao {

    private static java.sql.Connection getConnection() throws URISyntaxException, SQLException {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null) {
            // Not on Heroku, so use SQLite
            return DriverManager.getConnection("jdbc:sqlite:./MyBooksApp.db");
        }

        URI dbUri = new URI(databaseUrl);

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':'
                + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

        return DriverManager.getConnection(dbUrl, username, password);
    }

    public void createTable() {
        try (java.sql.Connection conn = getConnection()) {
            String sql;
            if ("SQLite".equalsIgnoreCase(conn.getMetaData().getDatabaseProductName())) {
                sql = "CREATE TABLE IF NOT EXISTS Books (id INTEGER PRIMARY KEY, title VARCHAR(100) NOT NULL UNIQUE," +
                        " isbn VARCHAR(30) NOT NULL UNIQUE, publisher VARCHAR(30), " +
                        "year INTEGER NOT NULL, authorid INTEGER NOT NULL);";
            } else {
                sql = "CREATE TABLE IF NOT EXISTS Books (id serial PRIMARY KEY, title VARCHAR(100) NOT NULL UNIQUE," +
                        " isbn VARCHAR(30) NOT NULL UNIQUE, publisher VARCHAR(30), " +
                        "year INTEGER NOT NULL, authorid INTEGER NOT NULL);";
            }

            Statement st = conn.createStatement();
            st.execute("DROP TABLE IF EXISTS Books;");
            st = conn.createStatement();
            st.execute(sql);
        } catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
        }
    }

    public int add(Book book) throws DaoException {
        String sql = String.format("INSERT INTO Books(title, isbn, publisher, year, authorid) " +
                        "VALUES ('%s', %s, '%s', '%d', '%d')",
                book.getTitle(), book.getIsbn(), book.getPublisher(), book.getYear(), book.getAuthorId());
        try (java.sql.Connection conn = getConnection()) {
            PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //Statement st = conn.createStatement();
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);

            book.setId(id);
            return id;
        }
        catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public List<Book> listAll() throws DaoException {
        String sql = "SELECT * FROM Books";
        List<Book> lstBooks = new ArrayList<>();
        try (java.sql.Connection conn = getConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lstBooks.add(new Book(rs.getString("title"),
                        rs.getString("isbn"), rs.getString("publisher"),
                        rs.getInt("year"), rs.getInt("authorid")));
            }
            return lstBooks;
        }
        catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Book book) throws DaoException {
        String sql = String.format("Update Books SET title = '%s', publisher = '%s', " +
                        "year = '%d', authorid = '%d' " +
                        " WHERE isbn = '%s'",
                book.getTitle(), book.getPublisher(), book.getYear(),
                book.getAuthorId(), book.getIsbn());
        try (java.sql.Connection conn = getConnection()) {
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
            return true;
        }
        catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Book book) {
        String sql = String.format("DELETE FROM Books WHERE isbn = '%s'", book.getIsbn());
        try (java.sql.Connection conn = getConnection()) {
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
            return true;
        }
        catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
