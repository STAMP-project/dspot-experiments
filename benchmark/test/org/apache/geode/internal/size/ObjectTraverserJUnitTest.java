/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.size;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class ObjectTraverserJUnitTest {
    @Test
    public void testBasic() throws Exception {
        Set testData = new HashSet();
        Object one = new Object();
        testData.add(one);
        Object[] two = new Object[2];
        testData.add(two);
        ArrayList three = new ArrayList();
        two[0] = three;
        three.add(testData);
        ObjectTraverserJUnitTest.TestVisitor visitor = new ObjectTraverserJUnitTest.TestVisitor();
        ObjectTraverser.breadthFirstSearch(testData, visitor, false);
        Assert.assertNotNull(visitor.visited.remove(testData));
        Assert.assertNotNull(visitor.visited.remove(one));
        Assert.assertNotNull(visitor.visited.remove(two));
        Assert.assertNotNull(visitor.visited.remove(three));
    }

    @Test
    public void testStatics() throws Exception {
        final Object staticObject = new Object();
        ObjectTraverserJUnitTest.TestObject1.test2 = staticObject;
        ObjectTraverserJUnitTest.TestObject1 test1 = new ObjectTraverserJUnitTest.TestObject1();
        ObjectTraverserJUnitTest.TestVisitor visitor = new ObjectTraverserJUnitTest.TestVisitor();
        ObjectTraverser.breadthFirstSearch(test1, visitor, false);
        Assert.assertNull(visitor.visited.get(staticObject));
        visitor = new ObjectTraverserJUnitTest.TestVisitor();
        ObjectTraverser.breadthFirstSearch(test1, visitor, true);
        Assert.assertNotNull(visitor.visited.get(staticObject));
    }

    @Test
    public void testStop() throws Exception {
        Set set1 = new HashSet();
        final Set set2 = new HashSet();
        Object object3 = new Object();
        set1.add(set2);
        set2.add(object3);
        ObjectTraverserJUnitTest.TestVisitor visitor = new ObjectTraverserJUnitTest.TestVisitor();
        visitor = new ObjectTraverserJUnitTest.TestVisitor() {
            @Override
            public boolean visit(Object parent, Object object) {
                super.visit(parent, object);
                return object != set2;
            }
        };
        ObjectTraverser.breadthFirstSearch(set1, visitor, true);
        Assert.assertNotNull(visitor.visited.get(set1));
        Assert.assertNotNull(visitor.visited.get(set2));
        Assert.assertNull(visitor.visited.get(object3));
    }

    private static class TestVisitor implements ObjectTraverser.Visitor {
        private static final Object VALUE = new Object();

        public Map visited = new IdentityHashMap();

        @Override
        public boolean visit(Object parent, Object object) {
            Assert.assertNull(visited.put(object, ObjectTraverserJUnitTest.TestVisitor.VALUE));
            return true;
        }
    }

    private static class TestObject1 {
        protected static Object test2;
    }
}

