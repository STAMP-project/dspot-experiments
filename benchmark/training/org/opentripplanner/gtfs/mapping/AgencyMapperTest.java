package org.opentripplanner.gtfs.mapping;


import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.org.opentripplanner.model.Agency;


public class AgencyMapperTest {
    private static final Agency AGENCY = new Agency();

    private static final String ID = "ID";

    private static final String NAME = "Ann";

    private static final String LANG = "NO";

    private static final String PHONE = "+47 987 65 432";

    private static final String TIMEZONE = "GMT";

    private static final String URL = "www.url.com";

    private static final String FARE_URL = "www.url.com/fare";

    private static final String BRANDING_URL = "www.url.com/brand";

    static {
        AgencyMapperTest.AGENCY.setId(AgencyMapperTest.ID);
        AgencyMapperTest.AGENCY.setName(AgencyMapperTest.NAME);
        AgencyMapperTest.AGENCY.setLang(AgencyMapperTest.LANG);
        AgencyMapperTest.AGENCY.setPhone(AgencyMapperTest.PHONE);
        AgencyMapperTest.AGENCY.setTimezone(AgencyMapperTest.TIMEZONE);
        AgencyMapperTest.AGENCY.setUrl(AgencyMapperTest.URL);
        AgencyMapperTest.AGENCY.setFareUrl(AgencyMapperTest.FARE_URL);
        AgencyMapperTest.AGENCY.setBrandingUrl(AgencyMapperTest.BRANDING_URL);
    }

    private AgencyMapper subject = new AgencyMapper();

    @Test
    public void testMapCollection() throws Exception {
        Assert.assertNull(subject.map(((Collection<Agency>) (null))));
        Assert.assertTrue(subject.map(Collections.emptyList()).isEmpty());
        Assert.assertEquals(1, subject.map(Collections.singleton(AgencyMapperTest.AGENCY)).size());
    }

    @Test
    public void testMap() throws Exception {
        org.opentripplanner.model.Agency result = subject.map(AgencyMapperTest.AGENCY);
        Assert.assertEquals(AgencyMapperTest.ID, result.getId());
        Assert.assertEquals(AgencyMapperTest.NAME, result.getName());
        Assert.assertEquals(AgencyMapperTest.LANG, result.getLang());
        Assert.assertEquals(AgencyMapperTest.PHONE, result.getPhone());
        Assert.assertEquals(AgencyMapperTest.TIMEZONE, result.getTimezone());
        Assert.assertEquals(AgencyMapperTest.URL, result.getUrl());
        Assert.assertEquals(AgencyMapperTest.FARE_URL, result.getFareUrl());
        Assert.assertEquals(AgencyMapperTest.BRANDING_URL, result.getBrandingUrl());
    }

    @Test
    public void testMapWithNulls() throws Exception {
        Agency orginal = new Agency();
        orginal.setId(AgencyMapperTest.ID);
        org.opentripplanner.model.Agency result = subject.map(orginal);
        Assert.assertNotNull(result.getId());
        Assert.assertNull(result.getName());
        Assert.assertNull(result.getLang());
        Assert.assertNull(result.getPhone());
        Assert.assertNull(result.getTimezone());
        Assert.assertNull(result.getUrl());
        Assert.assertNull(result.getFareUrl());
        Assert.assertNull(result.getBrandingUrl());
    }

    /**
     * Mapping the same object twice, should return the the same instance.
     */
    @Test
    public void testMapCache() throws Exception {
        org.opentripplanner.model.Agency result1 = subject.map(AgencyMapperTest.AGENCY);
        org.opentripplanner.model.Agency result2 = subject.map(AgencyMapperTest.AGENCY);
        Assert.assertTrue((result1 == result2));
    }
}

