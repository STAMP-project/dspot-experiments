/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.core.type;


import java.awt.Color;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.stereotype.Component;

import static java.lang.Thread.State.NEW;


/**
 * Unit tests demonstrating that the reflection-based {@link StandardAnnotationMetadata}
 * and ASM-based {@code AnnotationMetadataReadingVisitor} produce identical output.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Phillip Webb
 * @author Sam Brannen
 */
public class AnnotationMetadataTests {
    @Test
    public void standardAnnotationMetadata() throws Exception {
        AnnotationMetadata metadata = new StandardAnnotationMetadata(AnnotationMetadataTests.AnnotatedComponent.class, true);
        doTestAnnotationInfo(metadata);
        doTestMethodAnnotationInfo(metadata);
    }

    @Test
    public void asmAnnotationMetadata() throws Exception {
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(AnnotationMetadataTests.AnnotatedComponent.class.getName());
        AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
        doTestAnnotationInfo(metadata);
        doTestMethodAnnotationInfo(metadata);
    }

    @Test
    public void standardAnnotationMetadataForSubclass() throws Exception {
        AnnotationMetadata metadata = new StandardAnnotationMetadata(AnnotationMetadataTests.AnnotatedComponentSubClass.class, true);
        doTestSubClassAnnotationInfo(metadata);
    }

    @Test
    public void asmAnnotationMetadataForSubclass() throws Exception {
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(AnnotationMetadataTests.AnnotatedComponentSubClass.class.getName());
        AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
        doTestSubClassAnnotationInfo(metadata);
    }

    @Test
    public void standardAnnotationMetadataForInterface() throws Exception {
        AnnotationMetadata metadata = new StandardAnnotationMetadata(AnnotationMetadata.class, true);
        doTestMetadataForInterfaceClass(metadata);
    }

    @Test
    public void asmAnnotationMetadataForInterface() throws Exception {
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(AnnotationMetadata.class.getName());
        AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
        doTestMetadataForInterfaceClass(metadata);
    }

    @Test
    public void standardAnnotationMetadataForAnnotation() throws Exception {
        AnnotationMetadata metadata = new StandardAnnotationMetadata(Component.class, true);
        doTestMetadataForAnnotationClass(metadata);
    }

    @Test
    public void asmAnnotationMetadataForAnnotation() throws Exception {
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(Component.class.getName());
        AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
        doTestMetadataForAnnotationClass(metadata);
    }

    /**
     * In order to preserve backward-compatibility, {@link StandardAnnotationMetadata}
     * defaults to return nested annotations and annotation arrays as actual
     * Annotation instances. It is recommended for compatibility with ASM-based
     * AnnotationMetadata implementations to set the 'nestedAnnotationsAsMap' flag to
     * 'true' as is done in the main test above.
     */
    @Test
    public void standardAnnotationMetadata_nestedAnnotationsAsMap_false() throws Exception {
        AnnotationMetadata metadata = new StandardAnnotationMetadata(AnnotationMetadataTests.AnnotatedComponent.class);
        AnnotationAttributes specialAttrs = ((AnnotationAttributes) (metadata.getAnnotationAttributes(AnnotationMetadataTests.SpecialAttr.class.getName())));
        Annotation[] nestedAnnoArray = ((Annotation[]) (specialAttrs.get("nestedAnnoArray")));
        Assert.assertThat(nestedAnnoArray[0], CoreMatchers.instanceOf(AnnotationMetadataTests.NestedAnno.class));
    }

    @Test
    public void metaAnnotationOverridesUsingStandardAnnotationMetadata() {
        AnnotationMetadata metadata = new StandardAnnotationMetadata(AnnotationMetadataTests.ComposedConfigurationWithAttributeOverridesClass.class);
        assertMetaAnnotationOverrides(metadata);
    }

    @Test
    public void metaAnnotationOverridesUsingAnnotationMetadataReadingVisitor() throws Exception {
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(AnnotationMetadataTests.ComposedConfigurationWithAttributeOverridesClass.class.getName());
        AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
        assertMetaAnnotationOverrides(metadata);
    }

    /**
     * https://jira.spring.io/browse/SPR-11649
     */
    @Test
    public void multipleAnnotationsWithIdenticalAttributeNamesUsingStandardAnnotationMetadata() {
        AnnotationMetadata metadata = new StandardAnnotationMetadata(AnnotationMetadataTests.NamedAnnotationsClass.class);
        assertMultipleAnnotationsWithIdenticalAttributeNames(metadata);
    }

    /**
     * https://jira.spring.io/browse/SPR-11649
     */
    @Test
    public void multipleAnnotationsWithIdenticalAttributeNamesUsingAnnotationMetadataReadingVisitor() throws Exception {
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(AnnotationMetadataTests.NamedAnnotationsClass.class.getName());
        AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
        assertMultipleAnnotationsWithIdenticalAttributeNames(metadata);
    }

    /**
     * https://jira.spring.io/browse/SPR-11649
     */
    @Test
    public void composedAnnotationWithMetaAnnotationsWithIdenticalAttributeNamesUsingStandardAnnotationMetadata() {
        AnnotationMetadata metadata = new StandardAnnotationMetadata(AnnotationMetadataTests.NamedComposedAnnotationClass.class);
        assertMultipleAnnotationsWithIdenticalAttributeNames(metadata);
    }

    /**
     * https://jira.spring.io/browse/SPR-11649
     */
    @Test
    public void composedAnnotationWithMetaAnnotationsWithIdenticalAttributeNamesUsingAnnotationMetadataReadingVisitor() throws Exception {
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(AnnotationMetadataTests.NamedComposedAnnotationClass.class.getName());
        AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
        assertMultipleAnnotationsWithIdenticalAttributeNames(metadata);
    }

    // -------------------------------------------------------------------------
    public static enum SomeEnum {

        LABEL1,
        LABEL2,
        DEFAULT;}

    @Target({  })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface NestedAnno {
        String value() default "default";

        AnnotationMetadataTests.SomeEnum anEnum() default AnnotationMetadataTests.SomeEnum.DEFAULT;

        Class<?>[] classArray() default Void.class;
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SpecialAttr {
        Class<?> clazz();

        Thread.State state();

        AnnotationMetadataTests.NestedAnno nestedAnno();

        AnnotationMetadataTests.NestedAnno[] nestedAnnoArray();

        AnnotationMetadataTests.NestedAnno optional() default @AnnotationMetadataTests.NestedAnno(value = "optional", anEnum = AnnotationMetadataTests.SomeEnum.DEFAULT, classArray = Void.class);

        AnnotationMetadataTests.NestedAnno[] optionalArray() default { @AnnotationMetadataTests.NestedAnno(value = "optional", anEnum = AnnotationMetadataTests.SomeEnum.DEFAULT, classArray = Void.class) };
    }

    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DirectAnnotation {
        @AliasFor("myValue")
        String value() default "";

        @AliasFor("value")
        String myValue() default "";

        String additional() default "direct";

        String[] additionalArray() default "direct";
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface IsAnnotatedAnnotation {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @AnnotationMetadataTests.DirectAnnotation("meta")
    @AnnotationMetadataTests.IsAnnotatedAnnotation
    public @interface MetaAnnotation {
        String additional() default "meta";
    }

    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @AnnotationMetadataTests.MetaAnnotation
    public @interface MetaMetaAnnotation {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface EnumSubclasses {
        AnnotationMetadataTests.SubclassEnum[] value();
    }

    // SPR-10914
    public enum SubclassEnum {

        FOO() {},
        BAR() {};}

    @Component("myName")
    @Scope("myScope")
    @AnnotationMetadataTests.SpecialAttr(clazz = String.class, state = NEW, nestedAnno = @AnnotationMetadataTests.NestedAnno(value = "na", anEnum = AnnotationMetadataTests.SomeEnum.LABEL1, classArray = { String.class }), nestedAnnoArray = { @AnnotationMetadataTests.NestedAnno, @AnnotationMetadataTests.NestedAnno(value = "na1", anEnum = AnnotationMetadataTests.SomeEnum.LABEL2, classArray = { Number.class }) })
    @SuppressWarnings({ "serial", "unused" })
    @AnnotationMetadataTests.DirectAnnotation(value = "direct", additional = "", additionalArray = {  })
    @AnnotationMetadataTests.MetaMetaAnnotation
    @AnnotationMetadataTests.EnumSubclasses({ AnnotationMetadataTests.SubclassEnum.FOO, AnnotationMetadataTests.SubclassEnum.BAR })
    private static class AnnotatedComponent implements Serializable {
        @TestAutowired
        public void doWork(@TestQualifier("myColor")
        Color color) {
        }

        public void doSleep() {
        }

        @AnnotationMetadataTests.DirectAnnotation("direct")
        @AnnotationMetadataTests.MetaMetaAnnotation
        public void meta() {
        }
    }

    @SuppressWarnings("serial")
    private static class AnnotatedComponentSubClass extends AnnotationMetadataTests.AnnotatedComponent {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Component
    public @interface TestConfiguration {
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface TestComponentScan {
        String[] value() default {  };

        String[] basePackages() default {  };

        Class<?>[] basePackageClasses() default {  };
    }

    @AnnotationMetadataTests.TestConfiguration
    @AnnotationMetadataTests.TestComponentScan(basePackages = "bogus")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface ComposedConfigurationWithAttributeOverrides {
        String[] basePackages() default {  };
    }

    @AnnotationMetadataTests.ComposedConfigurationWithAttributeOverrides(basePackages = "org.example.componentscan")
    public static class ComposedConfigurationWithAttributeOverridesClass {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface NamedAnnotation1 {
        String name() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface NamedAnnotation2 {
        String name() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface NamedAnnotation3 {
        String name() default "";
    }

    @AnnotationMetadataTests.NamedAnnotation1(name = "name 1")
    @AnnotationMetadataTests.NamedAnnotation2(name = "name 2")
    @AnnotationMetadataTests.NamedAnnotation3(name = "name 3")
    public static class NamedAnnotationsClass {}

    @AnnotationMetadataTests.NamedAnnotation1(name = "name 1")
    @AnnotationMetadataTests.NamedAnnotation2(name = "name 2")
    @AnnotationMetadataTests.NamedAnnotation3(name = "name 3")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface NamedComposedAnnotation {}

    @AnnotationMetadataTests.NamedComposedAnnotation
    public static class NamedComposedAnnotationClass {}
}

