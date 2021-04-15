package persistence;

import com.mongodb.MongoClient;
import model.Data;

/** Data Access Object using a MongoDB database.
 *  Method signature may not be used, but ensures the use
 *  of a MongoDB database.
 *
 * @param <T> Java object type to connect to the database
 */
public interface DaoMongo<T extends Data> extends DataAccessObject<T> {
    /** Returns client connection used by the data access object
     *
     * @return Client connection
     */
    MongoClient getClient();
}
