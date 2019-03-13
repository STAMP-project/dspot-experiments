package org.apereo.cas.util;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;


/**
 * This is {@link ResourceUtilsTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class ResourceUtilsTests {
    @Test
    public void verifyResourceExists() {
        Assertions.assertFalse(ResourceUtils.doesResourceExist(new FileSystemResource("invalid.json")));
        Assertions.assertFalse(ResourceUtils.doesResourceExist("invalid.json"));
        Assertions.assertTrue(ResourceUtils.doesResourceExist("classpath:valid.json", new DefaultResourceLoader(ResourceUtilsTests.class.getClassLoader())));
    }

    @Test
    public void verifyResourceOnClasspath() {
        val res = new ClassPathResource("valid.json");
        Assertions.assertNotNull(ResourceUtils.prepareClasspathResourceIfNeeded(res, false, "valid"));
        Assertions.assertNull(ResourceUtils.prepareClasspathResourceIfNeeded(null, false, "valid"));
    }
}

