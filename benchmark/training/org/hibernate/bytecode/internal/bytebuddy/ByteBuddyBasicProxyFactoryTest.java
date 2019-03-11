/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.bytecode.internal.bytebuddy;


import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-12786")
public class ByteBuddyBasicProxyFactoryTest {
    private static final BasicProxyFactoryImpl BASIC_PROXY_FACTORY = new BasicProxyFactoryImpl(ByteBuddyBasicProxyFactoryTest.Entity.class, new Class[0], new ByteBuddyState());

    @Test
    public void testEqualsHashCode() {
        Object entityProxy = ByteBuddyBasicProxyFactoryTest.BASIC_PROXY_FACTORY.getProxy();
        Assert.assertTrue(entityProxy.equals(entityProxy));
        Assert.assertNotNull(entityProxy.hashCode());
        Object otherEntityProxy = ByteBuddyBasicProxyFactoryTest.BASIC_PROXY_FACTORY.getProxy();
        Assert.assertFalse(entityProxy.equals(otherEntityProxy));
    }

    @Test
    public void testToString() {
        Object entityProxy = ByteBuddyBasicProxyFactoryTest.BASIC_PROXY_FACTORY.getProxy();
        Assert.assertTrue(entityProxy.toString().contains("HibernateBasicProxy"));
    }

    @Test
    public void testGetterSetter() {
        ByteBuddyBasicProxyFactoryTest.Entity entityProxy = ((ByteBuddyBasicProxyFactoryTest.Entity) (ByteBuddyBasicProxyFactoryTest.BASIC_PROXY_FACTORY.getProxy()));
        entityProxy.setBool(true);
        Assert.assertTrue(entityProxy.isBool());
        entityProxy.setBool(false);
        Assert.assertFalse(entityProxy.isBool());
        entityProxy.setString("John Irving");
        Assert.assertEquals("John Irving", entityProxy.getString());
    }

    @Test
    public void testNonGetterSetterMethod() {
        ByteBuddyBasicProxyFactoryTest.Entity entityProxy = ((ByteBuddyBasicProxyFactoryTest.Entity) (ByteBuddyBasicProxyFactoryTest.BASIC_PROXY_FACTORY.getProxy()));
        Assert.assertNull(entityProxy.otherMethod());
    }

    public static class Entity {
        private String string;

        private boolean bool;

        public Entity() {
        }

        public boolean isBool() {
            return bool;
        }

        public void setBool(boolean bool) {
            this.bool = bool;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public String otherMethod() {
            return "a string";
        }
    }
}

