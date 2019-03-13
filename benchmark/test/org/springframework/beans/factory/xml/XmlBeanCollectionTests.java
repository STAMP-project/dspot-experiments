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
package org.springframework.beans.factory.xml;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.beans.factory.config.SetFactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.tests.sample.beans.HasMap;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Tests for collections in XML bean definitions.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 19.12.2004
 */
public class XmlBeanCollectionTests {
    private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    @Test
    public void testCollectionFactoryDefaults() throws Exception {
        ListFactoryBean listFactory = new ListFactoryBean();
        listFactory.setSourceList(new LinkedList());
        listFactory.afterPropertiesSet();
        Assert.assertTrue(((listFactory.getObject()) instanceof ArrayList));
        SetFactoryBean setFactory = new SetFactoryBean();
        setFactory.setSourceSet(new TreeSet());
        setFactory.afterPropertiesSet();
        Assert.assertTrue(((setFactory.getObject()) instanceof LinkedHashSet));
        MapFactoryBean mapFactory = new MapFactoryBean();
        mapFactory.setSourceMap(new TreeMap());
        mapFactory.afterPropertiesSet();
        Assert.assertTrue(((mapFactory.getObject()) instanceof LinkedHashMap));
    }

    @Test
    public void testRefSubelement() throws Exception {
        // assertTrue("5 beans in reftypes, not " + this.beanFactory.getBeanDefinitionCount(), this.beanFactory.getBeanDefinitionCount() == 5);
        TestBean jen = ((TestBean) (this.beanFactory.getBean("jenny")));
        TestBean dave = ((TestBean) (this.beanFactory.getBean("david")));
        Assert.assertTrue(((jen.getSpouse()) == dave));
    }

    @Test
    public void testPropertyWithLiteralValueSubelement() throws Exception {
        TestBean verbose = ((TestBean) (this.beanFactory.getBean("verbose")));
        Assert.assertTrue(verbose.getName().equals("verbose"));
    }

    @Test
    public void testPropertyWithIdRefLocalAttrSubelement() throws Exception {
        TestBean verbose = ((TestBean) (this.beanFactory.getBean("verbose2")));
        Assert.assertTrue(verbose.getName().equals("verbose"));
    }

    @Test
    public void testPropertyWithIdRefBeanAttrSubelement() throws Exception {
        TestBean verbose = ((TestBean) (this.beanFactory.getBean("verbose3")));
        Assert.assertTrue(verbose.getName().equals("verbose"));
    }

    @Test
    public void testRefSubelementsBuildCollection() throws Exception {
        TestBean jen = ((TestBean) (this.beanFactory.getBean("jenny")));
        TestBean dave = ((TestBean) (this.beanFactory.getBean("david")));
        TestBean rod = ((TestBean) (this.beanFactory.getBean("rod")));
        // Must be a list to support ordering
        // Our bean doesn't modify the collection:
        // of course it could be a different copy in a real object.
        Object[] friends = rod.getFriends().toArray();
        Assert.assertTrue(((friends.length) == 2));
        Assert.assertTrue(("First friend must be jen, not " + (friends[0])), ((friends[0]) == jen));
        Assert.assertTrue(((friends[1]) == dave));
        // Should be ordered
    }

    @Test
    public void testRefSubelementsBuildCollectionWithPrototypes() throws Exception {
        TestBean jen = ((TestBean) (this.beanFactory.getBean("pJenny")));
        TestBean dave = ((TestBean) (this.beanFactory.getBean("pDavid")));
        TestBean rod = ((TestBean) (this.beanFactory.getBean("pRod")));
        Object[] friends = rod.getFriends().toArray();
        Assert.assertTrue(((friends.length) == 2));
        Assert.assertTrue(("First friend must be jen, not " + (friends[0])), friends[0].toString().equals(jen.toString()));
        Assert.assertTrue("Jen not same instance", ((friends[0]) != jen));
        Assert.assertTrue(friends[1].toString().equals(dave.toString()));
        Assert.assertTrue("Dave not same instance", ((friends[1]) != dave));
        Assert.assertEquals("Jen", dave.getSpouse().getName());
        TestBean rod2 = ((TestBean) (this.beanFactory.getBean("pRod")));
        Object[] friends2 = rod2.getFriends().toArray();
        Assert.assertTrue(((friends2.length) == 2));
        Assert.assertTrue(("First friend must be jen, not " + (friends2[0])), friends2[0].toString().equals(jen.toString()));
        Assert.assertTrue("Jen not same instance", ((friends2[0]) != (friends[0])));
        Assert.assertTrue(friends2[1].toString().equals(dave.toString()));
        Assert.assertTrue("Dave not same instance", ((friends2[1]) != (friends[1])));
    }

    @Test
    public void testRefSubelementsBuildCollectionFromSingleElement() throws Exception {
        TestBean loner = ((TestBean) (this.beanFactory.getBean("loner")));
        TestBean dave = ((TestBean) (this.beanFactory.getBean("david")));
        Assert.assertTrue(((loner.getFriends().size()) == 1));
        Assert.assertTrue(loner.getFriends().contains(dave));
    }

    @Test
    public void testBuildCollectionFromMixtureOfReferencesAndValues() throws Exception {
        MixedCollectionBean jumble = ((MixedCollectionBean) (this.beanFactory.getBean("jumble")));
        Assert.assertTrue(("Expected 5 elements, not " + (jumble.getJumble().size())), ((jumble.getJumble().size()) == 5));
        List l = ((List) (jumble.getJumble()));
        Assert.assertTrue(l.get(0).equals(this.beanFactory.getBean("david")));
        Assert.assertTrue(l.get(1).equals("literal"));
        Assert.assertTrue(l.get(2).equals(this.beanFactory.getBean("jenny")));
        Assert.assertTrue(l.get(3).equals("rod"));
        Object[] array = ((Object[]) (l.get(4)));
        Assert.assertTrue(array[0].equals(this.beanFactory.getBean("david")));
        Assert.assertTrue(array[1].equals("literal2"));
    }

    @Test
    public void testInvalidBeanNameReference() throws Exception {
        try {
            this.beanFactory.getBean("jumble2");
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof BeanDefinitionStoreException));
            Assert.assertTrue(ex.getCause().getMessage().contains("rod2"));
        }
    }

    @Test
    public void testEmptyMap() throws Exception {
        HasMap hasMap = ((HasMap) (this.beanFactory.getBean("emptyMap")));
        Assert.assertTrue(((hasMap.getMap().size()) == 0));
    }

    @Test
    public void testMapWithLiteralsOnly() throws Exception {
        HasMap hasMap = ((HasMap) (this.beanFactory.getBean("literalMap")));
        Assert.assertTrue(((hasMap.getMap().size()) == 3));
        Assert.assertTrue(hasMap.getMap().get("foo").equals("bar"));
        Assert.assertTrue(hasMap.getMap().get("fi").equals("fum"));
        Assert.assertTrue(((hasMap.getMap().get("fa")) == null));
    }

    @Test
    public void testMapWithLiteralsAndReferences() throws Exception {
        HasMap hasMap = ((HasMap) (this.beanFactory.getBean("mixedMap")));
        Assert.assertTrue(((hasMap.getMap().size()) == 5));
        Assert.assertTrue(hasMap.getMap().get("foo").equals(new Integer(10)));
        TestBean jenny = ((TestBean) (this.beanFactory.getBean("jenny")));
        Assert.assertTrue(((hasMap.getMap().get("jenny")) == jenny));
        Assert.assertTrue(hasMap.getMap().get(new Integer(5)).equals("david"));
        Assert.assertTrue(((hasMap.getMap().get("bar")) instanceof Long));
        Assert.assertTrue(hasMap.getMap().get("bar").equals(new Long(100)));
        Assert.assertTrue(((hasMap.getMap().get("baz")) instanceof Integer));
        Assert.assertTrue(hasMap.getMap().get("baz").equals(new Integer(200)));
    }

    @Test
    public void testMapWithLiteralsAndPrototypeReferences() throws Exception {
        TestBean jenny = ((TestBean) (this.beanFactory.getBean("pJenny")));
        HasMap hasMap = ((HasMap) (this.beanFactory.getBean("pMixedMap")));
        Assert.assertTrue(((hasMap.getMap().size()) == 2));
        Assert.assertTrue(hasMap.getMap().get("foo").equals("bar"));
        Assert.assertTrue(hasMap.getMap().get("jenny").toString().equals(jenny.toString()));
        Assert.assertTrue("Not same instance", ((hasMap.getMap().get("jenny")) != jenny));
        HasMap hasMap2 = ((HasMap) (this.beanFactory.getBean("pMixedMap")));
        Assert.assertTrue(((hasMap2.getMap().size()) == 2));
        Assert.assertTrue(hasMap2.getMap().get("foo").equals("bar"));
        Assert.assertTrue(hasMap2.getMap().get("jenny").toString().equals(jenny.toString()));
        Assert.assertTrue("Not same instance", ((hasMap2.getMap().get("jenny")) != (hasMap.getMap().get("jenny"))));
    }

    @Test
    public void testMapWithLiteralsReferencesAndList() throws Exception {
        HasMap hasMap = ((HasMap) (this.beanFactory.getBean("mixedMapWithList")));
        Assert.assertTrue(((hasMap.getMap().size()) == 4));
        Assert.assertTrue(hasMap.getMap().get(null).equals("bar"));
        TestBean jenny = ((TestBean) (this.beanFactory.getBean("jenny")));
        Assert.assertTrue(hasMap.getMap().get("jenny").equals(jenny));
        // Check list
        List l = ((List) (hasMap.getMap().get("list")));
        Assert.assertNotNull(l);
        Assert.assertTrue(((l.size()) == 4));
        Assert.assertTrue(l.get(0).equals("zero"));
        Assert.assertTrue(((l.get(3)) == null));
        // Check nested map in list
        Map m = ((Map) (l.get(1)));
        Assert.assertNotNull(m);
        Assert.assertTrue(((m.size()) == 2));
        Assert.assertTrue(m.get("fo").equals("bar"));
        Assert.assertTrue(("Map element 'jenny' should be equal to jenny bean, not " + (m.get("jen"))), m.get("jen").equals(jenny));
        // Check nested list in list
        l = ((List) (l.get(2)));
        Assert.assertNotNull(l);
        Assert.assertTrue(((l.size()) == 2));
        Assert.assertTrue(l.get(0).equals(jenny));
        Assert.assertTrue(l.get(1).equals("ba"));
        // Check nested map
        m = ((Map) (hasMap.getMap().get("map")));
        Assert.assertNotNull(m);
        Assert.assertTrue(((m.size()) == 2));
        Assert.assertTrue(m.get("foo").equals("bar"));
        Assert.assertTrue(("Map element 'jenny' should be equal to jenny bean, not " + (m.get("jenny"))), m.get("jenny").equals(jenny));
    }

    @Test
    public void testEmptySet() throws Exception {
        HasMap hasMap = ((HasMap) (this.beanFactory.getBean("emptySet")));
        Assert.assertTrue(((hasMap.getSet().size()) == 0));
    }

    @Test
    public void testPopulatedSet() throws Exception {
        HasMap hasMap = ((HasMap) (this.beanFactory.getBean("set")));
        Assert.assertTrue(((hasMap.getSet().size()) == 3));
        Assert.assertTrue(hasMap.getSet().contains("bar"));
        TestBean jenny = ((TestBean) (this.beanFactory.getBean("jenny")));
        Assert.assertTrue(hasMap.getSet().contains(jenny));
        Assert.assertTrue(hasMap.getSet().contains(null));
        Iterator it = hasMap.getSet().iterator();
        Assert.assertEquals("bar", it.next());
        Assert.assertEquals(jenny, it.next());
        Assert.assertEquals(null, it.next());
    }

    @Test
    public void testPopulatedConcurrentSet() throws Exception {
        HasMap hasMap = ((HasMap) (this.beanFactory.getBean("concurrentSet")));
        Assert.assertTrue(((hasMap.getConcurrentSet().size()) == 3));
        Assert.assertTrue(hasMap.getConcurrentSet().contains("bar"));
        TestBean jenny = ((TestBean) (this.beanFactory.getBean("jenny")));
        Assert.assertTrue(hasMap.getConcurrentSet().contains(jenny));
        Assert.assertTrue(hasMap.getConcurrentSet().contains(null));
    }

    @Test
    public void testPopulatedIdentityMap() throws Exception {
        HasMap hasMap = ((HasMap) (this.beanFactory.getBean("identityMap")));
        Assert.assertTrue(((hasMap.getIdentityMap().size()) == 2));
        HashSet set = new HashSet(hasMap.getIdentityMap().keySet());
        Assert.assertTrue(set.contains("foo"));
        Assert.assertTrue(set.contains("jenny"));
    }

    @Test
    public void testEmptyProps() throws Exception {
        HasMap hasMap = ((HasMap) (this.beanFactory.getBean("emptyProps")));
        Assert.assertTrue(((hasMap.getProps().size()) == 0));
        Assert.assertEquals(hasMap.getProps().getClass(), Properties.class);
    }

    @Test
    public void testPopulatedProps() throws Exception {
        HasMap hasMap = ((HasMap) (this.beanFactory.getBean("props")));
        Assert.assertTrue(((hasMap.getProps().size()) == 2));
        Assert.assertTrue(hasMap.getProps().get("foo").equals("bar"));
        Assert.assertTrue(hasMap.getProps().get("2").equals("TWO"));
    }

    @Test
    public void testObjectArray() throws Exception {
        HasMap hasMap = ((HasMap) (this.beanFactory.getBean("objectArray")));
        Assert.assertTrue(((hasMap.getObjectArray().length) == 2));
        Assert.assertTrue(hasMap.getObjectArray()[0].equals("one"));
        Assert.assertTrue(hasMap.getObjectArray()[1].equals(this.beanFactory.getBean("jenny")));
    }

    @Test
    public void testIntegerArray() throws Exception {
        HasMap hasMap = ((HasMap) (this.beanFactory.getBean("integerArray")));
        Assert.assertTrue(((hasMap.getIntegerArray().length) == 3));
        Assert.assertTrue(((hasMap.getIntegerArray()[0].intValue()) == 0));
        Assert.assertTrue(((hasMap.getIntegerArray()[1].intValue()) == 1));
        Assert.assertTrue(((hasMap.getIntegerArray()[2].intValue()) == 2));
    }

    @Test
    public void testClassArray() throws Exception {
        HasMap hasMap = ((HasMap) (this.beanFactory.getBean("classArray")));
        Assert.assertTrue(((hasMap.getClassArray().length) == 2));
        Assert.assertTrue(hasMap.getClassArray()[0].equals(String.class));
        Assert.assertTrue(hasMap.getClassArray()[1].equals(Exception.class));
    }

    @Test
    public void testClassList() throws Exception {
        HasMap hasMap = ((HasMap) (this.beanFactory.getBean("classList")));
        Assert.assertTrue(((hasMap.getClassList().size()) == 2));
        Assert.assertTrue(hasMap.getClassList().get(0).equals(String.class));
        Assert.assertTrue(hasMap.getClassList().get(1).equals(Exception.class));
    }

    @Test
    public void testProps() throws Exception {
        HasMap hasMap = ((HasMap) (this.beanFactory.getBean("props")));
        Assert.assertEquals(2, hasMap.getProps().size());
        Assert.assertEquals("bar", hasMap.getProps().getProperty("foo"));
        Assert.assertEquals("TWO", hasMap.getProps().getProperty("2"));
        HasMap hasMap2 = ((HasMap) (this.beanFactory.getBean("propsViaMap")));
        Assert.assertEquals(2, hasMap2.getProps().size());
        Assert.assertEquals("bar", hasMap2.getProps().getProperty("foo"));
        Assert.assertEquals("TWO", hasMap2.getProps().getProperty("2"));
    }

    @Test
    public void testListFactory() throws Exception {
        List list = ((List) (this.beanFactory.getBean("listFactory")));
        Assert.assertTrue((list instanceof LinkedList));
        Assert.assertTrue(((list.size()) == 2));
        Assert.assertEquals("bar", list.get(0));
        Assert.assertEquals("jenny", list.get(1));
    }

    @Test
    public void testPrototypeListFactory() throws Exception {
        List list = ((List) (this.beanFactory.getBean("pListFactory")));
        Assert.assertTrue((list instanceof LinkedList));
        Assert.assertTrue(((list.size()) == 2));
        Assert.assertEquals("bar", list.get(0));
        Assert.assertEquals("jenny", list.get(1));
    }

    @Test
    public void testSetFactory() throws Exception {
        Set set = ((Set) (this.beanFactory.getBean("setFactory")));
        Assert.assertTrue((set instanceof TreeSet));
        Assert.assertTrue(((set.size()) == 2));
        Assert.assertTrue(set.contains("bar"));
        Assert.assertTrue(set.contains("jenny"));
    }

    @Test
    public void testPrototypeSetFactory() throws Exception {
        Set set = ((Set) (this.beanFactory.getBean("pSetFactory")));
        Assert.assertTrue((set instanceof TreeSet));
        Assert.assertTrue(((set.size()) == 2));
        Assert.assertTrue(set.contains("bar"));
        Assert.assertTrue(set.contains("jenny"));
    }

    @Test
    public void testMapFactory() throws Exception {
        Map map = ((Map) (this.beanFactory.getBean("mapFactory")));
        Assert.assertTrue((map instanceof TreeMap));
        Assert.assertTrue(((map.size()) == 2));
        Assert.assertEquals("bar", map.get("foo"));
        Assert.assertEquals("jenny", map.get("jen"));
    }

    @Test
    public void testPrototypeMapFactory() throws Exception {
        Map map = ((Map) (this.beanFactory.getBean("pMapFactory")));
        Assert.assertTrue((map instanceof TreeMap));
        Assert.assertTrue(((map.size()) == 2));
        Assert.assertEquals("bar", map.get("foo"));
        Assert.assertEquals("jenny", map.get("jen"));
    }

    @Test
    public void testChoiceBetweenSetAndMap() {
        XmlBeanCollectionTests.MapAndSet sam = ((XmlBeanCollectionTests.MapAndSet) (this.beanFactory.getBean("setAndMap")));
        Assert.assertTrue("Didn't choose constructor with Map argument", ((sam.getObject()) instanceof Map));
        Map map = ((Map) (sam.getObject()));
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("val1", map.get("key1"));
        Assert.assertEquals("val2", map.get("key2"));
        Assert.assertEquals("val3", map.get("key3"));
    }

    @Test
    public void testEnumSetFactory() throws Exception {
        Set set = ((Set) (this.beanFactory.getBean("enumSetFactory")));
        Assert.assertTrue(((set.size()) == 2));
        Assert.assertTrue(set.contains("ONE"));
        Assert.assertTrue(set.contains("TWO"));
    }

    public static class MapAndSet {
        private Object obj;

        public MapAndSet(Map map) {
            this.obj = map;
        }

        public MapAndSet(Set set) {
            this.obj = set;
        }

        public Object getObject() {
            return obj;
        }
    }
}

