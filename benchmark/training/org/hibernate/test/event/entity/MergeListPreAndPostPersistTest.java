/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.event.entity;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-9979")
public class MergeListPreAndPostPersistTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9979")
    public void testAllPropertiesCopied() {
        final MergeListPreAndPostPersistTest.Order order = new MergeListPreAndPostPersistTest.Order();
        order.id = 1L;
        order.name = "order";
        MergeListPreAndPostPersistTest.Item item = new MergeListPreAndPostPersistTest.Item();
        item.id = 1L;
        item.name = "item";
        order.items.add(item);
        addEntityListeners(order);
        Session s = openSession();
        s.getTransaction().begin();
        s.merge(order);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        s.delete(order);
        s.getTransaction().commit();
        s.close();
    }

    @Entity(name = "`Order`")
    private static class Order {
        @Id
        public Long id;

        @Basic(optional = false)
        public String name;

        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
        public List<MergeListPreAndPostPersistTest.Item> items = new ArrayList<MergeListPreAndPostPersistTest.Item>();

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            MergeListPreAndPostPersistTest.Order order = ((MergeListPreAndPostPersistTest.Order) (o));
            return name.equals(order.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    @Entity(name = "Item")
    private static class Item {
        @Id
        public Long id;

        @Basic(optional = false)
        public String name;

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            MergeListPreAndPostPersistTest.Item item = ((MergeListPreAndPostPersistTest.Item) (o));
            return name.equals(item.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}

