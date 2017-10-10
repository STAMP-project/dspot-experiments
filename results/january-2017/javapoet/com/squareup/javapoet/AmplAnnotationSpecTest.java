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


public final class AmplAnnotationSpecTest {
    @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
    public @interface AnnotationA {    }

    @java.lang.annotation.Inherited
    @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
    public @interface AnnotationB {    }

    @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
    public @interface AnnotationC {
        java.lang.String value();
    }

    public enum Breakfast {
WAFFLES, PANCAKES;
        public java.lang.String toString() {
            return (name()) + " with cherries!";
        }
    }

    @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
    public @interface HasDefaultsAnnotation {
        byte a() default 5;

        short b() default 6;

        int c() default 7;

        long d() default 8;

        float e() default 9.0F;

        double f() default 10.0;

        char[] g() default { 0 , 51966 , 'z' , '€' , 'ℕ' , '"' , '\'' , '\t' , '\n' };

        boolean h() default true;

        com.squareup.javapoet.AnnotationSpecTest.Breakfast i() default com.squareup.javapoet.AnnotationSpecTest.Breakfast.WAFFLES;

        com.squareup.javapoet.AnnotationSpecTest.AnnotationA j() default @com.squareup.javapoet.AnnotationSpecTest.AnnotationA
        ;

        java.lang.String k() default "maple";

        java.lang.Class<? extends java.lang.annotation.Annotation> l() default com.squareup.javapoet.AnnotationSpecTest.AnnotationB.class;

        int[] m() default { 1 , 2 , 3 };

        com.squareup.javapoet.AnnotationSpecTest.Breakfast[] n() default { com.squareup.javapoet.AnnotationSpecTest.Breakfast.WAFFLES , com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES };

        com.squareup.javapoet.AnnotationSpecTest.Breakfast o();

        int p();

        com.squareup.javapoet.AnnotationSpecTest.AnnotationC q() default @com.squareup.javapoet.AnnotationSpecTest.AnnotationC(value = "foo")
        ;

        java.lang.Class<? extends java.lang.Number>[] r() default { java.lang.Byte.class , java.lang.Short.class , java.lang.Integer.class , java.lang.Long.class };
    }

    // empty
    @com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation(o = com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES, p = 1701, f = 11.1, m = { 9 , 8 , 1 }, l = java.lang.Override.class, j = @com.squareup.javapoet.AnnotationSpecTest.AnnotationA
    , q = @com.squareup.javapoet.AnnotationSpecTest.AnnotationC(value = "bar")
    , r = { java.lang.Float.class , java.lang.Double.class })
    public class IsAnnotated {    }

    @org.junit.Rule
    public final com.google.testing.compile.CompilationRule compilation = new com.google.testing.compile.CompilationRule();

    @org.junit.Test
    public void equalsAndHashCode() {
        com.squareup.javapoet.AnnotationSpec a = com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.AnnotationSpecTest.AnnotationC.class).build();
        com.squareup.javapoet.AnnotationSpec b = com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.AnnotationSpecTest.AnnotationC.class).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.AnnotationSpecTest.AnnotationC.class).addMember("value", "$S", "123").build();
        b = com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.AnnotationSpecTest.AnnotationC.class).addMember("value", "$S", "123").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @org.junit.Test
    public void defaultAnnotation() {
        java.lang.String name = com.squareup.javapoet.AnnotationSpecTest.IsAnnotated.class.getCanonicalName();
        javax.lang.model.element.TypeElement element = compilation.getElements().getTypeElement(name);
        com.squareup.javapoet.AnnotationSpec annotation = com.squareup.javapoet.AnnotationSpec.get(element.getAnnotationMirrors().get(0));
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addAnnotation(annotation).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + ((((((((((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import com.squareup.javapoet.AnnotationSpecTest;\n") + "import java.lang.Double;\n") + "import java.lang.Float;\n") + "import java.lang.Override;\n") + "\n") + "@AnnotationSpecTest.HasDefaultsAnnotation(\n") + "    o = AnnotationSpecTest.Breakfast.PANCAKES,\n") + "    p = 1701,\n") + "    f = 11.1,\n") + "    m = {\n") + "        9,\n") + "        8,\n") + "        1\n") + "    },\n") + "    l = Override.class,\n") + "    j = @AnnotationSpecTest.AnnotationA,\n") + "    q = @AnnotationSpecTest.AnnotationC(\"bar\"),\n") + "    r = {\n") + "        Float.class,\n") + "        Double.class\n") + "    }\n") + ")\n") + "class Taco {\n") + "}\n")));
    }

    @org.junit.Test
    public void defaultAnnotationWithImport() {
        java.lang.String name = com.squareup.javapoet.AnnotationSpecTest.IsAnnotated.class.getCanonicalName();
        javax.lang.model.element.TypeElement element = compilation.getElements().getTypeElement(name);
        com.squareup.javapoet.AnnotationSpec annotation = com.squareup.javapoet.AnnotationSpec.get(element.getAnnotationMirrors().get(0));
        com.squareup.javapoet.TypeSpec.Builder typeBuilder = com.squareup.javapoet.TypeSpec.classBuilder(com.squareup.javapoet.AnnotationSpecTest.IsAnnotated.class.getSimpleName());
        typeBuilder.addAnnotation(annotation);
        com.squareup.javapoet.JavaFile file = com.squareup.javapoet.JavaFile.builder("com.squareup.javapoet", typeBuilder.build()).build();
        com.google.common.truth.Truth.assertThat(file.toString()).isEqualTo(("package com.squareup.javapoet;\n" + ((((((((((((((((((((((("\n" + "import java.lang.Double;\n") + "import java.lang.Float;\n") + "import java.lang.Override;\n") + "\n") + "@AnnotationSpecTest.HasDefaultsAnnotation(\n") + "    o = AnnotationSpecTest.Breakfast.PANCAKES,\n") + "    p = 1701,\n") + "    f = 11.1,\n") + "    m = {\n") + "        9,\n") + "        8,\n") + "        1\n") + "    },\n") + "    l = Override.class,\n") + "    j = @AnnotationSpecTest.AnnotationA,\n") + "    q = @AnnotationSpecTest.AnnotationC(\"bar\"),\n") + "    r = {\n") + "        Float.class,\n") + "        Double.class\n") + "    }\n") + ")\n") + "class IsAnnotated {\n") + "}\n")));
    }

    @org.junit.Test
    public void emptyArray() {
        com.squareup.javapoet.AnnotationSpec.Builder builder = com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation.class);
        builder.addMember("n", "$L", "{}");
        com.google.common.truth.Truth.assertThat(builder.build().toString()).isEqualTo(("@com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation(" + ("n = {}" + ")")));
        builder.addMember("m", "$L", "{}");
        com.google.common.truth.Truth.assertThat(builder.build().toString()).isEqualTo(("@com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation(" + ("n = {}, m = {}" + ")")));
    }

    @org.junit.Test
    public void dynamicArrayOfEnumConstants() {
        com.squareup.javapoet.AnnotationSpec.Builder builder = com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation.class);
        builder.addMember("n", "$T.$L", com.squareup.javapoet.AnnotationSpecTest.Breakfast.class, com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES.name());
        com.google.common.truth.Truth.assertThat(builder.build().toString()).isEqualTo(("@com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation(" + ("n = com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES" + ")")));
        // builder = AnnotationSpec.builder(HasDefaultsAnnotation.class);
        builder.addMember("n", "$T.$L", com.squareup.javapoet.AnnotationSpecTest.Breakfast.class, com.squareup.javapoet.AnnotationSpecTest.Breakfast.WAFFLES.name());
        builder.addMember("n", "$T.$L", com.squareup.javapoet.AnnotationSpecTest.Breakfast.class, com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES.name());
        com.google.common.truth.Truth.assertThat(builder.build().toString()).isEqualTo(("@com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation(" + (((("n = {" + "com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES") + ", com.squareup.javapoet.AnnotationSpecTest.Breakfast.WAFFLES") + ", com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES") + "})")));
        builder = builder.build().toBuilder();// idempotent
        
        com.google.common.truth.Truth.assertThat(builder.build().toString()).isEqualTo(("@com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation(" + (((("n = {" + "com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES") + ", com.squareup.javapoet.AnnotationSpecTest.Breakfast.WAFFLES") + ", com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES") + "})")));
        builder.addMember("n", "$T.$L", com.squareup.javapoet.AnnotationSpecTest.Breakfast.class, com.squareup.javapoet.AnnotationSpecTest.Breakfast.WAFFLES.name());
        com.google.common.truth.Truth.assertThat(builder.build().toString()).isEqualTo(("@com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation(" + ((((("n = {" + "com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES") + ", com.squareup.javapoet.AnnotationSpecTest.Breakfast.WAFFLES") + ", com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES") + ", com.squareup.javapoet.AnnotationSpecTest.Breakfast.WAFFLES") + "})")));
    }

    @org.junit.Test
    public void defaultAnnotationToBuilder() {
        java.lang.String name = com.squareup.javapoet.AnnotationSpecTest.IsAnnotated.class.getCanonicalName();
        javax.lang.model.element.TypeElement element = compilation.getElements().getTypeElement(name);
        com.squareup.javapoet.AnnotationSpec.Builder builder = com.squareup.javapoet.AnnotationSpec.get(element.getAnnotationMirrors().get(0)).toBuilder();
        builder.addMember("m", "$L", 123);
        com.google.common.truth.Truth.assertThat(builder.build().toString()).isEqualTo(("@com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation(" + (((((((("o = com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES" + ", p = 1701") + ", f = 11.1") + ", m = {9, 8, 1, 123}") + ", l = java.lang.Override.class") + ", j = @com.squareup.javapoet.AnnotationSpecTest.AnnotationA") + ", q = @com.squareup.javapoet.AnnotationSpecTest.AnnotationC(\"bar\")") + ", r = {java.lang.Float.class, java.lang.Double.class}") + ")")));
    }

    @org.junit.Test
    public void reflectAnnotation() {
        com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation annotation = com.squareup.javapoet.AnnotationSpecTest.IsAnnotated.class.getAnnotation(com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation.class);
        com.squareup.javapoet.AnnotationSpec spec = com.squareup.javapoet.AnnotationSpec.get(annotation);
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addAnnotation(spec).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import com.squareup.javapoet.AnnotationSpecTest;\n") + "import java.lang.Double;\n") + "import java.lang.Float;\n") + "import java.lang.Override;\n") + "\n") + "@AnnotationSpecTest.HasDefaultsAnnotation(\n") + "    f = 11.1,\n") + "    l = Override.class,\n") + "    m = {\n") + "        9,\n") + "        8,\n") + "        1\n") + "    },\n") + "    o = AnnotationSpecTest.Breakfast.PANCAKES,\n") + "    p = 1701,\n") + "    q = @AnnotationSpecTest.AnnotationC(\"bar\"),\n") + "    r = {\n") + "        Float.class,\n") + "        Double.class\n") + "    }\n") + ")\n") + "class Taco {\n") + "}\n")));
    }

    @org.junit.Test
    public void reflectAnnotationWithDefaults() {
        com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation annotation = com.squareup.javapoet.AnnotationSpecTest.IsAnnotated.class.getAnnotation(com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation.class);
        com.squareup.javapoet.AnnotationSpec spec = com.squareup.javapoet.AnnotationSpec.get(annotation, true);
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addAnnotation(spec).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((((((((((((((((((((((((((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import com.squareup.javapoet.AnnotationSpecTest;\n") + "import java.lang.Double;\n") + "import java.lang.Float;\n") + "import java.lang.Override;\n") + "\n") + "@AnnotationSpecTest.HasDefaultsAnnotation(\n") + "    a = 5,\n") + "    b = 6,\n") + "    c = 7,\n") + "    d = 8,\n") + "    e = 9.0f,\n") + "    f = 11.1,\n") + "    g = {\n") + "        \'\\u0000\',\n") + "        \'\ucafe\',\n") + "        \'z\',\n") + "        \'\u20ac\',\n") + "        \'\u2115\',\n") + "        \'\"\',\n") + "        \'\\\'\',\n") + "        \'\\t\',\n") + "        \'\\n\'\n") + "    },\n") + "    h = true,\n") + "    i = AnnotationSpecTest.Breakfast.WAFFLES,\n") + "    j = @AnnotationSpecTest.AnnotationA,\n") + "    k = \"maple\",\n") + "    l = Override.class,\n") + "    m = {\n") + "        9,\n") + "        8,\n") + "        1\n") + "    },\n") + "    n = {\n") + "        AnnotationSpecTest.Breakfast.WAFFLES,\n") + "        AnnotationSpecTest.Breakfast.PANCAKES\n") + "    },\n") + "    o = AnnotationSpecTest.Breakfast.PANCAKES,\n") + "    p = 1701,\n") + "    q = @AnnotationSpecTest.AnnotationC(\"bar\"),\n") + "    r = {\n") + "        Float.class,\n") + "        Double.class\n") + "    }\n") + ")\n") + "class Taco {\n") + "}\n")));
    }

    private java.lang.String toString(com.squareup.javapoet.TypeSpec typeSpec) {
        return com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", typeSpec).build().toString();
    }
}

