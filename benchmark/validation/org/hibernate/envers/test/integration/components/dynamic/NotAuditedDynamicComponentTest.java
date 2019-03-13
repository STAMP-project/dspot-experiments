/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.components.dynamic;


import java.util.Arrays;
import junit.framework.Assert;
import org.hibernate.Session;
import org.hibernate.envers.test.BaseEnversFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@TestForIssue(jiraKey = "HHH-8049")
public class NotAuditedDynamicComponentTest extends BaseEnversFunctionalTestCase {
    @Test
    @Priority(10)
    public void initData() {
        Session session = openSession();
        // Revision 1
        session.getTransaction().begin();
        NotAuditedDynamicMapComponent entity = new NotAuditedDynamicMapComponent(1L, "static field value");
        entity.getCustomFields().put("prop1", 13);
        entity.getCustomFields().put("prop2", 0.1F);
        session.save(entity);
        session.getTransaction().commit();
        // No revision
        session.getTransaction().begin();
        entity = ((NotAuditedDynamicMapComponent) (session.get(NotAuditedDynamicMapComponent.class, entity.getId())));
        entity.getCustomFields().put("prop1", 0);
        session.update(entity);
        session.getTransaction().commit();
        // Revision 2
        session.getTransaction().begin();
        entity = ((NotAuditedDynamicMapComponent) (session.get(NotAuditedDynamicMapComponent.class, entity.getId())));
        entity.setNote("updated note");
        session.update(entity);
        session.getTransaction().commit();
        // Revision 3
        session.getTransaction().begin();
        entity = ((NotAuditedDynamicMapComponent) (session.load(NotAuditedDynamicMapComponent.class, entity.getId())));
        session.delete(entity);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testRevisionsCounts() {
        Assert.assertEquals(Arrays.asList(1, 2, 3), getAuditReader().getRevisions(NotAuditedDynamicMapComponent.class, 1L));
    }

    @Test
    public void testHistoryOfId1() {
        // Revision 1
        NotAuditedDynamicMapComponent entity = new NotAuditedDynamicMapComponent(1L, "static field value");
        NotAuditedDynamicMapComponent ver1 = getAuditReader().find(NotAuditedDynamicMapComponent.class, entity.getId(), 1);
        Assert.assertEquals(entity, ver1);
        // Assume empty NotAuditedDynamicMapComponent#customFields map, because dynamic-component is not audited.
        Assert.assertTrue(ver1.getCustomFields().isEmpty());
        // Revision 2
        entity.setNote("updated note");
        NotAuditedDynamicMapComponent ver2 = getAuditReader().find(NotAuditedDynamicMapComponent.class, entity.getId(), 2);
        Assert.assertEquals(entity, ver2);
        // Assume empty NotAuditedDynamicMapComponent#customFields map, because dynamic-component is not audited.
        Assert.assertTrue(ver2.getCustomFields().isEmpty());
    }
}

