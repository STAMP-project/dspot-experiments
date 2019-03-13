/**
 * Copyright (C) 2016 The Android Open Source Project
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
package libcore.java.lang.reflect.annotations;


import junit.framework.TestCase;


/* TODO(b/62095729): repeatable annotations support.
public void testIsAnnotationPresent() throws Exception {
Class<Repeated> repeated = Repeated.class;
assertIsAnnotationPresent(NoAnnotation.class, repeated, false);
assertIsAnnotationPresent(SingleAnnotation.class, repeated, true);
assertIsAnnotationPresent(MultipleAnnotation.class, repeated, false);
assertIsAnnotationPresent(MultipleAnnotationExplicitSingle.class, repeated, false);
assertIsAnnotationPresent(MultipleAnnotationOddity.class, repeated, true);
assertIsAnnotationPresent(InheritedNoNewAnnotation.class, repeated, true);
assertIsAnnotationPresent(InheritedSingleWithNewSingleAnnotation.class, repeated, true);
assertIsAnnotationPresent(InheritedSingleWithNewMultipleAnnotations.class, repeated, true);
assertIsAnnotationPresent(InheritedMultipleWithNewSingleAnnotation.class, repeated, true);
assertIsAnnotationPresent(InheritedMultipleWithNewMultipleAnnotations.class, repeated,
false);

Class<Container> container = Container.class;
assertIsAnnotationPresent(NoAnnotation.class, repeated, false);
assertIsAnnotationPresent(SingleAnnotation.class, container, false);
assertIsAnnotationPresent(MultipleAnnotation.class, container, true);
assertIsAnnotationPresent(MultipleAnnotationExplicitSingle.class, container, true);
assertIsAnnotationPresent(MultipleAnnotationOddity.class, container, true);
assertIsAnnotationPresent(InheritedNoNewAnnotation.class, container, false);
assertIsAnnotationPresent(InheritedSingleWithNewSingleAnnotation.class, container, false);
assertIsAnnotationPresent(InheritedSingleWithNewMultipleAnnotations.class, container, true);
assertIsAnnotationPresent(InheritedMultipleWithNewSingleAnnotation.class, container, true);
assertIsAnnotationPresent(InheritedMultipleWithNewMultipleAnnotations.class, container,
true);
}

public void testGetDeclaredAnnotation() throws Exception {
Class<Repeated> repeated = Repeated.class;
assertGetDeclaredAnnotation(NoAnnotation.class, repeated, null);
assertGetDeclaredAnnotation(SingleAnnotation.class, repeated, "@Repeated(1)");
assertGetDeclaredAnnotation(MultipleAnnotation.class, repeated, null);
assertGetDeclaredAnnotation(MultipleAnnotationExplicitSingle.class, repeated, null);
assertGetDeclaredAnnotation(MultipleAnnotationOddity.class, repeated, "@Repeated(1)");
assertGetDeclaredAnnotation(InheritedNoNewAnnotation.class, repeated, null);
assertGetDeclaredAnnotation(InheritedSingleWithNewSingleAnnotation.class, repeated,
"@Repeated(2)");
assertGetDeclaredAnnotation(InheritedSingleWithNewMultipleAnnotations.class, repeated,
null);
assertGetDeclaredAnnotation(InheritedMultipleWithNewSingleAnnotation.class, repeated,
"@Repeated(2)");
assertGetDeclaredAnnotation(InheritedMultipleWithNewMultipleAnnotations.class, repeated,
null);

Class<Container> container = Container.class;
assertGetDeclaredAnnotation(NoAnnotation.class, container, null);
assertGetDeclaredAnnotation(SingleAnnotation.class, container, null);
assertGetDeclaredAnnotation(MultipleAnnotation.class, container,
"@Container({@Repeated(1), @Repeated(2)})");
assertGetDeclaredAnnotation(MultipleAnnotationExplicitSingle.class, container,
"@Container({@Repeated(1)})");
assertGetDeclaredAnnotation(MultipleAnnotationOddity.class, container,
"@Container({@Repeated(2), @Repeated(3)})");
assertGetDeclaredAnnotation(InheritedNoNewAnnotation.class, container, null);
assertGetDeclaredAnnotation(InheritedSingleWithNewSingleAnnotation.class, container, null);
assertGetDeclaredAnnotation(InheritedSingleWithNewMultipleAnnotations.class, container,
"@Container({@Repeated(2), @Repeated(3)})");
assertGetDeclaredAnnotation(InheritedMultipleWithNewSingleAnnotation.class, container,
null);
assertGetDeclaredAnnotation(InheritedMultipleWithNewMultipleAnnotations.class, container,
"@Container({@Repeated(2), @Repeated(3)})");
}

public void testGetDeclaredAnnotationsByType() throws Exception {
Class<Repeated> repeated = Repeated.class;
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
NoAnnotation.class, repeated, EXPECT_EMPTY);
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
SingleAnnotation.class, repeated, new String[] { "@Repeated(1)" });
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
MultipleAnnotation.class, repeated, new String[] { "@Repeated(1)", "@Repeated(2)" });
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
MultipleAnnotationExplicitSingle.class, repeated, new String[] { "@Repeated(1)" });
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
MultipleAnnotationOddity.class, repeated,
new String[] { "@Repeated(1)", "@Repeated(2)", "@Repeated(3)" });
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
InheritedNoNewAnnotation.class, repeated, EXPECT_EMPTY);
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
InheritedSingleWithNewSingleAnnotation.class, repeated,
new String[] { "@Repeated(2)" });
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
InheritedSingleWithNewMultipleAnnotations.class, repeated,
new String[] { "@Repeated(2)", "@Repeated(3)" });
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
InheritedMultipleWithNewSingleAnnotation.class, repeated,
new String[] { "@Repeated(2)" });
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
InheritedMultipleWithNewMultipleAnnotations.class, repeated,
new String[] { "@Repeated(2)", "@Repeated(3)" });

Class<Container> container = Container.class;
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
NoAnnotation.class, container, EXPECT_EMPTY);
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
SingleAnnotation.class, container, EXPECT_EMPTY);
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
MultipleAnnotation.class, container,
new String[] { "@Container({@Repeated(1), @Repeated(2)})" });
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
MultipleAnnotationExplicitSingle.class, container,
new String[] { "@Container({@Repeated(1)})" });
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
MultipleAnnotationOddity.class, container,
new String[] { "@Container({@Repeated(2), @Repeated(3)})" });
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
InheritedNoNewAnnotation.class, container, EXPECT_EMPTY);
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
InheritedSingleWithNewSingleAnnotation.class, container, EXPECT_EMPTY);
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
InheritedSingleWithNewMultipleAnnotations.class, container,
new String[] { "@Container({@Repeated(2), @Repeated(3)})" });
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
InheritedMultipleWithNewSingleAnnotation.class, container, EXPECT_EMPTY);
AnnotatedElementTestSupport.assertGetDeclaredAnnotationsByType(
InheritedMultipleWithNewMultipleAnnotations.class, container,
new String[] { "@Container({@Repeated(2), @Repeated(3)})" });
}

public void testGetAnnotationsByType() throws Exception {
Class<Repeated> repeated = Repeated.class;
AnnotatedElementTestSupport.assertGetAnnotationsByType(
NoAnnotation.class, repeated, EXPECT_EMPTY);
AnnotatedElementTestSupport.assertGetAnnotationsByType(
SingleAnnotation.class, repeated, new String[] { "@Repeated(1)" });
AnnotatedElementTestSupport.assertGetAnnotationsByType(
MultipleAnnotation.class, repeated, new String[] { "@Repeated(1)", "@Repeated(2)" });
AnnotatedElementTestSupport.assertGetAnnotationsByType(
MultipleAnnotationExplicitSingle.class, repeated, new String[] { "@Repeated(1)" });
AnnotatedElementTestSupport.assertGetAnnotationsByType(
MultipleAnnotationOddity.class, repeated,
new String[] { "@Repeated(1)", "@Repeated(2)", "@Repeated(3)" });
AnnotatedElementTestSupport.assertGetAnnotationsByType(
InheritedNoNewAnnotation.class, repeated, new String[] { "@Repeated(1)" });
AnnotatedElementTestSupport.assertGetAnnotationsByType(
InheritedSingleWithNewSingleAnnotation.class, repeated,
new String[] { "@Repeated(2)" });
AnnotatedElementTestSupport.assertGetAnnotationsByType(
InheritedSingleWithNewMultipleAnnotations.class, repeated,
new String[] { "@Repeated(2)", "@Repeated(3)" });
AnnotatedElementTestSupport.assertGetAnnotationsByType(
InheritedMultipleWithNewSingleAnnotation.class, repeated,
new String[] { "@Repeated(2)" });
AnnotatedElementTestSupport.assertGetAnnotationsByType(
InheritedMultipleWithNewMultipleAnnotations.class, repeated,
new String[] { "@Repeated(2)", "@Repeated(3)" });

Class<Container> container = Container.class;
AnnotatedElementTestSupport.assertGetAnnotationsByType(
NoAnnotation.class, container, EXPECT_EMPTY);
AnnotatedElementTestSupport.assertGetAnnotationsByType(
SingleAnnotation.class, container, EXPECT_EMPTY);
AnnotatedElementTestSupport.assertGetAnnotationsByType(
MultipleAnnotation.class, container,
new String[] { "@Container({@Repeated(1), @Repeated(2)})" });
AnnotatedElementTestSupport.assertGetAnnotationsByType(
MultipleAnnotationExplicitSingle.class, container,
new String[] { "@Container({@Repeated(1)})" });
AnnotatedElementTestSupport.assertGetAnnotationsByType(
MultipleAnnotationOddity.class, container,
new String[] { "@Container({@Repeated(2), @Repeated(3)})" });
AnnotatedElementTestSupport.assertGetAnnotationsByType(
InheritedNoNewAnnotation.class, container, EXPECT_EMPTY);
AnnotatedElementTestSupport.assertGetAnnotationsByType(
InheritedSingleWithNewSingleAnnotation.class, container, EXPECT_EMPTY);
AnnotatedElementTestSupport.assertGetAnnotationsByType(
InheritedSingleWithNewMultipleAnnotations.class, container,
new String[] { "@Container({@Repeated(2), @Repeated(3)})" });
AnnotatedElementTestSupport.assertGetAnnotationsByType(
InheritedMultipleWithNewSingleAnnotation.class, container,
new String[] { "@Container({@Repeated(1), @Repeated(2)})" });
AnnotatedElementTestSupport.assertGetAnnotationsByType(
InheritedMultipleWithNewMultipleAnnotations.class, container,
new String[] { "@Container({@Repeated(2), @Repeated(3)})" });
}
 */
public class ClassTest extends TestCase {
    @AnnotatedElementTestSupport.AnnotationA
    @AnnotatedElementTestSupport.AnnotationB
    private static class Type {}

    public static class ExtendsType extends ClassTest.Type {}

    public void testClassDirectAnnotations() {
        AnnotatedElementTestSupport.checkAnnotatedElementPresentMethods(ClassTest.Type.class, AnnotatedElementTestSupport.AnnotationA.class, AnnotatedElementTestSupport.AnnotationB.class);
        AnnotatedElementTestSupport.checkAnnotatedElementDirectMethods(ClassTest.Type.class, AnnotatedElementTestSupport.AnnotationA.class, AnnotatedElementTestSupport.AnnotationB.class);
    }

    public void testClassInheritedAnnotations() {
        AnnotatedElementTestSupport.checkAnnotatedElementPresentMethods(ClassTest.ExtendsType.class, AnnotatedElementTestSupport.AnnotationB.class);
        AnnotatedElementTestSupport.checkAnnotatedElementDirectMethods(ClassTest.ExtendsType.class);
    }

    @AnnotatedElementTestSupport.Repeated(1)
    private static class SingleAnnotation {}

    @AnnotatedElementTestSupport.Repeated(1)
    @AnnotatedElementTestSupport.Repeated(2)
    private static class MultipleAnnotation {}

    @AnnotatedElementTestSupport.Container({ @AnnotatedElementTestSupport.Repeated(1) })
    private static class MultipleAnnotationExplicitSingle {}

    @AnnotatedElementTestSupport.Repeated(1)
    @AnnotatedElementTestSupport.Container({ @AnnotatedElementTestSupport.Repeated(2), @AnnotatedElementTestSupport.Repeated(3) })
    private static class MultipleAnnotationOddity {}

    private static class NoAnnotation {}

    private static class InheritedNoNewAnnotation extends ClassTest.SingleAnnotation {}

    @AnnotatedElementTestSupport.Repeated(2)
    private static class InheritedSingleWithNewSingleAnnotation extends ClassTest.SingleAnnotation {}

    @AnnotatedElementTestSupport.Repeated(2)
    @AnnotatedElementTestSupport.Repeated(3)
    private static class InheritedSingleWithNewMultipleAnnotations extends ClassTest.SingleAnnotation {}

    @AnnotatedElementTestSupport.Repeated(2)
    private static class InheritedMultipleWithNewSingleAnnotation extends ClassTest.MultipleAnnotation {}

    @AnnotatedElementTestSupport.Repeated(2)
    @AnnotatedElementTestSupport.Repeated(3)
    private static class InheritedMultipleWithNewMultipleAnnotations extends ClassTest.MultipleAnnotation {}
}

