import okhttp3.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import persistence.Sql2oAuthorDao;
import persistence.Sql2oBookDao;

import java.io.IOException;
import java.sql.*;
import static org.junit.Assert.*;

public class RESTAPITest {
    private static Sql2o sql2o;
    private static Sql2oAuthorDao sql2oAuthorDao;
    private static Sql2oBookDao sql2oBookDao;
    static OkHttpClient client;

    @BeforeClass
    public static void beforeClassTests() throws SQLException {
        client = new OkHttpClient();

        final String URI = "jdbc:sqlite:./MyBooksApp.db";
        final String USERNAME = "";
        final String PASSWORD = "";
        sql2o = new Sql2o(URI, USERNAME, PASSWORD);

        sql2oAuthorDao = new Sql2oAuthorDao(sql2o);
        sql2oBookDao = new Sql2oBookDao(sql2o);
    }

    @Before
    public void beforeEachTest() {
        sql2oAuthorDao.dropCreateTable();
        sql2oBookDao.dropCreateTable();
    }

    @Test
    public void testListAuthors() throws IOException {
        addAuthor("author1", "10", "American");
        Request request = new Request.Builder()
                .url("http://localhost:7000/authors")
                .build();
        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
        System.out.println(response.body().string());
    }

    @Test
    public void testAddAuthor() throws IOException {
        RequestBody postBody = new FormBody.Builder()
                .add("name", "Sadegh Hedayat")
                .add("numOfBooks", "26")
                .add("nationality", "Iranian")
                .build();
        Request request = new Request.Builder()
                .url("http://localhost:7000/addauthor")
                .post(postBody)
                .build();
        Response response = client.newCall(request).execute();
        assertEquals(201, response.code());
        assertEquals(1, sql2oAuthorDao.listAll().size());
    }

    @Test
    public void testListBooks() throws IOException {
        addAuthor("author1", "10", "American");
        addBook("book1", "123456", "publisher1",
                "2021","1");
        Request request = new Request.Builder()
                .url("http://localhost:7000/books")
                .build();
        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
        System.out.println(response.body().string());
    }

    @Test
    public void testAddBook() throws IOException {
        addAuthor("author1", "10", "American");
        Response response = addBook("book1", "123456", "publisher1",
                "2021","1");
        assertEquals(201, response.code());
        assertEquals(1, sql2oBookDao.listAll().size());
    }

    @Test
    public void testDelAuthor() throws IOException {
        addAuthor("author11", "11", "Slovenian");
        assertEquals(1, sql2oAuthorDao.listAll().size());
        RequestBody delbody = new FormBody.Builder()
                .add("name", "author11")
                .build();
        Request delreq = new Request.Builder()
                .url("http://localhost:7000/delauthor")
                .post(delbody)
                .build();
        Response response = client.newCall(delreq).execute();
        assertEquals(200, response.code());
        assertEquals(0, sql2oAuthorDao.listAll().size());
    }

    @Test
    public void testDelBook() throws IOException {
        addAuthor("author11", "11", "Slovenian");
        int id = sql2oAuthorDao.listAll().get(0).getId();
        addBook("book1", "123456", "publisher1",
                "2021", String.valueOf(id));
        assertEquals(1, sql2oBookDao.listAll().size());
        RequestBody postBody = new FormBody.Builder()
                .add("isbn", "123456")
                .build();
        Request request = new Request.Builder()
                .url("http://localhost:7000/delbook")
                .post(postBody)
                .build();
        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals(0, sql2oBookDao.listAll().size());
    }

    @Test
    public void testDeleteAuthorCascade() throws IOException {
        addAuthor("author11", "11", "Slovenian");
        int id = sql2oAuthorDao.listAll().get(0).getId();
        addBook("book1", "123456", "publisher1",
                "2021", String.valueOf(id));
        assertEquals(1, sql2oAuthorDao.listAll().size());
        assertEquals(1, sql2oBookDao.listAll().size());
        RequestBody delbody = new FormBody.Builder()
                .add("name", "author11")
                .build();
        Request delreq = new Request.Builder()
                .url("http://localhost:7000/delauthor")
                .post(delbody)
                .build();
        Response response = client.newCall(delreq).execute();
        assertEquals(200, response.code());
        assertEquals(0, sql2oAuthorDao.listAll().size());
        assertEquals(0, sql2oBookDao.listAll().size());
    }

    private Response addAuthor(String name, String numOfBooks, String nationality)
            throws IOException {
        RequestBody postBody = new FormBody.Builder()
                .add("name", name)
                .add("numOfBooks", numOfBooks)
                .add("nationality", nationality)
                .build();
        Request request = new Request.Builder()
                .url("http://localhost:7000/addauthor")
                .post(postBody)
                .build();
        return client.newCall(request).execute();
    }

    private Response addBook(String title, String isbn, String publisher, String year, String authorId)
            throws IOException {
        RequestBody postBody = new FormBody.Builder()
                .add("title", title)
                .add("isbn", isbn)
                .add("publisher", publisher)
                .add("year", year)
                .add("authorId", authorId)
                .build();
        Request request = new Request.Builder()
                .url("http://localhost:7000/addbook")
                .post(postBody)
                .build();
        return client.newCall(request).execute();
    }
}