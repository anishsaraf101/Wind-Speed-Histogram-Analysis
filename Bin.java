/** Represents an individual bin for the histogram created in AuspurgerData class. This class is for quick information
 * using getters and setters and is used heavily in AuspurgerData
 * @author Anish Saraf
 * @version 1.0
 * @since 2023-12-13
 */
public class Bin {

    private Float interval;
    private float count;
    private float cumProbability;

    /** Creates a Bin object with 3 parameters in the constructor
     * @param interval The endpoint (inclusive) of the given bin's interval.
     * @param count The total count of values in the bin.
     * @param cumProbability the cumulative probability for the specific bin in relation to other bins created for the
     *                       histogram.
     */
    public Bin(float interval, float count, float cumProbability) {
        this.interval = interval;
        this.count = count;
        this.cumProbability = cumProbability;
    }
    /** Sets the bin's interval
     * @param interval a float containing the bin's interval to be set
     */
    public void setInterval(Float interval) {
        this.interval = interval;
    }

    /** Gets the bin's interval
     * @return a float value representing the float endpoint of the bin's interval
     */
    public Float getInterval() {
        return interval;
    }

    /** Sets the bin's count
     * @param count a float containing the total value of things stored in the bin
     */
    public void setCount(float count) {
        this.count = count;
    }

    /** Sets the bin's cumulative probability
     * @param cumProbability a float containing the bin's cumulative probability to be set
     */
    public void setCumProbability(float cumProbability) {
        this.cumProbability = cumProbability;
    }

    /** Gets the bin's count
     * @return a float value representing the total count of the things stored inside the bin
     */
    public float getCount() {
        return count;
    }

    /** Gets the bin's interval
     * @return A float value of the cumulative probability of the bin.
     */
    public Float getCumProbability() {
        return cumProbability;
    }
}
