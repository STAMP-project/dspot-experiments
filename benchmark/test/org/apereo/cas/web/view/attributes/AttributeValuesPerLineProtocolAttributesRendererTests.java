package org.apereo.cas.web.view.attributes;


import java.util.Map;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link AttributeValuesPerLineProtocolAttributesRendererTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class AttributeValuesPerLineProtocolAttributesRendererTests {
    @Test
    public void verifyAction() {
        val r = new AttributeValuesPerLineProtocolAttributesRenderer();
        val results = CoreAuthenticationTestUtils.getAttributeRepository().getBackingMap();
        Assertions.assertFalse(r.render(((Map) (results))).isEmpty());
    }
}

