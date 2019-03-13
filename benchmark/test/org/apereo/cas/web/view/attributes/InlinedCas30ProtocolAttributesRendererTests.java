package org.apereo.cas.web.view.attributes;


import java.util.Map;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link InlinedCas30ProtocolAttributesRendererTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class InlinedCas30ProtocolAttributesRendererTests {
    @Test
    public void verifyAction() {
        val r = new InlinedCas30ProtocolAttributesRenderer();
        val results = ((Map) (CoreAuthenticationTestUtils.getAttributeRepository().getBackingMap()));
        Assertions.assertFalse(r.render(results).isEmpty());
    }
}

