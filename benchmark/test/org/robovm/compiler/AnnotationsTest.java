/**
 * Copyright (C) 2014 RoboVM AB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 */
package org.robovm.compiler;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Assert;
import org.junit.Test;
import soot.SootClass;
import soot.SootMethod;


/**
 * Tests {@link Annotations}
 */
public class AnnotationsTest {
    @interface A {}

    @interface B {}

    @interface C {}

    @interface D {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface E {}

    @Test
    public void testCopyParameterAnnotationsNoParams() {
        SootClass sc = toSootClass(getClass());
        SootMethod src = sc.getMethodByName("src1");
        SootMethod dest = sc.getMethodByName("dest1");
        copyParameterAnnotations(src, dest, 0, 0, 0, Visibility.Any);
        Assert.assertEquals("void dest1()", toString(dest));
    }

    @Test
    public void testCopyParameterAnnotationsNoAnnotations() {
        SootClass sc = toSootClass(getClass());
        SootMethod src = sc.getMethodByName("src2");
        SootMethod dest = sc.getMethodByName("dest2");
        copyParameterAnnotations(src, dest, 0, 2, 0, Visibility.Any);
        Assert.assertEquals("void dest2(int, int)", toString(dest));
    }

    @Test
    public void testCopyParameterAnnotationsSingleAllNoShift() {
        SootClass sc = toSootClass(getClass());
        SootMethod src = sc.getMethodByName("src3");
        SootMethod dest = sc.getMethodByName("dest3");
        copyParameterAnnotations(src, dest, 0, 2, 0, Visibility.Any);
        Assert.assertEquals("void dest3(@A int, @B int)", toString(dest));
    }

    @Test
    public void testCopyParameterAnnotationsSingleFirstNoShift() {
        SootClass sc = toSootClass(getClass());
        SootMethod src = sc.getMethodByName("src4");
        SootMethod dest = sc.getMethodByName("dest4");
        copyParameterAnnotations(src, dest, 0, 1, 0, Visibility.Any);
        Assert.assertEquals("void dest4(@A int, int)", toString(dest));
    }

    @Test
    public void testCopyParameterAnnotationsSingleLastNoShift() {
        SootClass sc = toSootClass(getClass());
        SootMethod src = sc.getMethodByName("src5");
        SootMethod dest = sc.getMethodByName("dest5");
        copyParameterAnnotations(src, dest, 1, 2, 0, Visibility.Any);
        Assert.assertEquals("void dest5(int, @B int)", toString(dest));
    }

    @Test
    public void testCopyParameterAnnotationsMultipleAllNoShift() {
        SootClass sc = toSootClass(getClass());
        SootMethod src = sc.getMethodByName("src6");
        SootMethod dest = sc.getMethodByName("dest6");
        copyParameterAnnotations(src, dest, 0, 2, 0, Visibility.Any);
        Assert.assertEquals("void dest6(@A @B @C int, @B @C @D int)", toString(dest));
    }

    @Test
    public void testCopyParameterAnnotationsMultipleSubsetWithShift() {
        SootClass sc = toSootClass(getClass());
        SootMethod src = sc.getMethodByName("src7");
        SootMethod dest = sc.getMethodByName("dest7");
        copyParameterAnnotations(src, dest, 0, 3, 1, Visibility.Any);
        Assert.assertEquals("void dest7(int, @A @B @C int, @B @C @D int, @C @D @E int)", toString(dest));
    }

    @Test
    public void testCopyParameterAnnotationsTwiceMultipleSubsetWithShift() {
        SootClass sc = toSootClass(getClass());
        SootMethod src = sc.getMethodByName("src8");
        SootMethod dest = sc.getMethodByName("dest8");
        copyParameterAnnotations(src, dest, 0, 2, 0, Visibility.Any);
        copyParameterAnnotations(src, dest, 2, 3, 1, Visibility.Any);
        Assert.assertEquals("void dest8(@A @B @C int, @B @C @D int, int, @C @D @E int)", toString(dest));
    }

    @Test
    public void testCopyParameterAnnotationsOnlyInvisibleNoShift() {
        SootClass sc = toSootClass(getClass());
        SootMethod src = sc.getMethodByName("src9");
        SootMethod dest = sc.getMethodByName("dest9");
        copyParameterAnnotations(src, dest, 0, 2, 0, Visibility.RuntimeVisible);
        Assert.assertEquals("void dest9(int, @E int)", toString(dest));
    }
}

