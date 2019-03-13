/**
 * Copyright 2003 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.cglib.beans;


import java.beans.PropertyDescriptor;
import junit.framework.TestCase;
import net.sf.cglib.core.ReflectUtils;


/**
 *
 *
 * @author Juozas Baliuka, Chris Nokleberg
 */
public class TestBeanGenerator extends TestCase {
    public void testSimple() throws Exception {
        BeanGenerator bg = new BeanGenerator();
        bg.addProperty("sin", Double.TYPE);
        Object bean = bg.create();
        PropertyDescriptor[] pds = ReflectUtils.getBeanProperties(bean.getClass());
        TestCase.assertTrue(((pds.length) == 1));
        TestCase.assertTrue(pds[0].getName().equals("sin"));
        TestCase.assertTrue(pds[0].getPropertyType().equals(Double.TYPE));
    }

    public void testSuperclass() throws Exception {
        BeanGenerator bg = new BeanGenerator();
        bg.setSuperclass(MA.class);
        bg.addProperty("sin", Double.TYPE);
        Object bean = bg.create();
        TestCase.assertTrue((bean instanceof MA));
        TestCase.assertTrue(BeanMap.create(bean).keySet().contains("sin"));
    }

    public TestBeanGenerator(String testName) {
        super(testName);
    }
}

