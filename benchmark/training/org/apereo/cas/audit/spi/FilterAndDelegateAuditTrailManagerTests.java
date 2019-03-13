package org.apereo.cas.audit.spi;


import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.val;
import org.apereo.inspektr.audit.AuditActionContext;
import org.apereo.inspektr.audit.AuditTrailManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link FilterAndDelegateAuditTrailManagerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
public class FilterAndDelegateAuditTrailManagerTests {
    @Test
    public void verifyOperationForAllActions() {
        val ctx = new AuditActionContext("casuser", "TEST", "TEST", "CAS", new Date(), "1.2.3.4", "1.2.3.4");
        val mock = new FilterAndDelegateAuditTrailManagerTests.MockAuditTrailManager();
        val mgr = new FilterAndDelegateAuditTrailManager(Collections.singletonList(mock), Collections.singletonList("*"));
        mgr.record(ctx);
        Assertions.assertTrue(mock.isRecordedAuditEvent());
    }

    @Test
    public void verifyOperationForAllSupportedActions() {
        val ctx = new AuditActionContext("casuser", "TEST", "TEST", "CAS", new Date(), "1.2.3.4", "1.2.3.4");
        val mock = new FilterAndDelegateAuditTrailManagerTests.MockAuditTrailManager();
        val mgr = new FilterAndDelegateAuditTrailManager(Collections.singletonList(mock), Collections.singletonList("TEST.*"));
        mgr.record(ctx);
        Assertions.assertTrue(mock.isRecordedAuditEvent());
    }

    @Test
    public void verifyOperationForUnmatchedActions() {
        val ctx = new AuditActionContext("casuser", "TEST", "TEST", "CAS", new Date(), "1.2.3.4", "1.2.3.4");
        val mock = new FilterAndDelegateAuditTrailManagerTests.MockAuditTrailManager();
        val mgr = new FilterAndDelegateAuditTrailManager(Collections.singletonList(mock), Collections.singletonList("PASSED.*"));
        mgr.record(ctx);
        Assertions.assertFalse(mock.isRecordedAuditEvent());
    }

    @Getter
    private static class MockAuditTrailManager implements AuditTrailManager {
        private boolean recordedAuditEvent;

        @Override
        public void record(final AuditActionContext auditActionContext) {
            this.recordedAuditEvent = true;
        }

        @Override
        public Set<? extends AuditActionContext> getAuditRecordsSince(final LocalDate localDate) {
            return new HashSet<>();
        }
    }
}

