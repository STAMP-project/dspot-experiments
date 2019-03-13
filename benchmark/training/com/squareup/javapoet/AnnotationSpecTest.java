/**
 * Copyright (C) 2015 Square, Inc.
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
package com.squareup.javapoet;


import TypeSpec.Builder;
import com.google.testing.compile.CompilationRule;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.lang.model.element.TypeElement;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public final class AnnotationSpecTest {
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationA {}

    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationB {}

    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationC {
        String value();
    }

    public enum Breakfast {

        WAFFLES,
        PANCAKES;
        public String toString() {
            return (name()) + " with cherries!";
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface HasDefaultsAnnotation {
        byte a() default 5;

        short b() default 6;

        int c() default 7;

        long d() default 8;

        float e() default 9.0F;

        double f() default 10.0;

        char[] g() default { 0, 51966, 'z', '?', '?', '"', '\'', '\t', '\n' };

        boolean h() default true;

        AnnotationSpecTest.Breakfast i() default AnnotationSpecTest.Breakfast.WAFFLES;

        AnnotationSpecTest.AnnotationA j() default @AnnotationSpecTest.AnnotationA;

        String k() default "maple";

        Class<? extends Annotation> l() default AnnotationSpecTest.AnnotationB.class;

        int[] m() default { 1, 2, 3 };

        AnnotationSpecTest.Breakfast[] n() default { AnnotationSpecTest.Breakfast.WAFFLES, AnnotationSpecTest.Breakfast.PANCAKES };

        AnnotationSpecTest.Breakfast o();

        int p();

        AnnotationSpecTest.AnnotationC q() default @AnnotationSpecTest.AnnotationC("foo");

        Class<? extends Number>[] r() default { Byte.class, Short.class, Integer.class, Long.class };
    }

    // empty
    @AnnotationSpecTest.HasDefaultsAnnotation(o = AnnotationSpecTest.Breakfast.PANCAKES, p = 1701, f = 11.1, m = { 9, 8, 1 }, l = Override.class, j = @AnnotationSpecTest.AnnotationA, q = @AnnotationSpecTest.AnnotationC("bar"), r = { Float.class, Double.class })
    public class IsAnnotated {}

    @Rule
    public final CompilationRule compilation = new CompilationRule();

    @Test
    public void equalsAndHashCode() {
        AnnotationSpec a = AnnotationSpec.builder(AnnotationSpecTest.AnnotationC.class).build();
        AnnotationSpec b = AnnotationSpec.builder(AnnotationSpecTest.AnnotationC.class).build();
        assertThat(a.equals(b)).isTrue();
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = AnnotationSpec.builder(AnnotationSpecTest.AnnotationC.class).addMember("value", "$S", "123").build();
        b = AnnotationSpec.builder(AnnotationSpecTest.AnnotationC.class).addMember("value", "$S", "123").build();
        assertThat(a.equals(b)).isTrue();
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void defaultAnnotation() {
        String name = AnnotationSpecTest.IsAnnotated.class.getCanonicalName();
        TypeElement element = compilation.getElements().getTypeElement(name);
        AnnotationSpec annotation = AnnotationSpec.get(element.getAnnotationMirrors().get(0));
        TypeSpec taco = TypeSpec.classBuilder("Taco").addAnnotation(annotation).build();
        assertThat(toString(taco)).isEqualTo(("" + ((((((((((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import com.squareup.javapoet.AnnotationSpecTest;\n") + "import java.lang.Double;\n") + "import java.lang.Float;\n") + "import java.lang.Override;\n") + "\n") + "@AnnotationSpecTest.HasDefaultsAnnotation(\n") + "    o = AnnotationSpecTest.Breakfast.PANCAKES,\n") + "    p = 1701,\n") + "    f = 11.1,\n") + "    m = {\n") + "        9,\n") + "        8,\n") + "        1\n") + "    },\n") + "    l = Override.class,\n") + "    j = @AnnotationSpecTest.AnnotationA,\n") + "    q = @AnnotationSpecTest.AnnotationC(\"bar\"),\n") + "    r = {\n") + "        Float.class,\n") + "        Double.class\n") + "    }\n") + ")\n") + "class Taco {\n") + "}\n")));
    }

    @Test
    public void defaultAnnotationWithImport() {
        String name = AnnotationSpecTest.IsAnnotated.class.getCanonicalName();
        TypeElement element = compilation.getElements().getTypeElement(name);
        AnnotationSpec annotation = AnnotationSpec.get(element.getAnnotationMirrors().get(0));
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(AnnotationSpecTest.IsAnnotated.class.getSimpleName());
        typeBuilder.addAnnotation(annotation);
        JavaFile file = JavaFile.builder("com.squareup.javapoet", typeBuilder.build()).build();
        assertThat(file.toString()).isEqualTo(("package com.squareup.javapoet;\n" + ((((((((((((((((((((((("\n" + "import java.lang.Double;\n") + "import java.lang.Float;\n") + "import java.lang.Override;\n") + "\n") + "@AnnotationSpecTest.HasDefaultsAnnotation(\n") + "    o = AnnotationSpecTest.Breakfast.PANCAKES,\n") + "    p = 1701,\n") + "    f = 11.1,\n") + "    m = {\n") + "        9,\n") + "        8,\n") + "        1\n") + "    },\n") + "    l = Override.class,\n") + "    j = @AnnotationSpecTest.AnnotationA,\n") + "    q = @AnnotationSpecTest.AnnotationC(\"bar\"),\n") + "    r = {\n") + "        Float.class,\n") + "        Double.class\n") + "    }\n") + ")\n") + "class IsAnnotated {\n") + "}\n")));
    }

    @Test
    public void emptyArray() {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(AnnotationSpecTest.HasDefaultsAnnotation.class);
        builder.addMember("n", "$L", "{}");
        assertThat(builder.build().toString()).isEqualTo(("@com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation(" + ("n = {}" + ")")));
        builder.addMember("m", "$L", "{}");
        assertThat(builder.build().toString()).isEqualTo(("@com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation(" + ("n = {}, m = {}" + ")")));
    }

    @Test
    public void dynamicArrayOfEnumConstants() {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(AnnotationSpecTest.HasDefaultsAnnotation.class);
        builder.addMember("n", "$T.$L", AnnotationSpecTest.Breakfast.class, AnnotationSpecTest.Breakfast.PANCAKES.name());
        assertThat(builder.build().toString()).isEqualTo(("@com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation(" + ("n = com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES" + ")")));
        // builder = AnnotationSpec.builder(HasDefaultsAnnotation.class);
        builder.addMember("n", "$T.$L", AnnotationSpecTest.Breakfast.class, AnnotationSpecTest.Breakfast.WAFFLES.name());
        builder.addMember("n", "$T.$L", AnnotationSpecTest.Breakfast.class, AnnotationSpecTest.Breakfast.PANCAKES.name());
        assertThat(builder.build().toString()).isEqualTo(("@com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation(" + (((("n = {" + "com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES") + ", com.squareup.javapoet.AnnotationSpecTest.Breakfast.WAFFLES") + ", com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES") + "})")));
        builder = builder.build().toBuilder();// idempotent

        assertThat(builder.build().toString()).isEqualTo(("@com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation(" + (((("n = {" + "com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES") + ", com.squareup.javapoet.AnnotationSpecTest.Breakfast.WAFFLES") + ", com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES") + "})")));
        builder.addMember("n", "$T.$L", AnnotationSpecTest.Breakfast.class, AnnotationSpecTest.Breakfast.WAFFLES.name());
        assertThat(builder.build().toString()).isEqualTo(("@com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation(" + ((((("n = {" + "com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES") + ", com.squareup.javapoet.AnnotationSpecTest.Breakfast.WAFFLES") + ", com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES") + ", com.squareup.javapoet.AnnotationSpecTest.Breakfast.WAFFLES") + "})")));
    }

    @Test
    public void defaultAnnotationToBuilder() {
        String name = AnnotationSpecTest.IsAnnotated.class.getCanonicalName();
        TypeElement element = compilation.getElements().getTypeElement(name);
        AnnotationSpec.Builder builder = AnnotationSpec.get(element.getAnnotationMirrors().get(0)).toBuilder();
        builder.addMember("m", "$L", 123);
        assertThat(builder.build().toString()).isEqualTo(("@com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation(" + (((((((("o = com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES" + ", p = 1701") + ", f = 11.1") + ", m = {9, 8, 1, 123}") + ", l = java.lang.Override.class") + ", j = @com.squareup.javapoet.AnnotationSpecTest.AnnotationA") + ", q = @com.squareup.javapoet.AnnotationSpecTest.AnnotationC(\"bar\")") + ", r = {java.lang.Float.class, java.lang.Double.class}") + ")")));
    }

    @Test
    public void reflectAnnotation() {
        AnnotationSpecTest.HasDefaultsAnnotation annotation = AnnotationSpecTest.IsAnnotated.class.getAnnotation(AnnotationSpecTest.HasDefaultsAnnotation.class);
        AnnotationSpec spec = AnnotationSpec.get(annotation);
        TypeSpec taco = TypeSpec.classBuilder("Taco").addAnnotation(spec).build();
        assertThat(toString(taco)).isEqualTo(("" + (((((((((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import com.squareup.javapoet.AnnotationSpecTest;\n") + "import java.lang.Double;\n") + "import java.lang.Float;\n") + "import java.lang.Override;\n") + "\n") + "@AnnotationSpecTest.HasDefaultsAnnotation(\n") + "    f = 11.1,\n") + "    l = Override.class,\n") + "    m = {\n") + "        9,\n") + "        8,\n") + "        1\n") + "    },\n") + "    o = AnnotationSpecTest.Breakfast.PANCAKES,\n") + "    p = 1701,\n") + "    q = @AnnotationSpecTest.AnnotationC(\"bar\"),\n") + "    r = {\n") + "        Float.class,\n") + "        Double.class\n") + "    }\n") + ")\n") + "class Taco {\n") + "}\n")));
    }

    @Test
    public void reflectAnnotationWithDefaults() {
        AnnotationSpecTest.HasDefaultsAnnotation annotation = AnnotationSpecTest.IsAnnotated.class.getAnnotation(AnnotationSpecTest.HasDefaultsAnnotation.class);
        AnnotationSpec spec = AnnotationSpec.get(annotation, true);
        TypeSpec taco = TypeSpec.classBuilder("Taco").addAnnotation(spec).build();
        assertThat(toString(taco)).isEqualTo(("" + (((((((((((((((((((((((((((((((((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import com.squareup.javapoet.AnnotationSpecTest;\n") + "import java.lang.Double;\n") + "import java.lang.Float;\n") + "import java.lang.Override;\n") + "\n") + "@AnnotationSpecTest.HasDefaultsAnnotation(\n") + "    a = 5,\n") + "    b = 6,\n") + "    c = 7,\n") + "    d = 8,\n") + "    e = 9.0f,\n") + "    f = 11.1,\n") + "    g = {\n") + "        \'\\u0000\',\n") + "        \'\ucafe\',\n") + "        \'z\',\n") + "        \'\u20ac\',\n") + "        \'\u2115\',\n") + "        \'\"\',\n") + "        \'\\\'\',\n") + "        \'\\t\',\n") + "        \'\\n\'\n") + "    },\n") + "    h = true,\n") + "    i = AnnotationSpecTest.Breakfast.WAFFLES,\n") + "    j = @AnnotationSpecTest.AnnotationA,\n") + "    k = \"maple\",\n") + "    l = Override.class,\n") + "    m = {\n") + "        9,\n") + "        8,\n") + "        1\n") + "    },\n") + "    n = {\n") + "        AnnotationSpecTest.Breakfast.WAFFLES,\n") + "        AnnotationSpecTest.Breakfast.PANCAKES\n") + "    },\n") + "    o = AnnotationSpecTest.Breakfast.PANCAKES,\n") + "    p = 1701,\n") + "    q = @AnnotationSpecTest.AnnotationC(\"bar\"),\n") + "    r = {\n") + "        Float.class,\n") + "        Double.class\n") + "    }\n") + ")\n") + "class Taco {\n") + "}\n")));
    }

    @Test
    public void disallowsNullMemberName() {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(AnnotationSpecTest.HasDefaultsAnnotation.class);
        try {
            AnnotationSpec.Builder $L = builder.addMember(null, "$L", "");
            Assert.fail($L.build().toString());
        } catch (NullPointerException e) {
            assertThat(e).hasMessageThat().isEqualTo("name == null");
        }
    }

    @Test
    public void requiresValidMemberName() {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(AnnotationSpecTest.HasDefaultsAnnotation.class);
        try {
            AnnotationSpec.Builder $L = builder.addMember("@", "$L", "");
            Assert.fail($L.build().toString());
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().isEqualTo("not a valid name: @");
        }
    }
}

