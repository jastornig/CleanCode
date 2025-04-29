package paulxyh.util;

import paulxyh.exception.IncorrectInputException;
import paulxyh.util.logger.Logger;
import paulxyh.util.url.URLConnectionWrapper;
import paulxyh.util.url.URLConnectionWrapperImpl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkUtils {
    private static List<String> allowedDomains;
    private static final Map<String, Boolean> checkedUrls = new HashMap<>();
    private static URLConnectionWrapper urlConnectionWrapper = new URLConnectionWrapperImpl();

    public static void setAllowedDomains(List<String> domains) {
        allowedDomains = domains;
    }

    public static boolean isLinkValid(String link) {
        try {
            Logger.info("Checking link: " + link);
            if (isLinkAlreadyChecked(link)) {
                Logger.warn("Link was already checked! Stepping over!");
                return checkedUrls.get(link);
            }
            if (isHttpScheme(link)) {
                Logger.info("Http Scheme");
                return checkHttpScheme(link);
            }
            if (isSpecialScheme(link)) {
                Logger.warn("Special Scheme: adding as valid");
                checkedUrls.put(link, true);
                return true;
            }
            if (isFileScheme(link)) {
                Logger.info("File Scheme");
                return checkFileScheme(link);
            }
            Logger.info("Link not valid: " + link);
            checkedUrls.put(link, false);
            return false;
        } catch (IOException e) {
            Logger.error("Url could not be checked: " + e.getMessage());
            checkedUrls.put(link, false);
            return false;
        }
    }

    public static void setConnectionWrapper(URLConnectionWrapper wrapper) {
        urlConnectionWrapper = wrapper;
    }

    private static boolean checkFileScheme(String link) {
        return checkDomain(link) && checkFileLinkExists(link);
    }

    private static boolean isFileScheme(String link) {
        return link.startsWith("file:/");
    }

    private static boolean isSpecialScheme(String link) {
        return link.contains("mailto") || link.contains("tel:") || link.contains("sms:");
    }

    private static boolean checkHttpScheme(String link) throws IOException {
        URL url = new URL(link);
        String host = url.getHost();
        boolean isValid = checkDomain(host) && checkReachable(url);
        checkedUrls.put(link, isValid);
        return isValid;
    }

    private static boolean isHttpScheme(String link) {
        return link.startsWith("https://") || link.startsWith("http://");
    }

    // for testing purposes
    private static boolean checkFileLinkExists(String link) {
        Path filePath = Paths.get(URI.create(link));
        return Files.exists(filePath);
    }

    private static boolean isLinkAlreadyChecked(String link) {
        return checkedUrls.containsKey(link);
    }

    private static boolean checkReachable(URL url) throws IOException {
        Logger.info("Checking Reachability of url!");
        HttpURLConnection httpURLConnection = urlConnectionWrapper.openConnection(url);
        httpURLConnection.setRequestMethod("HEAD");
        return httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK;
    }

    private static boolean checkDomain(String host) {
        Logger.info("Checking if domain is allowed!");
        for (String domain : allowedDomains) {
            if (host.contains(domain)) return true;
        }
        return false;
    }

    public static void checkUrlFormatting(String url) throws IncorrectInputException {
        if (url.matches("http://.*") || url.matches("https://.*")) {
            return;
        }
        throw new IncorrectInputException();
    }
}
