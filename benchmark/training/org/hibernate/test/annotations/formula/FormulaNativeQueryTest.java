/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.formula;


import org.hibernate.annotations.Formula;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author ??????? ???????
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-7525")
public class FormulaNativeQueryTest extends BaseCoreFunctionalTestCase {
    @Test
    @FailureExpected(jiraKey = "HHH-7525")
    public void testNativeQuery() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createNativeQuery("SELECT ft.* FROM foo_table ft", .class);
            List<org.hibernate.test.annotations.formula.Foo> list = query.getResultList();
            assertEquals(3, list.size());
        });
    }

    @Test
    public void testHql() throws Exception {
        // Show that HQL does work
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery("SELECT ft FROM Foo ft", .class);
            List<org.hibernate.test.annotations.formula.Foo> list = query.getResultList();
            assertEquals(3, list.size());
        });
    }

    @Entity(name = "Foo")
    @Table(name = "foo_table")
    public static class Foo {
        private int id;

        private int locationStart;

        private int locationEnd;

        private int distance;

        public Foo() {
        }

        public Foo(int locationStart, int locationEnd) {
            this.locationStart = locationStart;
            this.locationEnd = locationEnd;
        }

        @Id
        @GeneratedValue
        public int getId() {
            return id;
        }

        private void setId(int id) {
            this.id = id;
        }

        public int getLocationStart() {
            return locationStart;
        }

        public void setLocationStart(int locationStart) {
            this.locationStart = locationStart;
        }

        public int getLocationEnd() {
            return locationEnd;
        }

        public void setLocationEnd(int locationEnd) {
            this.locationEnd = locationEnd;
        }

        @Formula("abs(locationEnd - locationStart)")
        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }
    }
}

