package persistence;

import exception.DaoException;
import model.Author;

import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Sql2oAuthorDao implements AuthorDao {

    private static Connection getConnection() throws URISyntaxException, SQLException {
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
        try (Connection conn = getConnection()) {
            String sql;
            if ("SQLite".equalsIgnoreCase(conn.getMetaData().getDatabaseProductName())) {
                sql = "CREATE TABLE IF NOT EXISTS Authors (id INTEGER PRIMARY KEY, name VARCHAR(100) NOT NULL UNIQUE," +
                        " numOfBooks INTEGER, nationality VARCHAR(30));";
            } else {
                sql = "CREATE TABLE IF NOT EXISTS Authors (id serial PRIMARY KEY, name VARCHAR(100) NOT NULL UNIQUE," +
                        " numOfBooks INTEGER, nationality VARCHAR(30));";
            }

            Statement st = conn.createStatement();
            st.execute("DROP TABLE IF EXISTS Authors;");
            st = conn.createStatement();
            st.execute(sql);
        } catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Add an author with the given name exists.
     * @param name The queried author's name.
     * @return ture if add is successful
     * @throws URISyntaxException or SQLException
     */
    @Override
    public int add(Author author) {
        String sql = String.format("INSERT INTO Authors(name, numOfBooks, nationality) VALUES ('%s', %d, '%s')",
                author.getName(), author.getNumOfBooks(), author.getNationality());
        try (Connection conn = getConnection()) {
            PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //Statement st = conn.createStatement();
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);

            author.setId(id);
            return id;
        }
        catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * List author name, number of books published and nationality
     * @param name The queried author's name.
     * @return A List of informaiton
     * @throws URISyntaxException or SQLException
     */
    @Override
    public List<Author> listAll() {
        String sql = "SELECT * FROM Authors";
        List<Author> lstAuthors = new ArrayList<>();
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lstAuthors.add(new Author(rs.getString("name"),
                        rs.getInt("numOfBooks"), rs.getString("nationality")));
            }
            return lstAuthors;
        }
        catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Update author name 
     * @param name - The queried author's name.
     * @return ture if update author name is successful
     * @throws URISyntaxException or SQLException
     */
    @Override
    public boolean update(Author author) {
        String sql = String.format("Update Authors SET numOfBooks = %d, nationality = '%s' WHERE name = '%s'",
                author.getNumOfBooks(), author.getNationality(), author.getName());
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
            return true;
        }
        catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete an author with the given name exists.
     * @param name The queried author's name.
     * @return ture if delete is successful
     * @throws URISyntaxException or SQLException
     */
    @Override
    public boolean delete(Author author) {
        String sql = String.format("DELETE FROM Authors WHERE name = '%s'", author.getName());
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
            return true;
        }
        catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks whether an author with the given name exists.
     * @param name The queried author's name.
     * @return -1 if the author doesn't exist; the author's id if it exists
     * @throws DaoException
     */
    public int checkAuthorExists(String name) throws DaoException{
        String sql = "SELECT id FROM Authors WHERE name = '" + name + "'";
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            List<Integer> idList = new ArrayList<>();
            while (rs.next()) {
                idList.add(rs.getInt(1));
            }
            if (idList.size() == 0) {
                return -1;
            } else {
                return idList.get(0);
            }
        } catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
            throw new DaoException();
        }
    }
}
