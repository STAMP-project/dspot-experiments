/**
 * This file is part of ClassGraph.
 *
 * Author: Luke Hutchison
 *
 * Hosted at: https://github.com/classgraph/classgraph
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Luke Hutchison
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.classgraph.issues.issue238;


import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import java.util.List;
import javax.persistence.Entity;
import org.junit.Test;


/**
 * The Class Issue238Test.
 */
@Entity
public class Issue238Test {
    /**
     * The Class B.
     */
    public static class B extends Issue238Test.D {}

    /**
     * The Class C.
     */
    public static class C {}

    /**
     * The Class D.
     */
    public static class D extends Issue238Test.C {}

    /**
     * The Class E.
     */
    public static class E extends Issue238Test.F {}

    /**
     * The Class A.
     */
    public static class A extends Issue238Test.G {}

    /**
     * The Class G.
     */
    public static class G extends Issue238Test.B {}

    /**
     * The Class F.
     */
    public static class F extends Issue238Test.A {}

    /**
     * Test superclass inheritance order.
     */
    @Test
    public void testSuperclassInheritanceOrder() {
        try (ScanResult scanResult = new ClassGraph().whitelistPackages(Issue238Test.class.getPackage().getName()).enableAllInfo().scan()) {
            final List<String> classNames = scanResult.getAllClasses().get(Issue238Test.E.class.getName()).getSuperclasses().getNames();
            assertThat(classNames).containsExactly(Issue238Test.F.class.getName(), Issue238Test.A.class.getName(), Issue238Test.G.class.getName(), Issue238Test.B.class.getName(), Issue238Test.D.class.getName(), Issue238Test.C.class.getName());
        }
    }
}

