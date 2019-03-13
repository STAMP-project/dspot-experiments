/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import org.drools.core.impl.InternalKnowledgeBase;
import org.junit.Test;


public class Query3Test {
    private InternalKnowledgeBase knowledgeBase;

    @Test
    public void testDifferent() {
        Query3Test.Foo foo = new Query3Test.Foo();
        foo.setId("x");
        Query3Test.Bar bar = new Query3Test.Bar();
        bar.setId("x");
        doIt(foo, bar, "testDifferent", 1, false, false);
    }

    @Test
    public void testDifferentWithUpdate() {
        Query3Test.Foo foo = new Query3Test.Foo();
        foo.setId("x");
        Query3Test.Bar bar = new Query3Test.Bar();
        bar.setId("x");
        doIt(foo, bar, "testDifferent", 1, true, false);
    }

    @Test
    public void testSame() {
        Query3Test.Foo foo = new Query3Test.Foo();
        foo.setId("x");
        Query3Test.Foo foo2 = new Query3Test.Foo();
        foo2.setId("x");
        doIt(foo, foo2, "testSame", 4, false, false);
    }

    @Test
    public void testSameWithUpdate() {
        Query3Test.Foo foo = new Query3Test.Foo();
        foo.setId("x");
        Query3Test.Foo foo2 = new Query3Test.Foo();
        foo2.setId("x");
        doIt(foo, foo2, "testSame", 4, true, false);
    }

    @Test
    public void testExtends() {
        Query3Test.Foo foo = new Query3Test.Foo();
        foo.setId("x");
        Query3Test.Foo2 foo2 = new Query3Test.Foo2();
        foo2.setId("x");
        doIt(foo, foo2, "testExtends", 2, false, false);
    }

    @Test
    public void testExtendsWithUpdate() {
        Query3Test.Foo foo = new Query3Test.Foo();
        foo.setId("x");
        Query3Test.Foo2 foo2 = new Query3Test.Foo2();
        foo2.setId("x");
        doIt(foo, foo2, "testExtends", 2, true, false);
    }

    @Test
    public void testExtendsWithRetract() {
        Query3Test.Foo foo = new Query3Test.Foo();
        foo.setId("x");
        Query3Test.Foo2 foo2 = new Query3Test.Foo2();
        foo2.setId("x");
        doIt(foo, foo2, "testExtends", 2, false, true);
    }

    public static class Bar {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class Foo {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class Foo2 extends Query3Test.Foo {}
}

