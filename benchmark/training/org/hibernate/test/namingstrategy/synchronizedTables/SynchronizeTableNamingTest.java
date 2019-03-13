/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.namingstrategy.synchronizedTables;


import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class SynchronizeTableNamingTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    @Test
    public void testAnnotationHandling() {
        final Metadata metadata = addAnnotatedClass(DynamicEntity.class).getMetadataBuilder().applyPhysicalNamingStrategy(SynchronizeTableNamingTest.TestingPhysicalNamingStrategy.INSTANCE).build();
        verify(metadata.getEntityBinding(DynamicEntity.class.getName()));
    }

    @Test
    public void testHbmXmlHandling() {
        final Metadata metadata = addResource("org/hibernate/test/namingstrategy/synchronizedTables/mapping.hbm.xml").getMetadataBuilder().applyPhysicalNamingStrategy(SynchronizeTableNamingTest.TestingPhysicalNamingStrategy.INSTANCE).build();
        verify(metadata.getEntityBinding(DynamicEntity.class.getName()));
    }

    public static class TestingPhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {
        /**
         * Singleton access
         */
        public static final SynchronizeTableNamingTest.TestingPhysicalNamingStrategy INSTANCE = new SynchronizeTableNamingTest.TestingPhysicalNamingStrategy();

        @Override
        public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
            String baseName = name.render(context.getDialect());
            if (baseName.equals("table_a")) {
                baseName = "tbl_a";
            }
            return context.getIdentifierHelper().toIdentifier(baseName);
        }
    }
}

