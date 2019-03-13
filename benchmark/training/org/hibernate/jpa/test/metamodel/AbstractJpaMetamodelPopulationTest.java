/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.metamodel;


import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.metamodel.Metamodel;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-12871")
public abstract class AbstractJpaMetamodelPopulationTest extends BaseEntityManagerFunctionalTestCase {
    @Entity(name = "SimpleAnnotatedEntity")
    public static class SimpleAnnotatedEntity {
        @Id
        @GeneratedValue
        private Integer id;

        private String data;
    }

    @Entity(name = "CompositeIdAnnotatedEntity")
    public static class CompositeIdAnnotatedEntity {
        @EmbeddedId
        private AbstractJpaMetamodelPopulationTest.CompositeIdId id;

        private String data;
    }

    @Embeddable
    public static class CompositeIdId implements Serializable {
        private Integer id1;

        private Integer id2;
    }

    @Test
    public void testMetamodel() {
        EntityManager entityManager = getOrCreateEntityManager();
        try {
            final Metamodel metamodel = entityManager.getMetamodel();
            if (getJpaMetamodelPopulationValue().equalsIgnoreCase("disabled")) {
                // In 5.1, metamodel returned null.
                // In 5.2+, metamodel erturned as a non-null instance.
                Assert.assertNotNull(metamodel);
                Assert.assertEquals(0, metamodel.getManagedTypes().size());
                Assert.assertEquals(0, metamodel.getEntities().size());
                Assert.assertEquals(0, metamodel.getEmbeddables().size());
                return;
            }
            Assert.assertNotNull(metamodel);
            assertManagedTypes(metamodel);
            assertEntityTypes(metamodel);
            assertEmbeddableTypes(metamodel);
        } finally {
            entityManager.close();
        }
    }
}

