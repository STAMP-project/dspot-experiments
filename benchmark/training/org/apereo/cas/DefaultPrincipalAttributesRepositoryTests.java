package org.apereo.cas;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.DefaultPrincipalAttributesRepository;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


/**
 * Handles tests for {@link DefaultPrincipalAttributesRepository}.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
public class DefaultPrincipalAttributesRepositoryTests extends BaseCasCoreTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "defaultPrincipalAttributesRepository.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Autowired
    @Qualifier("principalFactory")
    private ObjectProvider<PrincipalFactory> principalFactory;

    @Test
    public void checkDefaultAttributes() {
        val rep = new DefaultPrincipalAttributesRepository();
        val principal = CoreAuthenticationTestUtils.getPrincipal();
        Assertions.assertEquals(4, rep.getAttributes(principal, CoreAuthenticationTestUtils.getRegisteredService()).size());
    }

    @Test
    public void checkInitialAttributes() {
        val p = this.principalFactory.getIfAvailable().createPrincipal("uid", Collections.singletonMap("mail", "final@example.com"));
        val rep = new DefaultPrincipalAttributesRepository();
        val registeredService = CoreAuthenticationTestUtils.getRegisteredService();
        Assertions.assertEquals(1, rep.getAttributes(p, registeredService).size());
        Assertions.assertTrue(rep.getAttributes(p, registeredService).containsKey("mail"));
    }

    @Test
    public void verifySerializeADefaultPrincipalAttributesRepositoryToJson() throws IOException {
        val repositoryWritten = new DefaultPrincipalAttributesRepository();
        repositoryWritten.setIgnoreResolvedAttributes(true);
        repositoryWritten.setAttributeRepositoryIds(CollectionUtils.wrapSet("1", "2", "3"));
        DefaultPrincipalAttributesRepositoryTests.MAPPER.writeValue(DefaultPrincipalAttributesRepositoryTests.JSON_FILE, repositoryWritten);
        val repositoryRead = DefaultPrincipalAttributesRepositoryTests.MAPPER.readValue(DefaultPrincipalAttributesRepositoryTests.JSON_FILE, DefaultPrincipalAttributesRepository.class);
        Assertions.assertEquals(repositoryWritten, repositoryRead);
    }
}

