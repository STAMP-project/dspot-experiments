package org.apereo.cas;


import lombok.val;
import org.apereo.cas.config.CasPersonDirectoryConfiguration;
import org.apereo.services.persondir.IPersonAttributeDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link CachingAttributeRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { CasPersonDirectoryConfiguration.class, RefreshAutoConfiguration.class })
@TestPropertySource(properties = { "cas.authn.attributeRepository.stub.attributes.uid=uid", "cas.authn.attributeRepository.stub.attributes.givenName=givenName", "cas.authn.attributeRepository.stub.attributes.eppn=eppn" })
public class CachingAttributeRepositoryTests {
    @Autowired
    @Qualifier("cachingAttributeRepository")
    private IPersonAttributeDao cachingAttributeRepository;

    @Test
    public void verifyRepositoryCaching() {
        val person1 = cachingAttributeRepository.getPerson("casuser");
        Assertions.assertEquals("casuser", person1.getName());
        Assertions.assertEquals(4, person1.getAttributes().size());
        // The second call should not go out to the repositories again
        val person2 = cachingAttributeRepository.getPerson("casuser");
        Assertions.assertEquals(4, person2.getAttributes().size());
    }
}

