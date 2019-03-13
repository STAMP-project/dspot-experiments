/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
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
import com.google.devtools.build.lib.syntax.util.EvaluationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the validation process of Skylark files.
 */
@RunWith(JUnit4.class)
public class ValidationTest extends EvaluationTestCase {
    @Test
    public void testAssignmentNotValidLValue() {
        checkError("cannot assign to \'\"a\"\'", "'a' = 1");
    }

    @Test
    public void testAugmentedAssignmentWithMultipleLValues() {
        checkError("cannot perform augmented assignment on a list or tuple expression", "a, b += 2, 3");
    }

    @Test
    public void testReturnOutsideFunction() throws Exception {
        checkError("return statements must be inside a function", "return 2\n");
    }

    @Test
    public void testLoadAfterStatement() throws Exception {
        env = newEnvironmentWithSkylarkOptions("--incompatible_bzl_disallow_load_after_statement=true");
        checkError("load() statements must be called before any other statement", "a = 5", "load(':b.bzl', 'c')");
    }

    @Test
    public void testAllowLoadAfterStatement() throws Exception {
        env = newEnvironmentWithSkylarkOptions("--incompatible_bzl_disallow_load_after_statement=false");
        parse("a = 5", "load(':b.bzl', 'c')");
    }

    @Test
    public void testForbiddenToplevelIfStatement() throws Exception {
        checkError("if statements are not allowed at the top level", "if True: a = 2");
    }

    @Test
    public void testTwoFunctionsWithTheSameName() throws Exception {
        checkError("Variable foo is read only", "def foo():", "  return 1", "def foo(x, y):", "  return 1");
    }

    @Test
    public void testFunctionLocalVariable() throws Exception {
        checkError("name 'a' is not defined", "def func2(b):", "  c = b", "  c = a", "def func1():", "  a = 1", "  func2(2)");
    }

    @Test
    public void testFunctionLocalVariableDoesNotEffectGlobalValidationEnv() throws Exception {
        checkError("name 'a' is not defined", "def func1():", "  a = 1", "def func2(b):", "  b = a");
    }

    @Test
    public void testFunctionParameterDoesNotEffectGlobalValidationEnv() throws Exception {
        checkError("name 'a' is not defined", "def func1(a):", "  return a", "def func2():", "  b = a");
    }

    @Test
    public void testDefinitionByItself() throws Exception {
        // Variables are assumed to be statically visible in the block (even if they might not be
        // initialized).
        parse("a = a");
        parse("a += a");
        parse("[[] for a in a]");
        parse("def f():", "  for a in a: pass");
    }

    @Test
    public void testLocalValidationEnvironmentsAreSeparated() throws Exception {
        parse("def func1():", "  a = 1", "def func2():", "  a = \'abc\'\n");
    }

    @Test
    public void testBuiltinsCanBeShadowed() throws Exception {
        parse("repr = 1");
    }

    @Test
    public void testSkylarkGlobalVariablesAreReadonly() throws Exception {
        checkError("Variable a is read only", "a = 1", "a = 2");
    }

    @Test
    public void testFunctionDefRecursion() throws Exception {
        parse("def func():", "  func()\n");
    }

    @Test
    public void testMutualRecursion() throws Exception {
        parse("def foo(i):", "  bar(i)", "def bar(i):", "  foo(i)", "foo(4)");
    }

    @Test
    public void testFunctionDefinedBelow() {
        parse("def bar(): a = foo() + 'a'", "def foo(): return 1\n");
    }

    @Test
    public void testGlobalDefinedBelow() throws Exception {
        env = newEnvironmentWithSkylarkOptions();
        parse("def bar(): return x", "x = 5\n");
    }

    @Test
    public void testLocalVariableDefinedBelow() throws Exception {
        env = newEnvironmentWithSkylarkOptions();
        // x is visible in the entire function block
        parse("def bar():", "    for i in range(5):", "        if i > 2: return x", "        x = i");
    }

    @Test
    public void testFunctionDoesNotExist() {
        checkError("name 'foo' is not defined", "def bar(): a = foo() + 'a'");
    }

    @Test
    public void testTupleLiteralWorksForDifferentTypes() throws Exception {
        parse("('a', 1)");
    }

    @Test
    public void testDictLiteralDifferentValueTypeWorks() throws Exception {
        parse("{'a': 1, 'b': 'c'}");
    }

    @Test
    public void testNoneAssignment() throws Exception {
        parse("def func():", "  a = None", "  a = 2", "  a = None\n");
    }

    @Test
    public void testNoneIsAnyType() throws Exception {
        parse("None + None");
        parse("2 == None");
        parse("None > 'a'");
        parse("[] in None");
        parse("5 * None");
    }

    // Skylark built-in functions specific tests
    @Test
    public void testFuncReturningDictAssignmentAsLValue() throws Exception {
        parse("def my_dict():", "  return {'a': 1}", "def func():", "  my_dict()['b'] = 2");
    }

    @Test
    public void testEmptyLiteralGenericIsSetInLaterConcatWorks() {
        parse("def func():", "  s = {}", "  s[\'a\'] = \'b\'\n");
    }

    @Test
    public void testModulesReadOnlyInFuncDefBody() {
        parse("def func():", "  cmd_helper = depset()");
    }

    @Test
    public void testBuiltinGlobalFunctionsReadOnlyInFuncDefBody() {
        parse("def func():", "  rule = 'abc'");
    }

    @Test
    public void testBuiltinGlobalFunctionsReadOnlyAsFuncDefArg() {
        parse("def func(rule):", "  return rule");
    }

    @Test
    public void testFunctionReturnsFunction() {
        parse("def rule(*, implementation): return None", "def impl(ctx): return None", "", "skylark_rule = rule(implementation = impl)", "", "def macro(name):", "  skylark_rule(name = name)");
    }

    @Test
    public void testTypeForBooleanLiterals() {
        parse("len([1, 2]) == 0 and True");
        parse("len([1, 2]) == 0 and False");
    }

    @Test
    public void testDollarErrorDoesNotLeak() throws Exception {
        setFailFast(false);
        parseFile("def GenerateMapNames():", "  a = 2", "  b = [3, 4]", "  if a not b:", "    print(a)");
        assertContainsError("syntax error at 'b': expected 'in'");
        // Parser uses "$error" symbol for error recovery.
        // It should not be used in error messages.
        for (Event event : getEventCollector()) {
            assertThat(event.getMessage()).doesNotContain("$error$");
        }
    }

    @Test
    public void testPositionalAfterStarArg() throws Exception {
        env = newEnvironmentWithSkylarkOptions("--incompatible_strict_argument_ordering=true");
        checkError("positional argument is misplaced (positional arguments come first)", "def fct(*args, **kwargs): pass", "fct(1, *[2], 3)");
    }

    @Test
    public void testTwoStarArgs() throws Exception {
        env = newEnvironmentWithSkylarkOptions("--incompatible_strict_argument_ordering=true");
        checkError("*arg argument is misplaced", "def fct(*args, **kwargs):", "  pass", "fct(1, 2, 3, *[], *[])");
    }

    @Test
    public void testKeywordArgAfterStarArg() throws Exception {
        env = newEnvironmentWithSkylarkOptions("--incompatible_strict_argument_ordering=true");
        checkError("keyword argument is misplaced (keyword arguments must be before any *arg or **kwarg)", "def fct(*args, **kwargs): pass", "fct(1, *[2], a=3)");
    }
}

