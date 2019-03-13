package org.datatransferproject.transfer.microsoft.types;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.UUID;
import org.datatransferproject.spi.transfer.types.TempCalendarData;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class TempCalendarDataTest {
    private static final UUID JOB_ID = UUID.fromString("9b969983-a09b-4cb0-8017-7daae758126b");

    @Test
    public void verifySerializeDeserialize() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        TempCalendarData calendarData = new TempCalendarData(TempCalendarDataTest.JOB_ID, Collections.singletonMap("old1", "new1"));
        String serialized = objectMapper.writeValueAsString(calendarData);
        TempCalendarData deserialized = objectMapper.readValue(serialized, TempCalendarData.class);
        Assert.assertEquals(TempCalendarDataTest.JOB_ID, deserialized.getJobId());
        Assert.assertEquals("new1", deserialized.getImportedId("old1"));
    }
}

