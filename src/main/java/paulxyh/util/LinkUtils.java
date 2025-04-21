package paulxyh.util;

import paulxyh.util.logger.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LinkUtils {
    public static boolean isLinkValid(String link, List<String> allowedDomains){
        try{
            URL url = new URL(link);
            String host = url.getHost();
            return checkReachable(url) && checkDomain(host, allowedDomains);
        } catch (IOException e) {
            Logger.error("Url could not be checked: " + e.getMessage());
            return false;
        }
    }

    private static boolean checkReachable(URL url) throws IOException {
        Logger.info("Checking Reachability of url!");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        return httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK;
    }

    private static boolean checkDomain(String host, List<String> allowedDomains){
        Logger.info("Checking if domain is allowed!");
        for(String domain : allowedDomains){
            if(host.contains(domain)) return true;
        }
        return false;
    }
}
