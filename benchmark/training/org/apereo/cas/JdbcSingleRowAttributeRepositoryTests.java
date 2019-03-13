package org.apereo.cas;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link JdbcSingleRowAttributeRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@TestPropertySource(properties = { "cas.authn.attributeRepository.jdbc[0].attributes.uid=uid", "cas.authn.attributeRepository.jdbc[0].attributes.displayName=displayName", "cas.authn.attributeRepository.jdbc[0].attributes.cn=commonName", "cas.authn.attributeRepository.jdbc[0].singleRow=true", "cas.authn.attributeRepository.jdbc[0].requireAllAttributes=true", "cas.authn.attributeRepository.jdbc[0].sql=SELECT * FROM table_users WHERE {0}", "cas.authn.attributeRepository.jdbc[0].username=uid" })
public class JdbcSingleRowAttributeRepositoryTests extends BaseJdbcAttributeRepositoryTests {
    @Test
    public void verifySingleRowAttributeRepository() {
        Assertions.assertNotNull(attributeRepository);
        val person = attributeRepository.getPerson("casuser");
        Assertions.assertNotNull(person);
        Assertions.assertNotNull(person.getAttributes());
        Assertions.assertFalse(person.getAttributes().isEmpty());
        Assertions.assertTrue(person.getAttributeValue("uid").equals("casuser"));
        Assertions.assertTrue(person.getAttributeValue("displayName").equals("CAS Display Name"));
        Assertions.assertTrue(person.getAttributeValue("commonName").equals("CAS Common Name"));
    }
}

