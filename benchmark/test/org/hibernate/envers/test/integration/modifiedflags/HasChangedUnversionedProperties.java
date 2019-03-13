/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.modifiedflags;


import java.util.List;
import junit.framework.Assert;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.integration.basic.BasicTestEntity2;
import org.hibernate.envers.test.tools.TestTools;
import org.junit.Test;


/**
 *
 *
 * @author Adam Warski (adam at warski dot org)
 * @author Michal Skowronek (mskowr at o2 dot pl)
 */
public class HasChangedUnversionedProperties extends AbstractModifiedFlagsEntityTest {
    private Integer id1;

    @Test
    @Priority(10)
    public void initData() {
        id1 = addNewEntity("x", "a");// rev 1

        modifyEntity(id1, "x", "a");// no rev

        modifyEntity(id1, "y", "b");// rev 2

        modifyEntity(id1, "y", "c");// no rev

    }

    @Test
    public void testHasChangedQuery() throws Exception {
        List list = queryForPropertyHasChanged(BasicTestEntity2.class, id1, "str1");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(TestTools.makeList(1, 2), TestTools.extractRevisionNumbers(list));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionOnHasChangedQuery() throws Exception {
        queryForPropertyHasChangedWithDeleted(BasicTestEntity2.class, id1, "str2");
    }
}

