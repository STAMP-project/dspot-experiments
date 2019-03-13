package org.apereo.cas.services.support;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.RegisteredServiceAttributeFilter;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.ReturnAllowedAttributeReleasePolicy;
import org.apereo.cas.util.serialization.SerializationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
public class RegisteredServiceRegexAttributeFilterTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "registeredServiceRegexAttributeFilter.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String PHONE = "phone";

    private static final String FAMILY_NAME = "familyName";

    private static final String GIVEN_NAME = "givenName";

    private static final String UID = "uid";

    private final RegisteredServiceAttributeFilter filter;

    private final Map<String, Object> givenAttributesMap;

    @Mock
    private RegisteredService registeredService;

    public RegisteredServiceRegexAttributeFilterTests() {
        this.filter = new RegisteredServiceRegexAttributeFilter("^.{5,}$");
        this.givenAttributesMap = new HashMap<>();
        this.givenAttributesMap.put(RegisteredServiceRegexAttributeFilterTests.UID, "loggedInTestUid");
        this.givenAttributesMap.put(RegisteredServiceRegexAttributeFilterTests.PHONE, "1290");
        this.givenAttributesMap.put(RegisteredServiceRegexAttributeFilterTests.FAMILY_NAME, "Smith");
        this.givenAttributesMap.put(RegisteredServiceRegexAttributeFilterTests.GIVEN_NAME, "John");
        this.givenAttributesMap.put("employeeId", "E1234");
        this.givenAttributesMap.put("memberOf", Arrays.asList("math", "science", "chemistry"));
        this.givenAttributesMap.put("arrayAttribute", new String[]{ "math", "science", "chemistry" });
        this.givenAttributesMap.put("setAttribute", Stream.of("math", "science", "chemistry").collect(Collectors.toSet()));
        val mapAttributes = new HashMap<String, String>();
        mapAttributes.put(RegisteredServiceRegexAttributeFilterTests.UID, "loggedInTestUid");
        mapAttributes.put(RegisteredServiceRegexAttributeFilterTests.PHONE, "890");
        mapAttributes.put(RegisteredServiceRegexAttributeFilterTests.FAMILY_NAME, "Smith");
        this.givenAttributesMap.put("mapAttribute", mapAttributes);
    }

    @Test
    public void verifyPatternFilter() {
        val attrs = this.filter.filter(this.givenAttributesMap);
        Assertions.assertEquals(7, attrs.size());
        Assertions.assertFalse(attrs.containsKey(RegisteredServiceRegexAttributeFilterTests.PHONE));
        Assertions.assertFalse(attrs.containsKey(RegisteredServiceRegexAttributeFilterTests.GIVEN_NAME));
        Assertions.assertTrue(attrs.containsKey(RegisteredServiceRegexAttributeFilterTests.UID));
        Assertions.assertTrue(attrs.containsKey("memberOf"));
        Assertions.assertTrue(attrs.containsKey("mapAttribute"));
        val mapAttributes = ((Map<String, String>) (attrs.get("mapAttribute")));
        Assertions.assertTrue(mapAttributes.containsKey(RegisteredServiceRegexAttributeFilterTests.UID));
        Assertions.assertTrue(mapAttributes.containsKey(RegisteredServiceRegexAttributeFilterTests.FAMILY_NAME));
        Assertions.assertFalse(mapAttributes.containsKey(RegisteredServiceRegexAttributeFilterTests.PHONE));
        val obj = ((List<?>) (attrs.get("memberOf")));
        Assertions.assertEquals(2, obj.size());
    }

    @Test
    public void verifyServiceAttributeFilterAllowedAttributesWithARegexFilter() {
        val policy = new ReturnAllowedAttributeReleasePolicy();
        policy.setAllowedAttributes(Arrays.asList("attr1", "attr3", "another"));
        policy.setAttributeFilter(new RegisteredServiceRegexAttributeFilter("v3"));
        val p = Mockito.mock(Principal.class);
        val map = new HashMap<String, Object>();
        map.put("attr1", "value1");
        map.put("attr2", "value2");
        map.put("attr3", Arrays.asList("v3", "v4"));
        Mockito.when(p.getAttributes()).thenReturn(map);
        Mockito.when(p.getId()).thenReturn("principalId");
        val attr = policy.getAttributes(p, RegisteredServiceTestUtils.getService(), RegisteredServiceTestUtils.getRegisteredService("test"));
        Assertions.assertEquals(1, attr.size());
        Assertions.assertTrue(attr.containsKey("attr3"));
        val data = SerializationUtils.serialize(policy);
        val p2 = SerializationUtils.deserializeAndCheckObject(data, ReturnAllowedAttributeReleasePolicy.class);
        Assertions.assertNotNull(p2);
        Assertions.assertEquals(p2.getAllowedAttributes(), policy.getAllowedAttributes());
        Assertions.assertEquals(p2.getAttributeFilter(), policy.getAttributeFilter());
    }

    @Test
    public void verifySerialization() {
        val data = SerializationUtils.serialize(this.filter);
        val secondFilter = SerializationUtils.deserializeAndCheckObject(data, RegisteredServiceAttributeFilter.class);
        Assertions.assertEquals(secondFilter, this.filter);
    }

    @Test
    public void verifySerializeARegisteredServiceRegexAttributeFilterToJson() throws IOException {
        RegisteredServiceRegexAttributeFilterTests.MAPPER.writeValue(RegisteredServiceRegexAttributeFilterTests.JSON_FILE, filter);
        val filterRead = RegisteredServiceRegexAttributeFilterTests.MAPPER.readValue(RegisteredServiceRegexAttributeFilterTests.JSON_FILE, RegisteredServiceRegexAttributeFilter.class);
        Assertions.assertEquals(filter, filterRead);
    }
}

