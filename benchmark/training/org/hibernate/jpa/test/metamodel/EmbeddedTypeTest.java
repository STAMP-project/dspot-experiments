/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.metamodel;


import java.sql.Date;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class EmbeddedTypeTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-6896")
    public void ensureComponentsReturnedAsManagedType() {
        ManagedType<ShelfLife> managedType = entityManagerFactory().getMetamodel().managedType(ShelfLife.class);
        // the issue was in regards to throwing an exception, but also check for nullness
        Assert.assertNotNull(managedType);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-4702")
    public void testSingularAttributeAccessByName() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        SingularAttribute soldDate_ = em.getMetamodel().embeddable(ShelfLife.class).getSingularAttribute("soldDate");
        Assert.assertEquals(Date.class, soldDate_.getBindableJavaType());
        Assert.assertEquals(Date.class, soldDate_.getType().getJavaType());
        Assert.assertEquals(Date.class, soldDate_.getJavaType());
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-5821")
    public void testVersionAttributeMetadata() {
        EntityManager em = getOrCreateEntityManager();
        EntityType<VersionedEntity> metadata = em.getMetamodel().entity(VersionedEntity.class);
        Assert.assertNotNull(metadata.getDeclaredVersion(int.class));
        Assert.assertTrue(metadata.getDeclaredVersion(int.class).isVersion());
        Assert.assertEquals(3, metadata.getDeclaredSingularAttributes().size());
        Assert.assertTrue(metadata.getDeclaredSingularAttributes().contains(metadata.getDeclaredVersion(int.class)));
        em.close();
    }
}

