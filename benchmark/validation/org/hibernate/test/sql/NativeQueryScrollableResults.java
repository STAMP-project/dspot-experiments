/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.sql;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-11033")
public class NativeQueryScrollableResults extends BaseCoreFunctionalTestCase {
    @Test
    public void testSetParameters() {
        final List params = new ArrayList();
        params.add(new BigInteger("2"));
        params.add(new BigInteger("3"));
        try (Session s = openSession()) {
            final Query query = s.createNativeQuery("select e.big from MY_ENTITY e where e.big in (:bigValues)").setParameter("bigValues", params);
            try (ScrollableResults scroll = query.scroll()) {
                while (scroll.next()) {
                    Assert.assertThat(scroll.get()[0], IsNot.not(IsNull.nullValue()));
                } 
            }
        }
    }

    @Entity(name = "MyEntity")
    @Table(name = "MY_ENTITY")
    public static class MyEntity {
        @Id
        private Long id;

        private BigInteger big;

        private String description;

        public MyEntity() {
        }

        public MyEntity(Long id, String description, BigInteger big) {
            this.id = id;
            this.description = description;
            this.big = big;
        }

        public String getDescription() {
            return description;
        }
    }
}

