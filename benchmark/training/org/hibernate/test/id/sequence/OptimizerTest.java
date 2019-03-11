/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.id.sequence;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hamcrest.CoreMatchers;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class OptimizerTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10166")
    public void testGenerationPastBound() {
        Session session = openSession();
        session.getTransaction().begin();
        for (int i = 0; i < 100; i++) {
            OptimizerTest.TheEntity entity = new OptimizerTest.TheEntity(Integer.toString(i));
            session.save(entity);
        }
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        OptimizerTest.TheEntity number100 = session.get(OptimizerTest.TheEntity.class, 100);
        Assert.assertThat(number100, CoreMatchers.notNullValue());
        session.createQuery("delete TheEntity").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    @Entity(name = "TheEntity")
    @Table(name = "TheEntity")
    public static class TheEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq1")
        @SequenceGenerator(name = "seq1", sequenceName = "the_sequence")
        public Integer id;

        public String someString;

        public TheEntity() {
        }

        public TheEntity(String someString) {
            this.someString = someString;
        }
    }
}

