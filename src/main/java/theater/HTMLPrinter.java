package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Prints HTML formatted statements.
 */
public class HTMLPrinter {

    private final Invoice invoice;
    private final Map<String, Play> plays;

    /**
     * Creates an HTMLPrinter using provided invoice and plays.
     *
     * @param invoice invoice containing performances
     * @param plays   map of playID to Play object
     */
    public HTMLPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns an HTML formatted statement of the invoice.
     *
     * @return HTML formatted statement
     */
    public String htmlStatement() {

        final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);
        final StringBuilder result = new StringBuilder();

        result.append("<html>\n");
        result.append("<h1>Statement for ")
                .append(invoice.getCustomer())
                .append("</h1>\n");
        result.append("<ul>\n");

        int totalAmount = 0;
        int volumeCredits = 0;

        for (final Performance perf : invoice.getPerformances()) {

            // use getter
            final Play play = plays.get(perf.getPlayID());

            final int thisAmount = calculateAmount(play, perf);
            totalAmount += thisAmount;

            result.append("<li>")
                    .append(play.getName())
                    .append(": ")
                    .append(frmt.format((double) thisAmount / Constants.PERCENT_FACTOR))
                    .append(" (")
                    .append(perf.getAudience())
                    .append(" seats)</li>\n");

            volumeCredits += Math.max(
                    perf.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0
            );

            if ("comedy".equals(play.getType())) {
                volumeCredits += perf.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
            }
        }

        result.append("</ul>\n");
        result.append("<p>Amount owed is ")
                .append(frmt.format((double) totalAmount / Constants.PERCENT_FACTOR))
                .append("</p>\n");
        result.append("<p>You earned ")
                .append(volumeCredits)
                .append(" credits</p>\n");
        result.append("</html>");

        return result.toString();
    }

    private int calculateAmount(Play play, Performance perf) {
        int amount;

        switch (play.getType()) {
            case "tragedy":
                amount = Constants.TRAGEDY_BASE_AMOUNT;
                if (perf.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    amount += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (perf.getAudience() - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;

            case "comedy":
                amount = Constants.COMEDY_BASE_AMOUNT;
                if (perf.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    amount += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (perf.getAudience() - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                amount += Constants.COMEDY_AMOUNT_PER_AUDIENCE * perf.getAudience();
                break;

            default:
                throw new RuntimeException("Unknown play type: " + play.getType());
        }
        return amount;
    }
}
