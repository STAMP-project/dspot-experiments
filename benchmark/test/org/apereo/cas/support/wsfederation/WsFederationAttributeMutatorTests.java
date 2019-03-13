package org.apereo.cas.support.wsfederation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.val;
import org.apereo.cas.support.wsfederation.attributes.WsFederationAttributeMutator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test cases for {@link WsFederationAttributeMutator}.
 *
 * @author John Gasper
 * @since 4.2.0
 */
public class WsFederationAttributeMutatorTests extends AbstractWsFederationTests {
    private static final String UPN_PARAM = "upn";

    @Test
    public void verifyModifyAttributes() {
        val attributes = new HashMap<String, List<Object>>();
        val values = new ArrayList<Object>();
        values.add("test@example.com");
        attributes.put(WsFederationAttributeMutatorTests.UPN_PARAM, values);
        val instance = new WsFederationAttributeMutatorTests.WsFederationAttributeMutatorImpl();
        instance.modifyAttributes(attributes);
        Assertions.assertTrue(attributes.containsKey("test"));
        Assertions.assertTrue("newtest".equalsIgnoreCase(attributes.get("test").get(0).toString()));
        Assertions.assertTrue(attributes.containsKey(WsFederationAttributeMutatorTests.UPN_PARAM));
        Assertions.assertTrue("testing".equalsIgnoreCase(attributes.get(WsFederationAttributeMutatorTests.UPN_PARAM).get(0).toString()));
    }

    private static class WsFederationAttributeMutatorImpl implements WsFederationAttributeMutator {
        private static final long serialVersionUID = -1858140387002752668L;

        @Override
        public Map<String, List<Object>> modifyAttributes(final Map<String, List<Object>> attributes) {
            List<Object> values = new ArrayList<>();
            values.add("newtest");
            attributes.put("test", values);
            values = new ArrayList<>();
            values.add("testing");
            attributes.put(WsFederationAttributeMutatorTests.UPN_PARAM, values);
            return attributes;
        }
    }
}

