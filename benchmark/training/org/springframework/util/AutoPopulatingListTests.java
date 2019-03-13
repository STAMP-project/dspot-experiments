/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.util;


import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.tests.sample.objects.TestObject;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 */
public class AutoPopulatingListTests {
    @Test
    public void withClass() throws Exception {
        doTestWithClass(new AutoPopulatingList(TestObject.class));
    }

    @Test
    public void withClassAndUserSuppliedBackingList() throws Exception {
        doTestWithClass(new AutoPopulatingList<Object>(new LinkedList(), TestObject.class));
    }

    @Test
    public void withElementFactory() throws Exception {
        doTestWithElementFactory(new AutoPopulatingList(new AutoPopulatingListTests.MockElementFactory()));
    }

    @Test
    public void withElementFactoryAndUserSuppliedBackingList() throws Exception {
        doTestWithElementFactory(new AutoPopulatingList<Object>(new LinkedList(), new AutoPopulatingListTests.MockElementFactory()));
    }

    @Test
    public void serialization() throws Exception {
        AutoPopulatingList<?> list = new AutoPopulatingList<Object>(TestObject.class);
        Assert.assertEquals(list, SerializationTestUtils.serializeAndDeserialize(list));
    }

    private static class MockElementFactory implements AutoPopulatingList.ElementFactory<Object> {
        @Override
        public Object createElement(int index) {
            TestObject bean = new TestObject();
            bean.setAge(index);
            return bean;
        }
    }
}

