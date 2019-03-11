package org.apereo.cas;


import lombok.val;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.apereo.cas.util.junit.EnabledIfPortOpen;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link JdbcSingleRowAttributeRepositoryPostgresTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@TestPropertySource(properties = { "cas.authn.attributeRepository.jdbc[0].attributes.uid=uid", "cas.authn.attributeRepository.jdbc[0].attributes.locations=locations", "cas.authn.attributeRepository.jdbc[0].singleRow=true", "cas.authn.attributeRepository.jdbc[0].requireAllAttributes=true", "cas.authn.attributeRepository.jdbc[0].sql=SELECT * FROM table_users WHERE {0}", "cas.authn.attributeRepository.jdbc[0].username=uid", "cas.authn.attributeRepository.jdbc[0].user=postgres", "cas.authn.attributeRepository.jdbc[0].password=password", "cas.authn.attributeRepository.jdbc[0].driverClass=org.postgresql.Driver", "cas.authn.attributeRepository.jdbc[0].url=jdbc:postgresql://localhost:5432/postgres", "cas.authn.attributeRepository.jdbc[0].dialect=org.hibernate.dialect.PostgreSQL95Dialect" })
@EnabledIfPortOpen(port = 5432)
@EnabledIfContinuousIntegration
@Tag("Postgres")
public class JdbcSingleRowAttributeRepositoryPostgresTests extends JdbcSingleRowAttributeRepositoryTests {
    @Test
    public void verifySingleRowAttributeRepository() {
        Assertions.assertNotNull(attributeRepository);
        val person = attributeRepository.getPerson("casuser");
        Assertions.assertNotNull(person);
        Assertions.assertNotNull(person.getAttributes());
        Assertions.assertFalse(person.getAttributes().isEmpty());
        Assertions.assertEquals("casuser", person.getAttributeValue("uid"));
        Assertions.assertFalse(person.getAttributeValues("locations").isEmpty());
    }
}

