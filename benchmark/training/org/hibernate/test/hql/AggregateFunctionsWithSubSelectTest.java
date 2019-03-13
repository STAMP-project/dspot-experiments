/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author bjoern.moritz
 */
@TestForIssue(jiraKey = "HHH-9331")
@RequiresDialect(H2Dialect.class)
public class AggregateFunctionsWithSubSelectTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSum() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List results = session.createQuery(("SELECT " + ((((((((((("	d.id, " + "	SUM(") + "		(") + "			SELECT COUNT(localized) ") + "			FROM Person p ") + "			LEFT JOIN p.localized localized ") + "			WHERE p.id = c.id") + "		)") + "	) AS localizedCount ") + "FROM Document d ") + "LEFT JOIN d.contacts c ") + "GROUP BY d.id"))).getResultList();
            assertEquals(1, results.size());
            Object[] tuple = ((Object[]) (results.get(0)));
            assertEquals(1, tuple[0]);
        });
    }

    @Test
    public void testMin() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List results = session.createQuery(("SELECT " + ((((((((((("	d.id, " + "	MIN(") + "		(") + "			SELECT COUNT(localized) ") + "			FROM Person p ") + "			LEFT JOIN p.localized localized ") + "			WHERE p.id = c.id") + "		)") + "	) AS localizedCount ") + "FROM Document d ") + "LEFT JOIN d.contacts c ") + "GROUP BY d.id"))).getResultList();
            assertEquals(1, results.size());
            Object[] tuple = ((Object[]) (results.get(0)));
            assertEquals(1, tuple[0]);
        });
    }

    @Test
    public void testMax() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List results = session.createQuery(("SELECT " + ((((((((((("	d.id, " + "	MAX(") + "		(") + "			SELECT COUNT(localized) ") + "			FROM Person p ") + "			LEFT JOIN p.localized localized ") + "			WHERE p.id = c.id") + "		)") + "	) AS localizedCount ") + "FROM Document d ") + "LEFT JOIN d.contacts c ") + "GROUP BY d.id"))).getResultList();
            assertEquals(1, results.size());
            Object[] tuple = ((Object[]) (results.get(0)));
            assertEquals(1, tuple[0]);
        });
    }

    @Test
    public void testAvg() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List results = session.createQuery(("SELECT " + ((((((((((("	d.id, " + "	AVG(") + "		(") + "			SELECT COUNT(localized) ") + "			FROM Person p ") + "			LEFT JOIN p.localized localized ") + "			WHERE p.id = c.id") + "		)") + "	) AS localizedCount ") + "FROM Document d ") + "LEFT JOIN d.contacts c ") + "GROUP BY d.id"))).getResultList();
            assertEquals(1, results.size());
            Object[] tuple = ((Object[]) (results.get(0)));
            assertEquals(1, tuple[0]);
        });
    }

    @Entity(name = "Document")
    public static class Document {
        private Integer id;

        private Map<Integer, AggregateFunctionsWithSubSelectTest.Person> contacts = new HashMap<>();

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @OneToMany
        @CollectionTable
        @MapKeyColumn(name = "position")
        public Map<Integer, AggregateFunctionsWithSubSelectTest.Person> getContacts() {
            return contacts;
        }

        public void setContacts(Map<Integer, AggregateFunctionsWithSubSelectTest.Person> contacts) {
            this.contacts = contacts;
        }
    }

    @Entity(name = "Person")
    public static class Person {
        private Integer id;

        private Map<Integer, String> localized = new HashMap<>();

        @Id
        @GeneratedValue
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @ElementCollection
        public Map<Integer, String> getLocalized() {
            return localized;
        }

        public void setLocalized(Map<Integer, String> localized) {
            this.localized = localized;
        }
    }
}

