import exception.DaoException;
import model.Author;
import model.Book;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import persistence.BookDao;
import persistence.Sql2oAuthorDao;
import persistence.Sql2oBookDao;

import java.sql.*;
import java.util.List;

import static org.junit.Assert.*;

public class DBDaoCRUDTest {
    private static Sql2o sql2o;
    private static Sql2oAuthorDao sql2oAuthorDao;
    private static Sql2oBookDao sql2oBookDao;
    private static Author author1;
    private static Author author2;
    private static Book book1;
    private static Book book2;

    @BeforeClass
    public static void beforeClassTests() {
        author1 = new Author("J.K. Rowling", 20, "British");
        author2 = new Author("George Orwell", 15, "British");
        book1 = new Book("1984", "9780451524935", "Secker & Warburg", 1949, author1);
        book2 = new Book("Harry Potter and the Sorcerer's Stone", "9780590353427",
                "Scholastic Corporation", 1997, author2);

        final String URI = "jdbc:sqlite:./MyBooksApp.db";
        final String USERNAME = "";
        final String PASSWORD = "";
        sql2o = new Sql2o(URI, USERNAME, PASSWORD);

        sql2oAuthorDao = new Sql2oAuthorDao(sql2o);
        sql2oBookDao = new Sql2oBookDao(sql2o);
    }

    @Before
    public void beforeEachTest() {
        String sql_drop_authors = "DROP TABLE IF EXISTS Authors";
        String sql_drop_books = "DROP TABLE IF EXISTS Books";

        String sql1 = "CREATE TABLE IF NOT EXISTS Authors (id INTEGER PRIMARY KEY, name VARCHAR(100) NOT NULL UNIQUE," +
                " numOfBooks INTEGER, nationality VARCHAR(30));";

        String sql2 = "CREATE TABLE IF NOT EXISTS Books (id INTEGER PRIMARY KEY, title VARCHAR(200) NOT NULL, " +
                "isbn VARCHAR(14) NOT NULL UNIQUE, publisher VARCHAR(14), year INTEGER, " +
                "authorId INTEGER NOT NULL, FOREIGN KEY(authorId) REFERENCES Authors(id) " +
                "ON UPDATE CASCADE ON DELETE CASCADE);";

        try (Connection con = sql2o.open()) {
            con.createQuery(sql_drop_authors).executeUpdate();
            con.createQuery(sql_drop_books).executeUpdate();
            con.createQuery(sql1).executeUpdate();
            con.createQuery(sql2).executeUpdate();
        }
    }

    @Test
    public void testAddAuthor() {
        assertTrue(sql2oAuthorDao.add(author1) >= 0);
        assertTrue(sql2oAuthorDao.add(author2) >= 0);
    }

    @Test
    public void testListAllAuthors() {
        List<Author> list = sql2oAuthorDao.listAll();
        assert(list.size() == 0);
        sql2oAuthorDao.add(author1);
        list = sql2oAuthorDao.listAll();
        assert(list.size() == 1);
    }

    @Test
    public void testDeleteAuthor() {
        sql2oAuthorDao.add(author1);
        assertTrue(sql2oAuthorDao.delete(author1));
        sql2oAuthorDao.add(author2);
        assertTrue(sql2oAuthorDao.delete(author2));
        assertFalse(sql2oAuthorDao.delete(author2));
    }

    @Test
    public void testUpdateAuthor() {
        sql2oAuthorDao.add(author1);
        Author author1_updated = author1;
        author1_updated.setNumOfBooks(21);
        assertTrue(sql2oAuthorDao.update(author1_updated));
        Author ret = sql2oAuthorDao.listAll().get(0);
        assertEquals(ret.getNumOfBooks(), 21);
        assertFalse(sql2oAuthorDao.update(author2));
    }

    @Test
    public void testAddBook() {
        assertTrue(sql2oBookDao.add(book1) >= 0);
        assertTrue(sql2oBookDao.add(book2) >= 0);
    }

    @Test
    public void testListAllBooks() {
        sql2oAuthorDao.add(author1);
        sql2oAuthorDao.add(author2);
        List<Book> list = sql2oBookDao.listAll();
        assert(list.size() == 0);
        sql2oBookDao.add(book1);
        list = sql2oBookDao.listAll();
        assert(list.size() == 1);
    }

    @Test
    public void testDeleteBook() {
        sql2oBookDao.add(book1);
        assertTrue(sql2oBookDao.delete(book1));
        sql2oBookDao.add(book2);
        assertTrue(sql2oBookDao.delete(book2));
        assertFalse(sql2oBookDao.delete(book2));
    }

    @Test
    public void testUpdateBook() {
        sql2oAuthorDao.add(author1);
        sql2oBookDao.add(book1);
        Book book1_updated = book1;
        book1_updated.setTitle("1985");
        assertTrue(sql2oBookDao.update(book1_updated));
        assertEquals(sql2oBookDao.listAll().get(0).getTitle(), "1985");
        assertFalse(sql2oBookDao.update(book2));
    }

    @Test
    public void testDeleteAuthorCascade() {
        sql2oAuthorDao.add(author1);
        sql2oAuthorDao.add(author2);
        sql2oBookDao.add(book1);
        sql2oBookDao.add(book2);
        assertEquals(author1.getId(), book1.getAuthor().getId());
        sql2oAuthorDao.delete(author1);
        assertEquals(sql2oAuthorDao.listAll().size(), 1);
        assertEquals(sql2oBookDao.listAll().size(), 1);
    }

    @Test (expected = DaoException.class)
    public void testBookOpenDBException() {
        Sql2o sql2o_fake = new Sql2o("", "", "");
        Sql2oBookDao dao_fake = new Sql2oBookDao(sql2o_fake);
        dao_fake.add(book1);
    }

    @Test (expected = DaoException.class)
    public void testAuthorOpenDBException() {
        Sql2o sql2o_fake = new Sql2o("", "", "");
        Sql2oAuthorDao dao_fake = new Sql2oAuthorDao(sql2o_fake);
        dao_fake.add(author1);
    }

}
