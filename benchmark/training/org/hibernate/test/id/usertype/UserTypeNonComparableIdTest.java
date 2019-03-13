/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.id.usertype;


import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.type.LongType;
import org.hibernate.usertype.UserType;
import org.junit.Test;


public class UserTypeNonComparableIdTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-8999")
    public void testUserTypeId() {
        Session s = openSession();
        s.beginTransaction();
        UserTypeNonComparableIdTest.SomeEntity e1 = new UserTypeNonComparableIdTest.SomeEntity();
        UserTypeNonComparableIdTest.CustomId e1Id = new UserTypeNonComparableIdTest.CustomId(1L);
        e1.setCustomId(e1Id);
        UserTypeNonComparableIdTest.SomeEntity e2 = new UserTypeNonComparableIdTest.SomeEntity();
        UserTypeNonComparableIdTest.CustomId e2Id = new UserTypeNonComparableIdTest.CustomId(2L);
        e2.setCustomId(e2Id);
        s.persist(e1);
        s.persist(e2);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        e1 = s.get(UserTypeNonComparableIdTest.SomeEntity.class, e1Id);
        e2 = s.get(UserTypeNonComparableIdTest.SomeEntity.class, e2Id);
        s.delete(e1);
        s.delete(e2);
        s.getTransaction().commit();
        s.close();
    }

    @TypeDef(name = "customId", typeClass = UserTypeNonComparableIdTest.CustomIdType.class)
    @Entity
    @Table(name = "some_entity")
    public static class SomeEntity {
        @Id
        @Type(type = "customId")
        @Column(name = "id")
        private UserTypeNonComparableIdTest.CustomId customId;

        public UserTypeNonComparableIdTest.CustomId getCustomId() {
            return customId;
        }

        public void setCustomId(final UserTypeNonComparableIdTest.CustomId customId) {
            this.customId = customId;
        }
    }

    public static class CustomId implements Serializable {
        private final Long value;

        public CustomId(final Long value) {
            this.value = value;
        }

        public Long getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            UserTypeNonComparableIdTest.CustomId customId = ((UserTypeNonComparableIdTest.CustomId) (o));
            return !((value) != null ? !(value.equals(customId.value)) : (customId.value) != null);
        }

        @Override
        public int hashCode() {
            return (value) != null ? value.hashCode() : 0;
        }
    }

    public static class CustomIdType implements UserType {
        public static final LongType SQL_TYPE = LongType.INSTANCE;

        @Override
        public int[] sqlTypes() {
            return new int[]{ UserTypeNonComparableIdTest.CustomIdType.SQL_TYPE.sqlType() };
        }

        @Override
        public Object nullSafeGet(ResultSet resultSet, String[] names, SharedSessionContractImplementor sessionImplementor, Object o) throws SQLException, HibernateException {
            Long value = resultSet.getLong(names[0]);
            return new UserTypeNonComparableIdTest.CustomId(value);
        }

        @Override
        public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index, SharedSessionContractImplementor sessionImplementor) throws SQLException, HibernateException {
            UserTypeNonComparableIdTest.CustomId customId = ((UserTypeNonComparableIdTest.CustomId) (value));
            if (customId == null) {
                preparedStatement.setNull(index, UserTypeNonComparableIdTest.CustomIdType.SQL_TYPE.sqlType());
            } else {
                preparedStatement.setLong(index, customId.getValue());
            }
        }

        @Override
        public Class returnedClass() {
            return UserTypeNonComparableIdTest.CustomId.class;
        }

        @Override
        public boolean equals(Object x, Object y) throws HibernateException {
            return x.equals(y);
        }

        @Override
        public int hashCode(Object x) throws HibernateException {
            return x.hashCode();
        }

        @Override
        public Object deepCopy(Object value) throws HibernateException {
            return value;
        }

        @Override
        public boolean isMutable() {
            return true;
        }

        @Override
        public Serializable disassemble(Object value) throws HibernateException {
            return ((Serializable) (value));
        }

        @Override
        public Object assemble(Serializable cached, Object owner) throws HibernateException {
            return cached;
        }

        @Override
        public Object replace(Object original, Object target, Object owner) throws HibernateException {
            return original;
        }
    }
}

