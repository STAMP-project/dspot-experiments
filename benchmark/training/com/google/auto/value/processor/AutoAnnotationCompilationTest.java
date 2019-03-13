/**
 * Copyright (C) 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.auto.value.processor;


import com.google.common.collect.ImmutableList;
import com.google.testing.compile.JavaFileObjects;
import java.io.File;
import java.io.IOException;
import javax.tools.JavaFileObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 *
 *
 * @author emcmanus@google.com (?amonn McManus)
 */
@RunWith(JUnit4.class)
public class AutoAnnotationCompilationTest {
    @Test
    public void testSimple() {
        JavaFileObject myAnnotationJavaFile = JavaFileObjects.forSourceLines("com.example.annotations.MyAnnotation", "package com.example.annotations;", "", "import com.example.enums.MyEnum;", "", "public @interface MyAnnotation {", "  MyEnum value();", "  int defaultedValue() default 23;", "}");
        int invariableHash = (("defaultedValue".hashCode()) * 127) ^ 23;
        JavaFileObject myEnumJavaFile = JavaFileObjects.forSourceLines("com.example.enums.MyEnum", "package com.example.enums;", "", "public enum MyEnum {", "  ONE", "}");
        JavaFileObject annotationFactoryJavaFile = JavaFileObjects.forSourceLines("com.example.factories.AnnotationFactory", "package com.example.factories;", "", "import com.google.auto.value.AutoAnnotation;", "import com.example.annotations.MyAnnotation;", "import com.example.enums.MyEnum;", "", "public class AnnotationFactory {", "  @AutoAnnotation", "  public static MyAnnotation newMyAnnotation(MyEnum value) {", "    return new AutoAnnotation_AnnotationFactory_newMyAnnotation(value);", "  }", "}");
        JavaFileObject expectedOutput = JavaFileObjects.forSourceLines("com.example.factories.AutoAnnotation_AnnotationFactory_newMyAnnotation", "package com.example.factories;", "", "import com.example.annotations.MyAnnotation;", "import com.example.enums.MyEnum;", GeneratedImport.importGeneratedAnnotationType(), "", (("@Generated(\"" + (AutoAnnotationProcessor.class.getName())) + "\")"), "final class AutoAnnotation_AnnotationFactory_newMyAnnotation implements MyAnnotation {", "  private final MyEnum value;", "  private static final int defaultedValue = 23;", "", "  AutoAnnotation_AnnotationFactory_newMyAnnotation(MyEnum value) {", "    if (value == null) {", "      throw new NullPointerException(\"Null value\");", "    }", "    this.value = value;", "  }", "", "  @Override public Class<? extends MyAnnotation> annotationType() {", "    return MyAnnotation.class;", "  }", "", "  @Override public MyEnum value() {", "    return value;", "  }", "", "  @Override public int defaultedValue() {", "    return defaultedValue;", "  }", "", "  @Override public String toString() {", "    StringBuilder sb = new StringBuilder(\"@com.example.annotations.MyAnnotation(\");", "    sb.append(value);", "    return sb.append(')').toString();", "  }", "", "  @Override public boolean equals(Object o) {", "    if (o == this) {", "      return true;", "    }", "    if (o instanceof MyAnnotation) {", "      MyAnnotation that = (MyAnnotation) o;", "      return (value.equals(that.value()))", "          && (defaultedValue == that.defaultedValue());", "    }", "    return false;", "  }", "", "  @Override public int hashCode() {", "    return ", ("        " + invariableHash), (("        + (" + (127 * ("value".hashCode()))) + " ^ (value.hashCode()))"), "    ;", "  }", "}");
        assert_().about(javaSources()).that(ImmutableList.of(annotationFactoryJavaFile, myAnnotationJavaFile, myEnumJavaFile)).processedWith(new AutoAnnotationProcessor()).compilesWithoutError().and().generatesSources(expectedOutput);
    }

    @Test
    public void testEmptyPackage() {
        JavaFileObject myAnnotationJavaFile = // 
        JavaFileObjects.forSourceLines("MyAnnotation", "public @interface MyAnnotation {}");
        JavaFileObject annotationFactoryJavaFile = JavaFileObjects.forSourceLines("AnnotationFactory", "import com.google.auto.value.AutoAnnotation;", "", "public class AnnotationFactory {", "  @AutoAnnotation", "  public static MyAnnotation newMyAnnotation() {", "    return new AutoAnnotation_AnnotationFactory_newMyAnnotation();", "  }", "}");
        JavaFileObject expectedOutput = JavaFileObjects.forSourceLines("AutoAnnotation_AnnotationFactory_newMyAnnotation", GeneratedImport.importGeneratedAnnotationType(), "", (("@Generated(\"" + (AutoAnnotationProcessor.class.getName())) + "\")"), "final class AutoAnnotation_AnnotationFactory_newMyAnnotation implements MyAnnotation {", "  AutoAnnotation_AnnotationFactory_newMyAnnotation() {", "  }", "", "  @Override public Class<? extends MyAnnotation> annotationType() {", "    return MyAnnotation.class;", "  }", "", "  @Override public String toString() {", "    StringBuilder sb = new StringBuilder(\"@MyAnnotation(\");", "    return sb.append(')').toString();", "  }", "", "  @Override public boolean equals(Object o) {", "    if (o == this) {", "      return true;", "    }", "    if (o instanceof MyAnnotation) {", "      return true;", "    }", "    return false;", "  }", "", "  @Override public int hashCode() {", "    return 0;", "  }", "}");
        assertAbout(javaSources()).that(ImmutableList.of(annotationFactoryJavaFile, myAnnotationJavaFile)).processedWith(new AutoAnnotationProcessor()).compilesWithoutError().and().generatesSources(expectedOutput);
    }

    @Test
    public void testGwtSimple() {
        JavaFileObject myAnnotationJavaFile = JavaFileObjects.forSourceLines("com.example.annotations.MyAnnotation", "package com.example.annotations;", "", "import com.google.common.annotations.GwtCompatible;", "", "@GwtCompatible", "public @interface MyAnnotation {", "  int[] value();", "}");
        JavaFileObject gwtCompatibleJavaFile = JavaFileObjects.forSourceLines("com.google.common.annotations.GwtCompatible", "package com.google.common.annotations;", "", "public @interface GwtCompatible {}");
        JavaFileObject annotationFactoryJavaFile = JavaFileObjects.forSourceLines("com.example.factories.AnnotationFactory", "package com.example.factories;", "", "import com.google.auto.value.AutoAnnotation;", "import com.example.annotations.MyAnnotation;", "", "public class AnnotationFactory {", "  @AutoAnnotation", "  public static MyAnnotation newMyAnnotation(int[] value) {", "    return new AutoAnnotation_AnnotationFactory_newMyAnnotation(value);", "  }", "}");
        JavaFileObject expectedOutput = JavaFileObjects.forSourceLines("com.example.factories.AutoAnnotation_AnnotationFactory_newMyAnnotation", "package com.example.factories;", "", "import com.example.annotations.MyAnnotation;", "import java.util.Arrays;", GeneratedImport.importGeneratedAnnotationType(), "", (("@Generated(\"" + (AutoAnnotationProcessor.class.getName())) + "\")"), "final class AutoAnnotation_AnnotationFactory_newMyAnnotation implements MyAnnotation {", "  private final int[] value;", "", "  AutoAnnotation_AnnotationFactory_newMyAnnotation(int[] value) {", "    if (value == null) {", "      throw new NullPointerException(\"Null value\");", "    }", "    this.value = Arrays.copyOf(value, value.length);", "  }", "", "  @Override public Class<? extends MyAnnotation> annotationType() {", "    return MyAnnotation.class;", "  }", "", "  @Override public int[] value() {", "    return Arrays.copyOf(value, value.length);", "  }", "", "  @Override public String toString() {", "    StringBuilder sb = new StringBuilder(\"@com.example.annotations.MyAnnotation(\");", "    sb.append(Arrays.toString(value));", "    return sb.append(')').toString();", "  }", "", "  @Override public boolean equals(Object o) {", "    if (o == this) {", "      return true;", "    }", "    if (o instanceof MyAnnotation) {", "      MyAnnotation that = (MyAnnotation) o;", "      return (Arrays.equals(value,", "          (that instanceof AutoAnnotation_AnnotationFactory_newMyAnnotation)", "              ? ((AutoAnnotation_AnnotationFactory_newMyAnnotation) that).value", "              : that.value()));", "    }", "    return false;", "  }", "", "  @Override public int hashCode() {", "    return ", (("        + (" + (127 * ("value".hashCode()))) + " ^ (Arrays.hashCode(value)));"), "  }", "}");
        assert_().about(javaSources()).that(ImmutableList.of(annotationFactoryJavaFile, myAnnotationJavaFile, gwtCompatibleJavaFile)).processedWith(new AutoAnnotationProcessor()).compilesWithoutError().and().generatesSources(expectedOutput);
    }

    @Test
    public void testCollectionsForArrays() {
        JavaFileObject myAnnotationJavaFile = JavaFileObjects.forSourceLines("com.example.annotations.MyAnnotation", "package com.example.annotations;", "", "import com.example.enums.MyEnum;", "", "public @interface MyAnnotation {", "  int[] value();", "  MyEnum[] enums() default {};", "}");
        JavaFileObject myEnumJavaFile = JavaFileObjects.forSourceLines("com.example.enums.MyEnum", "package com.example.enums;", "", "public enum MyEnum {", "  ONE", "}");
        JavaFileObject annotationFactoryJavaFile = JavaFileObjects.forSourceLines("com.example.factories.AnnotationFactory", "package com.example.factories;", "", "import com.google.auto.value.AutoAnnotation;", "import com.example.annotations.MyAnnotation;", "import com.example.enums.MyEnum;", "", "import java.util.List;", "import java.util.Set;", "", "public class AnnotationFactory {", "  @AutoAnnotation", "  public static MyAnnotation newMyAnnotation(", "      List<Integer> value, Set<MyEnum> enums) {", "    return new AutoAnnotation_AnnotationFactory_newMyAnnotation(value, enums);", "  }", "}");
        JavaFileObject expectedOutput = JavaFileObjects.forSourceLines("com.example.factories.AutoAnnotation_AnnotationFactory_newMyAnnotation", "package com.example.factories;", "", "import com.example.annotations.MyAnnotation;", "import com.example.enums.MyEnum;", "import java.util.Arrays;", "import java.util.Collection;", "import java.util.List;", "import java.util.Set;", GeneratedImport.importGeneratedAnnotationType(), "", (("@Generated(\"" + (AutoAnnotationProcessor.class.getName())) + "\")"), "final class AutoAnnotation_AnnotationFactory_newMyAnnotation implements MyAnnotation {", "  private final int[] value;", "  private final MyEnum[] enums;", "", "  AutoAnnotation_AnnotationFactory_newMyAnnotation(", "      List<Integer> value,", "      Set<MyEnum> enums) {", "    if (value == null) {", "      throw new NullPointerException(\"Null value\");", "    }", "    this.value = intArrayFromCollection(value);", "    if (enums == null) {", "      throw new NullPointerException(\"Null enums\");", "    }", "    this.enums = enums.toArray(new MyEnum[0];", "  }", "", "  @Override public Class<? extends MyAnnotation> annotationType() {", "    return MyAnnotation.class;", "  }", "", "  @Override public int[] value() {", "    return value.clone();", "  }", "", "  @Override public MyEnum[] enums() {", "    return enums.clone();", "  }", "", "  @Override public String toString() {", "    StringBuilder sb = new StringBuilder(\"@com.example.annotations.MyAnnotation(\");", "    sb.append(\"value=\");", "    sb.append(Arrays.toString(value));", "    sb.append(\", \");", "    sb.append(\"enums=\");", "    sb.append(Arrays.toString(enums));", "    return sb.append(')').toString();", "  }", "", "  @Override public boolean equals(Object o) {", "    if (o == this) {", "      return true;", "    }", "    if (o instanceof MyAnnotation) {", "      MyAnnotation that = (MyAnnotation) o;", "      return (Arrays.equals(value,", "          (that instanceof AutoAnnotation_AnnotationFactory_newMyAnnotation)", "              ? ((AutoAnnotation_AnnotationFactory_newMyAnnotation) that).value", "              : that.value()))", "          && (Arrays.equals(enums,", "          (that instanceof AutoAnnotation_AnnotationFactory_newMyAnnotation)", "              ? ((AutoAnnotation_AnnotationFactory_newMyAnnotation) that).enums", "              : that.enums()))", "    }", "    return false;", "  }", "", "  @Override public int hashCode() {", "    return ", (("        + (" + (127 * ("value".hashCode()))) + " ^ (Arrays.hashCode(value)))"), (("        + (" + (127 * ("enums".hashCode()))) + " ^ (Arrays.hashCode(enums)));"), "  }", "", "  private static int[] intArrayFromCollection(Collection<Integer> c) {", "    int[] a = new int[c.size()];", "    int i = 0;", "    for (int x : c) {", "      a[i++] = x;", "    }", "    return a;", "  }", "}");
        assert_().about(javaSources()).that(ImmutableList.of(annotationFactoryJavaFile, myEnumJavaFile, myAnnotationJavaFile)).processedWith(new AutoAnnotationProcessor()).compilesWithoutError().and().generatesSources(expectedOutput);
    }

    @Test
    public void testMissingClass() throws IOException {
        File tempDir = File.createTempFile("AutoAnnotationCompilationTest", "");
        Assert.assertTrue(tempDir.delete());
        Assert.assertTrue(tempDir.mkdir());
        try {
            doTestMissingClass(tempDir);
        } finally {
            AutoAnnotationCompilationTest.removeDirectory(tempDir);
        }
    }
}

