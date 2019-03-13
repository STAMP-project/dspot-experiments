/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode;


import org.hibernate.bytecode.internal.javassist.BulkAccessor;
import org.hibernate.bytecode.spi.BytecodeProvider;
import org.hibernate.bytecode.spi.ReflectionOptimizer;
import org.hibernate.cfg.Environment;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ReflectionOptimizerTest extends BaseUnitTestCase {
    @Test
    public void testBulkAccessorDirectly() {
        BulkAccessor bulkAccessor = BulkAccessor.create(Bean.class, BeanReflectionHelper.getGetterNames(), BeanReflectionHelper.getSetterNames(), BeanReflectionHelper.getTypes());
    }

    @Test
    public void testReflectionOptimization() {
        BytecodeProvider provider = Environment.getBytecodeProvider();
        ReflectionOptimizer optimizer = provider.getReflectionOptimizer(Bean.class, BeanReflectionHelper.getGetterNames(), BeanReflectionHelper.getSetterNames(), BeanReflectionHelper.getTypes());
        Assert.assertNotNull(optimizer);
        Assert.assertNotNull(optimizer.getInstantiationOptimizer());
        Assert.assertNotNull(optimizer.getAccessOptimizer());
        Object instance = optimizer.getInstantiationOptimizer().newInstance();
        Assert.assertEquals(instance.getClass(), Bean.class);
        Bean bean = ((Bean) (instance));
        optimizer.getAccessOptimizer().setPropertyValues(bean, BeanReflectionHelper.TEST_VALUES);
        Assert.assertEquals(bean.getSomeString(), BeanReflectionHelper.TEST_VALUES[0]);
        Object[] values = optimizer.getAccessOptimizer().getPropertyValues(bean);
        assertEquivalent(values, BeanReflectionHelper.TEST_VALUES);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12584")
    public void testAbstractClass() {
        BytecodeProvider provider = Environment.getBytecodeProvider();
        ReflectionOptimizer reflectionOptimizer = provider.getReflectionOptimizer(ReflectionOptimizerTest.AbstractClass.class, new String[]{ "getProperty" }, new String[]{ "setProperty" }, new Class[]{ String.class });
        Assert.assertNotNull(reflectionOptimizer);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12584")
    public void testInterface() {
        BytecodeProvider provider = Environment.getBytecodeProvider();
        ReflectionOptimizer reflectionOptimizer = provider.getReflectionOptimizer(ReflectionOptimizerTest.Interface.class, new String[]{ "getProperty" }, new String[]{ "setProperty" }, new Class[]{ String.class });
        Assert.assertNotNull(reflectionOptimizer);
    }

    public abstract static class AbstractClass {
        private String property;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }
    }

    public interface Interface {
        String getProperty();

        void setProperty(String property);
    }
}

