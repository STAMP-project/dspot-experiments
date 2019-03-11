/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.metamodel;


import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class MappedSuperclassTypeTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-6896")
    public void ensureMappedSuperclassTypeReturnedAsManagedType() {
        ManagedType<SomeMappedSuperclass> type = entityManagerFactory().getMetamodel().managedType(SomeMappedSuperclass.class);
        // the issue was in regards to throwing an exception, but also check for nullness
        Assert.assertNotNull(type);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-8533")
    @SuppressWarnings("unchecked")
    public void testAttributeAccess() {
        final EntityType<SomeMappedSuperclassSubclass> entityType = entityManagerFactory().getMetamodel().entity(SomeMappedSuperclassSubclass.class);
        final IdentifiableType<SomeMappedSuperclass> mappedSuperclassType = ((IdentifiableType<SomeMappedSuperclass>) (entityType.getSupertype()));
        Assert.assertNotNull(entityType.getId(Long.class));
        try {
            entityType.getDeclaredId(Long.class);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertNotNull(mappedSuperclassType.getId(Long.class));
        Assert.assertNotNull(mappedSuperclassType.getDeclaredId(Long.class));
    }
}

