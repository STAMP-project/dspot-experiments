/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.core.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests that verify support for getting and finding all composed, repeatable
 * annotations on a single annotated element.
 *
 * <p>See <a href="https://jira.spring.io/browse/SPR-13973">SPR-13973</a>.
 *
 * @author Sam Brannen
 * @since 4.3
 * @see AnnotatedElementUtils#getMergedRepeatableAnnotations
 * @see AnnotatedElementUtils#findMergedRepeatableAnnotations
 * @see AnnotatedElementUtilsTests
 * @see MultipleComposedAnnotationsOnSingleAnnotatedElementTests
 */
public class ComposedRepeatableAnnotationsTests {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void getNonRepeatableAnnotation() {
        expectNonRepeatableAnnotation();
        getMergedRepeatableAnnotations(getClass(), ComposedRepeatableAnnotationsTests.NonRepeatable.class);
    }

    @Test
    public void getInvalidRepeatableAnnotationContainerMissingValueAttribute() {
        expectContainerMissingValueAttribute();
        getMergedRepeatableAnnotations(getClass(), ComposedRepeatableAnnotationsTests.InvalidRepeatable.class, ComposedRepeatableAnnotationsTests.ContainerMissingValueAttribute.class);
    }

    @Test
    public void getInvalidRepeatableAnnotationContainerWithNonArrayValueAttribute() {
        expectContainerWithNonArrayValueAttribute();
        getMergedRepeatableAnnotations(getClass(), ComposedRepeatableAnnotationsTests.InvalidRepeatable.class, ComposedRepeatableAnnotationsTests.ContainerWithNonArrayValueAttribute.class);
    }

    @Test
    public void getInvalidRepeatableAnnotationContainerWithArrayValueAttributeButWrongComponentType() {
        expectContainerWithArrayValueAttributeButWrongComponentType();
        getMergedRepeatableAnnotations(getClass(), ComposedRepeatableAnnotationsTests.InvalidRepeatable.class, ComposedRepeatableAnnotationsTests.ContainerWithArrayValueAttributeButWrongComponentType.class);
    }

    @Test
    public void getRepeatableAnnotationsOnClass() {
        assertGetRepeatableAnnotations(ComposedRepeatableAnnotationsTests.RepeatableClass.class);
    }

    @Test
    public void getRepeatableAnnotationsOnSuperclass() {
        assertGetRepeatableAnnotations(ComposedRepeatableAnnotationsTests.SubRepeatableClass.class);
    }

    @Test
    public void getComposedRepeatableAnnotationsOnClass() {
        assertGetRepeatableAnnotations(ComposedRepeatableAnnotationsTests.ComposedRepeatableClass.class);
    }

    @Test
    public void getComposedRepeatableAnnotationsMixedWithContainerOnClass() {
        assertGetRepeatableAnnotations(ComposedRepeatableAnnotationsTests.ComposedRepeatableMixedWithContainerClass.class);
    }

    @Test
    public void getComposedContainerForRepeatableAnnotationsOnClass() {
        assertGetRepeatableAnnotations(ComposedRepeatableAnnotationsTests.ComposedContainerClass.class);
    }

    @Test
    public void getNoninheritedComposedRepeatableAnnotationsOnClass() {
        Class<?> element = ComposedRepeatableAnnotationsTests.NoninheritedRepeatableClass.class;
        Set<ComposedRepeatableAnnotationsTests.Noninherited> annotations = getMergedRepeatableAnnotations(element, ComposedRepeatableAnnotationsTests.Noninherited.class);
        assertNoninheritedRepeatableAnnotations(annotations);
    }

    @Test
    public void getNoninheritedComposedRepeatableAnnotationsOnSuperclass() {
        Class<?> element = ComposedRepeatableAnnotationsTests.SubNoninheritedRepeatableClass.class;
        Set<ComposedRepeatableAnnotationsTests.Noninherited> annotations = getMergedRepeatableAnnotations(element, ComposedRepeatableAnnotationsTests.Noninherited.class);
        Assert.assertNotNull(annotations);
        Assert.assertEquals(0, annotations.size());
    }

    @Test
    public void findNonRepeatableAnnotation() {
        expectNonRepeatableAnnotation();
        findMergedRepeatableAnnotations(getClass(), ComposedRepeatableAnnotationsTests.NonRepeatable.class);
    }

    @Test
    public void findInvalidRepeatableAnnotationContainerMissingValueAttribute() {
        expectContainerMissingValueAttribute();
        findMergedRepeatableAnnotations(getClass(), ComposedRepeatableAnnotationsTests.InvalidRepeatable.class, ComposedRepeatableAnnotationsTests.ContainerMissingValueAttribute.class);
    }

    @Test
    public void findInvalidRepeatableAnnotationContainerWithNonArrayValueAttribute() {
        expectContainerWithNonArrayValueAttribute();
        findMergedRepeatableAnnotations(getClass(), ComposedRepeatableAnnotationsTests.InvalidRepeatable.class, ComposedRepeatableAnnotationsTests.ContainerWithNonArrayValueAttribute.class);
    }

    @Test
    public void findInvalidRepeatableAnnotationContainerWithArrayValueAttributeButWrongComponentType() {
        expectContainerWithArrayValueAttributeButWrongComponentType();
        findMergedRepeatableAnnotations(getClass(), ComposedRepeatableAnnotationsTests.InvalidRepeatable.class, ComposedRepeatableAnnotationsTests.ContainerWithArrayValueAttributeButWrongComponentType.class);
    }

    @Test
    public void findRepeatableAnnotationsOnClass() {
        assertFindRepeatableAnnotations(ComposedRepeatableAnnotationsTests.RepeatableClass.class);
    }

    @Test
    public void findRepeatableAnnotationsOnSuperclass() {
        assertFindRepeatableAnnotations(ComposedRepeatableAnnotationsTests.SubRepeatableClass.class);
    }

    @Test
    public void findComposedRepeatableAnnotationsOnClass() {
        assertFindRepeatableAnnotations(ComposedRepeatableAnnotationsTests.ComposedRepeatableClass.class);
    }

    @Test
    public void findComposedRepeatableAnnotationsMixedWithContainerOnClass() {
        assertFindRepeatableAnnotations(ComposedRepeatableAnnotationsTests.ComposedRepeatableMixedWithContainerClass.class);
    }

    @Test
    public void findNoninheritedComposedRepeatableAnnotationsOnClass() {
        Class<?> element = ComposedRepeatableAnnotationsTests.NoninheritedRepeatableClass.class;
        Set<ComposedRepeatableAnnotationsTests.Noninherited> annotations = findMergedRepeatableAnnotations(element, ComposedRepeatableAnnotationsTests.Noninherited.class);
        assertNoninheritedRepeatableAnnotations(annotations);
    }

    @Test
    public void findNoninheritedComposedRepeatableAnnotationsOnSuperclass() {
        Class<?> element = ComposedRepeatableAnnotationsTests.SubNoninheritedRepeatableClass.class;
        Set<ComposedRepeatableAnnotationsTests.Noninherited> annotations = findMergedRepeatableAnnotations(element, ComposedRepeatableAnnotationsTests.Noninherited.class);
        assertNoninheritedRepeatableAnnotations(annotations);
    }

    @Test
    public void findComposedContainerForRepeatableAnnotationsOnClass() {
        assertFindRepeatableAnnotations(ComposedRepeatableAnnotationsTests.ComposedContainerClass.class);
    }

    // -------------------------------------------------------------------------
    @Retention(RetentionPolicy.RUNTIME)
    @interface NonRepeatable {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface ContainerMissingValueAttribute {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface ContainerWithNonArrayValueAttribute {
        ComposedRepeatableAnnotationsTests.InvalidRepeatable value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface ContainerWithArrayValueAttributeButWrongComponentType {
        String[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface InvalidRepeatable {}

    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface PeteRepeats {
        ComposedRepeatableAnnotationsTests.PeteRepeat[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Repeatable(ComposedRepeatableAnnotationsTests.PeteRepeats.class)
    @interface PeteRepeat {
        String value();
    }

    @ComposedRepeatableAnnotationsTests.PeteRepeat("shadowed")
    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface ForPetesSake {
        @AliasFor(annotation = ComposedRepeatableAnnotationsTests.PeteRepeat.class)
        String value();
    }

    @ComposedRepeatableAnnotationsTests.PeteRepeat("shadowed")
    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface ForTheLoveOfFoo {
        @AliasFor(annotation = ComposedRepeatableAnnotationsTests.PeteRepeat.class)
        String value();
    }

    @ComposedRepeatableAnnotationsTests.PeteRepeats({ @ComposedRepeatableAnnotationsTests.PeteRepeat("B"), @ComposedRepeatableAnnotationsTests.PeteRepeat("C") })
    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface ComposedContainer {}

    @ComposedRepeatableAnnotationsTests.PeteRepeat("A")
    @ComposedRepeatableAnnotationsTests.PeteRepeats({ @ComposedRepeatableAnnotationsTests.PeteRepeat("B"), @ComposedRepeatableAnnotationsTests.PeteRepeat("C") })
    static class RepeatableClass {}

    static class SubRepeatableClass extends ComposedRepeatableAnnotationsTests.RepeatableClass {}

    @ComposedRepeatableAnnotationsTests.ForPetesSake("B")
    @ComposedRepeatableAnnotationsTests.ForTheLoveOfFoo("C")
    @ComposedRepeatableAnnotationsTests.PeteRepeat("A")
    static class ComposedRepeatableClass {}

    @ComposedRepeatableAnnotationsTests.ForPetesSake("C")
    @ComposedRepeatableAnnotationsTests.PeteRepeats(@ComposedRepeatableAnnotationsTests.PeteRepeat("A"))
    @ComposedRepeatableAnnotationsTests.PeteRepeat("B")
    static class ComposedRepeatableMixedWithContainerClass {}

    @ComposedRepeatableAnnotationsTests.PeteRepeat("A")
    @ComposedRepeatableAnnotationsTests.ComposedContainer
    static class ComposedContainerClass {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Noninheriteds {
        ComposedRepeatableAnnotationsTests.Noninherited[] value();
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(ComposedRepeatableAnnotationsTests.Noninheriteds.class)
    @interface Noninherited {
        @AliasFor("name")
        String value() default "";

        @AliasFor("value")
        String name() default "";
    }

    @ComposedRepeatableAnnotationsTests.Noninherited(name = "shadowed")
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface ComposedNoninherited {
        @AliasFor(annotation = ComposedRepeatableAnnotationsTests.Noninherited.class)
        String name() default "";
    }

    @ComposedRepeatableAnnotationsTests.ComposedNoninherited(name = "C")
    @ComposedRepeatableAnnotationsTests.Noninheriteds({ @ComposedRepeatableAnnotationsTests.Noninherited("A"), @ComposedRepeatableAnnotationsTests.Noninherited(name = "B") })
    static class NoninheritedRepeatableClass {}

    static class SubNoninheritedRepeatableClass extends ComposedRepeatableAnnotationsTests.NoninheritedRepeatableClass {}
}

