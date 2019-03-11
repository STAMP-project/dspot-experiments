/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.management.jmx;


import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import org.flowable.management.jmx.testMbeans.BadAttributeGetterHavinParameter;
import org.flowable.management.jmx.testMbeans.BadAttributeGetterNameNotCapital;
import org.flowable.management.jmx.testMbeans.BadAttributeNameNoGetterSetter;
import org.flowable.management.jmx.testMbeans.BadAttributeSetterHavinReturn;
import org.flowable.management.jmx.testMbeans.BadAttributeSetterNameNotCapital;
import org.flowable.management.jmx.testMbeans.BadAttributeVoid;
import org.flowable.management.jmx.testMbeans.BadInherited;
import org.flowable.management.jmx.testMbeans.NotManagedMBean;
import org.flowable.management.jmx.testMbeans.TestMbean;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Saeid Mirzaei
 */
public class MBeanInfoAssemblerTest {
    protected TestMbean testMbean = new TestMbean();

    protected MBeanInfoAssembler mbeanInfoAssembler = new MBeanInfoAssembler();

    @Test
    public void testNullInputs() throws JMException {
        // at least one of the first parameters should be not null
        Assert.assertNull(mbeanInfoAssembler.getMBeanInfo(null, null, ""));
        // mbean should be not null
        Assert.assertNull(mbeanInfoAssembler.getMBeanInfo(testMbean, testMbean, null));
        // it should return something if at least one of the first parameters
        // are
        // not null
        NotManagedMBean notManagedMbean = new NotManagedMBean();
        Assert.assertNotNull(mbeanInfoAssembler.getMBeanInfo(null, notManagedMbean, "someName"));
        Assert.assertNotNull(mbeanInfoAssembler.getMBeanInfo(notManagedMbean, null, "someName"));
    }

    @Test
    public void testReadAttributeInfoHappyPath() throws JMException {
        ModelMBeanInfo beanInfo = mbeanInfoAssembler.getMBeanInfo(testMbean, null, "someName");
        Assert.assertNotNull(beanInfo);
        Assert.assertEquals("test description", beanInfo.getDescription());
        MBeanAttributeInfo[] testAttributes = beanInfo.getAttributes();
        Assert.assertNotNull(testAttributes);
        Assert.assertEquals(2, testAttributes.length);
        int counter = 0;
        for (MBeanAttributeInfo info : testAttributes) {
            if (info.getName().equals("TestAttributeBoolean")) {
                counter++;
                Assert.assertEquals("test attribute Boolean description", info.getDescription());
                Assert.assertEquals("java.lang.Boolean", info.getType());
                Assert.assertTrue(info.isReadable());
                Assert.assertFalse(info.isWritable());
            } else
                if (info.getName().equals("TestAttributeString")) {
                    counter++;
                    Assert.assertEquals("test attribute String description", info.getDescription());
                    Assert.assertEquals("java.lang.String", info.getType());
                    Assert.assertTrue(info.isReadable());
                    Assert.assertFalse(info.isWritable());
                }

        }
        Assert.assertEquals(2, counter);
        // check the single operation
        Assert.assertNotNull(beanInfo.getOperations());
        Assert.assertEquals(3, beanInfo.getOperations().length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAttributeGetterNameNotCapitial() throws JMException {
        mbeanInfoAssembler.getMBeanInfo(new BadAttributeGetterNameNotCapital(), null, "someName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAttributePOJONamingNoGetter() throws JMException {
        mbeanInfoAssembler.getMBeanInfo(new BadAttributeNameNoGetterSetter(), null, "someName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAttributeSetterNameNotCapitial() throws JMException {
        mbeanInfoAssembler.getMBeanInfo(new BadAttributeSetterNameNotCapital(), null, "someName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAttributeHavingParameter() throws JMException {
        mbeanInfoAssembler.getMBeanInfo(new BadAttributeGetterHavinParameter(), null, "someName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAttributeSetterHavingResult() throws JMException {
        mbeanInfoAssembler.getMBeanInfo(new BadAttributeSetterHavinReturn(), null, "someName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAttributeVoid() throws JMException {
        mbeanInfoAssembler.getMBeanInfo(new BadAttributeVoid(), null, "someName");
    }

    @Test
    public void testInherited() throws JMException {
        ModelMBeanInfo beanInfo = mbeanInfoAssembler.getMBeanInfo(new BadInherited(), null, "someName");
        Assert.assertNotNull(beanInfo);
        Assert.assertNotNull(beanInfo.getAttributes());
        Assert.assertEquals(2, beanInfo.getAttributes().length);
        Assert.assertNotNull(beanInfo.getOperations());
        Assert.assertEquals(3, beanInfo.getOperations().length);
    }
}

