package org.apereo.cas.ticket.support;


import lombok.val;
import org.apereo.cas.ticket.ExpirationPolicy;
import org.apereo.cas.util.serialization.AbstractJacksonBackedStringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link SurrogateSessionExpirationPolicyJsonSerializerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
public class SurrogateSessionExpirationPolicyJsonSerializerTests {
    @Test
    public void verifyOperation() {
        val policy = new SurrogateSessionExpirationPolicy();
        val serializer = new SurrogateSessionExpirationPolicyJsonSerializerTests.SurrogateSessionExpirationPolicyJsonSerializer();
        val result = serializer.toString(policy);
        Assertions.assertNotNull(result);
        val newPolicy = serializer.from(result);
        Assertions.assertNotNull(newPolicy);
        Assertions.assertEquals(policy, newPolicy);
    }

    private static class SurrogateSessionExpirationPolicyJsonSerializer extends AbstractJacksonBackedStringSerializer<ExpirationPolicy> {
        private static final long serialVersionUID = -7883370764375218898L;

        @Override
        protected Class<ExpirationPolicy> getTypeToSerialize() {
            return ExpirationPolicy.class;
        }
    }
}

