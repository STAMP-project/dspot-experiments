/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 * Test to illustrate that the <tt>org.hibernate.mapping.Table#sqlAlterStrings</tt> method
 * uses legacy logic for building table names and doesn't adequately specify the catalog
 * or schema name properly.
 *
 * @author Chris Cranford
 */
@RequiresDialect(MySQL5Dialect.class)
@TestForIssue(jiraKey = "HHH-11455")
public class SchemaUpdateSchemaNameTest extends BaseUnitTestCase {
    @Test
    public void testSqlAlterWithTableSchemaName() throws Exception {
        StandardServiceRegistry ssr = null;
        try {
            final Configuration cfg = SchemaUpdateSchemaNameTest.buildConfiguration(SchemaUpdateSchemaNameTest.SimpleNext.class);
            ssr = new StandardServiceRegistryBuilder(new BootstrapServiceRegistryBuilder().build(), cfg.getStandardServiceRegistryBuilder().getAggregatedCfgXml()).applySettings(cfg.getProperties()).build();
            SessionFactory sf = cfg.buildSessionFactory(ssr);
            try {
                Session session = sf.openSession();
                try {
                    session.getTransaction().begin();
                    session.createQuery("FROM Simple", SchemaUpdateSchemaNameTest.SimpleNext.class).getResultList();
                    session.getTransaction().commit();
                } catch (Throwable t) {
                    if (session.getTransaction().isActive()) {
                        session.getTransaction().rollback();
                    }
                    throw t;
                } finally {
                    session.close();
                }
            } finally {
                sf.close();
            }
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @MappedSuperclass
    public abstract static class AbstractSimple {
        @Id
        private Integer id;

        private Integer value;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    @Entity(name = "Simple")
    @Table(name = "Simple", schema = "test")
    public static class SimpleFirst extends SchemaUpdateSchemaNameTest.AbstractSimple {}

    @Entity(name = "Simple")
    @Table(name = "Simple", schema = "test")
    public static class SimpleNext extends SchemaUpdateSchemaNameTest.AbstractSimple {
        private String data;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}

