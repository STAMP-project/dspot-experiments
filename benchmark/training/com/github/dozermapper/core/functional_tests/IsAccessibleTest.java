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


import org.junit.Assert;
import org.junit.Test;


public class IsAccessibleTest extends AbstractFunctionalTest {
    @Test
    public void shouldWorkWithNestedFields() {
        IsAccessibleTest.Node child = new IsAccessibleTest.Node(null);
        IsAccessibleTest.Node root = new IsAccessibleTest.Node(new IsAccessibleTest.Node(child));
        mapper.map("a", root);
        Assert.assertEquals("a", root.child.child.value);
    }

    @Test
    public void shouldPreinstantiateChainElements() {
        IsAccessibleTest.Node node = new IsAccessibleTest.Node(null);
        mapper.map("a", node);
        Assert.assertEquals("a", node.child.child.value);
    }

    @Test
    public void shouldApplyIsAccessibleOnClass() {
        IsAccessibleTest.Node node = new IsAccessibleTest.Node(null);
        mapper.map("true", node, "class-level");
        Assert.assertEquals("true", node.value);
        Assert.assertEquals("true", node.publicValue);
        Assert.assertTrue(node.setterInvoked);
    }

    @Test
    public void shouldApplyIsAccessibleOnClass_Backwards() {
        IsAccessibleTest.Node node = new IsAccessibleTest.Node(null);
        node.publicValue = "true";
        IsAccessibleTest.Node result = mapper.map(node, IsAccessibleTest.Node.class, "third");
        Assert.assertEquals("true", result.value);
        Assert.assertTrue(node.getterInvoked);
    }

    public static class Node {
        IsAccessibleTest.Node child;

        String value;

        String publicValue;

        boolean setterInvoked;

        boolean getterInvoked;

        public Node() {
        }

        public Node(IsAccessibleTest.Node child) {
            this.child = child;
        }

        public String getPublicValue() {
            this.getterInvoked = true;
            return publicValue;
        }

        public void setPublicValue(String publicValue) {
            this.setterInvoked = true;
            this.publicValue = publicValue;
        }

        public boolean isSetterInvoked() {
            return setterInvoked;
        }

        public boolean isGetterInvoked() {
            return getterInvoked;
        }
    }
}

