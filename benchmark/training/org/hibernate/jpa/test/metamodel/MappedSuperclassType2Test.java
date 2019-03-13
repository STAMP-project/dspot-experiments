/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.metamodel;


import AvailableSettings.HBM2DDL_AUTO;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.ManagedType;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.test.PersistenceUnitDescriptorAdapter;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Specifically see if we can access a MappedSuperclass via Metamodel that is not part of a entity hierarchy
 *
 * @author Steve Ebersole
 */
public class MappedSuperclassType2Test extends BaseUnitTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-8534")
    @FailureExpected(jiraKey = "HHH-8534")
    public void testMappedSuperclassAccessNoEntity() {
        // stupid? yes.  tck does it? yes.
        final PersistenceUnitDescriptorAdapter pu = new PersistenceUnitDescriptorAdapter() {
            @Override
            public List<String> getManagedClassNames() {
                // pass in a MappedSuperclass that is not used in any entity hierarchy
                return Arrays.asList(SomeMappedSuperclass.class.getName());
            }
        };
        final Map settings = new HashMap();
        settings.put(HBM2DDL_AUTO, "create-drop");
        EntityManagerFactory emf = Bootstrap.getEntityManagerFactoryBuilder(pu, settings).build();
        try {
            ManagedType<SomeMappedSuperclass> type = emf.getMetamodel().managedType(SomeMappedSuperclass.class);
            // the issue was in regards to throwing an exception, but also check for nullness
            Assert.assertNotNull(type);
        } finally {
            emf.close();
        }
    }
}

