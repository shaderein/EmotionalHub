package persistence;

import com.mongodb.BasicDBObject;
import model.Data;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;

/** Generic interface for manipulating Java objects in a database.
 *
 * @param <T> Java object type to connect to the database
 */
public interface DataAccessObject<T extends Data> {
    /** Adds an object to the database.
     *
     * @param t Object to add to database
     * @return Unique ID of object added
     */
    ObjectId add(T t);

    /** Retrieves the object corresponding to the given unique ID.
     *
     * @param id Unique ID of the object in the database
     * @return The corresponding object
     */
    T get(ObjectId id);

    /** Lists all objects matching the given query.
     *
     * @param filter Bson filter representing the query
     * @return List of objects matching the filter
     */
    List<T> query(Bson filter);

    /** Lists all objects currently in the database.
     *
     * @return List of all members of the database as Java objects
     */
    List<T> listAll();

    /** Updates an object in the database.
     *
     * @param t Object in the database to update based on ID
     * @return Whether the object was successfully updated
     */
    boolean update(T t);

    /** Deletes an object from the database.
     *
     * @param t Object in the database to delete based on ID
     * @return Whether the object was successfully deleted
     */
    boolean delete(T t);

    /** Deletes every object in the database.
     *
     * @return Whether all objects were successfully deleted
     */
    boolean deleteAllDocuments();
}
