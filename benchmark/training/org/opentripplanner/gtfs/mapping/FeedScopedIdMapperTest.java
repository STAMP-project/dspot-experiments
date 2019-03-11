package org.opentripplanner.gtfs.mapping;


import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.opentripplanner.model.FeedScopedId;


public class FeedScopedIdMapperTest {
    @Test
    public void testMapAgencyAndId() throws Exception {
        AgencyAndId inputId = new AgencyAndId("A", "1");
        FeedScopedId mappedId = AgencyAndIdMapper.mapAgencyAndId(inputId);
        Assert.assertEquals("A", mappedId.getAgencyId());
        Assert.assertEquals("1", mappedId.getId());
    }

    @Test
    public void testMapAgencyAndIdWithNulls() throws Exception {
        AgencyAndId inputId = new AgencyAndId();
        FeedScopedId mappedId = AgencyAndIdMapper.mapAgencyAndId(inputId);
        Assert.assertNull(mappedId.getAgencyId());
        Assert.assertNull(mappedId.getId());
    }
}

