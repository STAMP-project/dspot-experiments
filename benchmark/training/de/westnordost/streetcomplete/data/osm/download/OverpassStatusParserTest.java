package de.westnordost.streetcomplete.data.osm.download;


import org.junit.Assert;
import org.junit.Test;


public class OverpassStatusParserTest {
    @Test
    public void parseRateLimit() {
        Assert.assertEquals(2, parse("Rate limit: 2").maxAvailableSlots);
    }

    @Test
    public void parseAvailableSlots() {
        Assert.assertEquals(33, parse("33 slots available now.").availableSlots);
    }

    @Test
    public void parseNoAvailableSlots() {
        Assert.assertEquals(25, ((int) (parse("Slot available after: 2016-11-20T18:08:05Z, in 25 seconds.").nextAvailableSlotIn)));
    }

    @Test
    public void parseNoAvailableSlotsMultiple() {
        Assert.assertEquals(25, ((int) (parse(("Slot available after: 2016-11-20T18:08:05Z, in 25 seconds.\n" + "Slot available after: 2016-11-20T20:08:05Z, in 564 seconds.\n")).nextAvailableSlotIn)));
    }
}

