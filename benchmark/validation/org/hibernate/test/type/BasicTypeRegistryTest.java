/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type;


import UUIDBinaryType.INSTANCE;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.BasicType;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.CompositeCustomType;
import org.hibernate.type.CustomType;
import org.hibernate.type.Type;
import org.hibernate.type.UUIDCharType;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class BasicTypeRegistryTest extends BaseUnitTestCase {
    @Test
    public void testOverriding() {
        BasicTypeRegistry registry = new BasicTypeRegistry();
        BasicType type = registry.getRegisteredType("uuid-binary");
        Assert.assertSame(INSTANCE, type);
        type = registry.getRegisteredType(UUID.class.getName());
        Assert.assertSame(INSTANCE, type);
        BasicType override = new UUIDCharType() {
            @Override
            protected boolean registerUnderJavaType() {
                return true;
            }
        };
        registry.register(override);
        type = registry.getRegisteredType(UUID.class.getName());
        Assert.assertNotSame(INSTANCE, type);
        Assert.assertSame(override, type);
    }

    @Test
    public void testExpanding() {
        BasicTypeRegistry registry = new BasicTypeRegistry();
        BasicType type = registry.getRegisteredType(BasicTypeRegistryTest.SomeNoopType.INSTANCE.getName());
        Assert.assertNull(type);
        registry.register(BasicTypeRegistryTest.SomeNoopType.INSTANCE);
        type = registry.getRegisteredType(BasicTypeRegistryTest.SomeNoopType.INSTANCE.getName());
        Assert.assertNotNull(type);
        Assert.assertSame(BasicTypeRegistryTest.SomeNoopType.INSTANCE, type);
    }

    @Test
    public void testRegisteringUserTypes() {
        BasicTypeRegistry registry = new BasicTypeRegistry();
        registry.register(new BasicTypeRegistryTest.TotallyIrrelevantUserType(), new String[]{ "key" });
        BasicType type = registry.getRegisteredType("key");
        Assert.assertNotNull(type);
        Assert.assertEquals(CustomType.class, type.getClass());
        Assert.assertEquals(BasicTypeRegistryTest.TotallyIrrelevantUserType.class, getUserType().getClass());
        registry.register(new BasicTypeRegistryTest.TotallyIrrelevantCompositeUserType(), new String[]{ "key" });
        type = registry.getRegisteredType("key");
        Assert.assertNotNull(type);
        Assert.assertEquals(CompositeCustomType.class, type.getClass());
        Assert.assertEquals(BasicTypeRegistryTest.TotallyIrrelevantCompositeUserType.class, getUserType().getClass());
        type = registry.getRegisteredType(UUID.class.getName());
        Assert.assertSame(INSTANCE, type);
        registry.register(new BasicTypeRegistryTest.TotallyIrrelevantUserType(), new String[]{ UUID.class.getName() });
        type = registry.getRegisteredType(UUID.class.getName());
        Assert.assertNotSame(INSTANCE, type);
        Assert.assertEquals(CustomType.class, type.getClass());
    }

    public static class SomeNoopType extends AbstractSingleColumnStandardBasicType<String> {
        public static final BasicTypeRegistryTest.SomeNoopType INSTANCE = new BasicTypeRegistryTest.SomeNoopType();

        public SomeNoopType() {
            super(VarcharTypeDescriptor.INSTANCE, StringTypeDescriptor.INSTANCE);
        }

        public String getName() {
            return "noop";
        }

        @Override
        protected boolean registerUnderJavaType() {
            return false;
        }
    }

    public static class TotallyIrrelevantUserType implements UserType {
        @Override
        public int[] sqlTypes() {
            return new int[0];
        }

        @Override
        public Class returnedClass() {
            return null;
        }

        @Override
        public boolean equals(Object x, Object y) throws HibernateException {
            return false;
        }

        @Override
        public int hashCode(Object x) throws HibernateException {
            return 0;
        }

        @Override
        public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws SQLException, HibernateException {
            return null;
        }

        @Override
        public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        }

        @Override
        public Object deepCopy(Object value) throws HibernateException {
            return null;
        }

        @Override
        public boolean isMutable() {
            return false;
        }

        @Override
        public Serializable disassemble(Object value) throws HibernateException {
            return null;
        }

        @Override
        public Object assemble(Serializable cached, Object owner) throws HibernateException {
            return null;
        }

        @Override
        public Object replace(Object original, Object target, Object owner) throws HibernateException {
            return null;
        }
    }

    public static class TotallyIrrelevantCompositeUserType implements CompositeUserType {
        @Override
        public String[] getPropertyNames() {
            return new String[0];
        }

        @Override
        public Type[] getPropertyTypes() {
            return new Type[0];
        }

        @Override
        public Object getPropertyValue(Object component, int property) throws HibernateException {
            return null;
        }

        @Override
        public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
        }

        @Override
        public Class returnedClass() {
            return null;
        }

        @Override
        public boolean equals(Object x, Object y) throws HibernateException {
            return false;
        }

        @Override
        public int hashCode(Object x) throws HibernateException {
            return 0;
        }

        @Override
        public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws SQLException, HibernateException {
            return null;
        }

        @Override
        public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        }

        @Override
        public Object deepCopy(Object value) throws HibernateException {
            return null;
        }

        @Override
        public boolean isMutable() {
            return false;
        }

        @Override
        public Serializable disassemble(Object value, SharedSessionContractImplementor session) throws HibernateException {
            return null;
        }

        @Override
        public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {
            return null;
        }

        @Override
        public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner) throws HibernateException {
            return null;
        }
    }
}

