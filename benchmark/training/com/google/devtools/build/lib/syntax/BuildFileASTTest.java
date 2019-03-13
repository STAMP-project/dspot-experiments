/**
 * Copyright 2006 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.syntax;


import com.google.devtools.build.lib.events.Event;
import com.google.devtools.build.lib.syntax.SkylarkList.Tuple;
import com.google.devtools.build.lib.syntax.util.EvaluationTestCase;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.testutil.Scratch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for BuildFileAST.
 */
@RunWith(JUnit4.class)
public class BuildFileASTTest extends EvaluationTestCase {
    private Scratch scratch = new Scratch();

    @Test
    public void testParseBuildFileOK() throws Exception {
        BuildFileAST buildfile = parseBuildFile("# a file in the build language", "", "x = [1,2,\'foo\',4] + [1,2, \"%s%d\" % (\'foo\', 1)]");
        assertThat(buildfile.exec(env, getEventHandler())).isTrue();
        // Test final environment is correctly modified:
        // 
        // input1.BUILD contains:
        // x = [1,2,'foo',4] + [1,2, "%s%d" % ('foo', 1)]
        assertThat(env.moduleLookup("x")).isEqualTo(SkylarkList.createImmutable(Tuple.of(1, 2, "foo", 4, 1, 2, "foo1")));
    }

    @Test
    public void testEvalException() throws Exception {
        setFailFast(false);
        BuildFileAST buildfile = parseBuildFile("x = 1", "y = [2,3]", "", "z = x + y");
        assertThat(buildfile.exec(env, getEventHandler())).isFalse();
        Event e = assertContainsError("unsupported operand type(s) for +: 'int' and 'list'");
        assertThat(e.getLocation().getStartLineAndColumn().getLine()).isEqualTo(4);
    }

    @Test
    public void testParsesFineWithNewlines() throws Exception {
        BuildFileAST buildFileAST = parseBuildFile("foo()", "bar()", "something = baz()", "bar()");
        assertThat(buildFileAST.getStatements()).hasSize(4);
    }

    @Test
    public void testFailsIfNewlinesAreMissing() throws Exception {
        setFailFast(false);
        BuildFileAST buildFileAST = parseBuildFile("foo() bar() something = baz() bar()");
        Event event = assertContainsError("syntax error at \'bar\': expected newline");
        assertThat(event.getLocation().getPath().toString()).isEqualTo("/a/build/file/BUILD");
        assertThat(event.getLocation().getStartLineAndColumn().getLine()).isEqualTo(1);
        assertThat(buildFileAST.containsErrors()).isTrue();
    }

    @Test
    public void testImplicitStringConcatenationFails() throws Exception {
        setFailFast(false);
        BuildFileAST buildFileAST = parseBuildFile("a = 'foo' 'bar'");
        Event event = assertContainsError("Implicit string concatenation is forbidden, use the + operator");
        assertThat(event.getLocation().getPath().toString()).isEqualTo("/a/build/file/BUILD");
        assertThat(event.getLocation().getStartLineAndColumn().getLine()).isEqualTo(1);
        assertThat(event.getLocation().getStartLineAndColumn().getColumn()).isEqualTo(10);
        assertThat(buildFileAST.containsErrors()).isTrue();
    }

    @Test
    public void testImplicitStringConcatenationAcrossLinesIsIllegal() throws Exception {
        setFailFast(false);
        BuildFileAST buildFileAST = parseBuildFile("a = \'foo\'\n  \'bar\'");
        Event event = assertContainsError("indentation error");
        assertThat(event.getLocation().getPath().toString()).isEqualTo("/a/build/file/BUILD");
        assertThat(event.getLocation().getStartLineAndColumn().getLine()).isEqualTo(2);
        assertThat(event.getLocation().getStartLineAndColumn().getColumn()).isEqualTo(2);
        assertThat(buildFileAST.containsErrors()).isTrue();
    }

    @Test
    public void testWithSyntaxErrorsDoesNotPrintDollarError() throws Exception {
        setFailFast(false);
        BuildFileAST buildFile = // syntax error at '+'
        parseBuildFile("abi = '$(ABI)-glibc-' + glibc_version + '-' + $(TARGET_CPU) + '-linux'", "libs = [abi + opt_level + '/lib/libcc.a']", "shlibs = [abi + opt_level + '/lib/libcc.so']", "+* shlibs", "cc_library(name = 'cc',", "           srcs = libs,", "           includes = [ abi + opt_level + '/include' ])");
        assertThat(buildFile.containsErrors()).isTrue();
        assertContainsError("syntax error at '+': expected expression");
        assertThat(buildFile.exec(env, getEventHandler())).isFalse();
        MoreAsserts.assertDoesNotContainEvent(getEventCollector(), "$error$");
        // This message should not be printed anymore.
        MoreAsserts.assertDoesNotContainEvent(getEventCollector(), "contains syntax error(s)");
    }
}

