package org.opentripplanner.gtfs.mapping;


import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.model.FeedInfo;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.model.org.opentripplanner.model.FeedInfo;


public class FeedInfoMapperTest {
    private static final FeedInfo FEED_INFO = new FeedInfo();

    private static final String ID = "45";

    private static final ServiceDate START_DATE = new ServiceDate(2016, 10, 5);

    private static final ServiceDate END_DATE = new ServiceDate(2017, 12, 7);

    private static final String LANG = "US";

    private static final String PUBLISHER_NAME = "Name";

    private static final String PUBLISHER_URL = "www.url.pub";

    private static final String VERSION = "Version";

    static {
        FeedInfoMapperTest.FEED_INFO.setId(FeedInfoMapperTest.ID);
        FeedInfoMapperTest.FEED_INFO.setStartDate(FeedInfoMapperTest.START_DATE);
        FeedInfoMapperTest.FEED_INFO.setEndDate(FeedInfoMapperTest.END_DATE);
        FeedInfoMapperTest.FEED_INFO.setLang(FeedInfoMapperTest.LANG);
        FeedInfoMapperTest.FEED_INFO.setPublisherName(FeedInfoMapperTest.PUBLISHER_NAME);
        FeedInfoMapperTest.FEED_INFO.setPublisherUrl(FeedInfoMapperTest.PUBLISHER_URL);
        FeedInfoMapperTest.FEED_INFO.setVersion(FeedInfoMapperTest.VERSION);
    }

    private FeedInfoMapper subject = new FeedInfoMapper();

    @Test
    public void testMapCollection() throws Exception {
        Assert.assertNull(subject.map(((Collection<FeedInfo>) (null))));
        Assert.assertTrue(subject.map(Collections.emptyList()).isEmpty());
        Assert.assertEquals(1, subject.map(Collections.singleton(FeedInfoMapperTest.FEED_INFO)).size());
    }

    @Test
    public void testMap() throws Exception {
        org.opentripplanner.model.FeedInfo result = subject.map(FeedInfoMapperTest.FEED_INFO);
        Assert.assertEquals(FeedInfoMapperTest.ID, result.getId());
        Assert.assertEquals("20161005", result.getStartDate().getAsString());
        Assert.assertEquals("20171207", result.getEndDate().getAsString());
        Assert.assertEquals(FeedInfoMapperTest.LANG, result.getLang());
        Assert.assertEquals(FeedInfoMapperTest.PUBLISHER_NAME, result.getPublisherName());
        Assert.assertEquals(FeedInfoMapperTest.PUBLISHER_URL, result.getPublisherUrl());
        Assert.assertEquals(FeedInfoMapperTest.VERSION, result.getVersion());
    }

    @Test
    public void testMapWithNulls() throws Exception {
        org.opentripplanner.model.FeedInfo result = subject.map(new FeedInfo());
        Assert.assertNotNull(result.getId());
        Assert.assertNull(result.getStartDate());
        Assert.assertNull(result.getEndDate());
        Assert.assertNull(result.getLang());
        Assert.assertNull(result.getPublisherName());
        Assert.assertNull(result.getPublisherUrl());
        Assert.assertNull(result.getVersion());
    }

    /**
     * Mapping the same object twice, should return the the same instance.
     */
    @Test
    public void testMapCache() throws Exception {
        org.opentripplanner.model.FeedInfo result1 = subject.map(FeedInfoMapperTest.FEED_INFO);
        org.opentripplanner.model.FeedInfo result2 = subject.map(FeedInfoMapperTest.FEED_INFO);
        Assert.assertTrue((result1 == result2));
    }
}

