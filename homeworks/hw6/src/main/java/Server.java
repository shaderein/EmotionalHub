import com.google.gson.Gson;
import exception.DaoException;
import model.Author;
import model.Book;
import org.sql2o.Sql2o;
import persistence.Sql2oAuthorDao;
import persistence.Sql2oBookDao;
import spark.ModelAndView;
import java.util.HashMap;
import java.util.Map;
import static spark.Spark.*;

import spark.Request;
import spark.template.velocity.VelocityTemplateEngine;

public class Server {
    // set port number
    final static int PORT_NUM = 7000;

    private static int getHerokuAssignedPort() {
        String herokuPort = System.getenv("PORT");
        if (herokuPort != null) {
            return Integer.parseInt(herokuPort);
        }
        return PORT_NUM;
    }

    private static int addAuthor(Request req) {
        String name = req.queryParams("name");
        int numOfBooks = Integer.parseInt(req.queryParams("numOfBooks"));
        String nationality = req.queryParams("nationality");
        Author author = new Author(name, numOfBooks, nationality);
        return new Sql2oAuthorDao().add(author);
    }

    public static void main(String[] args)  {
        port(getHerokuAssignedPort());

        new Sql2oAuthorDao().createTable();
        new Sql2oBookDao().createTable();

        staticFiles.location("/public");

        post("/", (req, res) -> {
            String username = req.queryParams("username");
            res.cookie("username", username);
            res.redirect("/");
            return null;
        });

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            if (req.cookie("username") != null)
                model.put("username", req.cookie("username"));
            res.status(200);
            res.type("text/html");
            return new ModelAndView(model, "public/templates/index.vm");
        }, new VelocityTemplateEngine());

        get("/books", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("books", new Sql2oBookDao().listAll());
            res.status(200);
            res.type("text/html");
            return new ModelAndView(model, "public/templates/books.vm");
        }, new VelocityTemplateEngine());

        get("/authors", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("authors", new Sql2oAuthorDao().listAll());
            res.status(200);
            res.type("text/html");
            return new ModelAndView(model, "public/templates/authors.vm");
        }, new VelocityTemplateEngine());

        post("/authors", (req, res) -> {
            int id = addAuthor(req);
            if (id > 0) {
                res.status(200);
            } else {
                res.status(201);
            }
            res.redirect("/authors");
            return null;
        });

        get("/addauthor", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            res.status(200);
            res.type("text/html");
            return new ModelAndView(model, "public/templates/addauthor.vm");
        }, new VelocityTemplateEngine());

        post("/addauthor", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int id = addAuthor(req);
            if (id > 0) {
                model.put("added", "true");
            } else {
                model.put("failedAdd", "true");
            }

            res.status(201);
            res.type("text/html");
            ModelAndView mdl = new ModelAndView(model, "public/templates/addauthor.vm");
            return new VelocityTemplateEngine().render(mdl);
        });

        post("/delauthor", (req, res) -> {
            String name = req.queryParams("name");
            Author a = new Author(name, 0, "");
            new Sql2oAuthorDao().delete(a);
            res.status(200);
            res.type("application/json");
            return new Gson().toJson(a.toString());
        });

        get("/addbook", (req, res) -> {
            if (req.cookie("username") == null)
                res.redirect("/");
            Map<String, Object> model = new HashMap<>();
            res.status(200);
            res.type("text/html");
            return new ModelAndView(model, "public/templates/addbook.vm");
        }, new VelocityTemplateEngine());

        //Make a request to add book to the database
        post("/addbook", (req, res) -> {
            if (req.cookie("username") == null)
                res.redirect("/");
            Map<String, Object> model = new HashMap<>();
            String title = req.queryParams("title");
            String isbn = req.queryParams("isbn");
            String publisher = req.queryParams("publisher");
            int year = Integer.parseInt(req.queryParams("year"));

            String name = req.queryParams("name");
            try {
                int authorId = new Sql2oAuthorDao().checkAuthorExists(name);
                if (authorId == -1) {
                    // Insert the author to Database
                    int numOfBooks = Integer.parseInt(req.queryParams("numOfBooks"));
                    String nationality = req.queryParams("nationality");
                    Author author = new Author(name, numOfBooks, nationality);
                    authorId = new Sql2oAuthorDao().add(author);
                    if (authorId > 0) {
                        model.put("added", "true");
                    } else {
                        throw new DaoException();
                    }
                }
                Book book = new Book(title, isbn, publisher, year, authorId);
                int bookId = new Sql2oBookDao().add(book);
                if (bookId > 0) {
                    model.put("added", "true");
                } else {
                    model.put("failedAdd", "true");
                }
            } catch (DaoException ex) {
                model.put("failedAdd", "true");
            }

            res.status(201);
            res.type("text/html");
            ModelAndView mdl = new ModelAndView(model, "public/templates/addbook.vm");
            return new VelocityTemplateEngine().render(mdl);
        });

        post("/delbook", (req, res) -> {
            String isbn = req.queryParams("isbn");
            Book b = new Book("", isbn, "", 0, 0);
            new Sql2oBookDao().delete(b);
            res.status(200);
            res.type("application/json");
            return new Gson().toJson(b.toString());
        });
    }
}
