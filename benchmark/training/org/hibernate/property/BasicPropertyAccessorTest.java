/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.property;


import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class BasicPropertyAccessorTest extends BaseUnitTestCase {
    public abstract static class Super {
        public abstract Object getIt();

        public abstract void setIt(Object it);
    }

    public static class Duper extends BasicPropertyAccessorTest.Super {
        private String it;

        public Duper(String it) {
            this.it = it;
        }

        public String getIt() {
            return it;
        }

        @Override
        public void setIt(Object it) {
            this.it = ((it == null) || (String.class.isInstance(it))) ? ((String) (it)) : it.toString();
        }
    }

    public static class Duper2 extends BasicPropertyAccessorTest.Super {
        private String it;

        public Duper2(String it) {
            this.it = it;
        }

        public String getIt() {
            return it;
        }

        public void setIt(String it) {
            this.it = it;
        }

        @Override
        public void setIt(Object it) {
            if ((it == null) || (String.class.isInstance(it))) {
                setIt(((String) (it)));
            } else {
                setIt(it.toString());
            }
        }
    }

    @Test
    public void testBridgeMethodDisregarded() {
        PropertyAccessStrategyBasicImpl accessStrategy = PropertyAccessStrategyBasicImpl.INSTANCE;
        {
            final PropertyAccess access = accessStrategy.buildPropertyAccess(BasicPropertyAccessorTest.Duper.class, "it");
            Assert.assertEquals(String.class, access.getGetter().getReturnType());
            Assert.assertEquals(Object.class, access.getSetter().getMethod().getParameterTypes()[0]);
        }
        {
            final PropertyAccess access = accessStrategy.buildPropertyAccess(BasicPropertyAccessorTest.Duper2.class, "it");
            Assert.assertEquals(String.class, access.getGetter().getReturnType());
            Assert.assertEquals(String.class, access.getSetter().getMethod().getParameterTypes()[0]);
        }
    }
}

