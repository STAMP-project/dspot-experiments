/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.strategy;


import java.util.Arrays;
import javax.persistence.EntityManager;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.entities.IntNoAutoIdTestEntity;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that reusing identifiers doesn't cause auditing misbehavior.
 *
 * @author adar
 */
@TestForIssue(jiraKey = "HHH-8280")
public class IdentifierReuseTest extends BaseEnversJPAFunctionalTestCase {
    @Test
    public void testIdentifierReuse() {
        final Integer reusedId = 1;
        EntityManager entityManager = getEntityManager();
        saveUpdateAndRemoveEntity(entityManager, reusedId);
        saveUpdateAndRemoveEntity(entityManager, reusedId);
        entityManager.close();
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), getAuditReader().getRevisions(IntNoAutoIdTestEntity.class, reusedId));
    }
}

