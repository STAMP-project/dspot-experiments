package org.apereo.cas.audit.spi;


import java.time.LocalDate;
import java.util.Date;
import lombok.val;
import org.apereo.inspektr.audit.AuditActionContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link BaseAuditConfigurationTests}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
public abstract class BaseAuditConfigurationTests {
    @Test
    public void verifyAuditManager() {
        val time = LocalDate.now().minusDays(2);
        val ctx = new AuditActionContext("casuser", "TEST", "TEST", "CAS", new Date(), "1.2.3.4", "1.2.3.4");
        getAuditTrailManager().record(ctx);
        val results = getAuditTrailManager().getAuditRecordsSince(time);
        Assertions.assertFalse(results.isEmpty());
    }
}

