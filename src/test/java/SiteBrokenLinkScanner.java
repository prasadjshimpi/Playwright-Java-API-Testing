import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class SiteBrokenLinkScanner {

    private static final Set<String> visitedPages = new HashSet<>();
    private static final Map<String, Integer> brokenLinks = new HashMap<>();
    private static final String BASE_DOMAIN = "https://websiterevamp.loginextsolutions.com/"; // Replace with your site

    public static void main(String[] args) {
        crawl(BASE_DOMAIN);

        System.out.println("\nSummary of broken links:");
        brokenLinks.forEach((url, code) -> System.out.println(code + " -> " + url));
    }

    private static void crawl(String url) {
        if (visitedPages.contains(url) || !url.startsWith(BASE_DOMAIN)) return;

        System.out.println("Crawling: " + url);
        visitedPages.add(url);

        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("a[href]");

            for (Element link : links) {
                String linkHref = link.absUrl("href");

                // Skip empty or unsupported links
                if (linkHref.isEmpty() || linkHref.startsWith("mailto:") || linkHref.startsWith("javascript:")) {
                    continue;
                }

                int status = getStatusCode(linkHref);
                if (status >= 400) {
                    brokenLinks.put(linkHref, status);
                    System.out.println("Broken: " + linkHref + " (Status: " + status + ")");
                }

                // Crawl internal links only
                if (linkHref.startsWith(BASE_DOMAIN) && !visitedPages.contains(linkHref)) {
                    crawl(linkHref);  // Recursively crawl
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to fetch: " + url + " - " + e.getMessage());
        }
    }

    private static int getStatusCode(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            return connection.getResponseCode();
        } catch (IOException e) {
            return 500; // Treat unreachable links as 500
        }
    }
}
