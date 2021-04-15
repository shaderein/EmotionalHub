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
import spark.template.velocity.VelocityTemplateEngine;

public class Server {
//Connect to the database
    private static Sql2o getSql2o() {
        final String URI = "jdbc:sqlite:./MyBooksApp.db";
        final String USERNAME = "";
        final String PASSWORD = "";
        return new Sql2o(URI, USERNAME, PASSWORD);
    }

    public static void main(String[] args)  {
        // set port number
        final int PORT_NUM = 7000;
        port(PORT_NUM);

        Sql2o sql2o = getSql2o();
//log in
        post("/", (req, res) -> {
            String username = req.queryParams("username");
            res.cookie("username", username);
            String color = req.queryParams("color");
            res.cookie("color", color);

            res.redirect("/");
            return null;
        });
//pick a color or log in
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            if (req.cookie("username") != null)
                model.put("username", req.cookie("username"));
            if (req.cookie("color") != null)
                model.put("color", req.cookie("color"));

            res.status(200);
            res.type("text/html");
            return new ModelAndView(model, "public/templates/index.vm");
        }, new VelocityTemplateEngine());
//Lists all the authors in the database
        get("/authors", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("authors", new Sql2oAuthorDao(sql2o).listAll());
            res.status(200);
            res.type("text/html");
            return new ModelAndView(model, "public/templates/authors.vm");
        }, new VelocityTemplateEngine());
//A page for you to add the author 
        get("/addauthor", (req, res) -> {
            if (req.cookie("username") == null)
                res.redirect("/");
            Map<String, Object> model = new HashMap<>();
            res.status(200);
            res.type("text/html");
            return new ModelAndView(model, "public/templates/addauthor.vm");
        }, new VelocityTemplateEngine());
//Make a request to add an author to the database
        post("/addauthor", (req, res) -> {
            if (req.cookie("username") == null)
                res.redirect("/");
            Map<String, Object> model = new HashMap<>();
            String name = req.queryParams("name");
            int numOfBooks = Integer.parseInt(req.queryParams("numOfBooks"));
            String nationality = req.queryParams("nationality");
            Author author = new Author(name, numOfBooks, nationality);
            try {
                int id = new Sql2oAuthorDao(sql2o).add(author);
                if (id > 0) {
                    model.put("added", "true");
                }
                else {
                    model.put("failedAdd", "true");
                }
            }
            catch (DaoException ex) {
                model.put("failedAdd", "true");
            }
            res.status(201);
            res.type("text/html");
            ModelAndView mdl = new ModelAndView(model, "public/templates/addauthor.vm");
            return new VelocityTemplateEngine().render(mdl);
        });
//Display the books
        get("/books", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("books", new Sql2oBookDao(sql2o).listAll());
            res.status(200);
            res.type("text/html");
            return new ModelAndView(model, "public/templates/books.vm");
        }, new VelocityTemplateEngine());
//Display the add book page
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
                int authorId = new Sql2oAuthorDao(sql2o).checkAuthorExists(name);
                if (authorId == -1) {
                    // Insert the author to Database
                    int numOfBooks = Integer.parseInt(req.queryParams("numOfBooks"));
                    String nationality = req.queryParams("nationality");
                    Author author = new Author(name, numOfBooks, nationality);
                    authorId = new Sql2oAuthorDao(sql2o).add(author);
                    if (authorId > 0) {
                        model.put("added", "true");
                    } else {
                        throw new DaoException();
                    }
                }
                Book book = new Book(title, isbn, publisher, year, authorId);
                int bookId = new Sql2oBookDao(sql2o).add(book);
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
    }
}
