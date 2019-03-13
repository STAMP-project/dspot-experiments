/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.typeoverride;


import BlobTypeDescriptor.BLOB_BINDING;
import BlobTypeDescriptor.DEFAULT;
import BlobTypeDescriptor.PRIMITIVE_ARRAY_BINDING;
import IntegerTypeDescriptor.INSTANCE;
import org.hibernate.Session;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.dialect.SybaseDialect;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.type.descriptor.sql.IntegerTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class TypeOverrideTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testStandardBasicSqlTypeDescriptor() {
        // no override
        Assert.assertSame(INSTANCE, remapSqlTypeDescriptor(INSTANCE));
        // A few dialects explicitly override BlobTypeDescriptor.DEFAULT
        if ((PostgreSQL81Dialect.class.isInstance(getDialect())) || (PostgreSQLDialect.class.isInstance(getDialect()))) {
            Assert.assertSame(BLOB_BINDING, getDialect().remapSqlTypeDescriptor(DEFAULT));
        } else
            if (SybaseDialect.class.isInstance(getDialect())) {
                Assert.assertSame(PRIMITIVE_ARRAY_BINDING, getDialect().remapSqlTypeDescriptor(DEFAULT));
            } else
                if (AbstractHANADialect.class.isInstance(getDialect())) {
                    Assert.assertSame(getBlobTypeDescriptor(), getDialect().remapSqlTypeDescriptor(DEFAULT));
                } else {
                    Assert.assertSame(DEFAULT, getDialect().remapSqlTypeDescriptor(DEFAULT));
                }


    }

    @Test
    public void testNonStandardSqlTypeDescriptor() {
        // no override
        SqlTypeDescriptor sqlTypeDescriptor = new IntegerTypeDescriptor() {
            @Override
            public boolean canBeRemapped() {
                return false;
            }
        };
        Assert.assertSame(sqlTypeDescriptor, remapSqlTypeDescriptor(sqlTypeDescriptor));
    }

    @Test
    public void testDialectWithNonStandardSqlTypeDescriptor() {
        Assert.assertNotSame(VarcharTypeDescriptor.INSTANCE, getSqlTypeDescriptor());
        final Dialect dialect = new H2DialectOverridePrefixedVarcharSqlTypeDesc();
        final SqlTypeDescriptor remapped = remapSqlTypeDescriptor(dialect, StoredPrefixedStringType.PREFIXED_VARCHAR_TYPE_DESCRIPTOR);
        Assert.assertSame(VarcharTypeDescriptor.INSTANCE, remapped);
    }

    @Test
    public void testInsert() {
        Session s = openSession();
        s.getTransaction().begin();
        Entity e = new Entity("name");
        s.save(e);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        e = ((Entity) (s.get(Entity.class, e.getId())));
        Assert.assertFalse(e.getName().startsWith(StoredPrefixedStringType.PREFIX));
        Assert.assertEquals("name", e.getName());
        s.delete(e);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = SybaseASE15Dialect.class, jiraKey = "HHH-6426")
    public void testRegisteredFunction() {
        Session s = openSession();
        s.getTransaction().begin();
        Entity e = new Entity("name ");
        s.save(e);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        e = ((Entity) (s.get(Entity.class, e.getId())));
        Assert.assertFalse(e.getName().startsWith(StoredPrefixedStringType.PREFIX));
        Assert.assertEquals("name ", e.getName());
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        s.delete(e);
        s.getTransaction().commit();
        s.close();
    }
}

