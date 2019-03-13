/**
 * Copyright 2002-2013 the original author or authors.
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
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Chris Beams
 */
public class MethodExclusionMBeanInfoAssemblerNotMappedTests extends AbstractJmxAssemblerTests {
    protected static final String OBJECT_NAME = "bean:name=testBean4";

    @Test
    public void testGetAgeIsReadOnly() throws Exception {
        ModelMBeanInfo info = getMBeanInfoFromAssembler();
        ModelMBeanAttributeInfo attr = info.getAttribute(AbstractJmxAssemblerTests.AGE_ATTRIBUTE);
        Assert.assertTrue("Age is not readable", attr.isReadable());
        Assert.assertTrue("Age is not writable", attr.isWritable());
    }

    @Test
    public void testNickNameIsExposed() throws Exception {
        ModelMBeanInfo inf = ((ModelMBeanInfo) (getMBeanInfo()));
        MBeanAttributeInfo attr = inf.getAttribute("NickName");
        Assert.assertNotNull("Nick Name should not be null", attr);
        Assert.assertTrue("Nick Name should be writable", attr.isWritable());
        Assert.assertTrue("Nick Name should be readable", attr.isReadable());
    }
}

