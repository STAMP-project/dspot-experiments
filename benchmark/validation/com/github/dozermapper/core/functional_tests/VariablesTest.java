/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.functional_tests;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class VariablesTest extends AbstractFunctionalTest {
    @Test
    public void testTest() {
        VariablesTest.Container<VariablesTest.Child> source = new VariablesTest.Container<>();
        VariablesTest.Container<VariablesTest.ChildClone> destination = new VariablesTest.Container<>();
        VariablesTest.Parent parent = newInstance(VariablesTest.Parent.class);
        parent.setId("PARENT");
        HashSet<VariablesTest.Child> children = new HashSet<>();
        VariablesTest.Child child = newInstance(VariablesTest.Child.class);
        child.setId("CHILD");
        child.setParent(parent);
        children.add(child);
        parent.setChildren(children);
        source.add(child);
        mapper.map(source, destination, "from");
        Assert.assertEquals(1, destination.size());
        VariablesTest.ChildClone childClone = destination.get(0);
        Assert.assertEquals("CHILD", childClone.getId());
        Assert.assertNotNull(childClone.getParent());
        VariablesTest.ParentClone parentClone = childClone.getParent();
        Assert.assertEquals("PARENT", parentClone.getId());
    }

    public static class Container<T> extends ArrayList<T> {}

    public static class Parent {
        String id;

        Set<VariablesTest.Child> children;

        public Set<VariablesTest.Child> getChildren() {
            throw new IllegalStateException("Not Allowed!");
        }

        public void setChildren(Set<VariablesTest.Child> children) {
            this.children = children;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class ParentClone {
        String id;

        Set<VariablesTest.ChildClone> children;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Set<VariablesTest.ChildClone> getChildren() {
            throw new IllegalStateException();
        }

        public void setChildren(Set<VariablesTest.ChildClone> children) {
            this.children = children;
        }
    }

    public static class ChildClone {
        String id;

        VariablesTest.ParentClone parent;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public VariablesTest.ParentClone getParent() {
            return parent;
        }

        public void setParent(VariablesTest.ParentClone parent) {
            this.parent = parent;
        }
    }

    public static class Child {
        String id;

        VariablesTest.Parent parent;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public VariablesTest.Parent getParent() {
            return parent;
        }

        public void setParent(VariablesTest.Parent parent) {
            this.parent = parent;
        }
    }
}

