/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.reventity;


import java.util.Arrays;
import javax.persistence.EntityManager;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.StrTestEntity;
import org.hibernate.mapping.Table;
import org.hibernate.testing.RequiresDialect;
import org.junit.Test;


/**
 * Tests simple auditing process (read and write operations) when <i>REVINFO</i> and audit tables
 * exist in a different database schema.
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@RequiresDialect({ H2Dialect.class })
public class DifferentDBSchemaTest extends BaseEnversJPAFunctionalTestCase {
    private static final String SCHEMA_NAME = "ENVERS_AUDIT";

    private Integer steId = null;

    @Test
    @Priority(10)
    public void initData() throws InterruptedException {
        // Revision 1
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        StrTestEntity ste = new StrTestEntity("x");
        em.persist(ste);
        steId = ste.getId();
        em.getTransaction().commit();
        // Revision 2
        em.getTransaction().begin();
        ste = em.find(StrTestEntity.class, steId);
        ste.setStr("y");
        em.getTransaction().commit();
    }

    @Test
    public void testRevinfoSchemaName() {
        Table revisionTable = metadata().getEntityBinding("org.hibernate.envers.enhanced.SequenceIdRevisionEntity").getTable();
        assert DifferentDBSchemaTest.SCHEMA_NAME.equals(revisionTable.getSchema());
    }

    @Test
    public void testRevisionsCounts() {
        assert Arrays.asList(1, 2).equals(getAuditReader().getRevisions(StrTestEntity.class, steId));
    }

    @Test
    public void testHistoryOfId1() {
        StrTestEntity ver1 = new StrTestEntity("x", steId);
        StrTestEntity ver2 = new StrTestEntity("y", steId);
        assert getAuditReader().find(StrTestEntity.class, steId, 1).equals(ver1);
        assert getAuditReader().find(StrTestEntity.class, steId, 2).equals(ver2);
    }
}

