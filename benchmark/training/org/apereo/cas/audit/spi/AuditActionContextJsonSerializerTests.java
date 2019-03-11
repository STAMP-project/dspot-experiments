package org.apereo.cas.audit.spi;


import java.util.Date;
import lombok.val;
import org.apereo.inspektr.audit.AuditActionContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link AuditActionContextJsonSerializerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class AuditActionContextJsonSerializerTests {
    @Test
    public void verifyOperation() {
        val ctx = new AuditActionContext("casuser", "TEST", "TEST", "CAS", new Date(), "1.2.3.4", "1.2.3.4");
        val serializer = new AuditActionContextJsonSerializer();
        val result = serializer.toString(ctx);
        Assertions.assertNotNull(result);
        val audit = serializer.from(result);
        Assertions.assertNotNull(audit);
    }
}

