package org.apereo.cas.support.events;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link AbstractCasEventRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public abstract class AbstractCasEventRepositoryTests {
    @Test
    public void verifySave() {
        val dto1 = getCasEvent();
        getEventRepository().save(dto1);
        val dto2 = getCasEvent();
        getEventRepository().save(dto2);
        val col = getEventRepository().load();
        Assertions.assertEquals(2, col.size());
        Assertions.assertNotEquals(dto2.getEventId(), dto1.getEventId(), "Created Event IDs are equal");
        Assertions.assertEquals(2, col.stream().map(CasEvent::getEventId).distinct().count(), "Stored event IDs are equal");
        col.forEach(( event) -> {
            assertFalse(event.getProperties().isEmpty());
            if (event.getEventId().equals(dto1.getEventId())) {
                assertEquals(dto1.getType(), event.getType());
                assertEquals(dto1.getTimestamp(), event.getTimestamp());
                assertEquals(dto1.getCreationTime(), event.getCreationTime());
                assertEquals(dto1.getPrincipalId(), event.getPrincipalId());
                assertEquals(dto1.getGeoLocation(), event.getGeoLocation());
                assertEquals(dto1.getClientIpAddress(), event.getClientIpAddress());
                assertEquals(dto1.getServerIpAddress(), event.getServerIpAddress());
            } else
                if (event.getEventId().equals(dto2.getEventId())) {
                    assertEquals(dto2.getType(), event.getType());
                    assertEquals(dto2.getTimestamp(), event.getTimestamp());
                    assertEquals(dto2.getCreationTime(), event.getCreationTime());
                    assertEquals(dto2.getPrincipalId(), event.getPrincipalId());
                    assertEquals(dto2.getGeoLocation(), event.getGeoLocation());
                    assertEquals(dto2.getClientIpAddress(), event.getClientIpAddress());
                    assertEquals(dto2.getServerIpAddress(), event.getServerIpAddress());
                } else {
                    fail("Unexpected event ID");
                }

        });
    }
}

