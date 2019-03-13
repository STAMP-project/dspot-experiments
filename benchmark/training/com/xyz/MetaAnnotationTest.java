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
package com.xyz;


import io.github.classgraph.ScanResult;
import org.junit.Test;


/**
 * The Class MetaAnnotationTest.
 */
public class MetaAnnotationTest {
    /**
     * The scan result.
     */
    static ScanResult scanResult;

    /**
     * One level.
     */
    @Test
    public void oneLevel() {
        assertThat(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.E").directOnly().getNames()).containsExactlyInAnyOrder("com.xyz.meta.B");
        assertThat(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.F").directOnly().getNames()).containsExactlyInAnyOrder("com.xyz.meta.B", "com.xyz.meta.A");
        assertThat(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.G").directOnly().getNames()).containsExactlyInAnyOrder("com.xyz.meta.C");
    }

    /**
     * Two levels.
     */
    @Test
    public void twoLevels() {
        assertThat(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.J").getNames()).containsExactlyInAnyOrder("com.xyz.meta.F", "com.xyz.meta.E", "com.xyz.meta.B", "com.xyz.meta.A");
    }

    /**
     * Three levels.
     */
    @Test
    public void threeLevels() {
        assertThat(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.L").getNames()).containsExactlyInAnyOrder("com.xyz.meta.I", "com.xyz.meta.E", "com.xyz.meta.B", "com.xyz.meta.H");
    }

    /**
     * Across cycle.
     */
    @Test
    public void acrossCycle() {
        assertThat(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.H").directOnly().getNames()).containsExactlyInAnyOrder("com.xyz.meta.I");
        assertThat(MetaAnnotationTest.scanResult.getAnnotationsOnClass("com.xyz.meta.H").directOnly().getNames()).containsExactlyInAnyOrder("com.xyz.meta.I", "com.xyz.meta.K", "java.lang.annotation.Retention", "java.lang.annotation.Target");
        assertThat(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.I").directOnly().getNames()).containsExactlyInAnyOrder("com.xyz.meta.E", "com.xyz.meta.H");
        assertThat(MetaAnnotationTest.scanResult.getAnnotationsOnClass("com.xyz.meta.I").directOnly().getNames()).containsExactlyInAnyOrder("com.xyz.meta.L", "com.xyz.meta.H", "java.lang.annotation.Retention", "java.lang.annotation.Target");
        assertThat(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.K").directOnly().getNames()).containsExactlyInAnyOrder("com.xyz.meta.H");
        assertThat(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.D").directOnly().getNames()).containsExactlyInAnyOrder("com.xyz.meta.K");
    }

    /**
     * Cycle annotates self.
     */
    @Test
    public void cycleAnnotatesSelf() {
        assertThat(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.I").getNames()).containsExactlyInAnyOrder("com.xyz.meta.E", "com.xyz.meta.B", "com.xyz.meta.H", "com.xyz.meta.I");
    }

    /**
     * Names of meta annotations.
     */
    @Test
    public void namesOfMetaAnnotations() {
        assertThat(MetaAnnotationTest.scanResult.getAnnotationsOnClass("com.xyz.meta.A").getNames()).containsExactlyInAnyOrder("com.xyz.meta.J", "com.xyz.meta.F");
        assertThat(MetaAnnotationTest.scanResult.getAnnotationsOnClass("com.xyz.meta.C").getNames()).containsExactlyInAnyOrder("com.xyz.meta.G");
    }

    /**
     * Union.
     */
    @Test
    public void union() {
        assertThat(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.J").union(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.G")).directOnly().getNames()).containsExactlyInAnyOrder("com.xyz.meta.E", "com.xyz.meta.F", "com.xyz.meta.C");
        assertThat(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.I").union(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.J")).getNames()).containsExactlyInAnyOrder("com.xyz.meta.A", "com.xyz.meta.B", "com.xyz.meta.F", "com.xyz.meta.E", "com.xyz.meta.H", "com.xyz.meta.I");
        assertThat(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.I").union(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.J")).directOnly().getNames()).containsExactlyInAnyOrder("com.xyz.meta.F", "com.xyz.meta.E", "com.xyz.meta.H");
    }

    /**
     * Intersect.
     */
    @Test
    public void intersect() {
        assertThat(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.I").intersect(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.J")).getNames()).containsExactlyInAnyOrder("com.xyz.meta.E", "com.xyz.meta.B");
        assertThat(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.I").intersect(MetaAnnotationTest.scanResult.getClassesWithAnnotation("com.xyz.meta.J")).directOnly().getNames()).containsExactlyInAnyOrder("com.xyz.meta.E");
    }
}

