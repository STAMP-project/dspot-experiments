package org.apereo.cas.authentication;


import lombok.val;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.core.io.ClassPathResource;


/**
 * This is {@link GroovyPrincipalFactoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("Groovy")
@SpringBootTest(classes = RefreshAutoConfiguration.class)
public class GroovyPrincipalFactoryTests {
    @Test
    public void verifyAction() {
        val factory = PrincipalFactoryUtils.newGroovyPrincipalFactory(new ClassPathResource("PrincipalFactory.groovy"));
        val p = factory.createPrincipal("casuser", CollectionUtils.wrap("name", "CAS"));
        Assertions.assertEquals("casuser", p.getId());
        Assertions.assertEquals(1, p.getAttributes().size());
    }
}

