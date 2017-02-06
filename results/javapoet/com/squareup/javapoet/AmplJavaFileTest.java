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


@org.junit.runner.RunWith(value = org.junit.runners.JUnit4.class)
public final class AmplJavaFileTest {
    @org.junit.Test
    public void importStaticReadmeExample() {
        com.squareup.javapoet.ClassName hoverboard = com.squareup.javapoet.ClassName.get("com.mattel", "Hoverboard");
        com.squareup.javapoet.ClassName namedBoards = com.squareup.javapoet.ClassName.get("com.mattel", "Hoverboard", "Boards");
        com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get("java.util", "List");
        com.squareup.javapoet.ClassName arrayList = com.squareup.javapoet.ClassName.get("java.util", "ArrayList");
        com.squareup.javapoet.TypeName listOfHoverboards = com.squareup.javapoet.ParameterizedTypeName.get(list, hoverboard);
        com.squareup.javapoet.MethodSpec beyond = com.squareup.javapoet.MethodSpec.methodBuilder("beyond").returns(listOfHoverboards).addStatement("$T result = new $T<>()", listOfHoverboards, arrayList).addStatement("result.add($T.createNimbus(2000))", hoverboard).addStatement("result.add($T.createNimbus(\"2001\"))", hoverboard).addStatement("result.add($T.createNimbus($T.THUNDERBOLT))", hoverboard, namedBoards).addStatement("$T.sort(result)", java.util.Collections.class).addStatement("return result.isEmpty() ? $T.emptyList() : result", java.util.Collections.class).build();
        com.squareup.javapoet.TypeSpec hello = com.squareup.javapoet.TypeSpec.classBuilder("HelloWorld").addMethod(beyond).build();
        com.squareup.javapoet.JavaFile example = com.squareup.javapoet.JavaFile.builder("com.example.helloworld", hello).addStaticImport(hoverboard, "createNimbus").addStaticImport(namedBoards, "*").addStaticImport(java.util.Collections.class, "*").build();
        com.google.common.truth.Truth.assertThat(example.toString()).isEqualTo(("" + ((((((((((((((((((("package com.example.helloworld;\n" + "\n") + "import static com.mattel.Hoverboard.Boards.*;\n") + "import static com.mattel.Hoverboard.createNimbus;\n") + "import static java.util.Collections.*;\n") + "\n") + "import com.mattel.Hoverboard;\n") + "import java.util.ArrayList;\n") + "import java.util.List;\n") + "\n") + "class HelloWorld {\n") + "  List<Hoverboard> beyond() {\n") + "    List<Hoverboard> result = new ArrayList<>();\n") + "    result.add(createNimbus(2000));\n") + "    result.add(createNimbus(\"2001\"));\n") + "    result.add(createNimbus(THUNDERBOLT));\n") + "    sort(result);\n") + "    return result.isEmpty() ? emptyList() : result;\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void importStaticForCrazyFormatsWorks() {
        com.squareup.javapoet.MethodSpec method = com.squareup.javapoet.MethodSpec.methodBuilder("method").build();
        com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", com.squareup.javapoet.TypeSpec.classBuilder("Taco").addStaticBlock(com.squareup.javapoet.CodeBlock.builder().addStatement("$T", java.lang.Runtime.class).addStatement("$T.a()", java.lang.Runtime.class).addStatement("$T.X", java.lang.Runtime.class).addStatement("$T$T", java.lang.Runtime.class, java.lang.Runtime.class).addStatement("$T.$T", java.lang.Runtime.class, java.lang.Runtime.class).addStatement("$1T$1T", java.lang.Runtime.class).addStatement("$1T$2L$1T", java.lang.Runtime.class, "?").addStatement("$1T$2L$2S$1T", java.lang.Runtime.class, "?").addStatement("$1T$2L$2S$1T$3N$1T", java.lang.Runtime.class, "?", method).addStatement("$T$L", java.lang.Runtime.class, "?").addStatement("$T$S", java.lang.Runtime.class, "?").addStatement("$T$N", java.lang.Runtime.class, method).build()).build()).addStaticImport(java.lang.Runtime.class, "*").build().toString();// don't look at the generated code...
        
    }

    @org.junit.Test
    public void importStaticMixed() {
        com.squareup.javapoet.JavaFile source = com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", com.squareup.javapoet.TypeSpec.classBuilder("Taco").addStaticBlock(com.squareup.javapoet.CodeBlock.builder().addStatement("assert $1T.valueOf(\"BLOCKED\") == $1T.BLOCKED", java.lang.Thread.State.class).addStatement("$T.gc()", java.lang.System.class).addStatement("$1T.out.println($1T.nanoTime())", java.lang.System.class).build()).addMethod(com.squareup.javapoet.MethodSpec.constructorBuilder().addParameter(java.lang.Thread.State[].class, "states").varargs(true).build()).build()).addStaticImport(java.lang.Thread.State.BLOCKED).addStaticImport(java.lang.System.class, "*").addStaticImport(java.lang.Thread.State.class, "valueOf").build();
        com.google.common.truth.Truth.assertThat(source.toString()).isEqualTo(("" + ((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import static java.lang.System.*;\n") + "import static java.lang.Thread.State.BLOCKED;\n") + "import static java.lang.Thread.State.valueOf;\n") + "\n") + "import java.lang.Thread;\n") + "\n") + "class Taco {\n") + "  static {\n") + "    assert valueOf(\"BLOCKED\") == BLOCKED;\n") + "    gc();\n") + "    out.println(nanoTime());\n") + "  }\n") + "\n") + "  Taco(Thread.State... states) {\n") + "  }\n") + "}\n")));
    }

    @org.junit.Ignore(value = "addStaticImport doesn't support members with $L")
    @org.junit.Test
    public void importStaticDynamic() {
        com.squareup.javapoet.JavaFile source = com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("main").addStatement("$T.$L.println($S)", java.lang.System.class, "out", "hello").build()).build()).addStaticImport(java.lang.System.class, "out").build();
        com.google.common.truth.Truth.assertThat(source.toString()).isEqualTo(("" + (((((((("package com.squareup.tacos;\n" + "\n") + "import static java.lang.System.out;\n") + "\n") + "class Taco {\n") + "  void main() {\n") + "    out.println(\"hello\");\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void importStaticNone() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.JavaFile.builder("readme", importStaticTypeSpec("Util")).build().toString()).isEqualTo(("" + (((((((((("package readme;\n" + "\n") + "import java.lang.System;\n") + "import java.util.concurrent.TimeUnit;\n") + "\n") + "class Util {\n") + "  public static long minutesToSeconds(long minutes) {\n") + "    System.gc();\n") + "    return TimeUnit.SECONDS.convert(minutes, TimeUnit.MINUTES);\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void importStaticOnce() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.JavaFile.builder("readme", importStaticTypeSpec("Util")).addStaticImport(java.util.concurrent.TimeUnit.SECONDS).build().toString()).isEqualTo(("" + (((((((((((("package readme;\n" + "\n") + "import static java.util.concurrent.TimeUnit.SECONDS;\n") + "\n") + "import java.lang.System;\n") + "import java.util.concurrent.TimeUnit;\n") + "\n") + "class Util {\n") + "  public static long minutesToSeconds(long minutes) {\n") + "    System.gc();\n") + "    return SECONDS.convert(minutes, TimeUnit.MINUTES);\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void importStaticTwice() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.JavaFile.builder("readme", importStaticTypeSpec("Util")).addStaticImport(java.util.concurrent.TimeUnit.SECONDS).addStaticImport(java.util.concurrent.TimeUnit.MINUTES).build().toString()).isEqualTo(("" + (((((((((((("package readme;\n" + "\n") + "import static java.util.concurrent.TimeUnit.MINUTES;\n") + "import static java.util.concurrent.TimeUnit.SECONDS;\n") + "\n") + "import java.lang.System;\n") + "\n") + "class Util {\n") + "  public static long minutesToSeconds(long minutes) {\n") + "    System.gc();\n") + "    return SECONDS.convert(minutes, MINUTES);\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void importStaticUsingWildcards() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.JavaFile.builder("readme", importStaticTypeSpec("Util")).addStaticImport(java.util.concurrent.TimeUnit.class, "*").addStaticImport(java.lang.System.class, "*").build().toString()).isEqualTo(("" + (((((((((("package readme;\n" + "\n") + "import static java.lang.System.*;\n") + "import static java.util.concurrent.TimeUnit.*;\n") + "\n") + "class Util {\n") + "  public static long minutesToSeconds(long minutes) {\n") + "    gc();\n") + "    return SECONDS.convert(minutes, MINUTES);\n") + "  }\n") + "}\n")));
    }

    private com.squareup.javapoet.TypeSpec importStaticTypeSpec(java.lang.String name) {
        com.squareup.javapoet.MethodSpec method = com.squareup.javapoet.MethodSpec.methodBuilder("minutesToSeconds").addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).returns(long.class).addParameter(long.class, "minutes").addStatement("$T.gc()", java.lang.System.class).addStatement("return $1T.SECONDS.convert(minutes, $1T.MINUTES)", java.util.concurrent.TimeUnit.class).build();
        return com.squareup.javapoet.TypeSpec.classBuilder(name).addMethod(method).build();
    }

    @org.junit.Test
    public void noImports() throws java.lang.Exception {
        java.lang.String source = com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", com.squareup.javapoet.TypeSpec.classBuilder("Taco").build()).build().toString();
        com.google.common.truth.Truth.assertThat(source).isEqualTo(("" + ((("package com.squareup.tacos;\n" + "\n") + "class Taco {\n") + "}\n")));
    }

    @org.junit.Test
    public void singleImport() throws java.lang.Exception {
        java.lang.String source = com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", com.squareup.javapoet.TypeSpec.classBuilder("Taco").addField(java.util.Date.class, "madeFreshDate").build()).build().toString();
        com.google.common.truth.Truth.assertThat(source).isEqualTo(("" + (((((("package com.squareup.tacos;\n" + "\n") + "import java.util.Date;\n") + "\n") + "class Taco {\n") + "  Date madeFreshDate;\n") + "}\n")));
    }

    @org.junit.Test
    public void conflictingImports() throws java.lang.Exception {
        java.lang.String source = com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", com.squareup.javapoet.TypeSpec.classBuilder("Taco").addField(java.util.Date.class, "madeFreshDate").addField(com.squareup.javapoet.ClassName.get("java.sql", "Date"), "madeFreshDatabaseDate").build()).build().toString();
        com.google.common.truth.Truth.assertThat(source).isEqualTo(("" + (((((((("package com.squareup.tacos;\n" + "\n") + "import java.util.Date;\n") + "\n") + "class Taco {\n") + "  Date madeFreshDate;\n") + "\n") + "  java.sql.Date madeFreshDatabaseDate;\n") + "}\n")));
    }

    @org.junit.Test
    public void skipJavaLangImportsWithConflictingClassLast() throws java.lang.Exception {
        // Whatever is used first wins! In this case the Float in java.lang is imported.
        java.lang.String source = com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", com.squareup.javapoet.TypeSpec.classBuilder("Taco").addField(com.squareup.javapoet.ClassName.get("java.lang", "Float"), "litres").addField(com.squareup.javapoet.ClassName.get("com.squareup.soda", "Float"), "beverage").build()).skipJavaLangImports(true).build().toString();
        com.google.common.truth.Truth.assertThat(source).isEqualTo(("" + (((((("package com.squareup.tacos;\n" + "\n") + "class Taco {\n") + "  Float litres;\n") + "\n") + "  com.squareup.soda.Float beverage;\n") + "}\n")));
    }

    @org.junit.Test
    public void skipJavaLangImportsWithConflictingClassFirst() throws java.lang.Exception {
        // Whatever is used first wins! In this case the Float in com.squareup.soda is imported.
        java.lang.String source = com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", com.squareup.javapoet.TypeSpec.classBuilder("Taco").addField(com.squareup.javapoet.ClassName.get("com.squareup.soda", "Float"), "beverage").addField(com.squareup.javapoet.ClassName.get("java.lang", "Float"), "litres").build()).skipJavaLangImports(true).build().toString();
        com.google.common.truth.Truth.assertThat(source).isEqualTo(("" + (((((((("package com.squareup.tacos;\n" + "\n") + "import com.squareup.soda.Float;\n") + "\n") + "class Taco {\n") + "  Float beverage;\n") + "\n") + "  java.lang.Float litres;\n") + "}\n")));
    }

    @org.junit.Test
    public void conflictingParentName() throws java.lang.Exception {
        java.lang.String source = com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", com.squareup.javapoet.TypeSpec.classBuilder("A").addType(com.squareup.javapoet.TypeSpec.classBuilder("B").addType(com.squareup.javapoet.TypeSpec.classBuilder("Twin").build()).addType(com.squareup.javapoet.TypeSpec.classBuilder("C").addField(com.squareup.javapoet.ClassName.get("com.squareup.tacos", "A", "Twin", "D"), "d").build()).build()).addType(com.squareup.javapoet.TypeSpec.classBuilder("Twin").addType(com.squareup.javapoet.TypeSpec.classBuilder("D").build()).build()).build()).build().toString();
        com.google.common.truth.Truth.assertThat(source).isEqualTo(("" + (((((((((((((((("package com.squareup.tacos;\n" + "\n") + "class A {\n") + "  class B {\n") + "    class Twin {\n") + "    }\n") + "\n") + "    class C {\n") + "      A.Twin.D d;\n") + "    }\n") + "  }\n") + "\n") + "  class Twin {\n") + "    class D {\n") + "    }\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void conflictingChildName() throws java.lang.Exception {
        java.lang.String source = com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", com.squareup.javapoet.TypeSpec.classBuilder("A").addType(com.squareup.javapoet.TypeSpec.classBuilder("B").addType(com.squareup.javapoet.TypeSpec.classBuilder("C").addField(com.squareup.javapoet.ClassName.get("com.squareup.tacos", "A", "Twin", "D"), "d").addType(com.squareup.javapoet.TypeSpec.classBuilder("Twin").build()).build()).build()).addType(com.squareup.javapoet.TypeSpec.classBuilder("Twin").addType(com.squareup.javapoet.TypeSpec.classBuilder("D").build()).build()).build()).build().toString();
        com.google.common.truth.Truth.assertThat(source).isEqualTo(("" + (((((((((((((((("package com.squareup.tacos;\n" + "\n") + "class A {\n") + "  class B {\n") + "    class C {\n") + "      A.Twin.D d;\n") + "\n") + "      class Twin {\n") + "      }\n") + "    }\n") + "  }\n") + "\n") + "  class Twin {\n") + "    class D {\n") + "    }\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void conflictingNameOutOfScope() throws java.lang.Exception {
        java.lang.String source = com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", com.squareup.javapoet.TypeSpec.classBuilder("A").addType(com.squareup.javapoet.TypeSpec.classBuilder("B").addType(com.squareup.javapoet.TypeSpec.classBuilder("C").addField(com.squareup.javapoet.ClassName.get("com.squareup.tacos", "A", "Twin", "D"), "d").addType(com.squareup.javapoet.TypeSpec.classBuilder("Nested").addType(com.squareup.javapoet.TypeSpec.classBuilder("Twin").build()).build()).build()).build()).addType(com.squareup.javapoet.TypeSpec.classBuilder("Twin").addType(com.squareup.javapoet.TypeSpec.classBuilder("D").build()).build()).build()).build().toString();
        com.google.common.truth.Truth.assertThat(source).isEqualTo(("" + (((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "class A {\n") + "  class B {\n") + "    class C {\n") + "      Twin.D d;\n") + "\n") + "      class Nested {\n") + "        class Twin {\n") + "        }\n") + "      }\n") + "    }\n") + "  }\n") + "\n") + "  class Twin {\n") + "    class D {\n") + "    }\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void nestedClassAndSuperclassShareName() throws java.lang.Exception {
        java.lang.String source = com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", com.squareup.javapoet.TypeSpec.classBuilder("Taco").superclass(com.squareup.javapoet.ClassName.get("com.squareup.wire", "Message")).addType(com.squareup.javapoet.TypeSpec.classBuilder("Builder").superclass(com.squareup.javapoet.ClassName.get("com.squareup.wire", "Message", "Builder")).build()).build()).build().toString();
        com.google.common.truth.Truth.assertThat(source).isEqualTo(("" + ((((((("package com.squareup.tacos;\n" + "\n") + "import com.squareup.wire.Message;\n") + "\n") + "class Taco extends Message {\n") + "  class Builder extends Message.Builder {\n") + "  }\n") + "}\n")));
    }

    /**
     * * https://github.com/square/javapoet/issues/366
     */
    @org.junit.Test
    public void annotationIsNestedClass() throws java.lang.Exception {
        java.lang.String source = com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", com.squareup.javapoet.TypeSpec.classBuilder("TestComponent").addAnnotation(com.squareup.javapoet.ClassName.get("dagger", "Component")).addType(com.squareup.javapoet.TypeSpec.classBuilder("Builder").addAnnotation(com.squareup.javapoet.ClassName.get("dagger", "Component", "Builder")).build()).build()).build().toString();
        com.google.common.truth.Truth.assertThat(source).isEqualTo(("" + ((((((((("package com.squareup.tacos;\n" + "\n") + "import dagger.Component;\n") + "\n") + "@Component\n") + "class TestComponent {\n") + "  @Component.Builder\n") + "  class Builder {\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void defaultPackage() throws java.lang.Exception {
        java.lang.String source = com.squareup.javapoet.JavaFile.builder("", com.squareup.javapoet.TypeSpec.classBuilder("HelloWorld").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("main").addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).addParameter(java.lang.String[].class, "args").addCode("$T.out.println($S);\n", java.lang.System.class, "Hello World!").build()).build()).build().toString();
        com.google.common.truth.Truth.assertThat(source).isEqualTo(("" + ((((((("import java.lang.String;\n" + "import java.lang.System;\n") + "\n") + "class HelloWorld {\n") + "  public static void main(String[] args) {\n") + "    System.out.println(\"Hello World!\");\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void defaultPackageTypesAreNotImported() throws java.lang.Exception {
        java.lang.String source = com.squareup.javapoet.JavaFile.builder("hello", com.squareup.javapoet.TypeSpec.classBuilder("World").addSuperinterface(com.squareup.javapoet.ClassName.get("", "Test")).build()).build().toString();
        com.google.common.truth.Truth.assertThat(source).isEqualTo(("" + ((("package hello;\n" + "\n") + "class World implements Test {\n") + "}\n")));
    }

    @org.junit.Test
    public void topOfFileComment() throws java.lang.Exception {
        java.lang.String source = com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", com.squareup.javapoet.TypeSpec.classBuilder("Taco").build()).addFileComment("Generated $L by JavaPoet. DO NOT EDIT!", "2015-01-13").build().toString();
        com.google.common.truth.Truth.assertThat(source).isEqualTo(("" + (((("// Generated 2015-01-13 by JavaPoet. DO NOT EDIT!\n" + "package com.squareup.tacos;\n") + "\n") + "class Taco {\n") + "}\n")));
    }

    @org.junit.Test
    public void emptyLinesInTopOfFileComment() throws java.lang.Exception {
        java.lang.String source = com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", com.squareup.javapoet.TypeSpec.classBuilder("Taco").build()).addFileComment("\nGENERATED FILE:\n\nDO NOT EDIT!\n").build().toString();
        com.google.common.truth.Truth.assertThat(source).isEqualTo(("" + (((((((("//\n" + "// GENERATED FILE:\n") + "//\n") + "// DO NOT EDIT!\n") + "//\n") + "package com.squareup.tacos;\n") + "\n") + "class Taco {\n") + "}\n")));
    }

    @org.junit.Test
    public void packageClassConflictsWithNestedClass() throws java.lang.Exception {
        java.lang.String source = com.squareup.javapoet.JavaFile.builder("com.squareup.tacos", com.squareup.javapoet.TypeSpec.classBuilder("Taco").addField(com.squareup.javapoet.ClassName.get("com.squareup.tacos", "A"), "a").addType(com.squareup.javapoet.TypeSpec.classBuilder("A").build()).build()).build().toString();
        com.google.common.truth.Truth.assertThat(source).isEqualTo(("" + ((((((("package com.squareup.tacos;\n" + "\n") + "class Taco {\n") + "  com.squareup.tacos.A a;\n") + "\n") + "  class A {\n") + "  }\n") + "}\n")));
    }
}

