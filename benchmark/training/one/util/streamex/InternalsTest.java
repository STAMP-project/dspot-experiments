/**
 * Copyright 2015, 2017 StreamEx contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package one.util.streamex;


import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for non-public APIs in StreamExInternals
 *
 * @author Tagir Valeev
 */
public class InternalsTest {
    @Test
    public void testArrayCollection() {
        Collection<Object> collection = new StreamExInternals.ArrayCollection(new Object[]{ "1", "2" });
        List<Object> list = new LinkedList<>(collection);
        Assert.assertEquals("1", list.get(0));
        Assert.assertEquals("2", list.get(1));
        List<Object> list2 = new ArrayList<>(collection);
        Assert.assertEquals("1", list.get(0));
        Assert.assertEquals("2", list.get(1));
        Assert.assertEquals(list2, list);
        Set<Object> set = new HashSet<>(collection);
        Assert.assertTrue(set.contains("1"));
        Assert.assertTrue(set.contains("2"));
        Assert.assertEquals(2, set.size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPartialCollector() {
        StreamExInternals.PartialCollector.intSum().accumulator();
    }

    @Test
    public void testJdk9Basics() {
        MethodHandle[][] jdk9Methods = Java9Specific.initJdk9Methods();
        if (Stream.of(Stream.class.getMethods()).anyMatch(( m) -> m.getName().equals("takeWhile")))
            Assert.assertNotNull(jdk9Methods);
        else
            Assert.assertNull(jdk9Methods);

    }
}

