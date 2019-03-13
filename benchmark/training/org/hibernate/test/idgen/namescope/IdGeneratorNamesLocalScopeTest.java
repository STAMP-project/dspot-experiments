/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.idgen.namescope;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import org.hamcrest.core.Is;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class IdGeneratorNamesLocalScopeTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testNoSequenceGenratorNameClash() {
        final IdGeneratorNamesLocalScopeTest.FirstEntity first = new IdGeneratorNamesLocalScopeTest.FirstEntity();
        final IdGeneratorNamesLocalScopeTest.SecondEntity second = new IdGeneratorNamesLocalScopeTest.SecondEntity();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(first);
            session.persist(second);
        });
        Assert.assertThat(first.getId(), Is.is(2L));
        Assert.assertThat(second.getId(), Is.is(11L));
    }

    @Entity(name = "FirstEntity")
    @TableGenerator(name = "table-generator", table = "table_identifier_2", pkColumnName = "identifier", valueColumnName = "value", allocationSize = 5, initialValue = 1)
    public static class FirstEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "table-generator")
        private Long id;

        public Long getId() {
            return id;
        }
    }

    @Entity(name = "SecondEntity")
    @TableGenerator(name = "table-generator", table = "table_identifier", pkColumnName = "identifier", valueColumnName = "value", allocationSize = 5, initialValue = 10)
    public static class SecondEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "table-generator")
        private Long id;

        public Long getId() {
            return id;
        }
    }
}

