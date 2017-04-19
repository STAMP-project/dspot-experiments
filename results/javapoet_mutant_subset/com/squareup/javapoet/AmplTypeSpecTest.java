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
public final class AmplTypeSpecTest {
    private final java.lang.String tacosPackage = "com.squareup.tacos";

    private static final java.lang.String donutsPackage = "com.squareup.donuts";

    @org.junit.Rule
    public final com.google.testing.compile.CompilationRule compilation = new com.google.testing.compile.CompilationRule();

    private javax.lang.model.element.TypeElement getElement(java.lang.Class<?> clazz) {
        return compilation.getElements().getTypeElement(clazz.getCanonicalName());
    }

    private boolean isJava8() {
        return (com.squareup.javapoet.Util.DEFAULT) != null;
    }

    @org.junit.Test
    public void basic() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("toString").addAnnotation(java.lang.Override.class).addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.FINAL).returns(java.lang.String.class).addCode("return $S;\n", "taco").build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Override;\n") + "import java.lang.String;\n") + "\n") + "class Taco {\n") + "  @Override\n") + "  public final String toString() {\n") + "    return \"taco\";\n") + "  }\n") + "}\n")));
        org.junit.Assert.assertEquals(472949424, taco.hashCode());// update expected number if source changes
        
    }

    @org.junit.Test
    public void interestingTypes() throws java.lang.Exception {
        com.squareup.javapoet.TypeName listOfAny = com.squareup.javapoet.ParameterizedTypeName.get(com.squareup.javapoet.ClassName.get(java.util.List.class), com.squareup.javapoet.WildcardTypeName.subtypeOf(java.lang.Object.class));
        com.squareup.javapoet.TypeName listOfExtends = com.squareup.javapoet.ParameterizedTypeName.get(com.squareup.javapoet.ClassName.get(java.util.List.class), com.squareup.javapoet.WildcardTypeName.subtypeOf(java.io.Serializable.class));
        com.squareup.javapoet.TypeName listOfSuper = com.squareup.javapoet.ParameterizedTypeName.get(com.squareup.javapoet.ClassName.get(java.util.List.class), com.squareup.javapoet.WildcardTypeName.supertypeOf(java.lang.String.class));
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addField(listOfAny, "extendsObject").addField(listOfExtends, "extendsSerializable").addField(listOfSuper, "superString").build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.io.Serializable;\n") + "import java.lang.String;\n") + "import java.util.List;\n") + "\n") + "class Taco {\n") + "  List<?> extendsObject;\n") + "\n") + "  List<? extends Serializable> extendsSerializable;\n") + "\n") + "  List<? super String> superString;\n") + "}\n")));
    }

    @org.junit.Test
    public void anonymousInnerClass() throws java.lang.Exception {
        com.squareup.javapoet.ClassName foo = com.squareup.javapoet.ClassName.get(tacosPackage, "Foo");
        com.squareup.javapoet.ClassName bar = com.squareup.javapoet.ClassName.get(tacosPackage, "Bar");
        com.squareup.javapoet.ClassName thingThang = com.squareup.javapoet.ClassName.get(tacosPackage, "Thing", "Thang");
        com.squareup.javapoet.TypeName thingThangOfFooBar = com.squareup.javapoet.ParameterizedTypeName.get(thingThang, foo, bar);
        com.squareup.javapoet.ClassName thung = com.squareup.javapoet.ClassName.get(tacosPackage, "Thung");
        com.squareup.javapoet.ClassName simpleThung = com.squareup.javapoet.ClassName.get(tacosPackage, "SimpleThung");
        com.squareup.javapoet.TypeName thungOfSuperBar = com.squareup.javapoet.ParameterizedTypeName.get(thung, com.squareup.javapoet.WildcardTypeName.supertypeOf(bar));
        com.squareup.javapoet.TypeName thungOfSuperFoo = com.squareup.javapoet.ParameterizedTypeName.get(thung, com.squareup.javapoet.WildcardTypeName.supertypeOf(foo));
        com.squareup.javapoet.TypeName simpleThungOfBar = com.squareup.javapoet.ParameterizedTypeName.get(simpleThung, bar);
        com.squareup.javapoet.ParameterSpec thungParameter = com.squareup.javapoet.ParameterSpec.builder(thungOfSuperFoo, "thung").addModifiers(javax.lang.model.element.Modifier.FINAL).build();
        com.squareup.javapoet.TypeSpec aSimpleThung = com.squareup.javapoet.TypeSpec.anonymousClassBuilder("$N", thungParameter).superclass(simpleThungOfBar).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("doSomething").addAnnotation(java.lang.Override.class).addModifiers(javax.lang.model.element.Modifier.PUBLIC).addParameter(bar, "bar").addCode("/* code snippets */\n").build()).build();
        com.squareup.javapoet.TypeSpec aThingThang = com.squareup.javapoet.TypeSpec.anonymousClassBuilder("").superclass(thingThangOfFooBar).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("call").addAnnotation(java.lang.Override.class).addModifiers(javax.lang.model.element.Modifier.PUBLIC).returns(thungOfSuperBar).addParameter(thungParameter).addCode("return $L;\n", aSimpleThung).build()).build();
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addField(com.squareup.javapoet.FieldSpec.builder(thingThangOfFooBar, "NAME").addModifiers(javax.lang.model.element.Modifier.STATIC, javax.lang.model.element.Modifier.FINAL, javax.lang.model.element.Modifier.FINAL).initializer("$L", aThingThang).build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Override;\n") + "\n") + "class Taco {\n") + "  static final Thing.Thang<Foo, Bar> NAME = new Thing.Thang<Foo, Bar>() {\n") + "    @Override\n") + "    public Thung<? super Bar> call(final Thung<? super Foo> thung) {\n") + "      return new SimpleThung<Bar>(thung) {\n") + "        @Override\n") + "        public void doSomething(Bar bar) {\n") + "          /* code snippets */\n") + "        }\n") + "      };\n") + "    }\n") + "  };\n") + "}\n")));
    }

    @org.junit.Test
    public void annotatedParameters() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec service = com.squareup.javapoet.TypeSpec.classBuilder("Foo").addMethod(com.squareup.javapoet.MethodSpec.constructorBuilder().addModifiers(javax.lang.model.element.Modifier.).addParameter(long.class, "id").addParameter(com.squareup.javapoet.ParameterSpec.builder(java.lang.String.class, "one").addAnnotation(com.squareup.javapoet.ClassName.get(tacosPackage, "Ping")).build()).addParameter(com.squareup.javapoet.ParameterSpec.builder(java.lang.String.class, "two").addAnnotation(com.squareup.javapoet.ClassName.get(tacosPackage, "Ping")).build()).addParameter(com.squareup.javapoet.ParameterSpec.builder(java.lang.String.class, "three").addAnnotation(com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.ClassName.get(tacosPackage, "Pong")).addMember("value", "$S", "pong").build()).build()).addParameter(com.squareup.javapoet.ParameterSpec.builder(java.lang.String.class, "four").addAnnotation(com.squareup.javapoet.ClassName.get(tacosPackage, "Ping")).build()).addCode("/* code snippets */\n").build()).build();
        com.google.common.truth.Truth.assertThat(toString(service)).isEqualTo(("" + ((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.String;\n") + "\n") + "class Foo {\n") + "  public Foo(long id, @Ping String one, @Ping String two, @Pong(\"pong\") String three,\n") + "      @Ping String four) {\n") + "    /* code snippets */\n") + "  }\n") + "}\n")));
    }

    /**
     * We had a bug where annotations were preventing us from doing the right thing when resolving
     * imports. https://github.com/square/javapoet/issues/422
     */
    @org.junit.Test
    public void annotationsAndJavaLangTypes() throws java.lang.Exception {
        com.squareup.javapoet.ClassName freeRange = com.squareup.javapoet.ClassName.get("javax.annotation", "FreeRange");
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("EthicalTaco").addField(com.squareup.javapoet.ClassName.get(java.lang.String.class).annotated(com.squareup.javapoet.AnnotationSpec.builder(freeRange).build()), "meat").build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + ((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.String;\n") + "import javax.annotation.FreeRange;\n") + "\n") + "class EthicalTaco {\n") + "  @FreeRange String meat;\n") + "}\n")));
    }

    @org.junit.Test
    public void retrofitStyleInterface() throws java.lang.Exception {
        com.squareup.javapoet.ClassName observable = com.squareup.javapoet.ClassName.get(tacosPackage, "Observable");
        com.squareup.javapoet.ClassName fooBar = com.squareup.javapoet.ClassName.get(tacosPackage, "FooBar");
        com.squareup.javapoet.ClassName thing = com.squareup.javapoet.ClassName.get(tacosPackage, "Thing");
        com.squareup.javapoet.ClassName things = com.squareup.javapoet.ClassName.get(tacosPackage, "Things");
        com.squareup.javapoet.ClassName map = com.squareup.javapoet.ClassName.get("java.util", "Map");
        com.squareup.javapoet.ClassName string = com.squareup.javapoet.ClassName.get("java.lang", "String");
        com.squareup.javapoet.ClassName headers = com.squareup.javapoet.ClassName.get(tacosPackage, "Headers");
        com.squareup.javapoet.ClassName post = com.squareup.javapoet.ClassName.get(tacosPackage, "POST");
        com.squareup.javapoet.ClassName body = com.squareup.javapoet.ClassName.get(tacosPackage, "Body");
        com.squareup.javapoet.ClassName queryMap = com.squareup.javapoet.ClassName.get(tacosPackage, "QueryMap");
        com.squareup.javapoet.ClassName header = com.squareup.javapoet.ClassName.get(tacosPackage, "Header");
        com.squareup.javapoet.TypeSpec service = com.squareup.javapoet.TypeSpec.interfaceBuilder("Service").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("fooBar").addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.ABSTRACT).addAnnotation(com.squareup.javapoet.AnnotationSpec.builder(headers).addMember("value", "$S", "Accept: application/json").addMember("value", "$S", "User-Agent: foobar").build()).addAnnotation(com.squareup.javapoet.AnnotationSpec.builder(post).addMember("value", "$S", "/foo/bar").build()).returns(com.squareup.javapoet.ParameterizedTypeName.get(observable, fooBar)).addParameter(com.squareup.javapoet.ParameterSpec.builder(com.squareup.javapoet.ParameterizedTypeName.get(things, thing), "things").addAnnotation(body).build()).addParameter(com.squareup.javapoet.ParameterSpec.builder(com.squareup.javapoet.ParameterizedTypeName.get(map, string, string), "query").addAnnotation(com.squareup.javapoet.AnnotationSpec.builder(queryMap).addMember("encodeValues", "false").build()).build()).addParameter(com.squareup.javapoet.ParameterSpec.builder(string, "authorization").addAnnotation(com.squareup.javapoet.AnnotationSpec.builder(header).addMember("value", "$S", "Authorization").build()).build()).build()).build();
        com.google.common.truth.Truth.assertThat(toString(service)).isEqualTo(("" + (((((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.String;\n") + "import java.util.Map;\n") + "\n") + "interface Service {\n") + "  @Headers({\n") + "      \"Accept: application/json\",\n") + "      \"User-Agent: foobar\"\n") + "  })\n") + "  @POST(\"/foo/bar\")\n") + "  Observable<FooBar> fooBar(@Body Things<Thing> things,\n") + "      @QueryMap(encodeValues = false) Map<String, String> query,\n") + "      @Header(\"Authorization\") String authorization);\n") + "}\n")));
    }

    @org.junit.Test
    public void annotatedField() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addField(com.squareup.javapoet.FieldSpec.builder(java.lang.String.class, "thing", javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.FINAL).addAnnotation(com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.ClassName.get(tacosPackage, "JsonAdapter")).addMember("value", "$T.class", com.squareup.javapoet.ClassName.get(tacosPackage, "Foo")).build()).build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + ((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.String;\n") + "\n") + "class Taco {\n") + "  @JsonAdapter(Foo.class)\n") + "  private final String thing;\n") + "}\n")));
    }

    @org.junit.Test
    public void annotatedClass() throws java.lang.Exception {
        com.squareup.javapoet.ClassName someType = com.squareup.javapoet.ClassName.get(tacosPackage, "SomeType");
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Foo").addAnnotation(com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.ClassName.get(tacosPackage, "Something")).addMember("hi", "$T.$N", someType, "FIELD").addMember("hey", "$L", 12).addMember("hello", "$S", "goodbye").build()).addModifiers(javax.lang.model.element.Modifier.PUBLIC).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((("package com.squareup.tacos;\n" + "\n") + "@Something(\n") + "    hi = SomeType.FIELD,\n") + "    hey = 12,\n") + "    hello = \"goodbye\"\n") + ")\n") + "public class Foo {\n") + "}\n")));
    }

    @org.junit.Test
    public void enumWithSubclassing() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec roshambo = com.squareup.javapoet.TypeSpec.enumBuilder("Roshambo").addModifiers(javax.lang.model.element.Modifier.).addEnumConstant("ROCK", com.squareup.javapoet.TypeSpec.anonymousClassBuilder("").addJavadoc("Avalanche!\n").build()).addEnumConstant("PAPER", com.squareup.javapoet.TypeSpec.anonymousClassBuilder("$S", "flat").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("toString").addAnnotation(java.lang.Override.class).addModifiers(javax.lang.model.element.Modifier.PUBLIC).returns(java.lang.String.class).addCode("return $S;\n", "paper airplane!").build()).build()).addEnumConstant("SCISSORS", com.squareup.javapoet.TypeSpec.anonymousClassBuilder("$S", "peace sign").build()).addField(java.lang.String.class, "handPosition", javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.FINAL).addMethod(com.squareup.javapoet.MethodSpec.constructorBuilder().addParameter(java.lang.String.class, "handPosition").addCode("this.handPosition = handPosition;\n").build()).addMethod(com.squareup.javapoet.MethodSpec.constructorBuilder().addCode("this($S);\n", "fist").build()).build();
        com.google.common.truth.Truth.assertThat(toString(roshambo)).isEqualTo(("" + ((((((((((((((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Override;\n") + "import java.lang.String;\n") + "\n") + "public enum Roshambo {\n") + "  /**\n") + "   * Avalanche!\n") + "   */\n") + "  ROCK,\n") + "\n") + "  PAPER(\"flat\") {\n") + "    @Override\n") + "    public String toString() {\n") + "      return \"paper airplane!\";\n") + "    }\n") + "  },\n") + "\n") + "  SCISSORS(\"peace sign\");\n") + "\n") + "  private final String handPosition;\n") + "\n") + "  Roshambo(String handPosition) {\n") + "    this.handPosition = handPosition;\n") + "  }\n") + "\n") + "  Roshambo() {\n") + "    this(\"fist\");\n") + "  }\n") + "}\n")));
    }

    /**
     * * https://github.com/square/javapoet/issues/193
     */
    @org.junit.Test
    public void enumsMayDefineAbstractMethods() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec roshambo = com.squareup.javapoet.TypeSpec.enumBuilder("Tortilla").addModifiers(javax.lang.model.element.Modifier.).addEnumConstant("CORN", com.squareup.javapoet.TypeSpec.anonymousClassBuilder("").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("fold").addAnnotation(java.lang.Override.class).addModifiers(javax.lang.model.element.Modifier.PUBLIC).build()).build()).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("fold").addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.ABSTRACT).build()).build();
        com.google.common.truth.Truth.assertThat(toString(roshambo)).isEqualTo(("" + (((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Override;\n") + "\n") + "public enum Tortilla {\n") + "  CORN {\n") + "    @Override\n") + "    public void fold() {\n") + "    }\n") + "  };\n") + "\n") + "  public abstract void fold();\n") + "}\n")));
    }

    @org.junit.Test
    public void enumConstantsRequired() throws java.lang.Exception {
        try {
            com.squareup.javapoet.TypeSpec.enumBuilder("Roshambo").build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
        }
    }

    @org.junit.Test
    public void onlyEnumsMayHaveEnumConstants() throws java.lang.Exception {
        try {
            com.squareup.javapoet.TypeSpec.classBuilder("Roshambo").addEnumConstant("ROCK").build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException expected) {
        }
    }

    @org.junit.Test
    public void enumWithMembersButNoConstructorCall() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec roshambo = com.squareup.javapoet.TypeSpec.enumBuilder("Roshambo").addEnumConstant("SPOCK", com.squareup.javapoet.TypeSpec.anonymousClassBuilder("").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("toString").addAnnotation(java.lang.Override.).addModifiers(javax.lang.model.element.Modifier.PUBLIC).returns(java.lang.String.class).addCode("return $S;\n", "west side").build()).build()).build();
        com.google.common.truth.Truth.assertThat(toString(roshambo)).isEqualTo(("" + (((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Override;\n") + "import java.lang.String;\n") + "\n") + "enum Roshambo {\n") + "  SPOCK {\n") + "    @Override\n") + "    public String toString() {\n") + "      return \"west side\";\n") + "    }\n") + "  }\n") + "}\n")));
    }

    /**
     * * https://github.com/square/javapoet/issues/253
     */
    @org.junit.Test
    public void enumWithAnnotatedValues() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec roshambo = com.squareup.javapoet.TypeSpec.enumBuilder("Roshambo").addModifiers(javax.lang.model.element.Modifier.).addEnumConstant("ROCK", com.squareup.javapoet.TypeSpec.anonymousClassBuilder("").addAnnotation(java.lang.Deprecated.class).build()).addEnumConstant("PAPER").addEnumConstant("SCISSORS").build();
        com.google.common.truth.Truth.assertThat(toString(roshambo)).isEqualTo(("" + ((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Deprecated;\n") + "\n") + "public enum Roshambo {\n") + "  @Deprecated\n") + "  ROCK,\n") + "\n") + "  PAPER,\n") + "\n") + "  SCISSORS\n") + "}\n")));
    }

    @org.junit.Test
    public void methodThrows() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addModifiers(javax.lang.model.element.Modifier.ABSTRACT).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("throwOne").addException(java.io.IOException.class).build()).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("throwTwo").addException(java.io.IOException.class).addException(com.squareup.javapoet.ClassName.get(tacosPackage, "SourCreamException")).build()).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("abstractThrow").addModifiers(javax.lang.model.element.Modifier.ABSTRACT).addException(java.io.IOException.class).build()).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("nativeThrow").addModifiers(javax.lang.model.element.Modifier.NATIVE).addException(java.io.IOException.class).build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.io.IOException;\n") + "\n") + "abstract class Taco {\n") + "  void throwOne() throws IOException {\n") + "  }\n") + "\n") + "  void throwTwo() throws IOException, SourCreamException {\n") + "  }\n") + "\n") + "  abstract void abstractThrow() throws IOException;\n") + "\n") + "  native void nativeThrow() throws IOException;\n") + "}\n")));
    }

    @org.junit.Test
    public void typeVariables() throws java.lang.Exception {
        com.squareup.javapoet.TypeVariableName t = com.squareup.javapoet.TypeVariableName.get("T");
        com.squareup.javapoet.TypeVariableName p = com.squareup.javapoet.TypeVariableName.get("P", java.lang.Number.class);
        com.squareup.javapoet.ClassName location = com.squareup.javapoet.ClassName.get(tacosPackage, "Location");
        com.squareup.javapoet.TypeSpec typeSpec = com.squareup.javapoet.TypeSpec.classBuilder("Location").addTypeVariable(t).addTypeVariable(p).addSuperinterface(com.squareup.javapoet.ParameterizedTypeName.get(com.squareup.javapoet.ClassName.get(java.lang.Comparable.class), p)).addField(t, "label").addField(p, "x").addField(p, "y").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("compareTo").addAnnotation(java.lang.Override.class).addModifiers(javax.lang.model.element.Modifier.PUBLIC).returns(int.class).addParameter(p, "p").addCode("return 0;\n").build()).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("of").addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).addTypeVariable(t).addTypeVariable(p).returns(com.squareup.javapoet.ParameterizedTypeName.get(location, t, p)).addParameter(t, "label").addParameter(p, "x").addParameter(p, "y").addCode("throw new $T($S);\n", java.lang.UnsupportedOperationException.class, "TODO").build()).build();
        com.google.common.truth.Truth.assertThat(toString(typeSpec)).isEqualTo(("" + (((((((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Comparable;\n") + "import java.lang.Number;\n") + "import java.lang.Override;\n") + "import java.lang.UnsupportedOperationException;\n") + "\n") + "class Location<T, P extends Number> implements Comparable<P> {\n") + "  T label;\n") + "\n") + "  P x;\n") + "\n") + "  P y;\n") + "\n") + "  @Override\n") + "  public int compareTo(P p) {\n") + "    return 0;\n") + "  }\n") + "\n") + "  public static <T, P extends Number> Location<T, P> of(T label, P x, P y) {\n") + "    throw new UnsupportedOperationException(\"TODO\");\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void typeVariableWithBounds() {
        com.squareup.javapoet.AnnotationSpec a = com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.ClassName.get("com.squareup.tacos", "A")).build();
        com.squareup.javapoet.TypeVariableName p = com.squareup.javapoet.TypeVariableName.get("P", java.lang.Number.class);
        com.squareup.javapoet.TypeVariableName q = ((com.squareup.javapoet.TypeVariableName) (com.squareup.javapoet.TypeVariableName.get("Q", java.lang.Number.class).annotated(a)));
        com.squareup.javapoet.TypeSpec typeSpec = com.squareup.javapoet.TypeSpec.classBuilder("Location").addTypeVariable(p.withBounds(java.lang.Comparable.class)).addTypeVariable(q.withBounds(java.lang.Comparable.class)).addField(p, "x").addField(q, "y").build();
        com.google.common.truth.Truth.assertThat(toString(typeSpec)).isEqualTo(("" + ((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Comparable;\n") + "import java.lang.Number;\n") + "\n") + "class Location<P extends Number & Comparable, Q extends Number & Comparable> {\n") + "  P x;\n") + "\n") + "  @A Q y;\n") + "}\n")));
    }

    @org.junit.Test
    public void classImplementsExtends() throws java.lang.Exception {
        com.squareup.javapoet.ClassName taco = com.squareup.javapoet.ClassName.get(tacosPackage, "Taco");
        com.squareup.javapoet.ClassName food = com.squareup.javapoet.ClassName.get("com.squareup.tacos", "Food");
        com.squareup.javapoet.TypeSpec typeSpec = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addModifiers(javax.lang.model.element.Modifier.).superclass(com.squareup.javapoet.ParameterizedTypeName.get(com.squareup.javapoet.ClassName.get(java.util.AbstractSet.class), food)).addSuperinterface(java.io.Serializable.class).addSuperinterface(com.squareup.javapoet.ParameterizedTypeName.get(com.squareup.javapoet.ClassName.get(java.lang.Comparable.class), taco)).build();
        com.google.common.truth.Truth.assertThat(toString(typeSpec)).isEqualTo(("" + (((((((("package com.squareup.tacos;\n" + "\n") + "import java.io.Serializable;\n") + "import java.lang.Comparable;\n") + "import java.util.AbstractSet;\n") + "\n") + "abstract class Taco extends AbstractSet<Food> ") + "implements Serializable, Comparable<Taco> {\n") + "}\n")));
    }

    @org.junit.Test
    public void classImplementsExtendsSameName() throws java.lang.Exception {
        com.squareup.javapoet.ClassName javapoetTaco = com.squareup.javapoet.ClassName.get(tacosPackage, "Taco");
        com.squareup.javapoet.ClassName tacoBellTaco = com.squareup.javapoet.ClassName.get("com.taco.bell", "Taco");
        com.squareup.javapoet.ClassName fishTaco = com.squareup.javapoet.ClassName.get("org.fish.taco", "Taco");
        com.squareup.javapoet.TypeSpec typeSpec = com.squareup.javapoet.TypeSpec.classBuilder("Taco").superclass(fishTaco).addSuperinterface(com.squareup.javapoet.ParameterizedTypeName.get(com.squareup.javapoet.ClassName.get(java.lang.Comparable.class), javapoetTaco)).addSuperinterface(tacoBellTaco).build();
        com.google.common.truth.Truth.assertThat(toString(typeSpec)).isEqualTo(("" + (((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Comparable;\n") + "\n") + "class Taco extends org.fish.taco.Taco ") + "implements Comparable<Taco>, com.taco.bell.Taco {\n") + "}\n")));
    }

    @org.junit.Test
    public void classImplementsNestedClass() throws java.lang.Exception {
        com.squareup.javapoet.ClassName outer = com.squareup.javapoet.ClassName.get(tacosPackage, "Outer");
        com.squareup.javapoet.ClassName inner = outer.nestedClass("Inner");
        com.squareup.javapoet.ClassName callable = com.squareup.javapoet.ClassName.get(java.util.concurrent.Callable.);
        com.squareup.javapoet.TypeSpec typeSpec = com.squareup.javapoet.TypeSpec.classBuilder("Outer").superclass(com.squareup.javapoet.ParameterizedTypeName.get(callable, inner)).addType(com.squareup.javapoet.TypeSpec.classBuilder("Inner").addModifiers(javax.lang.model.element.Modifier.STATIC).build()).build();
        com.google.common.truth.Truth.assertThat(toString(typeSpec)).isEqualTo(("" + ((((((("package com.squareup.tacos;\n" + "\n") + "import java.util.concurrent.Callable;\n") + "\n") + "class Outer extends Callable<Outer.Inner> {\n") + "  static class Inner {\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void enumImplements() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec typeSpec = com.squareup.javapoet.TypeSpec.enumBuilder("Food").addSuperinterface(java.io.Serializable.).addSuperinterface(java.lang.Cloneable.class).addEnumConstant("LEAN_GROUND_BEEF").addEnumConstant("SHREDDED_CHEESE").build();
        com.google.common.truth.Truth.assertThat(toString(typeSpec)).isEqualTo(("" + ((((((((("package com.squareup.tacos;\n" + "\n") + "import java.io.Serializable;\n") + "import java.lang.Cloneable;\n") + "\n") + "enum Food implements Serializable, Cloneable {\n") + "  LEAN_GROUND_BEEF,\n") + "\n") + "  SHREDDED_CHEESE\n") + "}\n")));
    }

    @org.junit.Test
    public void interfaceExtends() throws java.lang.Exception {
        com.squareup.javapoet.ClassName taco = com.squareup.javapoet.ClassName.get(tacosPackage, "Taco");
        com.squareup.javapoet.TypeSpec typeSpec = com.squareup.javapoet.TypeSpec.interfaceBuilder("Taco").addSuperinterface(java.io.Serializable.class).addSuperinterface(com.squareup.javapoet.ParameterizedTypeName.get(com.squareup.javapoet.ClassName.get(java.lang.Comparable.class), taco)).build();
        com.google.common.truth.Truth.assertThat(toString(typeSpec)).isEqualTo(("" + (((((("package com.squareup.tacos;\n" + "\n") + "import java.io.Serializable;\n") + "import java.lang.Comparable;\n") + "\n") + "interface Taco extends Serializable, Comparable<Taco> {\n") + "}\n")));
    }

    @org.junit.Test
    public void nestedClasses() throws java.lang.Exception {
        com.squareup.javapoet.ClassName taco = com.squareup.javapoet.ClassName.get(tacosPackage, "Combo", "Taco");
        com.squareup.javapoet.ClassName topping = com.squareup.javapoet.ClassName.get(tacosPackage, "Combo", "Taco", "Topping");
        com.squareup.javapoet.ClassName chips = com.squareup.javapoet.ClassName.get(tacosPackage, "Combo", "Chips");
        com.squareup.javapoet.ClassName sauce = com.squareup.javapoet.ClassName.get(tacosPackage, "Combo", "Sauce");
        com.squareup.javapoet.TypeSpec typeSpec = com.squareup.javapoet.TypeSpec.classBuilder("Combo").addField(taco, "taco").addField(chips, "chips").addType(com.squareup.javapoet.TypeSpec.classBuilder(taco.simpleName()).addModifiers(javax.lang.model.element.Modifier.STATIC).addField(com.squareup.javapoet.ParameterizedTypeName.get(com.squareup.javapoet.ClassName.get(java.util.List.class), topping), "toppings").addField(sauce, "sauce").addType(com.squareup.javapoet.TypeSpec.enumBuilder(topping.simpleName()).addEnumConstant("SHREDDED_CHEESE").addEnumConstant("LEAN_GROUND_BEEF").build()).build()).addType(com.squareup.javapoet.TypeSpec.classBuilder(chips.simpleName()).addModifiers(javax.lang.model.element.Modifier.STATIC).addField(topping, "topping").addField(sauce, "dippingSauce").build()).addType(com.squareup.javapoet.TypeSpec.enumBuilder(sauce.simpleName()).addEnumConstant("SOUR_CREAM").addEnumConstant("SALSA").addEnumConstant("QUESO").addEnumConstant("MILD").addEnumConstant("FIRE").build()).build();
        com.google.common.truth.Truth.assertThat(toString(typeSpec)).isEqualTo(("" + (((((((((((((((((((((((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.util.List;\n") + "\n") + "class Combo {\n") + "  Taco taco;\n") + "\n") + "  Chips chips;\n") + "\n") + "  static class Taco {\n") + "    List<Topping> toppings;\n") + "\n") + "    Sauce sauce;\n") + "\n") + "    enum Topping {\n") + "      SHREDDED_CHEESE,\n") + "\n") + "      LEAN_GROUND_BEEF\n") + "    }\n") + "  }\n") + "\n") + "  static class Chips {\n") + "    Taco.Topping topping;\n") + "\n") + "    Sauce dippingSauce;\n") + "  }\n") + "\n") + "  enum Sauce {\n") + "    SOUR_CREAM,\n") + "\n") + "    SALSA,\n") + "\n") + "    QUESO,\n") + "\n") + "    MILD,\n") + "\n") + "    FIRE\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void annotation() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec annotation = com.squareup.javapoet.TypeSpec.annotationBuilder("MyAnnotation").addModifiers(javax.lang.model.element.Modifier.).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("test").addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.ABSTRACT).defaultValue("$L", 0).returns(int.class).build()).build();
        com.google.common.truth.Truth.assertThat(toString(annotation)).isEqualTo(("" + (((("package com.squareup.tacos;\n" + "\n") + "public @interface MyAnnotation {\n") + "  int test() default 0;\n") + "}\n")));
    }

    @org.junit.Test
    public void innerAnnotationInAnnotationDeclaration() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec bar = com.squareup.javapoet.TypeSpec.annotationBuilder("Bar").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("value").addModifiers(javax.lang.model.element.Modifier., javax.lang.model.element.Modifier.ABSTRACT).defaultValue("@$T", java.lang.Deprecated.class).returns(java.lang.Deprecated.class).build()).build();
        com.google.common.truth.Truth.assertThat(toString(bar)).isEqualTo(("" + (((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Deprecated;\n") + "\n") + "@interface Bar {\n") + "  Deprecated value() default @Deprecated;\n") + "}\n")));
    }

    @org.junit.Test
    public void annotationWithFields() {
        com.squareup.javapoet.FieldSpec field = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO").addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC, javax.lang.model.element.Modifier.FINAL).initializer("$L", 101).build();
        com.squareup.javapoet.TypeSpec anno = com.squareup.javapoet.TypeSpec.annotationBuilder("Anno").addField(field).build();
        com.google.common.truth.Truth.assertThat(toString(anno)).isEqualTo(("" + (((("package com.squareup.tacos;\n" + "\n") + "@interface Anno {\n") + "  int FOO = 101;\n") + "}\n")));
    }

    @org.junit.Test
    public void classCannotHaveDefaultValueForMethod() throws java.lang.Exception {
        try {
            com.squareup.javapoet.TypeSpec.classBuilder("Tacos").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("test").addModifiers(javax.lang.model.element.Modifier.PUBLIC).defaultValue("0").returns(int.class).build()).build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException expected) {
        }
    }

    @org.junit.Test
    public void classCannotHaveDefaultMethods() throws java.lang.Exception {
        org.junit.Assume.assumeTrue(isJava8());
        try {
            com.squareup.javapoet.TypeSpec.classBuilder("Tacos").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("test").addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.valueOf("DEFAULT")).returns(int.class).addCode(com.squareup.javapoet.CodeBlock.builder().addStatement("return 0").build()).build()).build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException expected) {
        }
    }

    @org.junit.Test
    public void interfaceStaticMethods() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec bar = com.squareup.javapoet.TypeSpec.interfaceBuilder("Tacos").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("test").addModifiers(javax.lang.model.element.Modifier., javax.lang.model.element.Modifier.STATIC).returns(int.class).addCode(com.squareup.javapoet.CodeBlock.builder().addStatement("return 0").build()).build()).build();
        com.google.common.truth.Truth.assertThat(toString(bar)).isEqualTo(("" + (((((("package com.squareup.tacos;\n" + "\n") + "interface Tacos {\n") + "  static int test() {\n") + "    return 0;\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void interfaceDefaultMethods() throws java.lang.Exception {
        org.junit.Assume.assumeTrue(isJava8());
        com.squareup.javapoet.TypeSpec bar = com.squareup.javapoet.TypeSpec.interfaceBuilder("Tacos").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("test").addModifiers(javax.lang.model.element.Modifier., javax.lang.model.element.Modifier.valueOf("DEFAULT")).returns(int.class).addCode(com.squareup.javapoet.CodeBlock.builder().addStatement("return 0").build()).build()).build();
        com.google.common.truth.Truth.assertThat(toString(bar)).isEqualTo(("" + (((((("package com.squareup.tacos;\n" + "\n") + "interface Tacos {\n") + "  default int test() {\n") + "    return 0;\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void referencedAndDeclaredSimpleNamesConflict() throws java.lang.Exception {
        com.squareup.javapoet.FieldSpec internalTop = com.squareup.javapoet.FieldSpec.builder(com.squareup.javapoet.ClassName.get(tacosPackage, "Top"), "internalTop").build();
        com.squareup.javapoet.FieldSpec internalBottom = com.squareup.javapoet.FieldSpec.builder(com.squareup.javapoet.ClassName.get(tacosPackage, "Top", "Middle", "Bottom"), "internalBottom").build();
        com.squareup.javapoet.FieldSpec externalTop = com.squareup.javapoet.FieldSpec.builder(com.squareup.javapoet.ClassName.get(com.squareup.javapoet.AmplTypeSpecTest.donutsPackage, "Top"), "externalTop").build();
        com.squareup.javapoet.FieldSpec externalBottom = com.squareup.javapoet.FieldSpec.builder(com.squareup.javapoet.ClassName.get(com.squareup.javapoet.AmplTypeSpecTest.donutsPackage, "Bottom"), "externalBottom").build();
        com.squareup.javapoet.TypeSpec top = com.squareup.javapoet.TypeSpec.classBuilder("Top").addField(internalTop).addField(internalBottom).addField(externalTop).addField(externalBottom).addType(com.squareup.javapoet.TypeSpec.classBuilder("Middle").addField(internalTop).addField(internalBottom).addField(externalTop).addField(externalBottom).addType(com.squareup.javapoet.TypeSpec.classBuilder("Bottom").addField(internalTop).addField(internalBottom).addField(externalTop).addField(externalBottom).build()).build()).build();
        com.google.common.truth.Truth.assertThat(toString(top)).isEqualTo(("" + (((((((((((((((((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import com.squareup.donuts.Bottom;\n") + "\n") + "class Top {\n") + "  Top internalTop;\n") + "\n") + "  Middle.Bottom internalBottom;\n") + "\n") + "  com.squareup.donuts.Top externalTop;\n") + "\n") + "  Bottom externalBottom;\n") + "\n") + "  class Middle {\n") + "    Top internalTop;\n") + "\n") + "    Bottom internalBottom;\n") + "\n") + "    com.squareup.donuts.Top externalTop;\n") + "\n") + "    com.squareup.donuts.Bottom externalBottom;\n") + "\n") + "    class Bottom {\n") + "      Top internalTop;\n") + "\n") + "      Bottom internalBottom;\n") + "\n") + "      com.squareup.donuts.Top externalTop;\n") + "\n") + "      com.squareup.donuts.Bottom externalBottom;\n") + "    }\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void simpleNamesConflictInThisAndOtherPackage() throws java.lang.Exception {
        com.squareup.javapoet.FieldSpec internalOther = com.squareup.javapoet.FieldSpec.builder(com.squareup.javapoet.ClassName.get(tacosPackage, "Other"), "internalOther").build();
        com.squareup.javapoet.FieldSpec externalOther = com.squareup.javapoet.FieldSpec.builder(com.squareup.javapoet.ClassName.get(com.squareup.javapoet.AmplTypeSpecTest.donutsPackage, "Other"), "externalOther").build();
        com.squareup.javapoet.TypeSpec gen = com.squareup.javapoet.TypeSpec.classBuilder("Gen").addField(internalOther).addField(externalOther).build();
        com.google.common.truth.Truth.assertThat(toString(gen)).isEqualTo(("" + (((((("package com.squareup.tacos;\n" + "\n") + "class Gen {\n") + "  Other internalOther;\n") + "\n") + "  com.squareup.donuts.Other externalOther;\n") + "}\n")));
    }

    @org.junit.Test
    public void originatingElementsIncludesThoseOfNestedTypes() {
        javax.lang.model.element.Element outerElement = org.mockito.Mockito.mock(javax.lang.model.element.Element.class);
        javax.lang.model.element.Element innerElement = org.mockito.Mockito.mock(javax.lang.model.element.Element.class);
        com.squareup.javapoet.TypeSpec outer = com.squareup.javapoet.TypeSpec.classBuilder("Outer").addOriginatingElement(outerElement).addType(com.squareup.javapoet.TypeSpec.classBuilder("Inner").addOriginatingElement(innerElement).build()).build();
        com.google.common.truth.Truth.assertThat(outer.originatingElements).containsExactly(outerElement, innerElement);
    }

    @org.junit.Test
    public void intersectionType() {
        com.squareup.javapoet.TypeVariableName typeVariable = com.squareup.javapoet.TypeVariableName.get("T", java.util.Comparator.class, java.io.Serializable.class);
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("getComparator").addTypeVariable(typeVariable).returns(typeVariable).addCode("return null;\n").build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + ((((((((("package com.squareup.tacos;\n" + "\n") + "import java.io.Serializable;\n") + "import java.util.Comparator;\n") + "\n") + "class Taco {\n") + "  <T extends Comparator & Serializable> T getComparator() {\n") + "    return null;\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void arrayType() {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addField(int[].class, "ints").build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((("package com.squareup.tacos;\n" + "\n") + "class Taco {\n") + "  int[] ints;\n") + "}\n")));
    }

    @org.junit.Test
    public void javadoc() {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addJavadoc("A hard or soft tortilla, loosely folded and filled with whatever {@link \n").addJavadoc("{@link $T random} tex-mex stuff we could find in the pantry\n", java.util.Random.class).addJavadoc(com.squareup.javapoet.CodeBlock.of("and some {@link $T} cheese.\n", java.lang.String.class)).addField(com.squareup.javapoet.FieldSpec.builder(boolean.class, "soft").addJavadoc("True for a soft flour tortilla; false for a crunchy corn tortilla.\n").build()).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("refold").addJavadoc(("Folds the back of this taco to reduce sauce leakage.\n" + ("\n" + "<p>For {@link $T#KOREAN}, the front may also be folded.\n")), java.util.Locale.class).addParameter(java.util.Locale.class, "locale").build()).build();
        // Mentioning a type in Javadoc will not cause an import to be added (java.util.Random here),
        // but the short name will be used if it's already imported (java.util.Locale here).
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.util.Locale;\n") + "\n") + "/**\n") + " * A hard or soft tortilla, loosely folded and filled with whatever {@link \n") + " * {@link java.util.Random random} tex-mex stuff we could find in the pantry\n") + " * and some {@link java.lang.String} cheese.\n") + " */\n") + "class Taco {\n") + "  /**\n") + "   * True for a soft flour tortilla; false for a crunchy corn tortilla.\n") + "   */\n") + "  boolean soft;\n") + "\n") + "  /**\n") + "   * Folds the back of this taco to reduce sauce leakage.\n") + "   *\n") + "   * <p>For {@link Locale#KOREAN}, the front may also be folded.\n") + "   */\n") + "  void refold(Locale locale) {\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void annotationsInAnnotations() throws java.lang.Exception {
        com.squareup.javapoet.ClassName beef = com.squareup.javapoet.ClassName.get(tacosPackage, "Beef");
        com.squareup.javapoet.ClassName chicken = com.squareup.javapoet.ClassName.get(tacosPackage, "Chicken");
        com.squareup.javapoet.ClassName option = com.squareup.javapoet.ClassName.get(tacosPackage, "Option");
        com.squareup.javapoet.ClassName mealDeal = com.squareup.javapoet.ClassName.get(tacosPackage, "MealDeal");
        com.squareup.javapoet.TypeSpec menu = com.squareup.javapoet.TypeSpec.classBuilder("Menu").addAnnotation(com.squareup.javapoet.AnnotationSpec.builder(mealDeal).addMember("price", "$L", 500).addMember("options", "$L", com.squareup.javapoet.AnnotationSpec.builder(option).addMember("name", "$S", "taco").addMember("meat", "$T.class", beef).build()).addMember("options", "$L", com.squareup.javapoet.AnnotationSpec.builder(option).addMember("name", "$S", "quesadilla").addMember("meat", "$T.class", chicken).build()).build()).build();
        com.google.common.truth.Truth.assertThat(toString(menu)).isEqualTo(("" + (((((((((("package com.squareup.tacos;\n" + "\n") + "@MealDeal(\n") + "    price = 500,\n") + "    options = {\n") + "        @Option(name = \"taco\", meat = Beef.class),\n") + "        @Option(name = \"quesadilla\", meat = Chicken.class)\n") + "    }\n") + ")\n") + "class Menu {\n") + "}\n")));
    }

    @org.junit.Test
    public void varargs() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec taqueria = com.squareup.javapoet.TypeSpec.classBuilder("Taqueria").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("prepare").addParameter(int., "workers").addParameter(java.lang.Runnable[].class, "jobs").varargs().build()).build();
        com.google.common.truth.Truth.assertThat(toString(taqueria)).isEqualTo(("" + ((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Runnable;\n") + "\n") + "class Taqueria {\n") + "  void prepare(int workers, Runnable... jobs) {\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void codeBlocks() throws java.lang.Exception {
        com.squareup.javapoet.CodeBlock ifBlock = com.squareup.javapoet.CodeBlock.builder().beginControlFlow("if (!a.equals(b))").addStatement("return i").endControlFlow().build();
        com.squareup.javapoet.CodeBlock methodBody = com.squareup.javapoet.CodeBlock.builder().addStatement("$T size = $T.min(listA.size(), listB.size())", int.class, java.lang.Math.class).beginControlFlow("for ($T i = 0; i < size; i++)", int.class).addStatement("$T $N = $N.get(i)", java.lang.String.class, "a", "listA").addStatement("$T $N = $N.get(i)", java.lang.String.class, "b", "listB").add("$L", ifBlock).endControlFlow().addStatement("return size").build();
        com.squareup.javapoet.CodeBlock fieldBlock = com.squareup.javapoet.CodeBlock.builder().add("$>$>").add("\n$T.<$T, $T>builder()$>$>", com.google.common.collect.ImmutableMap.class, java.lang.String.class, java.lang.String.class).add("\n.add($S, $S)", '\'', "&#39;").add("\n.add($S, $S)", '&', "&amp;").add("\n.add($S, $S)", '<', "&lt;").add("\n.add($S, $S)", '>', "&gt;").add("\n.build()$<$<").add("$<$<").build();
        com.squareup.javapoet.FieldSpec escapeHtml = com.squareup.javapoet.FieldSpec.builder(com.squareup.javapoet.ParameterizedTypeName.get(java.util.Map.class, java.lang.String.class, java.lang.String.class), "ESCAPE_HTML").addModifiers(javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.STATIC, javax.lang.model.element.Modifier.FINAL).initializer(fieldBlock).build();
        com.squareup.javapoet.TypeSpec util = com.squareup.javapoet.TypeSpec.classBuilder("Util").addField(escapeHtml).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("commonPrefixLength").returns(int.class).addParameter(com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class), "listA").addParameter(com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class), "listB").addCode(methodBody).build()).build();
        com.google.common.truth.Truth.assertThat(toString(util)).isEqualTo(("" + (((((((((((((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import com.google.common.collect.ImmutableMap;\n") + "import java.lang.Math;\n") + "import java.lang.String;\n") + "import java.util.List;\n") + "import java.util.Map;\n") + "\n") + "class Util {\n") + "  private static final Map<String, String> ESCAPE_HTML = \n") + "      ImmutableMap.<String, String>builder()\n") + "          .add(\"\'\", \"&#39;\")\n") + "          .add(\"&\", \"&amp;\")\n") + "          .add(\"<\", \"&lt;\")\n") + "          .add(\">\", \"&gt;\")\n") + "          .build();\n") + "\n") + "  int commonPrefixLength(List<String> listA, List<String> listB) {\n") + "    int size = Math.min(listA.size(), listB.size());\n") + "    for (int i = 0; i < size; i++) {\n") + "      String a = listA.get(i);\n") + "      String b = listB.get(i);\n") + "      if (!a.equals(b)) {\n") + "        return i;\n") + "      }\n") + "    }\n") + "    return size;\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void indexedElseIf() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("choices").beginControlFlow("if ($1L != null || $1L == $2L)", "taco", "otherTaco").addStatement("$T.out.println($S)", java.lang.System.class, "only one taco? NOO!").nextControlFlow("else if ($1L.$3L && $2L.$3L)", "taco", "otherTaco", "isSupreme()").addStatement("$T.out.println($S)", java.lang.System.class, "taco heaven").endControlFlow().build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.System;\n") + "\n") + "class Taco {\n") + "  void choices() {\n") + "    if (taco != null || taco == otherTaco) {\n") + "      System.out.println(\"only one taco? NOO!\");\n") + "    } else if (taco.isSupreme() && otherTaco.isSupreme()) {\n") + "      System.out.println(\"taco heaven\");\n") + "    }\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void elseIf() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("choices").beginControlFlow("if (5 < 4) ").addStatement("$T.out.println($S)", java.lang.System.class, "wat").nextControlFlow("else if (5 < 6)").addStatement("$T.out.println($S)", java.lang.System.class, "hello").endControlFlow().build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.System;\n") + "\n") + "class Taco {\n") + "  void choices() {\n") + "    if (5 < 4)  {\n") + "      System.out.println(\"wat\");\n") + "    } else if (5 < 6) {\n") + "      System.out.println(\"hello\");\n") + "    }\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void doWhile() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("loopForever").beginControlFlow("do").addStatement("$T.out.println($S)", java.lang.System.class, "hello").endControlFlow("while (5 < 6)").build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.System;\n") + "\n") + "class Taco {\n") + "  void loopForever() {\n") + "    do {\n") + "      System.out.println(\"hello\");\n") + "    } while (5 < 6);\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void inlineIndent() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("inlineIndent").addCode("if (3 < 4) {\n$>$T.out.println($S);\n$<}\n", java.lang.System.class, "hello").build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.System;\n") + "\n") + "class Taco {\n") + "  void inlineIndent() {\n") + "    if (3 < 4) {\n") + "      System.out.println(\"hello\");\n") + "    }\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void defaultModifiersForInterfaceMembers() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.interfaceBuilder("Taco").addField(com.squareup.javapoet.FieldSpec.builder(java.lang.String.class, "SHELL").addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC, javax.lang.model.element.Modifier.FINAL).initializer("$S", "crunchy corn").build()).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("fold").addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.ABSTRACT).build()).addType(com.squareup.javapoet.TypeSpec.classBuilder("Topping").addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + ((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.String;\n") + "\n") + "interface Taco {\n") + "  String SHELL = \"crunchy corn\";\n") + "\n") + "  void fold();\n") + "\n") + "  class Topping {\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void defaultModifiersForMemberInterfacesAndEnums() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addType(com.squareup.javapoet.TypeSpec.classBuilder("Meat").addModifiers(javax.lang.model.element.Modifier.STATIC).build()).addType(com.squareup.javapoet.TypeSpec.interfaceBuilder("Tortilla").addModifiers(javax.lang.model.element.Modifier.STATIC).build()).addType(com.squareup.javapoet.TypeSpec.enumBuilder("Topping").addModifiers(javax.lang.model.element.Modifier.STATIC).addEnumConstant("SALSA").build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((((("package com.squareup.tacos;\n" + "\n") + "class Taco {\n") + "  static class Meat {\n") + "  }\n") + "\n") + "  interface Tortilla {\n") + "  }\n") + "\n") + "  enum Topping {\n") + "    SALSA\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void membersOrdering() throws java.lang.Exception {
        // Hand out names in reverse-alphabetical order to defend against unexpected sorting.
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Members").addType(com.squareup.javapoet.TypeSpec.classBuilder("Z").build()).addType(com.squareup.javapoet.TypeSpec.classBuilder("Y").build()).addField(java.lang.String.class, "X", javax.lang.model.element.Modifier.STATIC).addField(java.lang.String.class, "W").addField(java.lang.String.class, "V", javax.lang.model.element.Modifier.STATIC).addField(java.lang.String.class, "U").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("T").addModifiers(javax.lang.model.element.Modifier.STATIC).build()).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("S").build()).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("R").addModifiers(javax.lang.model.element.Modifier.STATIC).build()).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("Q").build()).addMethod(com.squareup.javapoet.MethodSpec.constructorBuilder().addParameter(int.class, "p").build()).addMethod(com.squareup.javapoet.MethodSpec.constructorBuilder().addParameter(long.class, "o").build()).build();
        // Static fields, instance fields, constructors, methods, classes.
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((((((((((((((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.String;\n") + "\n") + "class Members {\n") + "  static String X;\n") + "\n") + "  static String V;\n") + "\n") + "  String W;\n") + "\n") + "  String U;\n") + "\n") + "  Members(int p) {\n") + "  }\n") + "\n") + "  Members(long o) {\n") + "  }\n") + "\n") + "  static void T() {\n") + "  }\n") + "\n") + "  void S() {\n") + "  }\n") + "\n") + "  static void R() {\n") + "  }\n") + "\n") + "  void Q() {\n") + "  }\n") + "\n") + "  class Z {\n") + "  }\n") + "\n") + "  class Y {\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void nativeMethods() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec taco = // GWT JSNI
        com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("nativeInt").addModifiers(javax.lang.model.element.Modifier.NATIVE).returns(int.class).build()).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("alert").addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC, javax.lang.model.element.Modifier.NATIVE).addParameter(java.lang.String.class, "msg").addCode(com.squareup.javapoet.CodeBlock.builder().add(" /*-{\n").indent().addStatement("$$wnd.alert(msg)").unindent().add("}-*/").build()).build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.String;\n") + "\n") + "class Taco {\n") + "  native int nativeInt();\n") + "\n") + "  public static native void alert(String msg) /*-{\n") + "    $wnd.alert(msg);\n") + "  }-*/;\n") + "}\n")));
    }

    @org.junit.Test
    public void nullStringLiteral() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addField(com.squareup.javapoet.FieldSpec.builder(java.lang.String.class, "NULL").initializer("$S", ((java.lang.Object) (null))).build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.String;\n") + "\n") + "class Taco {\n") + "  String NULL = null;\n") + "}\n")));
    }

    @org.junit.Test
    public void annotationToString() throws java.lang.Exception {
        com.squareup.javapoet.AnnotationSpec annotation = com.squareup.javapoet.AnnotationSpec.builder(java.lang.SuppressWarnings.class).addMember("value", "$S", "unused").build();
        com.google.common.truth.Truth.assertThat(annotation.toString()).isEqualTo("@java.lang.SuppressWarnings(\"unused\")");
    }

    @org.junit.Test
    public void codeBlockToString() throws java.lang.Exception {
        com.squareup.javapoet.CodeBlock codeBlock = com.squareup.javapoet.CodeBlock.builder().addStatement("$T $N = $S.substring(0, 3)", java.lang.String.class, "s", "taco").build();
        com.google.common.truth.Truth.assertThat(codeBlock.toString()).isEqualTo("java.lang.String s = \"taco\".substring(0, 3);\n");
    }

    @org.junit.Test
    public void fieldToString() throws java.lang.Exception {
        com.squareup.javapoet.FieldSpec field = com.squareup.javapoet.FieldSpec.builder(java.lang.String.class, "s", javax.lang.model.element.Modifier.FINAL).initializer("$S.substring(0, 3)", "taco").build();
        com.google.common.truth.Truth.assertThat(field.toString()).isEqualTo("final java.lang.String s = \"taco\".substring(0, 3);\n");
    }

    @org.junit.Test
    public void methodToString() throws java.lang.Exception {
        com.squareup.javapoet.MethodSpec method = com.squareup.javapoet.MethodSpec.methodBuilder("toString").addAnnotation(java.lang.Override.class).addModifiers(javax.lang.model.element.Modifier.PUBLIC).returns(java.lang.String.class).addStatement("return $S", "taco").build();
        com.google.common.truth.Truth.assertThat(method.toString()).isEqualTo(("" + ((("@java.lang.Override\n" + "public java.lang.String toString() {\n") + "  return \"taco\";\n") + "}\n")));
    }

    @org.junit.Test
    public void constructorToString() throws java.lang.Exception {
        com.squareup.javapoet.MethodSpec constructor = com.squareup.javapoet.MethodSpec.constructorBuilder().addModifiers(javax.lang.model.element.Modifier.PUBLIC).addParameter(com.squareup.javapoet.ClassName.get(tacosPackage, "Taco"), "taco").addStatement("this.$N = $N", "taco", "taco").build();
        com.google.common.truth.Truth.assertThat(constructor.toString()).isEqualTo(("" + (("public Constructor(com.squareup.tacos.Taco taco) {\n" + "  this.taco = taco;\n") + "}\n")));
    }

    @org.junit.Test
    public void parameterToString() throws java.lang.Exception {
        com.squareup.javapoet.ParameterSpec parameter = com.squareup.javapoet.ParameterSpec.builder(com.squareup.javapoet.ClassName.get(tacosPackage, "Taco"), "taco").addModifiers(javax.lang.model.element.Modifier.FINAL).addAnnotation(com.squareup.javapoet.ClassName.get("javax.annotation", "Nullable")).build();
        com.google.common.truth.Truth.assertThat(parameter.toString()).isEqualTo("@javax.annotation.Nullable final com.squareup.tacos.Taco taco");
    }

    @org.junit.Test
    public void classToString() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec type = com.squareup.javapoet.TypeSpec.classBuilder("Taco").build();
        com.google.common.truth.Truth.assertThat(type.toString()).isEqualTo(("" + ("class Taco {\n" + "}\n")));
    }

    @org.junit.Test
    public void anonymousClassToString() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec type = com.squareup.javapoet.TypeSpec.anonymousClassBuilder("").addSuperinterface(java.lang.Runnable.).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("run").addAnnotation(java.lang.Override.class).addModifiers(javax.lang.model.element.Modifier.PUBLIC).build()).build();
        com.google.common.truth.Truth.assertThat(type.toString()).isEqualTo(("" + (((("new java.lang.Runnable() {\n" + "  @java.lang.Override\n") + "  public void run() {\n") + "  }\n") + "}")));
    }

    @org.junit.Test
    public void interfaceClassToString() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec type = com.squareup.javapoet.TypeSpec.interfaceBuilder("Taco").build();
        com.google.common.truth.Truth.assertThat(type.toString()).isEqualTo(("" + ("interface Taco {\n" + "}\n")));
    }

    @org.junit.Test
    public void annotationDeclarationToString() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec type = com.squareup.javapoet.TypeSpec.annotationBuilder("Taco").build();
        com.google.common.truth.Truth.assertThat(type.toString()).isEqualTo(("" + ("@interface Taco {\n" + "}\n")));
    }

    private java.lang.String toString(com.squareup.javapoet.TypeSpec typeSpec) {
        return com.squareup.javapoet.JavaFile.builder(tacosPackage, typeSpec).build().toString();
    }

    @org.junit.Test
    public void multilineStatement() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("toString").addAnnotation(java.lang.Override.class).addModifiers(javax.lang.model.element.Modifier.PUBLIC).returns(java.lang.String.class).addStatement("return $S\n+ $S\n+ $S\n+ $S\n+ $S", "Taco(", "beef,", "lettuce,", "cheese", ")").build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Override;\n") + "import java.lang.String;\n") + "\n") + "class Taco {\n") + "  @Override\n") + "  public String toString() {\n") + "    return \"Taco(\"\n") + "        + \"beef,\"\n") + "        + \"lettuce,\"\n") + "        + \"cheese\"\n") + "        + \")\";\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void multilineStatementWithAnonymousClass() throws java.lang.Exception {
        com.squareup.javapoet.TypeName stringComparator = com.squareup.javapoet.ParameterizedTypeName.get(java.util.Comparator.class, java.lang.String.class);
        com.squareup.javapoet.TypeName listOfString = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
        com.squareup.javapoet.TypeSpec prefixComparator = com.squareup.javapoet.TypeSpec.anonymousClassBuilder("").addSuperinterface(stringComparator).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("compare").addAnnotation(java.lang.Override.class).addModifiers(javax.lang.model.element.Modifier.PUBLIC).returns(int.class).addParameter(java.lang.String.class, "a").addParameter(java.lang.String.class, "b").addStatement(("return a.substring(0, length)\n" + ".compareTo(b.substring(0, length))")).build()).build();
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("comparePrefix").returns(stringComparator).addParameter(int.class, "length", javax.lang.model.element.Modifier.FINAL).addStatement("return $L", prefixComparator).build()).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("sortPrefix").addParameter(listOfString, "list").addParameter(int.class, "length", javax.lang.model.element.Modifier.FINAL).addStatement("$T.sort(\nlist,\n$L)", java.util.Collections.class, prefixComparator).build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((((((((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Override;\n") + "import java.lang.String;\n") + "import java.util.Collections;\n") + "import java.util.Comparator;\n") + "import java.util.List;\n") + "\n") + "class Taco {\n") + "  Comparator<String> comparePrefix(final int length) {\n") + "    return new Comparator<String>() {\n") + "      @Override\n") + "      public int compare(String a, String b) {\n") + "        return a.substring(0, length)\n") + "            .compareTo(b.substring(0, length));\n") + "      }\n") + "    };\n") + "  }\n") + "\n") + "  void sortPrefix(List<String> list, final int length) {\n") + "    Collections.sort(\n") + "        list,\n") + "        new Comparator<String>() {\n") + "          @Override\n") + "          public int compare(String a, String b) {\n") + "            return a.substring(0, length)\n") + "                .compareTo(b.substring(0, length));\n") + "          }\n") + "        });\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void multilineStrings() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addField(com.squareup.javapoet.FieldSpec.builder(java.lang.String.class, "toppings").initializer("$S", "shell\nbeef\nlettuce\ncheese\n").build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + ((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.String;\n") + "\n") + "class Taco {\n") + "  String toppings = \"shell\\n\"\n") + "      + \"beef\\n\"\n") + "      + \"lettuce\\n\"\n") + "      + \"cheese\\n\";\n") + "}\n")));
    }

    @org.junit.Test
    public void doubleFieldInitialization() {
        try {
            com.squareup.javapoet.FieldSpec.builder(java.lang.String.class, "listA").initializer("foo").initializer("bar").build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException expected) {
        }
        try {
            com.squareup.javapoet.FieldSpec.builder(java.lang.String.class, "listA").initializer(com.squareup.javapoet.CodeBlock.builder().add("foo").build()).initializer(com.squareup.javapoet.CodeBlock.builder().add("bar").build()).build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException expected) {
        }
    }

    @org.junit.Test
    public void nullAnnotationsAddition() {
        try {
            com.squareup.javapoet.TypeSpec.classBuilder("Taco").addAnnotations(null);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected.getMessage()).isEqualTo("annotationSpecs == null");
        }
    }

    @org.junit.Test
    public void multipleAnnotationAddition() {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addAnnotations(java.util.Arrays.asList(com.squareup.javapoet.AnnotationSpec.builder(java.lang.SuppressWarnings.class).addMember("value", "$S", "unchecked").build(), com.squareup.javapoet.AnnotationSpec.builder(java.lang.Deprecated.class).build())).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Deprecated;\n") + "import java.lang.SuppressWarnings;\n") + "\n") + "@SuppressWarnings(\"unchecked\")\n") + "@Deprecated\n") + "class Taco {\n") + "}\n")));
    }

    @org.junit.Test
    public void nullFieldsAddition() {
        try {
            com.squareup.javapoet.TypeSpec.classBuilder("Taco").addFields(null);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected.getMessage()).isEqualTo("fieldSpecs == null");
        }
    }

    @org.junit.Test
    public void multipleFieldAddition() {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addFields(java.util.Arrays.asList(com.squareup.javapoet.FieldSpec.builder(int.class, "ANSWER", javax.lang.model.element.Modifier.STATIC, javax.lang.model.element.Modifier.FINAL).build(), com.squareup.javapoet.FieldSpec.builder(java.math.BigDecimal.class, "price", javax.lang.model.element.Modifier.PRIVATE).build())).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((("package com.squareup.tacos;\n" + "\n") + "import java.math.BigDecimal;\n") + "\n") + "class Taco {\n") + "  static final int ANSWER;\n") + "\n") + "  private BigDecimal price;\n") + "}\n")));
    }

    @org.junit.Test
    public void nullMethodsAddition() {
        try {
            com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethods(null);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected.getMessage()).isEqualTo("methodSpecs == null");
        }
    }

    @org.junit.Test
    public void multipleMethodAddition() {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethods(java.util.Arrays.asList(com.squareup.javapoet.MethodSpec.methodBuilder("getAnswer").addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).returns(int.class).addStatement("return $L", 42).build(), com.squareup.javapoet.MethodSpec.methodBuilder("getRandomQuantity").addModifiers(javax.lang.model.element.Modifier.PUBLIC).returns(int.class).addJavadoc("chosen by fair dice roll ;)").addStatement("return $L", 4).build())).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((((("package com.squareup.tacos;\n" + "\n") + "class Taco {\n") + "  public static int getAnswer() {\n") + "    return 42;\n") + "  }\n") + "\n") + "  /**\n") + "   * chosen by fair dice roll ;) */\n") + "  public int getRandomQuantity() {\n") + "    return 4;\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void nullSuperinterfacesAddition() {
        try {
            com.squareup.javapoet.TypeSpec.classBuilder("Taco").addSuperinterfaces(null);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected.getMessage()).isEqualTo("superinterfaces == null");
        }
    }

    @org.junit.Test
    public void nullSingleSuperinterfaceAddition() {
        try {
            com.squareup.javapoet.TypeSpec.classBuilder("Taco").addSuperinterface(((com.squareup.javapoet.TypeName) (null)));
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected.getMessage()).isEqualTo("superinterface == null");
        }
    }

    @org.junit.Test
    public void nullInSuperinterfaceIterableAddition() {
        java.util.List<com.squareup.javapoet.TypeName> superinterfaces = new java.util.ArrayList<>();
        superinterfaces.add(com.squareup.javapoet.TypeName.get(java.util.List.class));
        superinterfaces.add(null);
        try {
            com.squareup.javapoet.TypeSpec.classBuilder("Taco").addSuperinterfaces(superinterfaces);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected.getMessage()).isEqualTo("superinterface == null");
        }
    }

    @org.junit.Test
    public void multipleSuperinterfaceAddition() {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addSuperinterfaces(java.util.Arrays.asList(com.squareup.javapoet.TypeName.get(java.io.Serializable.class), com.squareup.javapoet.TypeName.get(java.util.EventListener.class))).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((("package com.squareup.tacos;\n" + "\n") + "import java.io.Serializable;\n") + "import java.util.EventListener;\n") + "\n") + "class Taco implements Serializable, EventListener {\n") + "}\n")));
    }

    @org.junit.Test
    public void nullTypeVariablesAddition() {
        try {
            com.squareup.javapoet.TypeSpec.classBuilder("Taco").addTypeVariables(null);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected.getMessage()).isEqualTo("typeVariables == null");
        }
    }

    @org.junit.Test
    public void multipleTypeVariableAddition() {
        com.squareup.javapoet.TypeSpec location = com.squareup.javapoet.TypeSpec.classBuilder("Location").addTypeVariables(java.util.Arrays.asList(com.squareup.javapoet.TypeVariableName.get("T"), com.squareup.javapoet.TypeVariableName.get("P", java.lang.Number.))).build();
        com.google.common.truth.Truth.assertThat(toString(location)).isEqualTo(("" + ((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Number;\n") + "\n") + "class Location<T, P extends Number> {\n") + "}\n")));
    }

    @org.junit.Test
    public void nullTypesAddition() {
        try {
            com.squareup.javapoet.TypeSpec.classBuilder("Taco").addTypes(null);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected.getMessage()).isEqualTo("typeSpecs == null");
        }
    }

    @org.junit.Test
    public void multipleTypeAddition() {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addTypes(java.util.Arrays.asList(com.squareup.javapoet.TypeSpec.classBuilder("Topping").build(), com.squareup.javapoet.TypeSpec.classBuilder("Sauce").build())).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((("package com.squareup.tacos;\n" + "\n") + "class Taco {\n") + "  class Topping {\n") + "  }\n") + "\n") + "  class Sauce {\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void tryCatch() {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("addTopping").addParameter(com.squareup.javapoet.ClassName.get("com.squareup.tacos", "Topping"), "topping").beginControlFlow("try").addCode("/* do something tricky with the topping */\n").nextControlFlow("catch ($T e)", com.squareup.javapoet.ClassName.get("com.squareup.tacos", "IllegalToppingException")).endControlFlow().build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + ((((((((("package com.squareup.tacos;\n" + "\n") + "class Taco {\n") + "  void addTopping(Topping topping) {\n") + "    try {\n") + "      /* do something tricky with the topping */\n") + "    } catch (IllegalToppingException e) {\n") + "    }\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void ifElse() {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("isDelicious").addParameter(com.squareup.javapoet.TypeName.INT, "count").returns(com.squareup.javapoet.TypeName.BOOLEAN).beginControlFlow("if (count > 0)").addStatement("return true").nextControlFlow("else").addStatement("return false").endControlFlow().build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((("package com.squareup.tacos;\n" + "\n") + "class Taco {\n") + "  boolean isDelicious(int count) {\n") + "    if (count > 0) {\n") + "      return true;\n") + "    } else {\n") + "      return false;\n") + "    }\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void literalFromAnything() {
        java.lang.Object value = new java.lang.Object() {
            @java.lang.Override
            public java.lang.String toString() {
                return "foo";
            }
        };
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.CodeBlock.of("$L", value).toString()).isEqualTo("foo");
    }

    @org.junit.Test
    public void nameFromCharSequence() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.CodeBlock.of("$N", "text").toString()).isEqualTo("text");
    }

    @org.junit.Test
    public void nameFromField() {
        com.squareup.javapoet.FieldSpec field = com.squareup.javapoet.FieldSpec.builder(java.lang.String.class, "field").build();
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.CodeBlock.of("$N", field).toString()).isEqualTo("field");
    }

    @org.junit.Test
    public void nameFromParameter() {
        com.squareup.javapoet.ParameterSpec parameter = com.squareup.javapoet.ParameterSpec.builder(java.lang.String.class, "parameter").build();
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.CodeBlock.of("$N", parameter).toString()).isEqualTo("parameter");
    }

    @org.junit.Test
    public void nameFromMethod() {
        com.squareup.javapoet.MethodSpec method = com.squareup.javapoet.MethodSpec.methodBuilder("method").addModifiers(javax.lang.model.element.Modifier.ABSTRACT).returns(java.lang.String.class).build();
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.CodeBlock.of("$N", method).toString()).isEqualTo("method");
    }

    @org.junit.Test
    public void nameFromType() {
        com.squareup.javapoet.TypeSpec type = com.squareup.javapoet.TypeSpec.classBuilder("Type").build();
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.CodeBlock.of("$N", type).toString()).isEqualTo("Type");
    }

    @org.junit.Test
    public void nameFromUnsupportedType() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$N", java.lang.String.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage(("expected name but was " + (java.lang.String.class)));
        }
    }

    @org.junit.Test
    public void stringFromAnything() {
        java.lang.Object value = new java.lang.Object() {
            @java.lang.Override
            public java.lang.String toString() {
                return "foo";
            }
        };
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.CodeBlock.of("$S", value).toString()).isEqualTo("\"foo\"");
    }

    @org.junit.Test
    public void stringFromNull() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.CodeBlock.of("$S", new java.lang.Object[]{ null }).toString()).isEqualTo("null");
    }

    @org.junit.Test
    public void typeFromTypeName() {
        com.squareup.javapoet.TypeName typeName = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.CodeBlock.of("$T", typeName).toString()).isEqualTo("java.lang.String");
    }

    @org.junit.Test
    public void typeFromTypeMirror() {
        javax.lang.model.type.TypeMirror mirror = getElement(java.lang.String.class).asType();
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.CodeBlock.of("$T", mirror).toString()).isEqualTo("java.lang.String");
    }

    @org.junit.Test
    public void typeFromTypeElement() {
        javax.lang.model.element.TypeElement element = getElement(java.lang.String.class);
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.CodeBlock.of("$T", element).toString()).isEqualTo("java.lang.String");
    }

    @org.junit.Test
    public void typeFromReflectType() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.CodeBlock.of("$T", java.lang.String.class).toString()).isEqualTo("java.lang.String");
    }

    @org.junit.Test
    public void typeFromUnsupportedType() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$T", "java.lang.String");
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("expected type but was java.lang.String");
        }
    }

    @org.junit.Test
    public void tooFewArguments() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$S");
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("index 1 for '$S' not in range (received 0 arguments)");
        }
    }

    @org.junit.Test
    public void unusedArgumentsRelative() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$L $L", "a", "b", "c");
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("unused arguments: expected 2, received 3");
        }
    }

    @org.junit.Test
    public void unusedArgumentsIndexed() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$1L $2L", "a", "b", "c");
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("unused argument: $3");
        }
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$1L $1L $1L", "a", "b", "c");
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("unused arguments: $2, $3");
        }
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$3L $1L $3L $1L $3L", "a", "b", "c", "d");
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("unused arguments: $2, $4");
        }
    }

    @org.junit.Test
    public void superClassOnlyValidForClasses() {
        try {
            com.squareup.javapoet.TypeSpec.annotationBuilder("A").superclass(com.squareup.javapoet.ClassName.get(java.lang.Object.class));
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException expected) {
        }
        try {
            com.squareup.javapoet.TypeSpec.enumBuilder("E").superclass(com.squareup.javapoet.ClassName.get(java.lang.Object.class));
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException expected) {
        }
        try {
            com.squareup.javapoet.TypeSpec.interfaceBuilder("I").superclass(com.squareup.javapoet.ClassName.get(java.lang.Object.class));
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException expected) {
        }
    }

    @org.junit.Test
    public void invalidSuperClass() {
        try {
            com.squareup.javapoet.TypeSpec.classBuilder("foo").superclass(com.squareup.javapoet.ClassName.get(java.util.List.class)).superclass(com.squareup.javapoet.ClassName.get(java.util.Map.class));
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException expected) {
        }
        try {
            com.squareup.javapoet.TypeSpec.classBuilder("foo").superclass(com.squareup.javapoet.TypeName.INT);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
        }
    }

    @org.junit.Test
    public void staticCodeBlock() {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addField(java.lang.String.class, "foo", javax.lang.model.element.Modifier.PRIVATE).addField(java.lang.String.class, "FOO", javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.STATIC, javax.lang.model.element.Modifier.FINAL).addStaticBlock(com.squareup.javapoet.CodeBlock.builder().addStatement("FOO = $S", "FOO").build()).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("toString").addAnnotation(java.lang.Override.class).addModifiers(javax.lang.model.element.Modifier.PUBLIC).returns(java.lang.String.class).addCode("return FOO;\n").build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Override;\n") + "import java.lang.String;\n") + "\n") + "class Taco {\n") + "  private static final String FOO;\n") + "\n") + "  static {\n") + "    FOO = \"FOO\";\n") + "  }\n") + "\n") + "  private String foo;\n") + "\n") + "  @Override\n") + "  public String toString() {\n") + "    return FOO;\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void initializerBlockInRightPlace() {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addField(java.lang.String.class, "foo", javax.lang.model.element.Modifier.PRIVATE).addField(java.lang.String.class, "FOO", javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.STATIC, javax.lang.model.element.Modifier.FINAL).addStaticBlock(com.squareup.javapoet.CodeBlock.builder().addStatement("FOO = $S", "FOO").build()).addMethod(com.squareup.javapoet.MethodSpec.constructorBuilder().build()).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("toString").addAnnotation(java.lang.Override.class).addModifiers(javax.lang.model.element.Modifier.PUBLIC).returns(java.lang.String.class).addCode("return FOO;\n").build()).addInitializerBlock(com.squareup.javapoet.CodeBlock.builder().addStatement("foo = $S", "FOO").build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + ((((((((((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Override;\n") + "import java.lang.String;\n") + "\n") + "class Taco {\n") + "  private static final String FOO;\n") + "\n") + "  static {\n") + "    FOO = \"FOO\";\n") + "  }\n") + "\n") + "  private String foo;\n") + "\n") + "  {\n") + "    foo = \"FOO\";\n") + "  }\n") + "\n") + "  Taco() {\n") + "  }\n") + "\n") + "  @Override\n") + "  public String toString() {\n") + "    return FOO;\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void initializersToBuilder() {
        // Tests if toBuilder() contains correct static and instance initializers
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addField(java.lang.String.class, "foo", javax.lang.model.element.Modifier.PRIVATE).addField(java.lang.String.class, "FOO", javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.STATIC, javax.lang.model.element.Modifier.FINAL).addStaticBlock(com.squareup.javapoet.CodeBlock.builder().addStatement("FOO = $S", "FOO").build()).addMethod(com.squareup.javapoet.MethodSpec.constructorBuilder().build()).addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("toString").addAnnotation(java.lang.Override.class).addModifiers(javax.lang.model.element.Modifier.PUBLIC).returns(java.lang.String.class).addCode("return FOO;\n").build()).addInitializerBlock(com.squareup.javapoet.CodeBlock.builder().addStatement("foo = $S", "FOO").build()).build();
        com.squareup.javapoet.TypeSpec recreatedTaco = taco.toBuilder().build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(toString(recreatedTaco));
        com.squareup.javapoet.TypeSpec initializersAdded = taco.toBuilder().addInitializerBlock(com.squareup.javapoet.CodeBlock.builder().addStatement("foo = $S", "instanceFoo").build()).addStaticBlock(com.squareup.javapoet.CodeBlock.builder().addStatement("FOO = $S", "staticFoo").build()).build();
        com.google.common.truth.Truth.assertThat(toString(initializersAdded)).isEqualTo(("" + ((((((((((((((((((((((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Override;\n") + "import java.lang.String;\n") + "\n") + "class Taco {\n") + "  private static final String FOO;\n") + "\n") + "  static {\n") + "    FOO = \"FOO\";\n") + "  }\n") + "  static {\n") + "    FOO = \"staticFoo\";\n") + "  }\n") + "\n") + "  private String foo;\n") + "\n") + "  {\n") + "    foo = \"FOO\";\n") + "  }\n") + "  {\n") + "    foo = \"instanceFoo\";\n") + "  }\n") + "\n") + "  Taco() {\n") + "  }\n") + "\n") + "  @Override\n") + "  public String toString() {\n") + "    return FOO;\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void initializerBlockUnsupportedExceptionOnInterface() {
        com.squareup.javapoet.TypeSpec.Builder interfaceBuilder = com.squareup.javapoet.TypeSpec.interfaceBuilder("Taco");
        try {
            interfaceBuilder.addInitializerBlock(com.squareup.javapoet.CodeBlock.builder().build());
            org.junit.Assert.fail("Exception expected");
        } catch (java.lang.UnsupportedOperationException e) {
        }
    }

    @org.junit.Test
    public void initializerBlockUnsupportedExceptionOnAnnotation() {
        com.squareup.javapoet.TypeSpec.Builder annotationBuilder = com.squareup.javapoet.TypeSpec.annotationBuilder("Taco");
        try {
            annotationBuilder.addInitializerBlock(com.squareup.javapoet.CodeBlock.builder().build());
            org.junit.Assert.fail("Exception expected");
        } catch (java.lang.UnsupportedOperationException e) {
        }
    }

    @org.junit.Test
    public void lineWrapping() {
        com.squareup.javapoet.MethodSpec.Builder methodBuilder = com.squareup.javapoet.MethodSpec.methodBuilder("call");
        methodBuilder.addCode("$[call(");
        for (int i = 0; i < 32; i++) {
            methodBuilder.addParameter(java.lang.String.class, ("s" + i));
            methodBuilder.addCode((i > 0 ? ",$W$S" : "$S"), i);
        }
        methodBuilder.addCode(");$]\n");
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethod(methodBuilder.build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + ((((((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.String;\n") + "\n") + "class Taco {\n") + "  void call(String s0, String s1, String s2, String s3, String s4, String s5, String s6, String s7,\n") + "      String s8, String s9, String s10, String s11, String s12, String s13, String s14, String s15,\n") + "      String s16, String s17, String s18, String s19, String s20, String s21, String s22,\n") + "      String s23, String s24, String s25, String s26, String s27, String s28, String s29,\n") + "      String s30, String s31) {\n") + "    call(\"0\", \"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"10\", \"11\", \"12\", \"13\", \"14\", \"15\", \"16\",\n") + "        \"17\", \"18\", \"19\", \"20\", \"21\", \"22\", \"23\", \"24\", \"25\", \"26\", \"27\", \"28\", \"29\", \"30\", \"31\");\n") + "  }\n") + "}\n")));
    }

    @org.junit.Test
    public void equalsAndHashCode() {
        com.squareup.javapoet.TypeSpec a = com.squareup.javapoet.TypeSpec.interfaceBuilder("taco").build();
        com.squareup.javapoet.TypeSpec b = com.squareup.javapoet.TypeSpec.interfaceBuilder("taco").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.TypeSpec.classBuilder("taco").build();
        b = com.squareup.javapoet.TypeSpec.classBuilder("taco").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.TypeSpec.enumBuilder("taco").addEnumConstant("SALSA").build();
        b = com.squareup.javapoet.TypeSpec.enumBuilder("taco").addEnumConstant("SALSA").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.TypeSpec.annotationBuilder("taco").build();
        b = com.squareup.javapoet.TypeSpec.annotationBuilder("taco").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @org.junit.Test
    public void classNameFactories() {
        com.squareup.javapoet.ClassName className = com.squareup.javapoet.ClassName.get("com.example", "Example");
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeSpec.classBuilder(className).build().name).isEqualTo("Example");
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeSpec.interfaceBuilder(className).build().name).isEqualTo("Example");
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeSpec.enumBuilder(className).addEnumConstant("A").build().name).isEqualTo("Example");
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeSpec.annotationBuilder(className).build().name).isEqualTo("Example");
    }

    /* amplification of com.squareup.javapoet.TypeSpecTest#basic */
    @org.junit.Test(timeout = 10000)
    public void basic_cf97826_failAssert77() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("toString").addAnnotation(java.lang.Override.class).addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.FINAL).returns(java.lang.String.class).addCode("return $S;\n", "taco").build()).build();
            com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Override;\n") + "import java.lang.String;\n") + "\n") + "class Taco {\n") + "  @Override\n") + "  public final String toString() {\n") + "    return \"taco\";\n") + "  }\n") + "}\n")));
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_4108 = "    return \"taco\";\n";
            // StatementAdderMethod cloned existing statement
            taco.annotationBuilder(String_vc_4108);
            // MethodAssertGenerator build local variable
            Object o_17_0 = taco.hashCode();
            org.junit.Assert.fail("basic_cf97826 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeSpecTest#basic */
    @org.junit.Test(timeout = 10000)
    public void basic_cf97803() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("toString").addAnnotation(java.lang.Override.class).addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.FINAL).returns(java.lang.String.class).addCode("return $S;\n", "taco").build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Override;\n") + "import java.lang.String;\n") + "\n") + "class Taco {\n") + "  @Override\n") + "  public final String toString() {\n") + "    return \"taco\";\n") + "  }\n") + "}\n")));
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_65716 = new java.lang.Object();
        // StatementAddOnAssert local variable replacement
        com.squareup.javapoet.TypeSpec recreatedTaco = taco.toBuilder().build();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((com.squareup.javapoet.TypeSpec.Builder)((com.squareup.javapoet.TypeSpec)recreatedTaco).toBuilder()).build().equals(recreatedTaco));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(recreatedTaco.equals(taco));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((com.squareup.javapoet.TypeSpec.Builder)((com.squareup.javapoet.TypeSpec)recreatedTaco).toBuilder()).build().equals(taco));
        // AssertGenerator replace invocation
        boolean o_basic_cf97803__20 = // StatementAdderMethod cloned existing statement
recreatedTaco.equals(vc_65716);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_basic_cf97803__20);
        org.junit.Assert.assertEquals(472949424, taco.hashCode());// update expected number if source changes
        
    }

    /* amplification of com.squareup.javapoet.TypeSpecTest#basic */
    @org.junit.Test(timeout = 10000)
    public void basic_cf97802() throws java.lang.Exception {
        com.squareup.javapoet.TypeSpec taco = com.squareup.javapoet.TypeSpec.classBuilder("Taco").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("toString").addAnnotation(java.lang.Override.class).addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.FINAL).returns(java.lang.String.class).addCode("return $S;\n", "taco").build()).build();
        com.google.common.truth.Truth.assertThat(toString(taco)).isEqualTo(("" + (((((((((("package com.squareup.tacos;\n" + "\n") + "import java.lang.Override;\n") + "import java.lang.String;\n") + "\n") + "class Taco {\n") + "  @Override\n") + "  public final String toString() {\n") + "    return \"taco\";\n") + "  }\n") + "}\n")));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_65715 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_65715);
        // StatementAddOnAssert local variable replacement
        com.squareup.javapoet.TypeSpec recreatedTaco = taco.toBuilder().build();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((com.squareup.javapoet.TypeSpec.Builder)((com.squareup.javapoet.TypeSpec)recreatedTaco).toBuilder()).build().equals(recreatedTaco));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(recreatedTaco.equals(taco));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((com.squareup.javapoet.TypeSpec.Builder)((com.squareup.javapoet.TypeSpec)recreatedTaco).toBuilder()).build().equals(taco));
        // AssertGenerator replace invocation
        boolean o_basic_cf97802__20 = // StatementAdderMethod cloned existing statement
recreatedTaco.equals(vc_65715);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_basic_cf97802__20);
        org.junit.Assert.assertEquals(472949424, taco.hashCode());// update expected number if source changes
        
    }
}

