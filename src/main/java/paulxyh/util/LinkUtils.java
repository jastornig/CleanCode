package paulxyh.util;

import paulxyh.exception.IncorrectInputException;
import paulxyh.util.logger.Logger;

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
    public static void setAllowedDomains(List<String> domains){
        allowedDomains = domains;
    }
    public static boolean isLinkValid(String link){
        try{
            Logger.info("Checking link: " + link);
            if(isLinkAlreadyChecked(link)){
                Logger.warn("Link was already checked! Stepping over!");
                return checkedUrls.get(link);
            }
            if(link.startsWith("https://") || link.startsWith("http://")) {
                URL url = new URL(link);
                String host = url.getHost();
                boolean isValid = checkReachable(url) && checkDomain(host);
                checkedUrls.put(link, isValid);
                return isValid;
            }
            if(link.contains("mailto") || link.contains("tel:") || link.contains("sms:")){
                checkedUrls.put(link, true);
                return true;
            }
            if(link.startsWith("file:/")){
                return checkDomain(link) && checkFileLinkExists(link);
            }
            return false;
        } catch (IOException e) {
            Logger.error("Url could not be checked: " + e.getMessage());
            checkedUrls.put(link, false);
            return false;
        }
    }

    // for testing purposes
    private static boolean checkFileLinkExists(String link){
        Path filePath = Paths.get(URI.create(link));
        return Files.exists(filePath);
    }

    private static boolean isLinkAlreadyChecked(String link){
        return checkedUrls.containsKey(link);
    }

    private static boolean checkReachable(URL url) throws IOException {
        Logger.info("Checking Reachability of url!");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("HEAD");
        return httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK;
    }

    private static boolean checkDomain(String host){
        Logger.info("Checking if domain is allowed!");
        for(String domain : allowedDomains){
            if(host.contains(domain)) return true;
        }
        return false;
    }

    public static void checkUrlFormatting(String url) throws IncorrectInputException {
        if(url.matches("http://.*") || url.matches("https://.*")){
            return;
        }
        throw new IncorrectInputException();
    }
}
