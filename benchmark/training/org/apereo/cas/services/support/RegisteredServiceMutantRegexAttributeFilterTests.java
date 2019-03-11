package org.apereo.cas.services.support;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.RegisteredServiceAttributeFilter;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.serialization.SerializationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;


/**
 * This is {@link RegisteredServiceMutantRegexAttributeFilterTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class RegisteredServiceMutantRegexAttributeFilterTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "RegisteredServiceMutantRegexAttributeFilterTests.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private RegisteredServiceMutantRegexAttributeFilter filter;

    @Mock
    private RegisteredService registeredService;

    private Map<String, Object> givenAttributesMap;

    @Test
    public void verifyPatternFilter() {
        this.filter.setPatterns(Collections.singletonMap("memberOf", "^m"));
        val attrs = this.filter.filter(this.givenAttributesMap);
        Assertions.assertEquals(attrs.size(), this.givenAttributesMap.size());
        Assertions.assertEquals(2, CollectionUtils.toCollection(attrs.get("memberOf")).size());
    }

    @Test
    public void verifySerialization() {
        val data = SerializationUtils.serialize(this.filter);
        val secondFilter = SerializationUtils.deserializeAndCheckObject(data, RegisteredServiceAttributeFilter.class);
        Assertions.assertEquals(secondFilter, this.filter);
    }

    @Test
    public void verifySerializeARegisteredServiceRegexAttributeFilterToJson() throws IOException {
        this.filter.setPatterns(Collections.singletonMap("memberOf", CollectionUtils.wrapList("^mar(.+)", "^mat(.+)", "prefix$1")));
        this.filter.setExcludeUnmappedAttributes(true);
        this.filter.setCaseInsensitive(true);
        RegisteredServiceMutantRegexAttributeFilterTests.MAPPER.writeValue(RegisteredServiceMutantRegexAttributeFilterTests.JSON_FILE, this.filter);
        val filterRead = RegisteredServiceMutantRegexAttributeFilterTests.MAPPER.readValue(RegisteredServiceMutantRegexAttributeFilterTests.JSON_FILE, RegisteredServiceMutantRegexAttributeFilter.class);
        Assertions.assertEquals(filter, filterRead);
    }

    @Test
    public void verifyMutantPatternValues() {
        this.filter.setPatterns(Collections.singletonMap("memberOf", CollectionUtils.wrapList("^mar(.+)(101) -> prefix$1$2", "^mat(.+)(101) -> postfix$1$2")));
        this.filter.setCaseInsensitive(false);
        this.filter.setExcludeUnmappedAttributes(true);
        val results = filter.filter(this.givenAttributesMap);
        Assertions.assertEquals(1, results.size());
        val values = ((Collection) (results.get("memberOf")));
        Assertions.assertTrue(values.contains("prefixathon101"));
        Assertions.assertTrue(values.contains("postfixh101"));
    }
}

