package org.apereo.cas;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link JdbcMultiRowAttributeRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@TestPropertySource(properties = { "cas.authn.attributeRepository.jdbc[0].attributes.nickname=cas_nickname", "cas.authn.attributeRepository.jdbc[0].attributes.role_code=cas_role", "cas.authn.attributeRepository.jdbc[0].singleRow=false", "cas.authn.attributeRepository.jdbc[0].columnMappings.attr_name=attr_value", "cas.authn.attributeRepository.jdbc[0].sql=SELECT * FROM table_users WHERE {0}", "cas.authn.attributeRepository.jdbc[0].username=uid" })
public class JdbcMultiRowAttributeRepositoryTests extends BaseJdbcAttributeRepositoryTests {
    @Test
    public void verifyMultiRowAttributeRepository() {
        Assertions.assertNotNull(attributeRepository);
        val person = attributeRepository.getPerson("casuser");
        Assertions.assertNotNull(person);
        Assertions.assertNotNull(person.getAttributes());
        Assertions.assertFalse(person.getAttributes().isEmpty());
        Assertions.assertEquals(3, person.getAttributeValues("cas_role").size());
        Assertions.assertEquals(2, person.getAttributeValues("cas_nickname").size());
    }
}

