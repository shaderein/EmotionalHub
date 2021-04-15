package model;

import org.bson.types.ObjectId;

/** Data interface for objects to be stored in a database.
 *  Doesn't really do much, but helps track all application
 *  data regardless.
 */
public interface Data {

    /** Gets the unique ID of the data object in the database.
     *
     * @return The ID of the object
     */
    ObjectId getId();

    /** Sets the unique ID of the object in the database.
     *
     * @param id The new ID of the object
     */
    void setId(ObjectId id);
}
