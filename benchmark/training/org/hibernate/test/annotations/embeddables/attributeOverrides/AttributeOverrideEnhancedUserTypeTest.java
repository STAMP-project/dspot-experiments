package org.hibernate.test.annotations.embeddables.attributeOverrides;


import IntegerType.INSTANCE;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import org.hibernate.HibernateException;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.usertype.UserType;
import org.junit.Test;


/**
 *
 *
 * @author Andr?s Eisenberger
 */
public class AttributeOverrideEnhancedUserTypeTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11465")
    public void testIt() {
        AttributeOverrideEnhancedUserTypeTest.AggregatedTypeValue _e1 = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.annotations.embeddables.attributeOverrides.AggregatedTypeValue e1 = new org.hibernate.test.annotations.embeddables.attributeOverrides.AggregatedTypeValue();
            e1.id = 1L;
            org.hibernate.test.annotations.embeddables.attributeOverrides.TypeValue t1 = new org.hibernate.test.annotations.embeddables.attributeOverrides.TypeValue();
            t1.time = YearMonth.of(2017, 5);
            e1.oneValue = t1;
            org.hibernate.test.annotations.embeddables.attributeOverrides.TypeValue t2 = new org.hibernate.test.annotations.embeddables.attributeOverrides.TypeValue();
            t2.time = YearMonth.of(2016, 4);
            e1.otherValue = t2;
            session.save(e1);
            return e1;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.annotations.embeddables.attributeOverrides.AggregatedTypeValue e1 = session.get(.class, _e1.id);
            assertEquals(e1.oneValue.time, YearMonth.of(2017, 5));
            assertEquals(e1.otherValue.time, YearMonth.of(2016, 4));
            session.delete(e1);
        });
    }

    @Embeddable
    public static class TypeValue {
        @Type(type = "year_month")
        @Columns(columns = { @Column(name = "year", nullable = true), @Column(name = "month", nullable = true) })
        YearMonth time;
    }

    @Entity
    @Table(name = "AGG_TYPE")
    @TypeDef(name = "year_month", typeClass = AttributeOverrideEnhancedUserTypeTest.YearMonthUserType.class)
    public static class AggregatedTypeValue {
        @Id
        private Long id;

        @Embedded
        @AttributeOverrides({ @AttributeOverride(name = "time", column = @Column(name = "one_year")), @AttributeOverride(name = "time", column = @Column(name = "one_month")) })
        private AttributeOverrideEnhancedUserTypeTest.TypeValue oneValue;

        @Embedded
        @AttributeOverrides({ @AttributeOverride(name = "time", column = @Column(name = "other_year")), @AttributeOverride(name = "time", column = @Column(name = "other_month")) })
        private AttributeOverrideEnhancedUserTypeTest.TypeValue otherValue;
    }

    public static class YearMonthUserType implements Serializable , UserType {
        @Override
        public int[] sqlTypes() {
            return new int[]{ INSTANCE.sqlType(), INSTANCE.sqlType() };
        }

        @Override
        public Class returnedClass() {
            return YearMonth.class;
        }

        @Override
        public boolean equals(final Object x, final Object y) throws HibernateException {
            if (x == y) {
                return true;
            }
            if ((x == null) || (y == null)) {
                return false;
            }
            final YearMonth mtx = ((YearMonth) (x));
            final YearMonth mty = ((YearMonth) (y));
            return mtx.equals(mty);
        }

        @Override
        public int hashCode(final Object x) throws HibernateException {
            return x.hashCode();
        }

        @Override
        public Object nullSafeGet(final ResultSet rs, final String[] names, final SharedSessionContractImplementor session, final Object owner) throws SQLException, HibernateException {
            assert (names.length) == 2;
            final Integer year = INSTANCE.nullSafeGet(rs, names[0], session);
            final Integer month = INSTANCE.nullSafeGet(rs, names[1], session);
            return (year == null) || (month == null) ? null : YearMonth.of(year, month);
        }

        @Override
        public void nullSafeSet(final PreparedStatement st, final Object value, final int index, final SharedSessionContractImplementor session) throws SQLException, HibernateException {
            if (value == null) {
                INSTANCE.set(st, null, index, session);
                INSTANCE.set(st, null, (index + 1), session);
            } else {
                final YearMonth YearMonth = ((YearMonth) (value));
                INSTANCE.set(st, YearMonth.getYear(), index, session);
                INSTANCE.set(st, YearMonth.getMonthValue(), (index + 1), session);
            }
        }

        @Override
        public Object deepCopy(final Object value) throws HibernateException {
            return value;
        }

        @Override
        public boolean isMutable() {
            return false;
        }

        @Override
        public Serializable disassemble(final Object value) throws HibernateException {
            return ((Serializable) (value));
        }

        @Override
        public Object assemble(final Serializable cached, final Object value) throws HibernateException {
            return cached;
        }

        @Override
        public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
            return original;
        }
    }
}

