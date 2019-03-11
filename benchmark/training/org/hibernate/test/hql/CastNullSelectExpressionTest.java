/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-10757")
public class CastNullSelectExpressionTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10757")
    public void testSelectCastNull() {
        Session s = openSession();
        s.getTransaction().begin();
        CastNullSelectExpressionTest.Person person = new CastNullSelectExpressionTest.Person();
        person.firstName = "Herman";
        person.middleName = "Joseph";
        person.lastName = "Munster";
        s.persist(person);
        s.flush();
        s.clear();
        Object[] result = ((Object[]) (s.createQuery("select firstName, cast( null as string ), lastName from CastNullSelectExpressionTest$Person where lastName='Munster'").uniqueResult()));
        Assert.assertEquals(3, result.length);
        Assert.assertEquals("Herman", result[0]);
        Assert.assertNull(result[1]);
        Assert.assertEquals("Munster", result[2]);
        s.getTransaction().rollback();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10757")
    public void testSelectNewCastNull() {
        Session s = openSession();
        s.getTransaction().begin();
        CastNullSelectExpressionTest.Person person = new CastNullSelectExpressionTest.Person();
        person.firstName = "Herman";
        person.middleName = "Joseph";
        person.lastName = "Munster";
        s.persist(person);
        s.flush();
        s.clear();
        CastNullSelectExpressionTest.Person result = ((CastNullSelectExpressionTest.Person) (s.createQuery("select new CastNullSelectExpressionTest$Person( id, firstName, cast( null as string ), lastName ) from CastNullSelectExpressionTest$Person where lastName='Munster'").uniqueResult()));
        Assert.assertEquals("Herman", result.firstName);
        Assert.assertNull(result.middleName);
        Assert.assertEquals("Munster", result.lastName);
        s.getTransaction().rollback();
        s.close();
    }

    @Entity
    @Table(name = "PERSON")
    private static class Person {
        @Id
        @GeneratedValue
        private long id;

        private String firstName;

        private String middleName;

        private String lastName;

        private Integer age;

        Person() {
        }

        public Person(long id, String firstName, String middleName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.middleName = middleName;
            this.lastName = lastName;
        }

        public Person(long id, String firstName, Integer age, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.middleName = null;
            this.lastName = lastName;
            this.age = age;
        }
    }
}

