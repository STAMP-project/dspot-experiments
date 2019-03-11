/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.customtype;


import IntegerType.INSTANCE;
import java.util.Arrays;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.envers.test.BaseEnversFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.customtype.UnspecifiedEnumTypeEntity;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;

import static org.hibernate.envers.test.entities.customtype.UnspecifiedEnumTypeEntity.E1.X;
import static org.hibernate.envers.test.entities.customtype.UnspecifiedEnumTypeEntity.E1.Y;
import static org.hibernate.envers.test.entities.customtype.UnspecifiedEnumTypeEntity.E2.A;
import static org.hibernate.envers.test.entities.customtype.UnspecifiedEnumTypeEntity.E2.B;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@TestForIssue(jiraKey = "HHH-7780")
public class UnspecifiedEnumTypeTest extends BaseEnversFunctionalTestCase {
    private Long id = null;

    @Test
    @Priority(9)
    public void initData() {
        Session session = getSession();
        // Revision 1
        session.getTransaction().begin();
        UnspecifiedEnumTypeEntity entity = new UnspecifiedEnumTypeEntity(X, A);
        session.persist(entity);
        session.getTransaction().commit();
        id = entity.getId();
        // Revision 2
        session.getTransaction().begin();
        entity = ((UnspecifiedEnumTypeEntity) (session.get(UnspecifiedEnumTypeEntity.class, entity.getId())));
        entity.setEnum1(Y);
        entity.setEnum2(B);
        session.update(entity);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @Priority(8)
    public void testRevisionCount() {
        Assert.assertEquals(Arrays.asList(1, 2), getAuditReader().getRevisions(UnspecifiedEnumTypeEntity.class, id));
    }

    @Test
    @Priority(7)
    public void testHistoryOfEnums() {
        UnspecifiedEnumTypeEntity ver1 = new UnspecifiedEnumTypeEntity(X, A, id);
        UnspecifiedEnumTypeEntity ver2 = new UnspecifiedEnumTypeEntity(Y, B, id);
        Assert.assertEquals(ver1, getAuditReader().find(UnspecifiedEnumTypeEntity.class, id, 1));
        Assert.assertEquals(ver2, getAuditReader().find(UnspecifiedEnumTypeEntity.class, id, 2));
    }

    @Test
    @Priority(6)
    public void testEnumRepresentation() {
        Session session = getSession();
        @SuppressWarnings("unchecked")
        List<Object[]> values = session.createNativeQuery("SELECT enum1 e1, enum2 e2 FROM ENUM_ENTITY_AUD ORDER BY rev ASC").addScalar("e1", INSTANCE).addScalar("e2", INSTANCE).list();
        session.close();
        Assert.assertNotNull(values);
        Assert.assertEquals(2, values.size());
        Assert.assertArrayEquals(new Object[]{ 0, 0 }, values.get(0));
        Assert.assertArrayEquals(new Object[]{ 1, 1 }, values.get(1));
    }
}

