/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.media.multipart;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.glassfish.jersey.message.internal.ParameterizedHeader;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link ParameterizedHeadersMap}.
 *
 * @author Paul Sandoz
 * @author Michal Gajdos
 */
public class ParameterizedHeadersMapTest {
    private ParameterizedHeadersMap map;

    /**
     * Test of add method, of class ParametrizedHeadersMap.
     */
    @Test
    public void testAdd() throws Exception {
        map.add("foo", new ParameterizedHeader("bar"));
        List<ParameterizedHeader> values = map.get("foo");
        Assert.assertNotNull(values);
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("bar", values.get(0).getValue());
        map.add("foo", new ParameterizedHeader("baz"));
        Assert.assertEquals(1, map.size());
        values = map.get("foo");
        Assert.assertEquals(2, values.size());
        Assert.assertEquals("bar", values.get(0).getValue());
        Assert.assertEquals("baz", values.get(1).getValue());
        map.add("bop", new ParameterizedHeader("boo"));
        Assert.assertEquals(2, map.size());
    }

    /**
     * Test of clear method, of class ParametrizedHeadersMap.
     */
    @Test
    public void testClear() throws Exception {
        map.add("foo", new ParameterizedHeader("bar"));
        map.add("baz", new ParameterizedHeader("bop"));
        Assert.assertTrue((!(map.isEmpty())));
        Assert.assertEquals(2, map.size());
        map.clear();
        Assert.assertEquals(0, map.size());
        Assert.assertTrue(map.isEmpty());
    }

    /**
     * Test of containsKey method, of class ParametrizedHeadersMap.
     */
    @Test
    public void testContainsKey() throws Exception {
        map.add("foo", new ParameterizedHeader("bar"));
        Assert.assertTrue(map.containsKey("foo"));
        Assert.assertTrue(map.containsKey("FOO"));
        Assert.assertTrue(map.containsKey("Foo"));
        Assert.assertTrue(map.containsKey("fOo"));
        Assert.assertTrue(map.containsKey("foO"));
        Assert.assertTrue((!(map.containsKey("bar"))));
    }

    /**
     * Test of containsValue method, of class ParametrizedHeadersMap.
     */
    @Test
    public void testContainsValue() throws Exception {
        List<ParameterizedHeader> values = new ArrayList<>();
        values.add(new ParameterizedHeader("bar"));
        values.add(new ParameterizedHeader("bop"));
        map.put("foo", values);
        Assert.assertTrue(map.containsValue(values));
        map.clear();
        Assert.assertTrue((!(map.containsValue(values))));
    }

    /**
     * Test of entrySet method, of class ParametrizedHeadersMap.
     */
    @Test
    public void testEntrySet() throws Exception {
        List<ParameterizedHeader> valuesFoo = new ArrayList<>();
        valuesFoo.add(new ParameterizedHeader("foo1"));
        valuesFoo.add(new ParameterizedHeader("foo2"));
        map.put("foo", valuesFoo);
        List<ParameterizedHeader> valuesBar = new ArrayList<>();
        valuesBar.add(new ParameterizedHeader("bar1"));
        valuesBar.add(new ParameterizedHeader("bar2"));
        map.put("bar", valuesBar);
        Set<Map.Entry<String, List<ParameterizedHeader>>> entrySet = map.entrySet();
        Assert.assertEquals(2, entrySet.size());
        // TODO - detailed tests for the HeadersEntries methods
    }

    /**
     * Test of get method, of class ParametrizedHeadersMap.
     */
    @Test
    public void testGet() throws Exception {
        map.add("foo", new ParameterizedHeader("bar"));
        Assert.assertNotNull(map.get("foo"));
        Assert.assertNotNull(map.get("FOO"));
        Assert.assertNotNull(map.get("Foo"));
        Assert.assertNotNull(map.get("fOo"));
        Assert.assertNotNull(map.get("foO"));
        Assert.assertNull(map.get("bar"));
        List<ParameterizedHeader> values = map.get("foo");
        Assert.assertNotNull(values);
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("bar", values.get(0).getValue());
    }

    @Test
    public void testEqualsAndHashCode() throws Exception {
        ParameterizedHeadersMap map2 = new ParameterizedHeadersMap();
        List<ParameterizedHeader> valuesFoo = new ArrayList<>();
        valuesFoo.add(new ParameterizedHeader("foo1"));
        valuesFoo.add(new ParameterizedHeader("foo2"));
        map.put("foo", valuesFoo);
        map2.put("foo", valuesFoo);
        List<ParameterizedHeader> valuesBar = new ArrayList<>();
        valuesBar.add(new ParameterizedHeader("bar1"));
        valuesBar.add(new ParameterizedHeader("bar2"));
        map.put("bar", valuesBar);
        map2.put("bar", valuesBar);
        Assert.assertTrue(map.equals(map2));
        Assert.assertTrue(map2.equals(map));
        Assert.assertEquals(map.hashCode(), map2.hashCode());
        map2.remove("bar");
        Assert.assertTrue((!(map.equals(map2))));
        Assert.assertTrue((!(map2.equals(map))));
    }

    /**
     * Test of getFirst method, of class ParametrizedHeadersMap.
     */
    @Test
    public void testGetFirst() throws Exception {
        map.add("foo", new ParameterizedHeader("bar"));
        map.add("foo", new ParameterizedHeader("baz"));
        map.add("foo", new ParameterizedHeader("bop"));
        Assert.assertEquals(3, map.get("foo").size());
        Assert.assertEquals("bar", map.getFirst("foo").getValue());
    }

    /**
     * Test of isEmpty method, of class ParametrizedHeadersMap.
     */
    @Test
    public void testIsEmpty() throws Exception {
        Assert.assertTrue(map.isEmpty());
        map.add("foo", new ParameterizedHeader("bar"));
        Assert.assertTrue((!(map.isEmpty())));
        map.clear();
        Assert.assertTrue(map.isEmpty());
    }

    /**
     * Test of keySet method, of class ParametrizedHeadersMap.
     */
    @Test
    public void testKeySet() throws Exception {
        map.add("foo", new ParameterizedHeader("bar"));
        map.add("baz", new ParameterizedHeader("bop"));
        Set<String> keySet = map.keySet();
        Assert.assertNotNull(keySet);
        Assert.assertEquals(2, keySet.size());
        Assert.assertTrue(keySet.contains("foo"));
        Assert.assertTrue((!(keySet.contains("bar"))));
        Assert.assertTrue(keySet.contains("baz"));
        Assert.assertTrue((!(keySet.contains("bop"))));
        // TODO - detailed tests for the HeadersKeys methods
    }

    @Test
    public void testParameters() throws Exception {
        ParameterizedHeader header;
        header = new ParameterizedHeader("foo");
        Assert.assertEquals("foo", header.getValue());
        Assert.assertEquals(0, header.getParameters().size());
        header = new ParameterizedHeader("foo;bar=baz;bop=abc");
        Assert.assertEquals("foo", header.getValue());
        Assert.assertEquals(2, header.getParameters().size());
        Assert.assertEquals("baz", header.getParameters().get("bar"));
        Assert.assertEquals("abc", header.getParameters().get("bop"));
    }

    /**
     * Test of put method, of class ParametrizedHeadersMap.
     */
    @Test
    public void testPut() throws Exception {
        List<ParameterizedHeader> fooValues1 = new ArrayList<>();
        fooValues1.add(new ParameterizedHeader("foo1"));
        fooValues1.add(new ParameterizedHeader("foo2"));
        Assert.assertNull(map.get("foo"));
        map.put("foo", fooValues1);
        Assert.assertTrue(map.containsKey("foo"));
        Assert.assertTrue(map.containsValue(fooValues1));
        Assert.assertTrue(((map.get("foo")) == fooValues1));
        List<ParameterizedHeader> fooValues2 = new ArrayList<>();
        fooValues2.add(new ParameterizedHeader("foo3"));
        fooValues2.add(new ParameterizedHeader("foo4"));
        map.put("foo", fooValues2);
        Assert.assertEquals(1, map.size());
        Assert.assertTrue(map.containsKey("foo"));
        Assert.assertTrue((!(map.containsValue(fooValues1))));
        Assert.assertTrue(map.containsValue(fooValues2));
        Assert.assertTrue(((map.get("foo")) == fooValues2));
    }

    /**
     * Test of putAll method, of class ParametrizedHeadersMap.
     */
    @Test
    public void testPutAll() throws Exception {
        Map<String, List<ParameterizedHeader>> all = new HashMap<>();
        List<ParameterizedHeader> fooValues = new ArrayList<>();
        fooValues.add(new ParameterizedHeader("foo1"));
        fooValues.add(new ParameterizedHeader("foo2"));
        all.put("foo", fooValues);
        List<ParameterizedHeader> barValues = new ArrayList<>();
        barValues.add(new ParameterizedHeader("bar1"));
        barValues.add(new ParameterizedHeader("bar2"));
        all.put("bar", barValues);
        Assert.assertTrue(map.isEmpty());
        map.putAll(all);
        Assert.assertTrue((!(map.isEmpty())));
        Assert.assertEquals(2, map.size());
        Assert.assertTrue(map.containsKey("foo"));
        Assert.assertTrue(map.containsKey("bar"));
        Assert.assertTrue(map.containsValue(fooValues));
        Assert.assertTrue(map.containsValue(barValues));
    }

    /**
     * Test of putSingle method, of class ParametrizedHeadersMap.
     */
    @Test
    public void testPutSingle() throws Exception {
        List<ParameterizedHeader> values = new ArrayList<>();
        values.add(new ParameterizedHeader("bar"));
        values.add(new ParameterizedHeader("baz"));
        map.put("foo", values);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(2, map.get("foo").size());
        map.putSingle("foo", new ParameterizedHeader("bop"));
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(1, map.get("foo").size());
        Assert.assertEquals("bop", map.get("foo").get(0).getValue());
    }

    /**
     * Test of remove method, of class ParametrizedHeadersMap.
     */
    @Test
    public void testRemove() throws Exception {
        map.add("foo", new ParameterizedHeader("bar"));
        map.add("baz", new ParameterizedHeader("bop"));
        Assert.assertEquals(2, map.size());
        Assert.assertTrue(map.containsKey("foo"));
        Assert.assertTrue(map.containsKey("baz"));
        map.remove("foo");
        Assert.assertEquals(1, map.size());
        Assert.assertTrue((!(map.containsKey("foo"))));
        Assert.assertTrue(map.containsKey("baz"));
    }

    /**
     * Test of size method, of class ParametrizedHeadersMap.
     */
    @Test
    public void testSize() throws Exception {
        Assert.assertEquals(0, map.size());
        map.add("foo", new ParameterizedHeader("bar"));
        Assert.assertEquals(1, map.size());
        map.add("foo", new ParameterizedHeader("arg"));
        Assert.assertEquals(1, map.size());
        map.add("baz", new ParameterizedHeader("bop"));
        Assert.assertEquals(2, map.size());
        map.remove("baz");
        Assert.assertEquals(1, map.size());
        map.remove("foo");
        Assert.assertEquals(0, map.size());
    }

    /**
     * Test of values method, of class ParametrizedHeadersMap.
     */
    @Test
    public void testValues() throws Exception {
        Map<String, List<ParameterizedHeader>> all = new HashMap<>();
        List<ParameterizedHeader> fooValues = new ArrayList<>();
        fooValues.add(new ParameterizedHeader("foo1"));
        fooValues.add(new ParameterizedHeader("foo2"));
        all.put("foo", fooValues);
        List<ParameterizedHeader> barValues = new ArrayList<>();
        barValues.add(new ParameterizedHeader("bar1"));
        barValues.add(new ParameterizedHeader("bar2"));
        all.put("bar", barValues);
        map.putAll(all);
        Collection<List<ParameterizedHeader>> values = map.values();
        Assert.assertNotNull(values);
        Assert.assertEquals(2, values.size());
        List[] array = new List[2];
        array = values.toArray(array);
        if ((array[0]) == fooValues) {
            Assert.assertTrue(((array[1]) == barValues));
        } else
            if ((array[0]) == barValues) {
                Assert.assertTrue(((array[1]) == fooValues));
            } else {
                Assert.fail("Returned values were corrupted");
            }

    }
}

