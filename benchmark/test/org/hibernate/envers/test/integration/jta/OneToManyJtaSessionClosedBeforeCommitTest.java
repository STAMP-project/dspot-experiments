/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.jta;


import TestingJtaPlatformImpl.INSTANCE;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityManager;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.onetomany.SetRefEdEntity;
import org.hibernate.envers.test.entities.onetomany.SetRefIngEntity;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jta.TestingJtaPlatformImpl;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-11570")
public class OneToManyJtaSessionClosedBeforeCommitTest extends BaseEnversJPAFunctionalTestCase {
    private Integer entityId;

    @Test
    @Priority(10)
    public void initData() throws Exception {
        INSTANCE.getTransactionManager().begin();
        EntityManager entityManager = getEntityManager();
        try {
            SetRefEdEntity edEntity = new SetRefEdEntity(2, "edEntity");
            entityManager.persist(edEntity);
            SetRefIngEntity ingEntity = new SetRefIngEntity(1, "ingEntity");
            Set<SetRefIngEntity> sries = new HashSet<>();
            sries.add(ingEntity);
            ingEntity.setReference(edEntity);
            edEntity.setReffering(sries);
            entityManager.persist(ingEntity);
            entityId = ingEntity.getId();
        } finally {
            entityManager.close();
            TestingJtaPlatformImpl.tryCommit();
        }
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(SetRefIngEntity.class, entityId));
    }

    @Test
    public void testRevisionHistory() {
        Assert.assertEquals(new SetRefIngEntity(1, "ingEntity"), getAuditReader().find(SetRefIngEntity.class, entityId, 1));
    }
}

