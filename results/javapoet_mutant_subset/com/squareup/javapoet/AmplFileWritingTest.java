/**
 * Copyright (C) 2014 Square, Inc.
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
public final class AmplFileWritingTest {
    // Used for testing java.io File behavior.
    @org.junit.Rule
    public final org.junit.rules.TemporaryFolder tmp = new org.junit.rules.TemporaryFolder();

    // Used for testing java.nio.file Path behavior.
    private final java.nio.file.FileSystem fs = com.google.common.jimfs.Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix());

    private final java.nio.file.Path fsRoot = fs.getRootDirectories().iterator().next();

    // Used for testing annotation processor Filer behavior.
    private final com.squareup.javapoet.TestFiler filer = new com.squareup.javapoet.TestFiler(fs, fsRoot);

    @org.junit.Test
    public void pathNotDirectory() throws java.io.IOException {
        com.squareup.javapoet.TypeSpec type = com.squareup.javapoet.TypeSpec.classBuilder("Test").build();
        com.squareup.javapoet.JavaFile javaFile = com.squareup.javapoet.JavaFile.builder("example", type).build();
        java.nio.file.Path path = fs.getPath("/foo/bar");
        java.nio.file.Files.createDirectories(path.getParent());
        java.nio.file.Files.createFile(path);
        try {
            javaFile.writeTo(path);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            com.google.common.truth.Truth.assertThat(e.getMessage()).isEqualTo("path /foo/bar exists but is not a directory.");
        }
    }

    @org.junit.Test
    public void fileNotDirectory() throws java.io.IOException {
        com.squareup.javapoet.TypeSpec type = com.squareup.javapoet.TypeSpec.classBuilder("Test").build();
        com.squareup.javapoet.JavaFile javaFile = com.squareup.javapoet.JavaFile.builder("example", type).build();
        java.io.File file = new java.io.File(tmp.newFolder("foo"), "bar");
        file.createNewFile();
        try {
            javaFile.writeTo(file);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            com.google.common.truth.Truth.assertThat(e.getMessage()).isEqualTo((("path " + (file.getPath())) + " exists but is not a directory."));
        }
    }

    @org.junit.Test
    public void pathDefaultPackage() throws java.io.IOException {
        com.squareup.javapoet.TypeSpec type = com.squareup.javapoet.TypeSpec.classBuilder("Test").build();
        com.squareup.javapoet.JavaFile.builder("", type).build().writeTo(fsRoot);
        java.nio.file.Path testPath = fsRoot.resolve("Test.java");
        com.google.common.truth.Truth.assertThat(java.nio.file.Files.exists(testPath)).isTrue();
    }

    @org.junit.Test
    public void fileDefaultPackage() throws java.io.IOException {
        com.squareup.javapoet.TypeSpec type = com.squareup.javapoet.TypeSpec.classBuilder("Test").build();
        com.squareup.javapoet.JavaFile.builder("", type).build().writeTo(tmp.getRoot());
        java.io.File testFile = new java.io.File(tmp.getRoot(), "Test.java");
        com.google.common.truth.Truth.assertThat(testFile.exists()).isTrue();
    }

    @org.junit.Test
    public void filerDefaultPackage() throws java.io.IOException {
        com.squareup.javapoet.TypeSpec type = com.squareup.javapoet.TypeSpec.classBuilder("Test").build();
        com.squareup.javapoet.JavaFile.builder("", type).build().writeTo(filer);
        java.nio.file.Path testPath = fsRoot.resolve("Test.java");
        com.google.common.truth.Truth.assertThat(java.nio.file.Files.exists(testPath)).isTrue();
    }

    @org.junit.Test
    public void pathNestedClasses() throws java.io.IOException {
        com.squareup.javapoet.TypeSpec type = com.squareup.javapoet.TypeSpec.classBuilder("Test").build();
        com.squareup.javapoet.JavaFile.builder("foo", type).build().writeTo(fsRoot);
        com.squareup.javapoet.JavaFile.builder("foo.bar", type).build().writeTo(fsRoot);
        com.squareup.javapoet.JavaFile.builder("foo.bar.baz", type).build().writeTo(fsRoot);
        java.nio.file.Path fooPath = fsRoot.resolve(fs.getPath("foo", "Test.java"));
        java.nio.file.Path barPath = fsRoot.resolve(fs.getPath("foo", "bar", "Test.java"));
        java.nio.file.Path bazPath = fsRoot.resolve(fs.getPath("foo", "bar", "baz", "Test.java"));
        com.google.common.truth.Truth.assertThat(java.nio.file.Files.exists(fooPath)).isTrue();
        com.google.common.truth.Truth.assertThat(java.nio.file.Files.exists(barPath)).isTrue();
        com.google.common.truth.Truth.assertThat(java.nio.file.Files.exists(bazPath)).isTrue();
    }

    @org.junit.Test
    public void fileNestedClasses() throws java.io.IOException {
        com.squareup.javapoet.TypeSpec type = com.squareup.javapoet.TypeSpec.classBuilder("Test").build();
        com.squareup.javapoet.JavaFile.builder("foo", type).build().writeTo(tmp.getRoot());
        com.squareup.javapoet.JavaFile.builder("foo.bar", type).build().writeTo(tmp.getRoot());
        com.squareup.javapoet.JavaFile.builder("foo.bar.baz", type).build().writeTo(tmp.getRoot());
        java.io.File fooDir = new java.io.File(tmp.getRoot(), "foo");
        java.io.File fooFile = new java.io.File(fooDir, "Test.java");
        java.io.File barDir = new java.io.File(fooDir, "bar");
        java.io.File barFile = new java.io.File(barDir, "Test.java");
        java.io.File bazDir = new java.io.File(barDir, "baz");
        java.io.File bazFile = new java.io.File(bazDir, "Test.java");
        com.google.common.truth.Truth.assertThat(fooFile.exists()).isTrue();
        com.google.common.truth.Truth.assertThat(barFile.exists()).isTrue();
        com.google.common.truth.Truth.assertThat(bazFile.exists()).isTrue();
    }

    @org.junit.Test
    public void filerNestedClasses() throws java.io.IOException {
        com.squareup.javapoet.TypeSpec type = com.squareup.javapoet.TypeSpec.classBuilder("Test").build();
        com.squareup.javapoet.JavaFile.builder("foo", type).build().writeTo(filer);
        com.squareup.javapoet.JavaFile.builder("foo.bar", type).build().writeTo(filer);
        com.squareup.javapoet.JavaFile.builder("foo.bar.baz", type).build().writeTo(filer);
        java.nio.file.Path fooPath = fsRoot.resolve(fs.getPath("foo", "Test.java"));
        java.nio.file.Path barPath = fsRoot.resolve(fs.getPath("foo", "bar", "Test.java"));
        java.nio.file.Path bazPath = fsRoot.resolve(fs.getPath("foo", "bar", "baz", "Test.java"));
        com.google.common.truth.Truth.assertThat(java.nio.file.Files.exists(fooPath)).isTrue();
        com.google.common.truth.Truth.assertThat(java.nio.file.Files.exists(barPath)).isTrue();
        com.google.common.truth.Truth.assertThat(java.nio.file.Files.exists(bazPath)).isTrue();
    }

    @org.junit.Test
    public void filerPassesOriginatingElements() throws java.io.IOException {
        javax.lang.model.element.Element element1_1 = org.mockito.Mockito.mock(javax.lang.model.element.Element.class);
        com.squareup.javapoet.TypeSpec test1 = com.squareup.javapoet.TypeSpec.classBuilder("Test1").addOriginatingElement(element1_1).build();
        javax.lang.model.element.Element element2_1 = org.mockito.Mockito.mock(javax.lang.model.element.Element.class);
        javax.lang.model.element.Element element2_2 = org.mockito.Mockito.mock(javax.lang.model.element.Element.class);
        com.squareup.javapoet.TypeSpec test2 = com.squareup.javapoet.TypeSpec.classBuilder("Test2").addOriginatingElement(element2_1).addOriginatingElement(element2_2).build();
        com.squareup.javapoet.JavaFile.builder("example", test1).build().writeTo(filer);
        com.squareup.javapoet.JavaFile.builder("example", test2).build().writeTo(filer);
        java.nio.file.Path testPath1 = fsRoot.resolve(fs.getPath("example", "Test1.java"));
        com.google.common.truth.Truth.assertThat(filer.getOriginatingElements(testPath1)).containsExactly(element1_1);
        java.nio.file.Path testPath2 = fsRoot.resolve(fs.getPath("example", "Test2.java"));
        com.google.common.truth.Truth.assertThat(filer.getOriginatingElements(testPath2)).containsExactly(element2_1, element2_2);
    }

    @org.junit.Test
    public void filerClassesWithTabIndent() throws java.io.IOException {
        com.squareup.javapoet.TypeSpec test = com.squareup.javapoet.TypeSpec.classBuilder("Test").addField(java.util.Date.class, "madeFreshDate").addMethod(com.squareup.javapoet.MethodSpec.methodBuilder("main").addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).addParameter(java.lang.String[].class, "args").addCode("$T.out.println($S);\n", java.lang.System.class, "Hello World!").build()).build();
        com.squareup.javapoet.JavaFile.builder("foo", test).indent("\t").build().writeTo(filer);
        java.nio.file.Path fooPath = fsRoot.resolve(fs.getPath("foo", "Test.java"));
        com.google.common.truth.Truth.assertThat(java.nio.file.Files.exists(fooPath)).isTrue();
        java.lang.String source = new java.lang.String(java.nio.file.Files.readAllBytes(fooPath));
        com.google.common.truth.Truth.assertThat(source).isEqualTo(("" + (((((((((((("package foo;\n" + "\n") + "import java.lang.String;\n") + "import java.lang.System;\n") + "import java.util.Date;\n") + "\n") + "class Test {\n") + "\tDate madeFreshDate;\n") + "\n") + "\tpublic static void main(String[] args) {\n") + "\t\tSystem.out.println(\"Hello World!\");\n") + "\t}\n") + "}\n")));
    }

    /**
     * This test confirms that JavaPoet ignores the host charset and always uses UTF-8. The host
     * charset is customized with {@code -Dfile.encoding=ISO-8859-1}.
     */
    @org.junit.Test
    public void fileIsUtf8() throws java.io.IOException {
        com.squareup.javapoet.JavaFile javaFile = com.squareup.javapoet.JavaFile.builder("foo", com.squareup.javapoet.TypeSpec.classBuilder("Taco").build()).addFileComment("Pi\u00f1ata\u00a1").build();
        javaFile.writeTo(fsRoot);
        java.nio.file.Path fooPath = fsRoot.resolve(fs.getPath("foo", "Taco.java"));
        com.google.common.truth.Truth.assertThat(new java.lang.String(java.nio.file.Files.readAllBytes(fooPath), java.nio.charset.StandardCharsets.UTF_8)).isEqualTo(("" + (((("// Pi\u00f1ata\u00a1\n" + "package foo;\n") + "\n") + "class Taco {\n") + "}\n")));
    }
}

