/**
 * Copyright 2002-2018 the original author or authors.
 *
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
package org.springframework.jmx.export.assembler;


import javax.management.MBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rob Harrop
 * @author Chris Beams
 */
public class InterfaceBasedMBeanInfoAssemblerMappedTests extends AbstractJmxAssemblerTests {
    protected static final String OBJECT_NAME = "bean:name=testBean4";

    @Test
    public void testGetAgeIsReadOnly() throws Exception {
        ModelMBeanInfo info = getMBeanInfoFromAssembler();
        ModelMBeanAttributeInfo attr = info.getAttribute(AbstractJmxAssemblerTests.AGE_ATTRIBUTE);
        Assert.assertTrue("Age is not readable", attr.isReadable());
        Assert.assertFalse("Age is not writable", attr.isWritable());
    }

    @Test
    public void testWithUnknownClass() throws Exception {
        try {
            getWithMapping("com.foo.bar.Unknown");
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testWithNonInterface() throws Exception {
        try {
            getWithMapping("JmxTestBean");
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testWithFallThrough() throws Exception {
        InterfaceBasedMBeanInfoAssembler assembler = getWithMapping("foobar", "org.springframework.jmx.export.assembler.ICustomJmxBean");
        assembler.setManagedInterfaces(new Class<?>[]{ IAdditionalTestMethods.class });
        ModelMBeanInfo inf = assembler.getMBeanInfo(getBean(), getObjectName());
        MBeanAttributeInfo attr = inf.getAttribute("NickName");
        assertNickName(attr);
    }

    @Test
    public void testNickNameIsExposed() throws Exception {
        ModelMBeanInfo inf = ((ModelMBeanInfo) (getMBeanInfo()));
        MBeanAttributeInfo attr = inf.getAttribute("NickName");
        assertNickName(attr);
    }
}

