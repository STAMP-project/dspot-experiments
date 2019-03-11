package com.commafeed.backend.service;


import com.commafeed.backend.model.FeedEntry;
import com.commafeed.backend.model.FeedEntryContent;
import com.commafeed.backend.service.FeedEntryFilteringService.FeedEntryFilterException;
import org.junit.Assert;
import org.junit.Test;


public class FeedEntryFilteringServiceTest {
    private FeedEntryFilteringService service;

    private FeedEntry entry;

    private FeedEntryContent content;

    @Test
    public void emptyFilterMatchesFilter() throws FeedEntryFilterException {
        Assert.assertTrue(service.filterMatchesEntry(null, entry));
    }

    @Test
    public void blankFilterMatchesFilter() throws FeedEntryFilterException {
        Assert.assertTrue(service.filterMatchesEntry("", entry));
    }

    @Test
    public void simpleExpression() throws FeedEntryFilterException {
        Assert.assertTrue(service.filterMatchesEntry("author.toString() eq 'athou'", entry));
    }

    @Test(expected = FeedEntryFilterException.class)
    public void newIsDisabled() throws FeedEntryFilterException {
        service.filterMatchesEntry("null eq new ('java.lang.String', 'athou')", entry);
    }

    @Test(expected = FeedEntryFilterException.class)
    public void getClassMethodIsDisabled() throws FeedEntryFilterException {
        service.filterMatchesEntry("null eq ''.getClass()", entry);
    }

    @Test
    public void dotClassIsDisabled() throws FeedEntryFilterException {
        Assert.assertTrue(service.filterMatchesEntry("null eq ''.class", entry));
    }

    @Test(expected = FeedEntryFilterException.class)
    public void cannotLoopForever() throws FeedEntryFilterException {
        service.filterMatchesEntry("while(true) {}", entry);
    }

    @Test
    public void handlesNullCorrectly() throws FeedEntryFilterException {
        entry.setUrl(null);
        entry.setContent(new FeedEntryContent());
        service.filterMatchesEntry("author eq 'athou'", entry);
    }

    @Test(expected = FeedEntryFilterException.class)
    public void incorrectScriptThrowsException() throws FeedEntryFilterException {
        service.filterMatchesEntry("aa eqz bb", entry);
    }

    @Test(expected = FeedEntryFilterException.class)
    public void incorrectReturnTypeThrowsException() throws FeedEntryFilterException {
        service.filterMatchesEntry("1", entry);
    }
}

