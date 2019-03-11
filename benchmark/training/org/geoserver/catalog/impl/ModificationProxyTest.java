/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ModificationProxyTest {
    @Test
    public void testRewrapNoProxyIdentity() throws Exception {
        ModificationProxyTest.TestBean bean = new ModificationProxyTest.TestBeanImpl("Mr. Bean", "Uhh", "Bean");
        ModificationProxyTest.TestBean result = ModificationProxy.rewrap(bean, ( b) -> b, ModificationProxyTest.TestBean.class);
        Assert.assertThat(result, Matchers.sameInstance(bean));
    }

    @Test
    public void testRewrapNoProxyInnerChange() throws Exception {
        ModificationProxyTest.TestBean bean = new ModificationProxyTest.TestBeanImpl("Mr. Bean", "Uhh", "Bean");
        ModificationProxyTest.TestBean newBean = new ModificationProxyTest.TestBeanImpl("Johnny English", "Not", "Bond");
        ModificationProxyTest.TestBean result = ModificationProxy.rewrap(bean, ( b) -> newBean, ModificationProxyTest.TestBean.class);
        Assert.assertThat(result, Matchers.sameInstance(newBean));
    }

    @Test
    public void testRewrapEmptyProxyIdentity() throws Exception {
        ModificationProxyTest.TestBean bean = new ModificationProxyTest.TestBeanImpl("Mr. Bean", "Uhh", "Bean");
        ModificationProxyTest.TestBean proxy = ModificationProxy.create(bean, ModificationProxyTest.TestBean.class);
        ModificationProxyTest.TestBean result = ModificationProxy.rewrap(proxy, ( b) -> b, ModificationProxyTest.TestBean.class);
        Assert.assertThat(result, ModificationProxyTest.modProxy(Matchers.sameInstance(bean)));
        Assert.assertThat(result.getValue(), Matchers.equalTo("Mr. Bean"));
        Assert.assertThat(result.getListValue(), Matchers.contains("Uhh", "Bean"));
    }

    @Test
    public void testRewrapChangedProxyIdentity() throws Exception {
        ModificationProxyTest.TestBean bean = new ModificationProxyTest.TestBeanImpl("Mr. Bean", "Uhh", "Bean");
        ModificationProxyTest.TestBean proxy = ModificationProxy.create(bean, ModificationProxyTest.TestBean.class);
        proxy.setValue("Edmond Blackadder");
        proxy.setListValue(Arrays.asList("Cunning", "Plan"));
        ModificationProxyTest.TestBean result = ModificationProxy.rewrap(proxy, ( b) -> b, ModificationProxyTest.TestBean.class);
        // Should be a new wrapper
        Assert.assertThat(result, Matchers.not(Matchers.sameInstance(proxy)));
        // Wrapping the same object
        Assert.assertThat(result, ModificationProxyTest.modProxy(Matchers.sameInstance(bean)));
        // With the same changes
        Assert.assertThat(result.getValue(), Matchers.equalTo("Edmond Blackadder"));
        Assert.assertThat(result.getListValue(), Matchers.contains("Cunning", "Plan"));
        // The changes should not have been committed
        Assert.assertThat(bean.getValue(), Matchers.equalTo("Mr. Bean"));
        Assert.assertThat(bean.getListValue(), Matchers.contains("Uhh", "Bean"));
    }

    @Test
    public void testRewrapChangedProxyInnerChange() throws Exception {
        ModificationProxyTest.TestBean bean = new ModificationProxyTest.TestBeanImpl("Mr. Bean", "Uhh", "Bean");
        ModificationProxyTest.TestBean newBean = new ModificationProxyTest.TestBeanImpl("Johnny English", "Not", "Bond");
        ModificationProxyTest.TestBean proxy = ModificationProxy.create(bean, ModificationProxyTest.TestBean.class);
        proxy.setValue("Edmond Blackadder");
        proxy.setListValue(Arrays.asList("Cunning", "Plan"));
        // Swap the old bean for the new one
        ModificationProxyTest.TestBean result = ModificationProxy.rewrap(proxy, ( b) -> newBean, ModificationProxyTest.TestBean.class);
        // Should be a new wrapper
        Assert.assertThat(result, Matchers.not(Matchers.sameInstance(proxy)));
        // Wrapping the new object
        Assert.assertThat(result, ModificationProxyTest.modProxy(Matchers.sameInstance(newBean)));
        // With the same changes
        Assert.assertThat(result.getValue(), Matchers.equalTo("Edmond Blackadder"));
        Assert.assertThat(result.getListValue(), Matchers.contains("Cunning", "Plan"));
        // The changes should not have been committed to either bean
        Assert.assertThat(bean.getValue(), Matchers.equalTo("Mr. Bean"));
        Assert.assertThat(bean.getListValue(), Matchers.contains("Uhh", "Bean"));
        Assert.assertThat(newBean.getValue(), Matchers.equalTo("Johnny English"));
        Assert.assertThat(newBean.getListValue(), Matchers.contains("Not", "Bond"));
    }

    @Test
    public void testRewrapEmptyProxyInnerChange() throws Exception {
        ModificationProxyTest.TestBean bean = new ModificationProxyTest.TestBeanImpl("Mr. Bean", "Uhh", "Bean");
        ModificationProxyTest.TestBean newBean = new ModificationProxyTest.TestBeanImpl("Johnny English", "Not", "Bond");
        ModificationProxyTest.TestBean proxy = ModificationProxy.create(bean, ModificationProxyTest.TestBean.class);
        // Swap the old bean for the new one
        ModificationProxyTest.TestBean result = ModificationProxy.rewrap(proxy, ( b) -> newBean, ModificationProxyTest.TestBean.class);
        // Should be a new wrapper
        Assert.assertThat(result, Matchers.not(Matchers.sameInstance(proxy)));
        // Wrapping the new object
        Assert.assertThat(result, ModificationProxyTest.modProxy(Matchers.sameInstance(newBean)));
        // Should show the properties of the new bean
        Assert.assertThat(result.getValue(), Matchers.equalTo("Johnny English"));
        Assert.assertThat(result.getListValue(), Matchers.contains("Not", "Bond"));
        // No changes should not have been committed to either bean
        Assert.assertThat(bean.getValue(), Matchers.equalTo("Mr. Bean"));
        Assert.assertThat(bean.getListValue(), Matchers.contains("Uhh", "Bean"));
        Assert.assertThat(newBean.getValue(), Matchers.equalTo("Johnny English"));
        Assert.assertThat(newBean.getListValue(), Matchers.contains("Not", "Bond"));
    }

    @Test
    public void testRewrapCommitToNew() throws Exception {
        ModificationProxyTest.TestBean bean = new ModificationProxyTest.TestBeanImpl("Mr. Bean", "Uhh", "Bean");
        ModificationProxyTest.TestBean newBean = new ModificationProxyTest.TestBeanImpl("Johnny English", "Not", "Bond");
        ModificationProxyTest.TestBean proxy = ModificationProxy.create(bean, ModificationProxyTest.TestBean.class);
        proxy.setValue("Edmond Blackadder");
        proxy.setListValue(Arrays.asList("Cunning", "Plan"));
        // Swap the old bean for the new one
        ModificationProxyTest.TestBean result = ModificationProxy.rewrap(proxy, ( b) -> newBean, ModificationProxyTest.TestBean.class);
        // Commit the changes
        ModificationProxy.handler(result).commit();
        // The changes should not have been committed to either bean
        Assert.assertThat(bean.getValue(), Matchers.equalTo("Mr. Bean"));
        Assert.assertThat(bean.getListValue(), Matchers.contains("Uhh", "Bean"));
        Assert.assertThat(newBean.getValue(), Matchers.equalTo("Edmond Blackadder"));
        Assert.assertThat(newBean.getListValue(), Matchers.contains("Cunning", "Plan"));
    }

    static interface TestBean {
        public String getValue();

        public void setValue(String value);

        public List<String> getListValue();

        public void setListValue(List<String> listValue);
    }

    static class TestBeanImpl implements ModificationProxyTest.TestBean {
        String value;

        List<String> listValue;

        public TestBeanImpl(String value, List<String> listValue) {
            super();
            this.value = value;
            this.listValue = new ArrayList<>(listValue);
        }

        public TestBeanImpl(String value, String... listValues) {
            this(value, Arrays.asList(listValues));
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public List<String> getListValue() {
            return listValue;
        }

        public void setListValue(List<String> listValue) {
            this.listValue = listValue;
        }
    }
}

