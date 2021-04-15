package predictors;

import java.util.Map;

/** Generic predictor which can extract arbitrary
 *  attributes from string input of an arbitrary type.
 *
 * @param <T> Type of text attribute values
 */
public interface Predictor<T> {
    /** Returns a map containing the names of attributes
     *  examined and their values.
     *
     * @param text Text input to analyze
     * @return Map from text attributes to values
     */
    Map<String, T> predict(String text);
}
