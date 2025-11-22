package theater;

/**
 * Represents a play.
 *
 * @null required by Checkstyle
 */
public class Play {
    private final String name;
    private final String type;

    public Play(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
