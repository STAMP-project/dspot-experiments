package org.keycloak.testsuite.broker;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;


/**
 *
 *
 * @author hmlnarik
 */
public abstract class AbstractUserAttributeMapperTest extends AbstractBaseBrokerTest {
    protected static final String MAPPED_ATTRIBUTE_NAME = "mapped-user-attribute";

    protected static final String MAPPED_ATTRIBUTE_FRIENDLY_NAME = "mapped-user-attribute-friendly";

    protected static final String ATTRIBUTE_TO_MAP_NAME = "user-attribute";

    protected static final String ATTRIBUTE_TO_MAP_FRIENDLY_NAME = "user-attribute-friendly";

    private static final Set<String> PROTECTED_NAMES = ImmutableSet.<String>builder().add("email").add("lastName").add("firstName").build();

    private static final Map<String, String> ATTRIBUTE_NAME_TRANSLATION = ImmutableMap.<String, String>builder().put("dotted.email", "dotted.email").put("nested.email", "nested.email").put(AbstractUserAttributeMapperTest.ATTRIBUTE_TO_MAP_FRIENDLY_NAME, AbstractUserAttributeMapperTest.MAPPED_ATTRIBUTE_FRIENDLY_NAME).put(AbstractUserAttributeMapperTest.ATTRIBUTE_TO_MAP_NAME, AbstractUserAttributeMapperTest.MAPPED_ATTRIBUTE_NAME).build();

    @Test
    public void testBasicMappingSingleValue() {
        testValueMapping(ImmutableMap.<String, List<String>>builder().put(AbstractUserAttributeMapperTest.ATTRIBUTE_TO_MAP_NAME, ImmutableList.<String>builder().add("value 1").build()).build(), ImmutableMap.<String, List<String>>builder().put(AbstractUserAttributeMapperTest.ATTRIBUTE_TO_MAP_NAME, ImmutableList.<String>builder().add("second value").build()).build());
    }

    @Test
    public void testBasicMappingEmail() {
        testValueMapping(ImmutableMap.<String, List<String>>builder().put("email", ImmutableList.<String>builder().add(bc.getUserEmail()).build()).put("nested.email", ImmutableList.<String>builder().add(bc.getUserEmail()).build()).put("dotted.email", ImmutableList.<String>builder().add(bc.getUserEmail()).build()).build(), ImmutableMap.<String, List<String>>builder().put("email", ImmutableList.<String>builder().add("other_email@redhat.com").build()).put("nested.email", ImmutableList.<String>builder().add("other_email@redhat.com").build()).put("dotted.email", ImmutableList.<String>builder().add("other_email@redhat.com").build()).build());
    }

    @Test
    public void testBasicMappingClearValue() {
        testValueMapping(ImmutableMap.<String, List<String>>builder().put(AbstractUserAttributeMapperTest.ATTRIBUTE_TO_MAP_NAME, ImmutableList.<String>builder().add("value 1").build()).build(), ImmutableMap.<String, List<String>>builder().put(AbstractUserAttributeMapperTest.ATTRIBUTE_TO_MAP_NAME, ImmutableList.<String>builder().build()).build());
    }

    @Test
    public void testBasicMappingRemoveValue() {
        testValueMapping(ImmutableMap.<String, List<String>>builder().put(AbstractUserAttributeMapperTest.ATTRIBUTE_TO_MAP_NAME, ImmutableList.<String>builder().add("value 1").build()).build(), ImmutableMap.<String, List<String>>builder().build());
    }

    @Test
    public void testBasicMappingMultipleValues() {
        testValueMapping(ImmutableMap.<String, List<String>>builder().put(AbstractUserAttributeMapperTest.ATTRIBUTE_TO_MAP_NAME, ImmutableList.<String>builder().add("value 1").add("value 2").build()).build(), ImmutableMap.<String, List<String>>builder().put(AbstractUserAttributeMapperTest.ATTRIBUTE_TO_MAP_NAME, ImmutableList.<String>builder().add("second value").add("second value 2").build()).build());
    }

    @Test
    public void testAddBasicMappingMultipleValues() {
        testValueMapping(ImmutableMap.<String, List<String>>builder().build(), ImmutableMap.<String, List<String>>builder().put(AbstractUserAttributeMapperTest.ATTRIBUTE_TO_MAP_NAME, ImmutableList.<String>builder().add("second value").add("second value 2").build()).build());
    }

    @Test
    public void testDeleteBasicMappingMultipleValues() {
        testValueMapping(ImmutableMap.<String, List<String>>builder().put(AbstractUserAttributeMapperTest.ATTRIBUTE_TO_MAP_NAME, ImmutableList.<String>builder().add("second value").add("second value 2").build()).build(), ImmutableMap.<String, List<String>>builder().build());
    }
}

