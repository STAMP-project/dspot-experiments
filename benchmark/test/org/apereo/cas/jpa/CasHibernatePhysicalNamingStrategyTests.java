package org.apereo.cas.jpa;


import lombok.val;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link CasHibernatePhysicalNamingStrategyTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreUtilConfiguration.class })
@TestPropertySource(properties = { "cas.jdbc.physicalTableNames.CasHibernatePhysicalNamingStrategyTests=testtable", "cas.jdbc.physicalTableNames.GroovyTable=classpath:GroovyHibernatePhysicalNaming.groovy" })
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CasHibernatePhysicalNamingStrategyTests {
    @Test
    public void verifyMappedTable() {
        val strategy = new CasHibernatePhysicalNamingStrategy();
        val id = strategy.toPhysicalTableName(Identifier.toIdentifier("CasHibernatePhysicalNamingStrategyTests"), Mockito.mock(JdbcEnvironment.class));
        Assertions.assertEquals("testtable", id.getText());
    }

    @Test
    public void verifyMappedTableViaGroovy() {
        val strategy = new CasHibernatePhysicalNamingStrategy();
        val id = strategy.toPhysicalTableName(Identifier.toIdentifier("GroovyTable"), Mockito.mock(JdbcEnvironment.class));
        Assertions.assertEquals("CasTableName", id.getText());
        Assertions.assertEquals("castablename", id.getCanonicalName());
    }
}

