/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.beanvalidation;


import DialectChecks.SupportsColumnCheck;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.validator.constraints.Range;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vladimir Klyushnikov
 * @author Hardy Ferentschik
 */
public class DDLWithoutCallbackTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @RequiresDialectFeature(SupportsColumnCheck.class)
    public void testListeners() {
        CupHolder ch = new CupHolder();
        ch.setRadius(new BigDecimal("12"));
        assertDatabaseConstraintViolationThrown(ch);
    }

    @Test
    @RequiresDialectFeature(SupportsColumnCheck.class)
    public void testMinAndMaxChecksGetApplied() {
        MinMax minMax = new MinMax(1);
        assertDatabaseConstraintViolationThrown(minMax);
        minMax = new MinMax(11);
        assertDatabaseConstraintViolationThrown(minMax);
        final MinMax validMinMax = new MinMax(5);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(validMinMax);
        });
    }

    @Test
    @RequiresDialectFeature(SupportsColumnCheck.class)
    public void testRangeChecksGetApplied() {
        DDLWithoutCallbackTest.RangeEntity range = new DDLWithoutCallbackTest.RangeEntity(1);
        assertDatabaseConstraintViolationThrown(range);
        range = new DDLWithoutCallbackTest.RangeEntity(11);
        assertDatabaseConstraintViolationThrown(range);
        DDLWithoutCallbackTest.RangeEntity validRange = new DDLWithoutCallbackTest.RangeEntity(5);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(validRange);
        });
    }

    @Test
    public void testDDLEnabled() {
        PersistentClass classMapping = metadata().getEntityBinding(Address.class.getName());
        Column countryColumn = ((Column) (classMapping.getProperty("country").getColumnIterator().next()));
        Assert.assertFalse("DDL constraints are not applied", countryColumn.isNullable());
    }

    @Entity(name = "RangeEntity")
    public static class RangeEntity {
        @Id
        @GeneratedValue
        private Long id;

        @Range(min = 2, max = 10)
        private Integer rangeProperty;

        private RangeEntity() {
        }

        public RangeEntity(Integer value) {
            this.rangeProperty = value;
        }
    }
}

