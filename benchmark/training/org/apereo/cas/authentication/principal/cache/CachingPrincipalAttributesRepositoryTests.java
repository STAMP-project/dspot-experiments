package org.apereo.cas.authentication.principal.cache;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * Handles tests for {@link CachingPrincipalAttributesRepository}.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreUtilConfiguration.class })
public class CachingPrincipalAttributesRepositoryTests extends AbstractCachingPrincipalAttributesRepositoryTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "cachingPrincipalAttributesRepository.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Test
    @SneakyThrows
    public void verifySerializeACachingPrincipalAttributesRepositoryToJson() {
        val repositoryWritten = getPrincipalAttributesRepository(TimeUnit.MILLISECONDS.toString(), 1);
        repositoryWritten.setAttributeRepositoryIds(CollectionUtils.wrapSet("1", "2", "3"));
        CachingPrincipalAttributesRepositoryTests.MAPPER.writeValue(CachingPrincipalAttributesRepositoryTests.JSON_FILE, repositoryWritten);
        val repositoryRead = CachingPrincipalAttributesRepositoryTests.MAPPER.readValue(CachingPrincipalAttributesRepositoryTests.JSON_FILE, CachingPrincipalAttributesRepository.class);
        Assertions.assertEquals(repositoryWritten, repositoryRead);
    }
}

