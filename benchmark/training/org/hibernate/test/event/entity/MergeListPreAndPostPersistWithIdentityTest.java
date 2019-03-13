/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.event.entity;


import DialectChecks.SupportsIdentityColumns;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.Session;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-9979")
@RequiresDialectFeature(value = SupportsIdentityColumns.class, jiraKey = "HHH-9918")
public class MergeListPreAndPostPersistWithIdentityTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9979")
    @FailureExpected(jiraKey = "HHH-9979")
    public void testAllPropertiesCopied() {
        final MergeListPreAndPostPersistWithIdentityTest.Order order = new MergeListPreAndPostPersistWithIdentityTest.Order();
        order.id = 1L;
        order.name = "order";
        // Item.id is an identity so don't initialize it.
        MergeListPreAndPostPersistWithIdentityTest.Item item = new MergeListPreAndPostPersistWithIdentityTest.Item();
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

    @Entity
    private static class Order {
        @Id
        public Long id;

        @Basic(optional = false)
        public String name;

        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
        public List<MergeListPreAndPostPersistWithIdentityTest.Item> items = new ArrayList<MergeListPreAndPostPersistWithIdentityTest.Item>();

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            MergeListPreAndPostPersistWithIdentityTest.Order order = ((MergeListPreAndPostPersistWithIdentityTest.Order) (o));
            return name.equals(order.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    @Entity
    private static class Item {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
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
            MergeListPreAndPostPersistWithIdentityTest.Item item = ((MergeListPreAndPostPersistWithIdentityTest.Item) (o));
            return name.equals(item.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}

