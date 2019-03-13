package org.apereo.cas;


import lombok.val;
import org.apereo.cas.config.CasPersonDirectoryConfiguration;
import org.apereo.cas.util.MockWebServer;
import org.apereo.services.persondir.IPersonAttributeDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link RestfulPersonAttributeDaoTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { CasPersonDirectoryConfiguration.class, RefreshAutoConfiguration.class })
@TestPropertySource(properties = { "cas.authn.attributeRepository.rest[0].method=GET", "cas.authn.attributeRepository.rest[0].url=http://localhost:8085" })
@Tag("RestfulApi")
public class RestfulPersonAttributeDaoTests {
    @Autowired
    @Qualifier("attributeRepository")
    protected IPersonAttributeDao attributeRepository;

    private MockWebServer webServer;

    @Test
    public void verifyRestAttributeRepository() {
        Assertions.assertNotNull(attributeRepository);
        val person = attributeRepository.getPerson("casuser");
        Assertions.assertNotNull(person);
        Assertions.assertNotNull(person.getAttributes());
        Assertions.assertFalse(person.getAttributes().isEmpty());
        Assertions.assertEquals("casuser", person.getAttributeValue("name"));
        Assertions.assertEquals(29, person.getAttributeValue("age"));
        Assertions.assertEquals(3, person.getAttributeValues("messages").size());
    }
}

