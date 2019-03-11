package org.jabref.logic.help;


import URLDownload.USER_AGENT;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class HelpFileTest {
    private final String jabrefHelp = "https://help.jabref.org/en/";

    @Test
    public void referToValidPage() throws IOException {
        for (HelpFile help : HelpFile.values()) {
            URL url = new URL(((jabrefHelp) + (help.getPageName())));
            HttpURLConnection http = ((HttpURLConnection) (url.openConnection()));
            http.setRequestProperty("User-Agent", USER_AGENT);
            Assertions.assertEquals(200, http.getResponseCode());
        }
    }
}

