/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.modifiedflags;


import java.util.List;
import junit.framework.Assert;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.integration.basic.BasicTestEntity1;
import org.hibernate.envers.test.tools.TestTools;
import org.junit.Test;


/**
 *
 *
 * @author Adam Warski (adam at warski dot org)
 * @author Michal Skowronek (mskowr at o2 dot pl)
 */
public class HasChangedNullProperties extends AbstractModifiedFlagsEntityTest {
    private Integer id1;

    private Integer id2;

    @Test
    @Priority(10)
    public void initData() {
        id1 = addNewEntity("x", 1);// rev 1

        id2 = addNewEntity(null, 20);// rev 2

        modifyEntity(id1, null, 1);// rev 3

        modifyEntity(id2, "y2", 20);// rev 4

    }

    @Test
    public void testHasChanged() throws Exception {
        List list = queryForPropertyHasChangedWithDeleted(BasicTestEntity1.class, id1, "str1");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(TestTools.makeList(1, 3), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasChangedWithDeleted(BasicTestEntity1.class, id1, "long1");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(1), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasChangedWithDeleted(BasicTestEntity1.class, id2, "str1");
        // str1 property was null before insert and after insert so in a way it didn't change - is it a good way to go?
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(4), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasChangedWithDeleted(BasicTestEntity1.class, id2, "long1");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(2), TestTools.extractRevisionNumbers(list));
        list = getAuditReader().createQuery().forRevisionsOfEntity(BasicTestEntity1.class, false, true).add(AuditEntity.property("str1").hasChanged()).add(AuditEntity.property("long1").hasChanged()).getResultList();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(1), TestTools.extractRevisionNumbers(list));
    }
}

