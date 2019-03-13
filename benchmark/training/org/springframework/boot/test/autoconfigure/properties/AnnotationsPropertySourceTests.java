/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.test.autoconfigure.properties;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Test;
import org.springframework.core.annotation.AliasFor;

import static SkipPropertyMapping.ON_DEFAULT_VALUE;
import static SkipPropertyMapping.YES;


/**
 * Tests for {@link AnnotationsPropertySource}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class AnnotationsPropertySourceTests {
    @Test
    public void createWhenSourceIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new AnnotationsPropertySource(null)).withMessageContaining("Property source must not be null");
    }

    @Test
    public void propertiesWhenHasNoAnnotationShouldBeEmpty() {
        AnnotationsPropertySource source = new AnnotationsPropertySource(AnnotationsPropertySourceTests.NoAnnotation.class);
        assertThat(source.getPropertyNames()).isEmpty();
        assertThat(source.getProperty("value")).isNull();
    }

    @Test
    public void propertiesWhenHasTypeLevelAnnotationShouldUseAttributeName() {
        AnnotationsPropertySource source = new AnnotationsPropertySource(AnnotationsPropertySourceTests.TypeLevel.class);
        assertThat(source.getPropertyNames()).containsExactly("value");
        assertThat(source.getProperty("value")).isEqualTo("abc");
    }

    @Test
    public void propertiesWhenHasTypeLevelWithPrefixShouldUsePrefixedName() {
        AnnotationsPropertySource source = new AnnotationsPropertySource(AnnotationsPropertySourceTests.TypeLevelWithPrefix.class);
        assertThat(source.getPropertyNames()).containsExactly("test.value");
        assertThat(source.getProperty("test.value")).isEqualTo("abc");
    }

    @Test
    public void propertiesWhenHasAttributeLevelWithPrefixShouldUsePrefixedName() {
        AnnotationsPropertySource source = new AnnotationsPropertySource(AnnotationsPropertySourceTests.AttributeLevelWithPrefix.class);
        assertThat(source.getPropertyNames()).containsExactly("test");
        assertThat(source.getProperty("test")).isEqualTo("abc");
    }

    @Test
    public void propertiesWhenHasTypeAndAttributeLevelWithPrefixShouldUsePrefixedName() {
        AnnotationsPropertySource source = new AnnotationsPropertySource(AnnotationsPropertySourceTests.TypeAndAttributeLevelWithPrefix.class);
        assertThat(source.getPropertyNames()).containsExactly("test.example");
        assertThat(source.getProperty("test.example")).isEqualTo("abc");
    }

    @Test
    public void propertiesWhenNotMappedAtTypeLevelShouldIgnoreAttributes() {
        AnnotationsPropertySource source = new AnnotationsPropertySource(AnnotationsPropertySourceTests.NotMappedAtTypeLevel.class);
        assertThat(source.getPropertyNames()).containsExactly("value");
        assertThat(source.getProperty("ignore")).isNull();
    }

    @Test
    public void propertiesWhenNotMappedAtAttributeLevelShouldIgnoreAttributes() {
        AnnotationsPropertySource source = new AnnotationsPropertySource(AnnotationsPropertySourceTests.NotMappedAtAttributeLevel.class);
        assertThat(source.getPropertyNames()).containsExactly("value");
        assertThat(source.getProperty("ignore")).isNull();
    }

    @Test
    public void propertiesWhenContainsArraysShouldExpandNames() {
        AnnotationsPropertySource source = new AnnotationsPropertySource(AnnotationsPropertySourceTests.Arrays.class);
        assertThat(source.getPropertyNames()).contains("strings[0]", "strings[1]", "classes[0]", "classes[1]", "ints[0]", "ints[1]", "longs[0]", "longs[1]", "floats[0]", "floats[1]", "doubles[0]", "doubles[1]", "booleans[0]", "booleans[1]");
        assertThat(source.getProperty("strings[0]")).isEqualTo("a");
        assertThat(source.getProperty("strings[1]")).isEqualTo("b");
        assertThat(source.getProperty("classes[0]")).isEqualTo(Integer.class);
        assertThat(source.getProperty("classes[1]")).isEqualTo(Long.class);
        assertThat(source.getProperty("ints[0]")).isEqualTo(1);
        assertThat(source.getProperty("ints[1]")).isEqualTo(2);
        assertThat(source.getProperty("longs[0]")).isEqualTo(1L);
        assertThat(source.getProperty("longs[1]")).isEqualTo(2L);
        assertThat(source.getProperty("floats[0]")).isEqualTo(1.0F);
        assertThat(source.getProperty("floats[1]")).isEqualTo(2.0F);
        assertThat(source.getProperty("doubles[0]")).isEqualTo(1.0);
        assertThat(source.getProperty("doubles[1]")).isEqualTo(2.0);
        assertThat(source.getProperty("booleans[0]")).isEqualTo(false);
        assertThat(source.getProperty("booleans[1]")).isEqualTo(true);
    }

    @Test
    public void propertiesWhenHasCamelCaseShouldConvertToKebabCase() {
        AnnotationsPropertySource source = new AnnotationsPropertySource(AnnotationsPropertySourceTests.CamelCaseToKebabCase.class);
        assertThat(source.getPropertyNames()).contains("camel-case-to-kebab-case");
    }

    @Test
    public void propertiesFromMetaAnnotationsAreMapped() {
        AnnotationsPropertySource source = new AnnotationsPropertySource(AnnotationsPropertySourceTests.PropertiesFromSingleMetaAnnotation.class);
        assertThat(source.getPropertyNames()).containsExactly("value");
        assertThat(source.getProperty("value")).isEqualTo("foo");
    }

    @Test
    public void propertiesFromMultipleMetaAnnotationsAreMappedUsingTheirOwnPropertyMapping() {
        AnnotationsPropertySource source = new AnnotationsPropertySource(AnnotationsPropertySourceTests.PropertiesFromMultipleMetaAnnotations.class);
        assertThat(source.getPropertyNames()).containsExactly("value", "test.value", "test.example");
        assertThat(source.getProperty("value")).isEqualTo("alpha");
        assertThat(source.getProperty("test.value")).isEqualTo("bravo");
        assertThat(source.getProperty("test.example")).isEqualTo("charlie");
    }

    @Test
    public void propertyMappedAttributesCanBeAliased() {
        AnnotationsPropertySource source = new AnnotationsPropertySource(AnnotationsPropertySourceTests.PropertyMappedAttributeWithAnAlias.class);
        assertThat(source.getPropertyNames()).containsExactly("aliasing.value");
        assertThat(source.getProperty("aliasing.value")).isEqualTo("baz");
    }

    @Test
    public void selfAnnotatingAnnotationDoesNotCauseStackOverflow() {
        new AnnotationsPropertySource(AnnotationsPropertySourceTests.PropertyMappedWithSelfAnnotatingAnnotation.class);
    }

    @Test
    public void typeLevelAnnotationOnSuperClass() {
        AnnotationsPropertySource source = new AnnotationsPropertySource(AnnotationsPropertySourceTests.PropertyMappedAnnotationOnSuperClass.class);
        assertThat(source.getPropertyNames()).containsExactly("value");
        assertThat(source.getProperty("value")).isEqualTo("abc");
    }

    @Test
    public void aliasedPropertyMappedAttributeOnSuperClass() {
        AnnotationsPropertySource source = new AnnotationsPropertySource(AnnotationsPropertySourceTests.AliasedPropertyMappedAnnotationOnSuperClass.class);
        assertThat(source.getPropertyNames()).containsExactly("aliasing.value");
        assertThat(source.getProperty("aliasing.value")).isEqualTo("baz");
    }

    @Test
    public void enumValueMapped() {
        AnnotationsPropertySource source = new AnnotationsPropertySource(AnnotationsPropertySourceTests.EnumValueMapped.class);
        assertThat(source.getProperty("testenum.value")).isEqualTo(AnnotationsPropertySourceTests.EnumItem.TWO);
    }

    @Test
    public void enumValueNotMapped() {
        AnnotationsPropertySource source = new AnnotationsPropertySource(AnnotationsPropertySourceTests.EnumValueNotMapped.class);
        assertThat(source.containsProperty("testenum.value")).isFalse();
    }

    static class NoAnnotation {}

    @AnnotationsPropertySourceTests.TypeLevelAnnotation("abc")
    static class TypeLevel {}

    @Retention(RetentionPolicy.RUNTIME)
    @PropertyMapping
    @interface TypeLevelAnnotation {
        String value();
    }

    @AnnotationsPropertySourceTests.TypeLevelWithPrefixAnnotation("abc")
    static class TypeLevelWithPrefix {}

    @Retention(RetentionPolicy.RUNTIME)
    @PropertyMapping("test")
    @interface TypeLevelWithPrefixAnnotation {
        String value();
    }

    @AnnotationsPropertySourceTests.AttributeLevelWithPrefixAnnotation("abc")
    static class AttributeLevelWithPrefix {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface AttributeLevelWithPrefixAnnotation {
        @PropertyMapping("test")
        String value();
    }

    @AnnotationsPropertySourceTests.TypeAndAttributeLevelWithPrefixAnnotation("abc")
    static class TypeAndAttributeLevelWithPrefix {}

    @Retention(RetentionPolicy.RUNTIME)
    @PropertyMapping("test")
    @interface TypeAndAttributeLevelWithPrefixAnnotation {
        @PropertyMapping("example")
        String value();
    }

    @AnnotationsPropertySourceTests.NotMappedAtTypeLevelAnnotation("abc")
    static class NotMappedAtTypeLevel {}

    @Retention(RetentionPolicy.RUNTIME)
    @PropertyMapping(skip = YES)
    @interface NotMappedAtTypeLevelAnnotation {
        @PropertyMapping
        String value();

        String ignore() default "xyz";
    }

    @AnnotationsPropertySourceTests.NotMappedAtAttributeLevelAnnotation("abc")
    static class NotMappedAtAttributeLevel {}

    @Retention(RetentionPolicy.RUNTIME)
    @PropertyMapping
    @interface NotMappedAtAttributeLevelAnnotation {
        String value();

        @PropertyMapping(skip = SkipPropertyMapping.YES)
        String ignore() default "xyz";
    }

    @AnnotationsPropertySourceTests.ArraysAnnotation(strings = { "a", "b" }, classes = { Integer.class, Long.class }, ints = { 1, 2 }, longs = { 1, 2 }, floats = { 1.0F, 2.0F }, doubles = { 1.0, 2.0 }, booleans = { false, true })
    static class Arrays {}

    @Retention(RetentionPolicy.RUNTIME)
    @PropertyMapping
    @interface ArraysAnnotation {
        String[] strings();

        Class<?>[] classes();

        int[] ints();

        long[] longs();

        float[] floats();

        double[] doubles();

        boolean[] booleans();
    }

    @AnnotationsPropertySourceTests.CamelCaseToKebabCaseAnnotation
    static class CamelCaseToKebabCase {}

    @Retention(RetentionPolicy.RUNTIME)
    @PropertyMapping
    @interface CamelCaseToKebabCaseAnnotation {
        String camelCaseToKebabCase() default "abc";
    }

    @AnnotationsPropertySourceTests.PropertiesFromSingleMetaAnnotationAnnotation
    static class PropertiesFromSingleMetaAnnotation {}

    @Retention(RetentionPolicy.RUNTIME)
    @AnnotationsPropertySourceTests.TypeLevelAnnotation("foo")
    @interface PropertiesFromSingleMetaAnnotationAnnotation {}

    @AnnotationsPropertySourceTests.PropertiesFromMultipleMetaAnnotationsAnnotation
    static class PropertiesFromMultipleMetaAnnotations {}

    @Retention(RetentionPolicy.RUNTIME)
    @AnnotationsPropertySourceTests.TypeLevelAnnotation("alpha")
    @AnnotationsPropertySourceTests.TypeLevelWithPrefixAnnotation("bravo")
    @AnnotationsPropertySourceTests.TypeAndAttributeLevelWithPrefixAnnotation("charlie")
    @interface PropertiesFromMultipleMetaAnnotationsAnnotation {}

    @AnnotationsPropertySourceTests.AttributeWithAliasAnnotation("baz")
    static class PropertyMappedAttributeWithAnAlias {}

    @Retention(RetentionPolicy.RUNTIME)
    @AnnotationsPropertySourceTests.AliasedAttributeAnnotation
    @interface AttributeWithAliasAnnotation {
        @AliasFor(annotation = AnnotationsPropertySourceTests.AliasedAttributeAnnotation.class)
        String value() default "foo";

        String someOtherAttribute() default "shouldNotBeMapped";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @PropertyMapping("aliasing")
    @interface AliasedAttributeAnnotation {
        String value() default "bar";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @AnnotationsPropertySourceTests.SelfAnnotating
    @interface SelfAnnotating {}

    @AnnotationsPropertySourceTests.SelfAnnotating
    static class PropertyMappedWithSelfAnnotatingAnnotation {}

    static class PropertyMappedAnnotationOnSuperClass extends AnnotationsPropertySourceTests.TypeLevel {}

    static class AliasedPropertyMappedAnnotationOnSuperClass extends AnnotationsPropertySourceTests.PropertyMappedAttributeWithAnAlias {}

    @AnnotationsPropertySourceTests.EnumAnnotation(AnnotationsPropertySourceTests.EnumItem.TWO)
    static class EnumValueMapped {}

    @AnnotationsPropertySourceTests.EnumAnnotation(AnnotationsPropertySourceTests.EnumItem.DEFAULT)
    static class EnumValueNotMapped {}

    @Retention(RetentionPolicy.RUNTIME)
    @PropertyMapping("testenum")
    @interface EnumAnnotation {
        @PropertyMapping(skip = ON_DEFAULT_VALUE)
        AnnotationsPropertySourceTests.EnumItem value() default AnnotationsPropertySourceTests.EnumItem.DEFAULT;
    }

    enum EnumItem {

        DEFAULT,
        ONE,
        TWO;}
}

