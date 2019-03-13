/**
 * The MIT License
 *
 *  Copyright (c) 2019, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.jeasy.rules.core;


import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Assert;
import org.junit.Test;


public class UtilsTest {
    @Test
    public void findAnnotationWithClassWhereAnnotationIsPresent() {
        Annotation foo = Utils.findAnnotation(UtilsTest.Foo.class, UtilsTest.AnnotationIsPresent.class);
        UtilsTest.assertCorrectAnnotationIsFound(UtilsTest.Foo.class, foo);
    }

    @Test
    public void findAnnotationWithClassWhereAnnotationIsPresentViaMetaAnnotation() {
        Annotation foo = Utils.findAnnotation(UtilsTest.Foo.class, UtilsTest.AnnotationIsPresentViaMetaAnnotation.class);
        UtilsTest.assertCorrectAnnotationIsFound(UtilsTest.Foo.class, foo);
    }

    @Test
    public void findAnnotationWithClassWhereAnnotationIsNotPresent() {
        Annotation foo = Utils.findAnnotation(UtilsTest.Foo.class, Object.class);
        Assert.assertNull(foo);
    }

    @Test
    public void isAnnotationPresentWithClassWhereAnnotationIsPresent() {
        Assert.assertTrue(Utils.isAnnotationPresent(UtilsTest.Foo.class, UtilsTest.AnnotationIsPresent.class));
    }

    @Test
    public void isAnnotationPresentWithClassWhereAnnotationIsPresentViaMetaAnnotation() {
        Assert.assertTrue(Utils.isAnnotationPresent(UtilsTest.Foo.class, UtilsTest.AnnotationIsPresentViaMetaAnnotation.class));
    }

    @Test
    public void isAnnotationPresentWithClassWhereAnnotationIsNotPresent() {
        Assert.assertFalse(Utils.isAnnotationPresent(UtilsTest.Foo.class, Object.class));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    private @interface Foo {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @UtilsTest.Foo
    private @interface MetaFoo {}

    @UtilsTest.Foo
    private static final class AnnotationIsPresent {}

    @UtilsTest.MetaFoo
    private static final class AnnotationIsPresentViaMetaAnnotation {}
}

