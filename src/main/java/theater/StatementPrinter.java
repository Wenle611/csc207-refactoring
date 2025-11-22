package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {

    /** Play type constant for comedy. */
    private static final String PLAY_TYPE_COMEDY = "comedy";

    /** Play type constant for tragedy. */
    private static final String PLAY_TYPE_TRAGEDY = "tragedy";

    private final Invoice invoice;
    private final Map<String, Play> plays;

    /**
     * Constructs a StatementPrinter.
     *
     * @param invoice invoice containing performances
     * @param plays   map of playID to Play
     */
    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement for the invoice.
     *
     * @return formatted statement string.
     * @throws RuntimeException if an unknown play type appears.
     */
    public String statement() {

        int totalAmount = 0;
        int volumeCredits = 0;

        final StringBuilder result =
                new StringBuilder("Statement for " + invoice.getCustomer()
                        + System.lineSeparator());

        final NumberFormat frmt =
                NumberFormat.getCurrencyInstance(Locale.US);

        for (Performance perf : invoice.getPerformances()) {

            final Play play = plays.get(perf.getPlayID());
            final int thisAmount = calculateAmount(play, perf);

            // Volume credits
            volumeCredits += Math.max(
                    perf.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);

            if (PLAY_TYPE_COMEDY.equals(play.getType())) {
                volumeCredits += perf.getAudience()
                        / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
            }

            result.append(String.format(
                    "  %s: %s (%s seats)%n",
                    play.getName(),
                    frmt.format(thisAmount / Constants.PERCENT_FACTOR),
                    perf.getAudience()
            ));

            totalAmount += thisAmount;
        }

        result.append(String.format(
                "Amount owed is %s%n",
                frmt.format(totalAmount / Constants.PERCENT_FACTOR)));

        result.append(String.format(
                "You earned %s credits%n",
                volumeCredits));

        return result.toString();
    }

    /**
     * Helper required by MarkUs.
     *
     * @param play play object
     * @param perf performance
     * @return calculated amount
     */
    public int getAmount(Play play, Performance perf) {
        return calculateAmount(play, perf);
    }

    /**
     * Helper required by MarkUs.
     *
     * @param playID play id
     * @return play
     */
    public Play getPlay(String playID) {
        return plays.get(playID);
    }

    /**
     * Helper required by MarkUs.
     *
     * @param play play
     * @param perf performance
     * @return volume credits
     */
    public int getVolumeCredits(Play play, Performance perf) {
        int result = Math.max(
                perf.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD,
                0);

        if (PLAY_TYPE_COMEDY.equals(play.getType())) {
            result += perf.getAudience()
                    / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        return result;
    }

    /**
     * Helper required by MarkUs.
     *
     * @param amount integer amount
     * @return formatted USD string
     */
    public String usd(int amount) {
        return NumberFormat.getCurrencyInstance(Locale.US)
                .format(amount / (double) Constants.PERCENT_FACTOR);
    }

    /**
     * Helper required by MarkUs.
     *
     * @return total amount
     */
    public int getTotalAmount() {
        int total = 0;
        for (Performance perf : invoice.getPerformances()) {
            final Play play = plays.get(perf.getPlayID());
            total += calculateAmount(play, perf);
        }
        return total;
    }

    /**
     * Helper required by MarkUs.
     *
     * @return total volume credits
     */
    public int getTotalVolumeCredits() {
        int total = 0;
        for (Performance perf : invoice.getPerformances()) {
            final Play play = plays.get(perf.getPlayID());
            total += getVolumeCredits(play, perf);
        }
        return total;
    }

    /**
     * Shared internal logic for calculating the price of a performance.
     *
     * @param play play object
     * @param perf performance
     * @return calculated amount
     * @throws RuntimeException if a play has an unknown type.
     */
    private int calculateAmount(Play play, Performance perf) {

        int amount;
        switch (play.getType()) {
            case PLAY_TYPE_TRAGEDY:
                amount = Constants.TRAGEDY_BASE_AMOUNT;
                if (perf.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    amount += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (perf.getAudience()
                            - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;

            case PLAY_TYPE_COMEDY:
                amount = Constants.COMEDY_BASE_AMOUNT;
                if (perf.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    amount += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (perf.getAudience()
                            - Constants.COMEDY_AUDIENCE_THRESHOLD);
                }
                amount += Constants.COMEDY_AMOUNT_PER_AUDIENCE
                        * perf.getAudience();
                break;

            default:
                throw new RuntimeException(
                        "unknown type: " + play.getType());
        }
        return amount;
    }
}
