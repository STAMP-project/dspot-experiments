package org.apereo.cas.services.support;


import java.io.File;
import java.util.Map;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;


/**
 * This is {@link RegisteredServiceScriptedAttributeFilterTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class RegisteredServiceScriptedAttributeFilterTests {
    private Map<String, Object> givenAttributesMap;

    @Test
    public void verifyScriptedAttributeFilter() throws Exception {
        val filter = new RegisteredServiceScriptedAttributeFilter();
        val f = File.createTempFile("attr", ".groovy");
        val stream = new ClassPathResource("groovy-attr-filter.groovy").getInputStream();
        FileUtils.copyInputStreamToFile(stream, f);
        filter.setScript(("file:" + (f.getCanonicalPath())));
        val results = filter.filter(this.givenAttributesMap);
        Assertions.assertEquals(3, results.size());
    }
}

