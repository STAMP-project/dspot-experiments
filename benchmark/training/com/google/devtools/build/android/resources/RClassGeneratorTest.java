/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.android.resources;


import SdkConstants.FN_COMPILED_RESOURCE_CLASS;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.devtools.build.android.resources.JavaIdentifierValidator.InvalidJavaIdentifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link RClassGenerator}.
 */
@RunWith(JUnit4.class)
public class RClassGeneratorTest {
    private Path temp;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void plainInts() throws Exception {
        checkSimpleInts(true);
    }

    @Test
    public void nonFinalFields() throws Exception {
        checkSimpleInts(false);
    }

    @Test
    public void checkFileWriteThrowsOnExisting() throws Exception {
        checkFileWriteThrowsOnExisting(FN_COMPILED_RESOURCE_CLASS);
    }

    @Test
    public void checkInnerFileWriteThrowsOnExisting() throws Exception {
        checkFileWriteThrowsOnExisting("R$string.class");
    }

    @Test
    public void emptyIntArrays() throws Exception {
        boolean finalFields = true;
        // Make sure we parse an empty array the way the R.txt writes it.
        ResourceSymbols symbolValues = createSymbolFile("R.txt", "int[] styleable ActionMenuView { }");
        ResourceSymbols symbolsInLibrary = symbolValues;
        Path out = temp.resolve("classes");
        Files.createDirectories(out);
        RClassGenerator writer = RClassGenerator.with(out, symbolValues.asInitializers(), finalFields);
        writer.write("com.testEmptyIntArray", symbolsInLibrary.asInitializers());
        Path packageDir = out.resolve("com/testEmptyIntArray");
        RClassGeneratorTest.checkFilesInPackage(packageDir, "R.class", "R$styleable.class");
        Class<?> outerClass = RClassGeneratorTest.checkTopLevelClass(out, "com.testEmptyIntArray.R", "com.testEmptyIntArray.R$styleable");
        checkInnerClass(out, "com.testEmptyIntArray.R$styleable", outerClass, ImmutableMap.<String, Integer>of(), ImmutableMap.<String, List<Integer>>of("ActionMenuView", ImmutableList.<Integer>of()), finalFields);
    }

    static final Matcher<Throwable> NUMBER_FORMAT_EXCEPTION = new BaseMatcher<Throwable>() {
        @Override
        public boolean matches(Object item) {
            if (item instanceof NumberFormatException) {
                return true;
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(NumberFormatException.class.toString());
        }
    };

    static final Matcher<Throwable> INVALID_JAVA_IDENTIFIER = new BaseMatcher<Throwable>() {
        @Override
        public boolean matches(Object item) {
            return item instanceof InvalidJavaIdentifier;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(InvalidJavaIdentifier.class.getName());
        }
    };

    @Test
    public void corruptIntArraysTrailingComma() throws Exception {
        // Test a few cases of what happens if the R.txt is corrupted. It shouldn't happen unless there
        // is a bug in aapt, or R.txt is manually written the wrong way.
        Path path = createFile("R.txt", new String[]{ "int[] styleable ActionMenuView { 1, }" });
        thrown.expectCause(RClassGeneratorTest.NUMBER_FORMAT_EXCEPTION);
        ResourceSymbols.load(path, MoreExecutors.newDirectExecutorService()).get();
    }

    @Test
    public void corruptIntArraysOmittedMiddle() throws Exception {
        Path path = createFile("R.txt", "int[] styleable ActionMenuView { 1, , 2 }");
        thrown.expectCause(RClassGeneratorTest.NUMBER_FORMAT_EXCEPTION);
        ResourceSymbols.load(path, MoreExecutors.newDirectExecutorService()).get();
    }

    @Test
    public void invalidJavaIdentifierNumber() throws Exception {
        Path path = createFile("R.txt", "int id 42ActionMenuView 0x7f020000");
        final ResourceSymbols resourceSymbols = ResourceSymbols.load(path, MoreExecutors.newDirectExecutorService()).get();
        Path out = Files.createDirectories(temp.resolve("classes"));
        thrown.expect(RClassGeneratorTest.INVALID_JAVA_IDENTIFIER);
        RClassGenerator.with(out, resourceSymbols.asInitializers(), true).write("somepackage");
    }

    @Test
    public void invalidJavaIdentifierColon() throws Exception {
        Path path = createFile("R.txt", "int id Action:MenuView 0x7f020000");
        final ResourceSymbols resourceSymbols = ResourceSymbols.load(path, MoreExecutors.newDirectExecutorService()).get();
        Path out = Files.createDirectories(temp.resolve("classes"));
        thrown.expect(RClassGeneratorTest.INVALID_JAVA_IDENTIFIER);
        RClassGenerator.with(out, resourceSymbols.asInitializers(), true).write("somepackage");
    }

    @Test
    public void reservedJavaIdentifier() throws Exception {
        Path path = createFile("R.txt", "int id package 0x7f020000");
        final ResourceSymbols resourceSymbols = ResourceSymbols.load(path, MoreExecutors.newDirectExecutorService()).get();
        Path out = Files.createDirectories(temp.resolve("classes"));
        thrown.expect(RClassGeneratorTest.INVALID_JAVA_IDENTIFIER);
        RClassGenerator.with(out, resourceSymbols.asInitializers(), true).write("somepackage");
    }

    @Test
    public void binaryDropsLibraryFields() throws Exception {
        boolean finalFields = true;
        // Test what happens if the binary R.txt is not a strict superset of the
        // library R.txt (overrides that drop elements).
        ResourceSymbols symbolValues = createSymbolFile("R.txt", "int layout stubbable_activity 0x7f020000");
        ResourceSymbols symbolsInLibrary = createSymbolFile("lib.R.txt", "int id debug_text_field 0x1", "int id debug_text_field2 0x1", "int layout stubbable_activity 0x1");
        Path out = temp.resolve("classes");
        Files.createDirectories(out);
        RClassGenerator writer = RClassGenerator.with(out, symbolValues.asInitializers(), finalFields);
        writer.write("com.foo", symbolsInLibrary.asInitializers());
        Path packageDir = out.resolve("com/foo");
        RClassGeneratorTest.checkFilesInPackage(packageDir, "R.class", "R$layout.class");
        Class<?> outerClass = RClassGeneratorTest.checkTopLevelClass(out, "com.foo.R", "com.foo.R$layout");
        checkInnerClass(out, "com.foo.R$layout", outerClass, ImmutableMap.of("stubbable_activity", 2130837504), ImmutableMap.<String, List<Integer>>of(), finalFields);
    }

    @Test
    public void writeNothingWithNoResources() throws Exception {
        boolean finalFields = true;
        // Test what happens if the library R.txt has no elements.
        ResourceSymbols symbolValues = createSymbolFile("R.txt", "int layout stubbable_activity 0x7f020000");
        ResourceSymbols symbolsInLibrary = createSymbolFile("lib.R.txt");
        Path out = temp.resolve("classes");
        Files.createDirectories(out);
        RClassGenerator writer = RClassGenerator.with(out, symbolValues.asInitializers(), finalFields);
        writer.write("com.foo", symbolsInLibrary.asInitializers());
        Path packageDir = out.resolve("com/foo");
        RClassGeneratorTest.checkFilesInPackage(packageDir);
    }

    @Test
    public void intArraysFinal() throws Exception {
        checkIntArrays(true);
    }

    @Test
    public void intArraysNonFinal() throws Exception {
        checkIntArrays(false);
    }

    @Test
    public void emptyPackage() throws Exception {
        boolean finalFields = true;
        // Make sure we handle an empty package string.
        ResourceSymbols symbolValues = createSymbolFile("R.txt", "int string some_string 0x7f200000");
        ResourceSymbols symbolsInLibrary = symbolValues;
        Path out = temp.resolve("classes");
        Files.createDirectories(out);
        RClassGenerator writer = RClassGenerator.with(out, symbolValues.asInitializers(), finalFields);
        writer.write("", symbolsInLibrary.asInitializers());
        Path packageDir = out.resolve("");
        RClassGeneratorTest.checkFilesInPackage(packageDir, "R.class", "R$string.class");
        Class<?> outerClass = RClassGeneratorTest.checkTopLevelClass(out, "R", "R$string");
        checkInnerClass(out, "R$string", outerClass, ImmutableMap.of("some_string", 2132803584), ImmutableMap.<String, List<Integer>>of(), finalFields);
    }
}

