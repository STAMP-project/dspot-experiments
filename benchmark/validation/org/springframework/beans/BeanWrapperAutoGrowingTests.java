/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.beans;


import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 */
public class BeanWrapperAutoGrowingTests {
    private final BeanWrapperAutoGrowingTests.Bean bean = new BeanWrapperAutoGrowingTests.Bean();

    private final BeanWrapperImpl wrapper = new BeanWrapperImpl(bean);

    @Test
    public void getPropertyValueNullValueInNestedPath() {
        Assert.assertNull(wrapper.getPropertyValue("nested.prop"));
    }

    @Test
    public void setPropertyValueNullValueInNestedPath() {
        wrapper.setPropertyValue("nested.prop", "test");
        Assert.assertEquals("test", bean.getNested().getProp());
    }

    @Test(expected = NullValueInNestedPathException.class)
    public void getPropertyValueNullValueInNestedPathNoDefaultConstructor() {
        wrapper.getPropertyValue("nestedNoConstructor.prop");
    }

    @Test
    public void getPropertyValueAutoGrowArray() {
        Assert.assertNotNull(wrapper.getPropertyValue("array[0]"));
        Assert.assertEquals(1, bean.getArray().length);
        Assert.assertThat(bean.getArray()[0], instanceOf(BeanWrapperAutoGrowingTests.Bean.class));
    }

    @Test
    public void setPropertyValueAutoGrowArray() {
        wrapper.setPropertyValue("array[0].prop", "test");
        Assert.assertEquals("test", bean.getArray()[0].getProp());
    }

    @Test
    public void getPropertyValueAutoGrowArrayBySeveralElements() {
        Assert.assertNotNull(wrapper.getPropertyValue("array[4]"));
        Assert.assertEquals(5, bean.getArray().length);
        Assert.assertThat(bean.getArray()[0], instanceOf(BeanWrapperAutoGrowingTests.Bean.class));
        Assert.assertThat(bean.getArray()[1], instanceOf(BeanWrapperAutoGrowingTests.Bean.class));
        Assert.assertThat(bean.getArray()[2], instanceOf(BeanWrapperAutoGrowingTests.Bean.class));
        Assert.assertThat(bean.getArray()[3], instanceOf(BeanWrapperAutoGrowingTests.Bean.class));
        Assert.assertThat(bean.getArray()[4], instanceOf(BeanWrapperAutoGrowingTests.Bean.class));
        Assert.assertNotNull(wrapper.getPropertyValue("array[0]"));
        Assert.assertNotNull(wrapper.getPropertyValue("array[1]"));
        Assert.assertNotNull(wrapper.getPropertyValue("array[2]"));
        Assert.assertNotNull(wrapper.getPropertyValue("array[3]"));
    }

    @Test
    public void getPropertyValueAutoGrowMultiDimensionalArray() {
        Assert.assertNotNull(wrapper.getPropertyValue("multiArray[0][0]"));
        Assert.assertEquals(1, bean.getMultiArray()[0].length);
        Assert.assertThat(bean.getMultiArray()[0][0], instanceOf(BeanWrapperAutoGrowingTests.Bean.class));
    }

    @Test
    public void getPropertyValueAutoGrowList() {
        Assert.assertNotNull(wrapper.getPropertyValue("list[0]"));
        Assert.assertEquals(1, bean.getList().size());
        Assert.assertThat(bean.getList().get(0), instanceOf(BeanWrapperAutoGrowingTests.Bean.class));
    }

    @Test
    public void setPropertyValueAutoGrowList() {
        wrapper.setPropertyValue("list[0].prop", "test");
        Assert.assertEquals("test", bean.getList().get(0).getProp());
    }

    @Test
    public void getPropertyValueAutoGrowListBySeveralElements() {
        Assert.assertNotNull(wrapper.getPropertyValue("list[4]"));
        Assert.assertEquals(5, bean.getList().size());
        Assert.assertThat(bean.getList().get(0), instanceOf(BeanWrapperAutoGrowingTests.Bean.class));
        Assert.assertThat(bean.getList().get(1), instanceOf(BeanWrapperAutoGrowingTests.Bean.class));
        Assert.assertThat(bean.getList().get(2), instanceOf(BeanWrapperAutoGrowingTests.Bean.class));
        Assert.assertThat(bean.getList().get(3), instanceOf(BeanWrapperAutoGrowingTests.Bean.class));
        Assert.assertThat(bean.getList().get(4), instanceOf(BeanWrapperAutoGrowingTests.Bean.class));
        Assert.assertNotNull(wrapper.getPropertyValue("list[0]"));
        Assert.assertNotNull(wrapper.getPropertyValue("list[1]"));
        Assert.assertNotNull(wrapper.getPropertyValue("list[2]"));
        Assert.assertNotNull(wrapper.getPropertyValue("list[3]"));
    }

    @Test
    public void getPropertyValueAutoGrowListFailsAgainstLimit() {
        wrapper.setAutoGrowCollectionLimit(2);
        try {
            Assert.assertNotNull(wrapper.getPropertyValue("list[4]"));
            Assert.fail("Should have thrown InvalidPropertyException");
        } catch (InvalidPropertyException ex) {
            // expected
            Assert.assertTrue(((ex.getRootCause()) instanceof IndexOutOfBoundsException));
        }
    }

    @Test
    public void getPropertyValueAutoGrowMultiDimensionalList() {
        Assert.assertNotNull(wrapper.getPropertyValue("multiList[0][0]"));
        Assert.assertEquals(1, bean.getMultiList().get(0).size());
        Assert.assertThat(bean.getMultiList().get(0).get(0), instanceOf(BeanWrapperAutoGrowingTests.Bean.class));
    }

    @Test(expected = InvalidPropertyException.class)
    public void getPropertyValueAutoGrowListNotParameterized() {
        wrapper.getPropertyValue("listNotParameterized[0]");
    }

    @Test
    public void setPropertyValueAutoGrowMap() {
        wrapper.setPropertyValue("map[A]", new BeanWrapperAutoGrowingTests.Bean());
        Assert.assertThat(bean.getMap().get("A"), instanceOf(BeanWrapperAutoGrowingTests.Bean.class));
    }

    @Test
    public void setNestedPropertyValueAutoGrowMap() {
        wrapper.setPropertyValue("map[A].nested", new BeanWrapperAutoGrowingTests.Bean());
        Assert.assertThat(bean.getMap().get("A").getNested(), instanceOf(BeanWrapperAutoGrowingTests.Bean.class));
    }

    public static class Bean {
        private String prop;

        private BeanWrapperAutoGrowingTests.Bean nested;

        private BeanWrapperAutoGrowingTests.NestedNoDefaultConstructor nestedNoConstructor;

        private BeanWrapperAutoGrowingTests.Bean[] array;

        private BeanWrapperAutoGrowingTests.Bean[][] multiArray;

        private List<BeanWrapperAutoGrowingTests.Bean> list;

        private List<List<BeanWrapperAutoGrowingTests.Bean>> multiList;

        private List listNotParameterized;

        private Map<String, BeanWrapperAutoGrowingTests.Bean> map;

        public String getProp() {
            return prop;
        }

        public void setProp(String prop) {
            this.prop = prop;
        }

        public BeanWrapperAutoGrowingTests.Bean getNested() {
            return nested;
        }

        public void setNested(BeanWrapperAutoGrowingTests.Bean nested) {
            this.nested = nested;
        }

        public BeanWrapperAutoGrowingTests.Bean[] getArray() {
            return array;
        }

        public void setArray(BeanWrapperAutoGrowingTests.Bean[] array) {
            this.array = array;
        }

        public BeanWrapperAutoGrowingTests.Bean[][] getMultiArray() {
            return multiArray;
        }

        public void setMultiArray(BeanWrapperAutoGrowingTests.Bean[][] multiArray) {
            this.multiArray = multiArray;
        }

        public List<BeanWrapperAutoGrowingTests.Bean> getList() {
            return list;
        }

        public void setList(List<BeanWrapperAutoGrowingTests.Bean> list) {
            this.list = list;
        }

        public List<List<BeanWrapperAutoGrowingTests.Bean>> getMultiList() {
            return multiList;
        }

        public void setMultiList(List<List<BeanWrapperAutoGrowingTests.Bean>> multiList) {
            this.multiList = multiList;
        }

        public BeanWrapperAutoGrowingTests.NestedNoDefaultConstructor getNestedNoConstructor() {
            return nestedNoConstructor;
        }

        public void setNestedNoConstructor(BeanWrapperAutoGrowingTests.NestedNoDefaultConstructor nestedNoConstructor) {
            this.nestedNoConstructor = nestedNoConstructor;
        }

        public List getListNotParameterized() {
            return listNotParameterized;
        }

        public void setListNotParameterized(List listNotParameterized) {
            this.listNotParameterized = listNotParameterized;
        }

        public Map<String, BeanWrapperAutoGrowingTests.Bean> getMap() {
            return map;
        }

        public void setMap(Map<String, BeanWrapperAutoGrowingTests.Bean> map) {
            this.map = map;
        }
    }

    public static class NestedNoDefaultConstructor {
        private NestedNoDefaultConstructor() {
        }
    }
}

