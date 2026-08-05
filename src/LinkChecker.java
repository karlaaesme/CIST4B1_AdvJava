import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Runs with no user interaction — meant to be triggered on a schedule (GitHub Actions).
 * It never changes Status itself; it only sets Flagged/FlagNote so a staff member
 * can quickly review and decide.
 *
 * Usage: java LinkChecker [path-to-data.csv]
 */
public class LinkChecker {

    // phrases that commonly show up on a closed/expired application page
    private static final String[] CLOSED_SIGNALS = {
            "no longer accepting", "applications are closed", "applications closed",
            "position has been filled", "no longer available", "opportunity has expired",
            "deadline has passed", "registration is closed", "registration closed"
    };

    public static void main(String[] args) throws Exception {
        String path = args.length > 0 ? args[0] : "docs/data.csv";

        OpportunityManager manager = new OpportunityManager();
        manager.loadFile(path);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        int flaggedCount = 0;
        for (Opportunity opp : manager.getAll()) {
            if ("Close".equalsIgnoreCase(opp.getStatus())) {
                continue; // already marked closed, nothing to check
            }

            List<String> reasons = new ArrayList<>();

            if (opp.getDeadline().isBefore(LocalDate.now())) {
                reasons.add("Due date has passed but status is still \"" + opp.getStatus() + "\".");
            } else {
                reasons.addAll(checkLink(client, opp.getLink()));
            }

            if (!reasons.isEmpty()) {
                opp.setFlag(true, String.join(" ", reasons));
                flaggedCount++;
                System.out.println("Flagged: " + opp.getName() + " — " + opp.getFlagNote());
            }
        }

        manager.saveFile(path);
        System.out.println("Check complete. " + flaggedCount + " opportunity(ies) flagged for review.");
    }

    private static List<String> checkLink(HttpClient client, String link) {
        List<String> reasons = new ArrayList<>();

        if (link == null || !(link.startsWith("http://") || link.startsWith("https://"))) {
            reasons.add("Link doesn't look like a valid URL — please verify.");
            return reasons;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(link))
                    .timeout(Duration.ofSeconds(8))
                    .header("User-Agent", "OpTrack-LinkChecker/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                reasons.add("Link returned HTTP " + response.statusCode() + " — may be broken or removed.");
                return reasons;
            }

            String bodyLower = response.body() == null ? "" : response.body().toLowerCase(Locale.ROOT);
            for (String signal : CLOSED_SIGNALS) {
                if (bodyLower.contains(signal)) {
                    reasons.add("Page text suggests this may be closed (found phrase: \"" + signal + "\").");
                    break;
                }
            }
        } catch (Exception e) {
            reasons.add("Could not reach the link — may be broken. (" + e.getClass().getSimpleName() + ")");
        }

        return reasons;
    }
}
