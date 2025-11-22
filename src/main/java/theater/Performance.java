package theater;

/**
 * Represents a performance entry.
 *
 * @null required by Checkstyle
 */
public class Performance {
    private final String playID;
    private final int audience;

    public Performance(String playID, int audience) {
        this.playID = playID;
        this.audience = audience;
    }

    public String getPlayID() {
        return playID;
    }

    public int getAudience() {
        return audience;
    }
}
