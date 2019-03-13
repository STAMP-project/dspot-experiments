package com.baeldung;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import twitter4j.TwitterException;


public class ApplicationIntegrationTest {
    /**
     * In order run this jUnit test you need to configure your API details in the twitter4j.properties
     */
    String tweet = "baeldung is awsome";

    @Test
    public void givenText_updateStatus() throws TwitterException {
        String text = Application.createTweet(tweet);
        Assert.assertEquals(tweet, text);
    }

    @Test
    public void givenCredential_fetchStatus() throws TwitterException {
        List<String> statuses = Application.getTimeLine();
        List<String> expectedStatuses = new ArrayList<String>();
        expectedStatuses.add(tweet);
        Assert.assertEquals(expectedStatuses, statuses);
    }

    @Test
    public void givenRecipientNameAndMessage_sendDirectMessage() throws TwitterException {
        String msg = Application.sendDirectMessage("YOUR_RECCIPIENT_ID", tweet);
        Assert.assertEquals(msg, tweet);
    }
}

