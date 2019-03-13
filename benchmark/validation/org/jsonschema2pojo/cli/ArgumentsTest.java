/**
 * Copyright ? 2010-2017 Nokia
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
package org.jsonschema2pojo.cli;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ArgumentsTest {
    private static final PrintStream SYSTEM_OUT = System.out;

    private static final PrintStream SYSTEM_ERR = System.err;

    private final ByteArrayOutputStream systemOutCapture = new ByteArrayOutputStream();

    private final ByteArrayOutputStream systemErrCapture = new ByteArrayOutputStream();

    @Test
    public void parseRecognisesValidArguments() {
        ArgumentsTest.ArgsForTest args = ((ArgumentsTest.ArgsForTest) (parse(new String[]{ "--source", "/home/source", "--target", "/home/target", "--disable-getters", "--package", "mypackage", "--generate-builders", "--use-primitives", "--omit-hashcode-and-equals", "--omit-tostring", "--include-dynamic-accessors", "--include-dynamic-getters", "--include-dynamic-setters", "--include-dynamic-builders", "--inclusion-level", "ALWAYS" })));
        MatcherAssert.assertThat(args.didExit(), is(false));
        MatcherAssert.assertThat(getSource().next().getFile(), endsWith("/home/source"));
        MatcherAssert.assertThat(getTargetDirectory(), is(theFile("/home/target")));
        MatcherAssert.assertThat(getTargetPackage(), is("mypackage"));
        MatcherAssert.assertThat(isGenerateBuilders(), is(true));
        MatcherAssert.assertThat(isUsePrimitives(), is(true));
        MatcherAssert.assertThat(isIncludeHashcodeAndEquals(), is(false));
        MatcherAssert.assertThat(isIncludeToString(), is(false));
        MatcherAssert.assertThat(isIncludeGetters(), is(false));
        MatcherAssert.assertThat(isIncludeSetters(), is(true));
        MatcherAssert.assertThat(isIncludeDynamicAccessors(), is(true));
        MatcherAssert.assertThat(isIncludeDynamicGetters(), is(true));
        MatcherAssert.assertThat(isIncludeDynamicSetters(), is(true));
        MatcherAssert.assertThat(isIncludeDynamicBuilders(), is(true));
        MatcherAssert.assertThat(getInclusionLevel(), is(InclusionLevel.ALWAYS));
    }

    @Test
    public void parseRecognisesShorthandArguments() {
        ArgumentsTest.ArgsForTest args = ((ArgumentsTest.ArgsForTest) (parse(new String[]{ "-s", "/home/source", "-t", "/home/target", "-p", "mypackage", "-b", "-P", "-E", "-S", "-ida", "-idg", "-ids", "-idb", "-il", "ALWAYS" })));
        MatcherAssert.assertThat(args.didExit(), is(false));
        MatcherAssert.assertThat(getSource().next().getFile(), endsWith("/home/source"));
        MatcherAssert.assertThat(getTargetDirectory(), is(theFile("/home/target")));
        MatcherAssert.assertThat(getTargetPackage(), is("mypackage"));
        MatcherAssert.assertThat(isGenerateBuilders(), is(true));
        MatcherAssert.assertThat(isUsePrimitives(), is(true));
        MatcherAssert.assertThat(isIncludeHashcodeAndEquals(), is(false));
        MatcherAssert.assertThat(isIncludeToString(), is(false));
        MatcherAssert.assertThat(isIncludeDynamicAccessors(), is(true));
        MatcherAssert.assertThat(isIncludeDynamicGetters(), is(true));
        MatcherAssert.assertThat(isIncludeDynamicSetters(), is(true));
        MatcherAssert.assertThat(isIncludeDynamicBuilders(), is(true));
        MatcherAssert.assertThat(getInclusionLevel(), is(InclusionLevel.ALWAYS));
    }

    @Test
    public void parserAcceptsHyphenWordDelimiter() {
        ArgumentsTest.ArgsForTest args = ((ArgumentsTest.ArgsForTest) (parse(new String[]{ "-s", "/home/source", "-t", "/home/target", "--word-delimiters", "-" })));
        MatcherAssert.assertThat(getPropertyWordDelimiters(), is(new char[]{ '-' }));
    }

    @Test
    public void allOptionalArgsCanBeOmittedAndDefaultsPrevail() {
        ArgumentsTest.ArgsForTest args = ((ArgumentsTest.ArgsForTest) (parse(new String[]{ "--source", "/home/source", "--target", "/home/target" })));
        MatcherAssert.assertThat(args.didExit(), is(false));
        MatcherAssert.assertThat(getSource().next().getFile(), endsWith("/home/source"));
        MatcherAssert.assertThat(getTargetDirectory(), is(theFile("/home/target")));
        MatcherAssert.assertThat(getTargetPackage(), is(nullValue()));
        MatcherAssert.assertThat(isGenerateBuilders(), is(false));
        MatcherAssert.assertThat(isUsePrimitives(), is(false));
        MatcherAssert.assertThat(isIncludeHashcodeAndEquals(), is(true));
        MatcherAssert.assertThat(isIncludeToString(), is(true));
        MatcherAssert.assertThat(isIncludeGetters(), is(true));
        MatcherAssert.assertThat(isIncludeSetters(), is(true));
        MatcherAssert.assertThat(isIncludeDynamicAccessors(), is(false));
        MatcherAssert.assertThat(isIncludeDynamicGetters(), is(false));
        MatcherAssert.assertThat(isIncludeDynamicSetters(), is(false));
        MatcherAssert.assertThat(isIncludeDynamicBuilders(), is(false));
    }

    @Test
    public void missingArgsCausesHelp() throws IOException {
        ArgumentsTest.ArgsForTest args = ((ArgumentsTest.ArgsForTest) (parse(new String[]{  })));
        MatcherAssert.assertThat(args.status, is(1));
        MatcherAssert.assertThat(new String(systemErrCapture.toByteArray(), "UTF-8"), is(containsString("--target")));
        MatcherAssert.assertThat(new String(systemErrCapture.toByteArray(), "UTF-8"), is(containsString("--source")));
        MatcherAssert.assertThat(new String(systemOutCapture.toByteArray(), "UTF-8"), is(containsString("Usage: jsonschema2pojo")));
    }

    @Test
    public void requestingHelpCausesHelp() throws IOException {
        ArgumentsTest.ArgsForTest args = ((ArgumentsTest.ArgsForTest) (parse(new String[]{ "--help" })));
        MatcherAssert.assertThat(args.status, is(notNullValue()));
        MatcherAssert.assertThat(new String(systemOutCapture.toByteArray(), "UTF-8"), is(containsString("Usage: jsonschema2pojo")));
    }

    private static class ArgsForTest extends Arguments {
        protected Integer status;

        @Override
        protected void exit(int status) {
            this.status = status;
        }

        protected boolean didExit() {
            return (status) != null;
        }
    }
}

