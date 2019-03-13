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
public class NullProperties extends BaseEnversJPAFunctionalTestCase {
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
    public void testRevisionsCounts() {
        assert Arrays.asList(1, 3).equals(getAuditReader().getRevisions(BasicTestEntity1.class, id1));
        assert Arrays.asList(2, 4).equals(getAuditReader().getRevisions(BasicTestEntity1.class, id2));
    }

    @Test
    public void testHistoryOfId1() {
        BasicTestEntity1 ver1 = new BasicTestEntity1(id1, "x", 1);
        BasicTestEntity1 ver2 = new BasicTestEntity1(id1, null, 1);
        assert getAuditReader().find(BasicTestEntity1.class, id1, 1).equals(ver1);
        assert getAuditReader().find(BasicTestEntity1.class, id1, 2).equals(ver1);
        assert getAuditReader().find(BasicTestEntity1.class, id1, 3).equals(ver2);
        assert getAuditReader().find(BasicTestEntity1.class, id1, 4).equals(ver2);
    }

    @Test
    public void testHistoryOfId2() {
        BasicTestEntity1 ver1 = new BasicTestEntity1(id2, null, 20);
        BasicTestEntity1 ver2 = new BasicTestEntity1(id2, "y2", 20);
        assert (getAuditReader().find(BasicTestEntity1.class, id2, 1)) == null;
        assert getAuditReader().find(BasicTestEntity1.class, id2, 2).equals(ver1);
        assert getAuditReader().find(BasicTestEntity1.class, id2, 3).equals(ver1);
        assert getAuditReader().find(BasicTestEntity1.class, id2, 4).equals(ver2);
    }
}

