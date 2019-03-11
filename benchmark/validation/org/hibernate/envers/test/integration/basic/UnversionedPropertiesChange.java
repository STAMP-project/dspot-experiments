/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.basic;


import java.util.Arrays;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.junit.Test;


/**
 *
 *
 * @author Adam Warski (adam at warski dot org)
 */
public class UnversionedPropertiesChange extends BaseEnversJPAFunctionalTestCase {
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
    public void testRevisionsCounts() {
        assert Arrays.asList(1, 2).equals(getAuditReader().getRevisions(BasicTestEntity2.class, id1));
    }

    @Test
    public void testHistoryOfId1() {
        BasicTestEntity2 ver1 = new BasicTestEntity2(id1, "x", null);
        BasicTestEntity2 ver2 = new BasicTestEntity2(id1, "y", null);
        assert getAuditReader().find(BasicTestEntity2.class, id1, 1).equals(ver1);
        assert getAuditReader().find(BasicTestEntity2.class, id1, 2).equals(ver2);
    }
}

