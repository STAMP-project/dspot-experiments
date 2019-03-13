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


import FlagIdentifier.EXPERIMENTAL_BUILD_SETTING_API;
import FlagIdentifier.INCOMPATIBLE_NO_TARGET_OUTPUT_GROUP;
import Location.BUILTIN;
import SkylarkList.Tuple;
import StructProvider.STRUCT;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.analysis.test.AnalysisFailure;
import com.google.devtools.build.lib.analysis.test.AnalysisFailureInfo;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.collect.nestedset.NestedSetBuilder;
import com.google.devtools.build.lib.concurrent.ThreadSafety.Immutable;
import com.google.devtools.build.lib.events.Location;
import com.google.devtools.build.lib.packages.NativeInfo;
import com.google.devtools.build.lib.packages.NativeProvider;
import com.google.devtools.build.lib.skylarkinterface.Param;
import com.google.devtools.build.lib.skylarkinterface.ParamType;
import com.google.devtools.build.lib.skylarkinterface.SkylarkCallable;
import com.google.devtools.build.lib.skylarkinterface.SkylarkModule;
import com.google.devtools.build.lib.skylarkinterface.SkylarkSignature;
import com.google.devtools.build.lib.skylarkinterface.SkylarkValue;
import com.google.devtools.build.lib.skylarkinterface.StarlarkContext;
import com.google.devtools.build.lib.syntax.SkylarkList.MutableList;
import com.google.devtools.build.lib.syntax.util.EvaluationTestCase;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Evaluation tests with Skylark Environment.
 */
@RunWith(JUnit4.class)
public class SkylarkEvaluationTest extends EvaluationTest {
    @Immutable
    static class Bad {
        Bad() {
        }
    }

    @SkylarkSignature(name = "foobar", returnType = String.class, documented = false)
    static BuiltinFunction foobar = new BuiltinFunction("foobar") {
        public String invoke() throws EvalException {
            return "foobar";
        }
    };

    @SkylarkSignature(name = "interrupted_function", returnType = .class, documented = false)
    static BuiltinFunction interruptedFunction = new BuiltinFunction("interrupted_function") {
        public Runtime.NoneType invoke() throws InterruptedException {
            throw new InterruptedException();
        }
    };

    @SkylarkModule(name = "Mock", doc = "")
    static class NativeInfoMock extends NativeInfo {
        private static final NativeProvider<SkylarkEvaluationTest.NativeInfoMock> CONSTRUCTOR = new NativeProvider<SkylarkEvaluationTest.NativeInfoMock>(SkylarkEvaluationTest.NativeInfoMock.class, "native_info_mock") {};

        public NativeInfoMock() {
            super(SkylarkEvaluationTest.NativeInfoMock.CONSTRUCTOR);
        }

        @SkylarkCallable(name = "callable_string", documented = false, structField = false)
        public String callableString() {
            return "a";
        }

        @SkylarkCallable(name = "struct_field_string", documented = false, structField = true)
        public String structFieldString() {
            return "a";
        }

        @SkylarkCallable(name = "struct_field_callable", documented = false, structField = true)
        public BuiltinFunction structFieldCallable() {
            return SkylarkEvaluationTest.foobar;
        }

        @SkylarkCallable(name = "struct_field_none", documented = false, structField = true, allowReturnNones = true)
        public String structFieldNone() {
            return null;
        }
    }

    @SkylarkModule(name = "Mock", doc = "")
    static class Mock {
        @SkylarkCallable(name = "MockFn", selfCall = true, documented = false, parameters = { @Param(name = "pos", positional = true, type = String.class) })
        public String selfCall(String myName) {
            return "I'm a mock named " + myName;
        }

        @SkylarkCallable(name = "value_of", parameters = { @Param(name = "str", type = String.class) }, documented = false)
        public Integer valueOf(String str) {
            return Integer.valueOf(str);
        }

        @SkylarkCallable(name = "is_empty", parameters = { @Param(name = "str", type = String.class) }, documented = false)
        public Boolean isEmpty(String str) {
            return str.isEmpty();
        }

        public void value() {
        }

        @SkylarkCallable(name = "return_bad", documented = false)
        public SkylarkEvaluationTest.Bad returnBad() {
            return new SkylarkEvaluationTest.Bad();
        }

        @SkylarkCallable(name = "struct_field", documented = false, structField = true)
        public String structField() {
            return "a";
        }

        @SkylarkCallable(name = "struct_field_with_extra", documented = false, structField = true, useSkylarkSemantics = true)
        public String structFieldWithExtra(StarlarkSemantics sem) {
            return ("struct_field_with_extra(" + (sem != null)) + ")";
        }

        @SkylarkCallable(name = "struct_field_callable", documented = false, structField = true)
        public BuiltinFunction structFieldCallable() {
            return SkylarkEvaluationTest.foobar;
        }

        @SkylarkCallable(name = "interrupted_struct_field", documented = false, structField = true)
        public BuiltinFunction structFieldInterruptedCallable() throws InterruptedException {
            throw new InterruptedException();
        }

        @SkylarkCallable(name = "function", documented = false, structField = false)
        public String function() {
            return "a";
        }

        @SuppressWarnings("unused")
        @SkylarkCallable(name = "nullfunc_failing", parameters = { @Param(name = "p1", type = String.class), @Param(name = "p2", type = Integer.class) }, documented = false, allowReturnNones = false)
        public SkylarkValue nullfuncFailing(String p1, Integer p2) {
            return null;
        }

        @SkylarkCallable(name = "nullfunc_working", documented = false, allowReturnNones = true)
        public SkylarkValue nullfuncWorking() {
            return null;
        }

        @SkylarkCallable(name = "voidfunc", documented = false)
        public void voidfunc() {
        }

        @SkylarkCallable(name = "string_list", documented = false)
        public ImmutableList<String> stringList() {
            return ImmutableList.<String>of("a", "b");
        }

        @SkylarkCallable(name = "string", documented = false)
        public String string() {
            return "a";
        }

        @SkylarkCallable(name = "string_list_dict", documented = false)
        public Map<String, List<String>> stringListDict() {
            return ImmutableMap.of("a", ImmutableList.of("b", "c"));
        }

        @SkylarkCallable(name = "legacy_method", documented = false, parameters = { @Param(name = "pos", positional = true, type = Boolean.class), @Param(name = "legacyNamed", type = Boolean.class, positional = true, named = false, legacyNamed = true), @Param(name = "named", type = Boolean.class, positional = false, named = true) })
        public String legacyMethod(Boolean pos, Boolean legacyNamed, Boolean named) {
            return ((((("legacy_method(" + pos) + ", ") + legacyNamed) + ", ") + named) + ")";
        }

        @SkylarkCallable(name = "with_params", documented = false, parameters = { @Param(name = "pos1"), @Param(name = "pos2", defaultValue = "False", type = Boolean.class), @Param(name = "posOrNamed", defaultValue = "False", type = Boolean.class, positional = true, named = true), @Param(name = "named", type = Boolean.class, positional = false, named = true), @Param(name = "optionalNamed", type = Boolean.class, defaultValue = "False", positional = false, named = true), @Param(name = "nonNoneable", type = Object.class, defaultValue = "\"a\"", positional = false, named = true), @Param(name = "noneable", type = Integer.class, defaultValue = "None", noneable = true, positional = false, named = true), @Param(name = "multi", allowedTypes = { @ParamType(type = String.class), @ParamType(type = Integer.class), @ParamType(type = SkylarkList.class, generic1 = Integer.class) }, defaultValue = "None", noneable = true, positional = false, named = true) })
        public String withParams(Integer pos1, boolean pos2, boolean posOrNamed, boolean named, boolean optionalNamed, Object nonNoneable, Object noneable, Object multi) {
            return ((((((((((((("with_params(" + pos1) + ", ") + pos2) + ", ") + posOrNamed) + ", ") + named) + ", ") + optionalNamed) + ", ") + nonNoneable) + (noneable != (NONE) ? ", " + noneable : "")) + (multi != (NONE) ? ", " + multi : "")) + ")";
        }

        @SkylarkCallable(name = "with_extra", documented = false, useLocation = true, useAst = true, useEnvironment = true, useSkylarkSemantics = true, useContext = true)
        public String withExtraInterpreterParams(Location location, FuncallExpression func, Environment env, StarlarkSemantics sem, StarlarkContext context) {
            return ((((((((("with_extra(" + (location.getStartLine())) + ", ") + (func.getArguments().size())) + ", ") + (env.isGlobal())) + ", ") + (sem != null)) + ", ") + (context != null)) + ")";
        }

        @SkylarkCallable(name = "with_params_and_extra", documented = false, parameters = { @Param(name = "pos1"), @Param(name = "pos2", defaultValue = "False", type = Boolean.class), @Param(name = "posOrNamed", defaultValue = "False", type = Boolean.class, positional = true, named = true), @Param(name = "named", type = Boolean.class, positional = false, named = true), @Param(name = "optionalNamed", type = Boolean.class, defaultValue = "False", positional = false, named = true), @Param(name = "nonNoneable", type = Object.class, defaultValue = "\"a\"", positional = false, named = true), @Param(name = "noneable", type = Integer.class, defaultValue = "None", noneable = true, positional = false, named = true), @Param(name = "multi", allowedTypes = { @ParamType(type = String.class), @ParamType(type = Integer.class), @ParamType(type = SkylarkList.class, generic1 = Integer.class) }, defaultValue = "None", noneable = true, positional = false, named = true) }, useAst = true, useLocation = true, useEnvironment = true, useSkylarkSemantics = true)
        public String withParamsAndExtraInterpreterParams(Integer pos1, boolean pos2, boolean posOrNamed, boolean named, boolean optionalNamed, Object nonNoneable, Object noneable, Object multi, Location location, FuncallExpression func, Environment env, StarlarkSemantics sem) {
            return ((((((((((((((((((((("with_params_and_extra(" + pos1) + ", ") + pos2) + ", ") + posOrNamed) + ", ") + named) + ", ") + optionalNamed) + ", ") + nonNoneable) + (noneable != (NONE) ? ", " + noneable : "")) + (multi != (NONE) ? ", " + multi : "")) + ", ") + (location.getStartLine())) + ", ") + (func.getArguments().size())) + ", ") + (env.isGlobal())) + ", ") + (sem != null)) + ")";
        }

        @SkylarkCallable(name = "proxy_methods_object", doc = "Returns a struct containing all callable method objects of this mock", allowReturnNones = true)
        public ClassObject proxyMethodsObject() {
            ImmutableMap.Builder<String, Object> builder = new ImmutableMap.Builder<>();
            for (String nativeFunction : FuncallExpression.getMethodNames(SkylarkEvaluationTest.Mock.class)) {
                builder.put(nativeFunction, FuncallExpression.getBuiltinCallable(this, nativeFunction));
            }
            return STRUCT.create(builder.build(), "no native callable '%s'");
        }

        @SkylarkCallable(name = "with_args_and_env", documented = false, parameters = { @Param(name = "pos1", type = Integer.class), @Param(name = "pos2", defaultValue = "False", type = Boolean.class), @Param(name = "named", type = Boolean.class, positional = false, named = true) }, extraPositionals = @Param(name = "args"), useEnvironment = true)
        public String withArgsAndEnv(Integer pos1, boolean pos2, boolean named, SkylarkList<?> args, Environment env) {
            String argsString = ("args(" + (args.stream().map(Printer::debugPrint).collect(Collectors.joining(", ")))) + ")";
            return ((((((((("with_args_and_env(" + pos1) + ", ") + pos2) + ", ") + named) + ", ") + argsString) + ", ") + (env.isGlobal())) + ")";
        }

        @SkylarkCallable(name = "with_kwargs", documented = false, parameters = { @Param(name = "pos", defaultValue = "False", type = Boolean.class), @Param(name = "named", type = Boolean.class, positional = false, named = true) }, extraKeywords = @Param(name = "kwargs"))
        public String withKwargs(boolean pos, boolean named, SkylarkDict<?, ?> kwargs) throws EvalException {
            String kwargsString = ("kwargs(" + (kwargs.getContents(String.class, Object.class, "kwargs").entrySet().stream().map(( entry) -> ((entry.getKey()) + "=") + (entry.getValue())).collect(Collectors.joining(", ")))) + ")";
            return ((((("with_kwargs(" + pos) + ", ") + named) + ", ") + kwargsString) + ")";
        }

        @SkylarkCallable(name = "with_args_and_kwargs", documented = false, parameters = { @Param(name = "foo", named = true, positional = true, type = String.class) }, extraPositionals = @Param(name = "args"), extraKeywords = @Param(name = "kwargs"))
        public String withArgsAndKwargs(String foo, SkylarkList<?> args, SkylarkDict<?, ?> kwargs) throws EvalException {
            String argsString = ("args(" + (args.stream().map(Printer::debugPrint).collect(Collectors.joining(", ")))) + ")";
            String kwargsString = ("kwargs(" + (kwargs.getContents(String.class, Object.class, "kwargs").entrySet().stream().map(( entry) -> ((entry.getKey()) + "=") + (entry.getValue())).collect(Collectors.joining(", ")))) + ")";
            return ((((("with_args_and_kwargs(" + foo) + ", ") + argsString) + ", ") + kwargsString) + ")";
        }

        @Override
        public String toString() {
            return "<mock>";
        }
    }

    @SkylarkModule(name = "MockInterface", doc = "")
    static interface MockInterface {
        @SkylarkCallable(name = "is_empty_interface", parameters = { @Param(name = "str", type = String.class) }, documented = false)
        public Boolean isEmptyInterface(String str);
    }

    @SkylarkModule(name = "MockSubClass", doc = "")
    static final class MockSubClass extends SkylarkEvaluationTest.Mock implements SkylarkEvaluationTest.MockInterface {
        @Override
        public Boolean isEmpty(String str) {
            return str.isEmpty();
        }

        @Override
        public Boolean isEmptyInterface(String str) {
            return str.isEmpty();
        }
    }

    @SkylarkModule(name = "MockClassObject", documented = false, doc = "")
    static final class MockClassObject implements ClassObject {
        @Override
        public Object getValue(String name) {
            switch (name) {
                case "field" :
                    return "a";
                case "nset" :
                    return NestedSetBuilder.stableOrder().build();
                default :
                    return null;
            }
        }

        @Override
        public ImmutableCollection<String> getFieldNames() {
            return ImmutableList.of("field", "nset");
        }

        @Override
        public String getErrorMessageForUnknownField(String name) {
            return null;
        }
    }

    @SkylarkModule(name = "ParamterizedMock", doc = "")
    static interface ParameterizedApi<ObjectT> {
        @SkylarkCallable(name = "method", documented = false, parameters = { @Param(name = "foo", named = true, positional = true, type = Object.class) })
        public ObjectT method(ObjectT o);
    }

    static final class ParameterizedMock implements SkylarkEvaluationTest.ParameterizedApi<String> {
        @Override
        public String method(String o) {
            return o;
        }
    }

    // Verifies that a method implementation overriding a parameterized annotated interface method
    // is still treated as skylark-callable. Concretely, method() below should be treated as
    // callable even though its method signature isn't an *exact* match of the annotated method
    // declaration, due to the interface's method declaration being generic.
    @Test
    public void testParameterizedMock() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.ParameterizedMock()).setUp("result = mock.method('bar')").testLookup("result", "bar");
    }

    @Test
    public void testSimpleIf() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  a = 0", "  x = 0", "  if x: a = 5", "  return a", "a = foo()").testLookup("a", 0);
    }

    @Test
    public void testIfPass() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  a = 1", "  x = True", "  if x: pass", "  return a", "a = foo()").testLookup("a", 1);
    }

    @Test
    public void testNestedIf() throws Exception {
        executeNestedIf(0, 0, 0);
        executeNestedIf(1, 0, 3);
        executeNestedIf(1, 1, 5);
    }

    @Test
    public void testIfElse() throws Exception {
        executeIfElse("foo", "something", 2);
        executeIfElse("bar", "", 3);
    }

    @Test
    public void testIfElifElse_IfExecutes() throws Exception {
        execIfElifElse(1, 0, 1);
    }

    @Test
    public void testIfElifElse_ElifExecutes() throws Exception {
        execIfElifElse(0, 1, 2);
    }

    @Test
    public void testIfElifElse_ElseExecutes() throws Exception {
        execIfElifElse(0, 0, 3);
    }

    @Test
    public void testForOnList() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  s = ''", "  for i in ['hello', ' ', 'world']:", "    s = s + i", "  return s", "s = foo()").testLookup("s", "hello world");
    }

    @Test
    public void testForAssignmentList() throws Exception {
        // check that we use the old list
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  d = ['a', 'b', 'c']", "  s = ''", "  for i in d:", "    s = s + i", "    d = ['d', 'e', 'f']", "  return s", "s = foo()").testLookup("s", "abc");
    }

    @Test
    public void testForAssignmentDict() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def func():", "  d = {'a' : 1, 'b' : 2, 'c' : 3}", "  s = ''", "  for i in d:", "    s = s + i", "    d = {'d' : 1, 'e' : 2, 'f' : 3}", "  return s", "s = func()").testLookup("s", "abc");
    }

    @Test
    public void testForUpdateList() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  xs = [1, 2, 3]", "  for x in xs:", "    if x == 1:", "      xs.append(10)").testIfErrorContains("trying to mutate a locked object", "foo()");
    }

    @Test
    public void testForUpdateDict() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  d = {'a': 1, 'b': 2, 'c': 3}", "  for k in d:", "    d[k] *= 2").testIfErrorContains("trying to mutate a locked object", "foo()");
    }

    @Test
    public void testForUnlockedAfterBreak() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  xs = [1, 2]", "  for x in xs:", "    break", "  xs.append(3)", "  return xs").testEval("foo()", "[1, 2, 3]");
    }

    @Test
    public void testForNestedOnSameListStillLocked() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  xs = [1, 2]", "  ys = []", "  for x1 in xs:", "    for x2 in xs:", "      ys.append(x1 * x2)", "    xs.append(4)", "  return ys").testIfErrorContains("trying to mutate a locked object", "foo()");
    }

    @Test
    public void testForNestedOnSameListErrorMessage() throws Exception {
        // No file name in message, due to how test is set up.
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  xs = [1, 2]", "  ys = []", "  for x1 in xs:", "    for x2 in xs:", "      ys.append(x1 * x2)", "      xs.append(4)", "  return ys").testIfErrorContains("Object locked at the following location(s): :4:3, :5:5", "foo()");
    }

    @Test
    public void testForNestedOnSameListUnlockedAtEnd() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  xs = [1, 2]", "  ys = []", "  for x1 in xs:", "    for x2 in xs:", "      ys.append(x1 * x2)", "  xs.append(4)", "  return ys").testEval("foo()", "[1, 2, 2, 4]");
    }

    @Test
    public void testForNestedWithListCompGood() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  xs = [1, 2]", "  ys = []", "  for x in xs:", "    zs = [None for x in xs for y in (ys.append(x) or ys)]", "  return ys").testEval("foo()", "[1, 2, 1, 2]");
    }

    @Test
    public void testForNestedWithListCompBad() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  xs = [1, 2, 3]", "  ys = []", "  for x in xs:", "    zs = [None for x in xs for y in (xs.append(x) or ys)]", "  return ys").testIfErrorContains("trying to mutate a locked object", "foo()");
    }

    @Test
    public void testForDeepUpdate() throws Exception {
        // Check that indirectly reachable values can still be manipulated as normal.
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  xs = [['a'], ['b'], ['c']]", "  ys = []", "  for x in xs:", "    for y in x:", "      ys.append(y)", "    xs[2].append(x[0])", "  return ys", "ys = foo()").testLookup("ys", MutableList.of(null, "a", "b", "c", "a", "b"));
    }

    @Test
    public void testForNotIterable() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testIfErrorContains("type 'int' is not iterable", "def func():", "  for i in mock.value_of('1'): a = i", "func()\n");
    }

    @Test
    public void testForStringNotIterable() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testIfErrorContains("type 'string' is not iterable", "def func():", "  for i in 'abc': a = i", "func()\n");
    }

    @Test
    public void testForOnDictionary() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  d = {1: 'a', 2: 'b', 3: 'c'}", "  s = ''", "  for i in d: s = s + d[i]", "  return s", "s = foo()").testLookup("s", "abc");
    }

    @Test
    public void testBadDictKey() throws Exception {
        new EvaluationTestCase.SkylarkTest().testIfErrorContains("unhashable type: 'list'", "{ [1, 2]: [3, 4] }");
    }

    @Test
    public void testForLoopReuseVariable() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  s = ''", "  for i in ['a', 'b']:", "    for i in ['c', 'd']: s = s + i", "  return s", "s = foo()").testLookup("s", "cdcd");
    }

    @Test
    public void testForLoopMultipleVariables() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  s = ''", "  for [i, j] in [[1, 2], [3, 4]]:", "    s = s + str(i) + str(j) + '.'", "  return s", "s = foo()").testLookup("s", "12.34.");
    }

    @Test
    public void testForLoopBreak() throws Exception {
        simpleFlowTest("break", 1);
    }

    @Test
    public void testForLoopContinue() throws Exception {
        simpleFlowTest("continue", 10);
    }

    @Test
    public void testForLoopBreakFromDeeperBlock() throws Exception {
        flowFromDeeperBlock("break", 1);
        flowFromNestedBlocks("break", 29);
    }

    @Test
    public void testForLoopContinueFromDeeperBlock() throws Exception {
        flowFromDeeperBlock("continue", 5);
        flowFromNestedBlocks("continue", 39);
    }

    @Test
    public void testNestedForLoopsMultipleBreaks() throws Exception {
        nestedLoopsTest("break", 2, 6, 6);
    }

    @Test
    public void testNestedForLoopsMultipleContinues() throws Exception {
        nestedLoopsTest("continue", 4, 20, 20);
    }

    @Test
    public void testForLoopBreakError() throws Exception {
        flowStatementInsideFunction("break");
        flowStatementAfterLoop("break");
    }

    @Test
    public void testForLoopContinueError() throws Exception {
        flowStatementInsideFunction("continue");
        flowStatementAfterLoop("continue");
    }

    @Test
    public void testNoneAssignment() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo(x=None):", "  x = 1", "  x = None", "  return 2", "s = foo()").testLookup("s", 2);
    }

    @Test
    public void testReassignment() throws Exception {
        eval("def foo(x=None):", "  x = 1", "  x = [1, 2]", "  x = 'str'", "  return x", "s = foo()");
        assertThat(lookup("s")).isEqualTo("str");
    }

    @Test
    public void testJavaCalls() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.is_empty('a')").testLookup("b", Boolean.FALSE);
    }

    @Test
    public void testJavaCallsOnSubClass() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.MockSubClass()).setUp("b = mock.is_empty('a')").testLookup("b", Boolean.FALSE);
    }

    @Test
    public void testJavaCallsOnInterface() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.MockSubClass()).setUp("b = mock.is_empty_interface('a')").testLookup("b", Boolean.FALSE);
    }

    @Test
    public void testJavaCallsNotSkylarkCallable() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testIfExactError("type 'Mock' has no method value()", "mock.value()");
    }

    @Test
    public void testNoOperatorIndex() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testIfExactError("type 'Mock' has no operator [](int)", "mock[2]");
    }

    @Test
    public void testJavaCallsNoMethod() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testIfExactError("type 'Mock' has no method bad()", "mock.bad()");
    }

    @Test
    public void testJavaCallsNoMethodErrorMsg() throws Exception {
        new EvaluationTestCase.SkylarkTest().testIfExactError("type 'int' has no method bad()", "s = 3.bad('a', 'b', 'c')");
    }

    @Test
    public void testJavaCallWithKwargs() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testIfExactError("type 'Mock' has no method isEmpty()", "mock.isEmpty(str='abc')");
    }

    @Test
    public void testStringListDictValues() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("def func(mock):", "  for i, v in mock.string_list_dict().items():", "    modified_list = v + ['extra_string']", "  return modified_list", "m = func(mock)").testLookup("m", MutableList.of(env, "b", "c", "extra_string"));
    }

    @Test
    public void testProxyMethodsObject() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("m = mock.proxy_methods_object()", "b = m.with_params(1, True, named=True)").testLookup("b", "with_params(1, true, false, true, false, a)");
    }

    @Test
    public void testLegacyNamed() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.legacy_method(True, legacyNamed=True, named=True)").testLookup("b", "legacy_method(true, true, true)");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.legacy_method(True, True, named=True)").testLookup("b", "legacy_method(true, true, true)");
        // Verify legacyNamed also works with proxy method objects.
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("m = mock.proxy_methods_object()", "b = m.legacy_method(True, legacyNamed=True, named=True)").testLookup("b", "legacy_method(true, true, true)");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("m = mock.proxy_methods_object()", "b = m.legacy_method(True, True, named=True)").testLookup("b", "legacy_method(true, true, true)");
    }

    /**
     * This test verifies an error is raised when a method parameter is set both positionally and
     * by name.
     */
    @Test
    public void testArgSpecifiedBothByNameAndPosition() throws Exception {
        // in with_params, 'posOrNamed' is positional parameter index 2. So by specifying both
        // posOrNamed by name and three positional parameters, there is a conflict.
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testIfErrorContains("got multiple values for keyword argument 'posOrNamed'", "mock.with_params(1, True, True, posOrNamed=True, named=True)");
    }

    @Test
    public void testTooManyPositionalArgs() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testIfErrorContains("expected no more than 3 positional arguments, but got 4", "mock.with_params(1, True, True, 'toomany', named=True)");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testIfErrorContains("expected no more than 3 positional arguments, but got 5", "mock.with_params(1, True, True, 'toomany', 'alsotoomany', named=True)");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testIfErrorContains("expected no more than 1 positional arguments, but got 2", "mock.is_empty('a', 'b')");
    }

    @Test
    public void testJavaCallWithPositionalAndKwargs() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.with_params(1, True, named=True)").testLookup("b", "with_params(1, true, false, true, false, a)");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.with_params(1, True, named=True, multi=1)").testLookup("b", "with_params(1, true, false, true, false, a, 1)");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.with_params(1, True, named=True, multi='abc')").testLookup("b", "with_params(1, true, false, true, false, a, abc)");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.with_params(1, True, named=True, multi=[1,2,3])").testLookup("b", "with_params(1, true, false, true, false, a, [1, 2, 3])");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("").testIfExactError(("parameter 'named' has no default value, for call to " + (("method with_params(pos1, pos2 = False, posOrNamed = False, named, " + "optionalNamed = False, nonNoneable = \"a\", noneable = None, multi = None) ") + "of 'Mock'")), "mock.with_params(1, True)");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("").testIfExactError(("parameter 'named' has no default value, for call to " + (("method with_params(pos1, pos2 = False, posOrNamed = False, named, " + "optionalNamed = False, nonNoneable = \"a\", noneable = None, multi = None) ") + "of 'Mock'")), "mock.with_params(1, True, True)");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.with_params(1, True, True, named=True)").testLookup("b", "with_params(1, true, true, true, false, a)");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.with_params(1, True, named=True, posOrNamed=True)").testLookup("b", "with_params(1, true, true, true, false, a)");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.with_params(1, True, named=True, posOrNamed=True, optionalNamed=True)").testLookup("b", "with_params(1, true, true, true, true, a)");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("").testIfExactError(("unexpected keyword 'n', for call to " + (("method with_params(pos1, pos2 = False, posOrNamed = False, named, " + "optionalNamed = False, nonNoneable = \"a\", noneable = None, multi = None) ") + "of 'Mock'")), "mock.with_params(1, True, named=True, posOrNamed=True, n=2)");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("").testIfExactError(("parameter 'nonNoneable' cannot be None, for call to method " + (("with_params(pos1, pos2 = False, posOrNamed = False, named, " + "optionalNamed = False, nonNoneable = \"a\", noneable = None, multi = None) ") + "of 'Mock'")), "mock.with_params(1, True, True, named=True, optionalNamed=False, nonNoneable=None)");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("").testIfExactError(("expected value of type 'string or int or sequence of ints or NoneType' for parameter" + (((" 'multi', for call to method " + "with_params(pos1, pos2 = False, posOrNamed = False, named, ") + "optionalNamed = False, nonNoneable = \"a\", noneable = None, multi = None) ") + "of 'Mock'")), "mock.with_params(1, True, named=True, multi=False)");
        // We do not enforce list item parameter type constraints.
        // Test for this behavior.
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.with_params(1, True, named=True, multi=['a', 'b'])").testLookup("b", "with_params(1, true, false, true, false, a, [\"a\", \"b\"])");
    }

    @Test
    public void testNoJavaCallsWithoutSkylark() throws Exception {
        new EvaluationTestCase.SkylarkTest().testIfExactError("type 'int' has no method to_string()", "s = 3.to_string()");
    }

    @Test
    public void testStructAccess() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("v = mock.struct_field").testLookup("v", "a");
    }

    @Test
    public void testStructAccessAsFuncallNonCallable() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testIfExactError("'string' object is not callable", "v = mock.struct_field()");
    }

    @Test
    public void testSelfCall() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("v = mock('bestmock')").testLookup("v", "I'm a mock named bestmock");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("mockfunction = mock", "v = mockfunction('bestmock')").testLookup("v", "I'm a mock named bestmock");
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testIfErrorContains("expected value of type 'string' for parameter 'pos', for call to function MockFn(pos)", "v = mock(1)");
    }

    @Test
    public void testStructAccessAsFuncall() throws Exception {
        SkylarkEvaluationTest.foobar.configure(getClass().getDeclaredField("foobar").getAnnotation(SkylarkSignature.class));
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("v = mock.struct_field_callable()").testLookup("v", "foobar");
    }

    @Test
    public void testCallingInterruptedStructField() throws Exception {
        update("mock", new SkylarkEvaluationTest.Mock());
        MoreAsserts.assertThrows(InterruptedException.class, () -> eval("mock.interrupted_struct_field()"));
    }

    @Test
    public void testCallingInterruptedFunction() throws Exception {
        SkylarkEvaluationTest.interruptedFunction.configure(getClass().getDeclaredField("interruptedFunction").getAnnotation(SkylarkSignature.class));
        update("interrupted_function", SkylarkEvaluationTest.interruptedFunction);
        MoreAsserts.assertThrows(InterruptedException.class, () -> eval("interrupted_function()"));
    }

    @Test
    public void testJavaFunctionWithExtraInterpreterParams() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("v = mock.with_extra()").testLookup("v", "with_extra(1, 0, true, true, true)");
    }

    @Test
    public void testStructFieldWithExtraInterpreterParams() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("v = mock.struct_field_with_extra").testLookup("v", "struct_field_with_extra(true)");
    }

    @Test
    public void testJavaFunctionWithParamsAndExtraInterpreterParams() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.with_params_and_extra(1, True, named=True)").testLookup("b", "with_params_and_extra(1, true, false, true, false, a, 1, 3, true, true)");
    }

    @Test
    public void testJavaFunctionWithExtraArgsAndEnv() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.with_args_and_env(1, True, 'extraArg1', 'extraArg2', named=True)").testLookup("b", "with_args_and_env(1, true, true, args(extraArg1, extraArg2), true)");
        // Use an args list.
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("myargs = ['extraArg2']", "b = mock.with_args_and_env(1, True, 'extraArg1', named=True, *myargs)").testLookup("b", "with_args_and_env(1, true, true, args(extraArg1, extraArg2), true)");
    }

    @Test
    public void testJavaFunctionWithExtraKwargs() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.with_kwargs(True, extraKey1=True, named=True, extraKey2='x')").testLookup("b", "with_kwargs(true, true, kwargs(extraKey1=true, extraKey2=x))");
        // Use a kwargs dict.
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("mykwargs = {'extraKey2':'x', 'named':True}", "b = mock.with_kwargs(True, extraKey1=True, **mykwargs)").testLookup("b", "with_kwargs(true, true, kwargs(extraKey1=true, extraKey2=x))");
    }

    @Test
    public void testJavaFunctionWithArgsAndKwargs() throws Exception {
        // Foo is used positionally
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.with_args_and_kwargs('foo', 'bar', 'baz', extraKey1=True, extraKey2='x')").testLookup("b", "with_args_and_kwargs(foo, args(bar, baz), kwargs(extraKey1=true, extraKey2=x))");
        // Use an args list and a kwargs dict
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("mykwargs = {'extraKey1':True}", "myargs = ['baz']", "b = mock.with_args_and_kwargs('foo', 'bar', extraKey2='x', *myargs, **mykwargs)").testLookup("b", "with_args_and_kwargs(foo, args(bar, baz), kwargs(extraKey2=x, extraKey1=true))");
        // Foo is used by name
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.with_args_and_kwargs(foo='foo', extraKey1=True)").testLookup("b", "with_args_and_kwargs(foo, args(), kwargs(extraKey1=true))");
        // Empty args and kwargs.
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("b = mock.with_args_and_kwargs('foo')").testLookup("b", "with_args_and_kwargs(foo, args(), kwargs())");
    }

    @Test
    public void testProxyMethodsObjectWithArgsAndKwargs() throws Exception {
        // Foo is used positionally
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("m = mock.proxy_methods_object()", "b = m.with_args_and_kwargs('foo', 'bar', 'baz', extraKey1=True, extraKey2='x')").testLookup("b", "with_args_and_kwargs(foo, args(bar, baz), kwargs(extraKey1=true, extraKey2=x))");
        // Use an args list and a kwargs dict
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("mykwargs = {'extraKey1':True}", "myargs = ['baz']", "m = mock.proxy_methods_object()", "b = m.with_args_and_kwargs('foo', 'bar', extraKey2='x', *myargs, **mykwargs)").testLookup("b", "with_args_and_kwargs(foo, args(bar, baz), kwargs(extraKey2=x, extraKey1=true))");
        // Foo is used by name
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("m = mock.proxy_methods_object()", "b = m.with_args_and_kwargs(foo='foo', extraKey1=True)").testLookup("b", "with_args_and_kwargs(foo, args(), kwargs(extraKey1=true))");
        // Empty args and kwargs.
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("m = mock.proxy_methods_object()", "b = m.with_args_and_kwargs('foo')").testLookup("b", "with_args_and_kwargs(foo, args(), kwargs())");
    }

    @Test
    public void testStructAccessOfMethod() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testIfExactError("object of type 'Mock' has no field 'function', however, a method of that name exists", "v = mock.function");
    }

    @Test
    public void testStructAccessTypo() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.MockClassObject()).testIfExactError("object of type 'MockClassObject' has no field 'fild' (did you mean 'field'?)", "mock.fild");
    }

    @Test
    public void testStructAccessType_nonClassObject() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testIfExactError("object of type 'Mock' has no field 'sturct_field' (did you mean 'struct_field'?)", "v = mock.sturct_field");
    }

    @Test
    public void testJavaFunctionReturnsMutableObject() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testIfExactError("method 'return_bad' returns an object of invalid type Bad", "mock.return_bad()");
    }

    @Test
    public void testJavaFunctionReturnsNullFails() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testIfErrorContains(("method invocation returned None," + " please file a bug report: nullfunc_failing(\"abc\", 1)"), "mock.nullfunc_failing('abc', 1)");
    }

    @Test
    public void testClassObjectAccess() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.MockClassObject()).setUp("v = mock.field").testLookup("v", "a");
    }

    @Test
    public void testInSet() throws Exception {
        new EvaluationTestCase.SkylarkTest().testStatement("'b' in depset(['a', 'b'])", Boolean.TRUE).testStatement("'c' in depset(['a', 'b'])", Boolean.FALSE).testStatement("1 in depset(['a', 'b'])", Boolean.FALSE);
    }

    @Test
    public void testUnionSet() throws Exception {
        new EvaluationTestCase.SkylarkTest().testStatement("str(depset([1, 3]) | depset([1, 2]))", "depset([1, 2, 3])").testStatement("str(depset([1, 2]) | [1, 3])", "depset([1, 2, 3])").testIfExactError("unsupported operand type(s) for |: 'int' and 'int'", "2 | 4");
    }

    @Test
    public void testSetIsNotIterable() throws Exception {
        new EvaluationTestCase.SkylarkTest("--incompatible_depset_is_not_iterable=true").testIfErrorContains("not iterable", "list(depset(['a', 'b']))").testIfErrorContains("not iterable", "max(depset([1, 2, 3]))").testIfErrorContains("not iterable", "1 in depset([1, 2, 3])").testIfErrorContains("not iterable", "sorted(depset(['a', 'b']))").testIfErrorContains("not iterable", "tuple(depset(['a', 'b']))").testIfErrorContains("not iterable", "[x for x in depset()]").testIfErrorContains("not iterable", "len(depset(['a']))");
    }

    @Test
    public void testSetIsIterable() throws Exception {
        new EvaluationTestCase.SkylarkTest("--incompatible_depset_is_not_iterable=false").testStatement("str(list(depset(['a', 'b'])))", "[\"a\", \"b\"]").testStatement("max(depset([1, 2, 3]))", 3).testStatement("1 in depset([1, 2, 3])", true).testStatement("str(sorted(depset(['b', 'a'])))", "[\"a\", \"b\"]").testStatement("str(tuple(depset(['a', 'b'])))", "(\"a\", \"b\")").testStatement("str([x for x in depset()])", "[]").testStatement("len(depset(['a']))", 1);
    }

    @Test
    public void testClassObjectCannotAccessNestedSet() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.MockClassObject()).testIfErrorContains("internal error: type 'NestedSet' is not allowed", "v = mock.nset");
    }

    @Test
    public void testJavaFunctionReturnsNone() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("v = mock.nullfunc_working()").testLookup("v", NONE);
    }

    @Test
    public void testVoidJavaFunctionReturnsNone() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("v = mock.voidfunc()").testLookup("v", NONE);
    }

    @Test
    public void testAugmentedAssignment() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def f1(x):", "  x += 1", "  return x", "", "foo = f1(41)").testLookup("foo", 42);
    }

    @Test
    public void testAugmentedAssignmentHasNoSideEffects() throws Exception {
        // Check object position.
        // `f()` should be called only once here
        new EvaluationTestCase.SkylarkTest().setUp("counter = [0]", "value = [1, 2]", "", "def f():", "  counter[0] = counter[0] + 1", "  return value", "", "f()[1] += 1").testLookup("counter", MutableList.of(env, 1));
        // Check key position.
        // `f()` should be called only once here
        new EvaluationTestCase.SkylarkTest().setUp("counter = [0]", "value = [1, 2]", "", "def f():", "  counter[0] = counter[0] + 1", "  return 1", "", "value[f()] += 1").testLookup("counter", MutableList.of(env, 1));
    }

    @Test
    public void testInvalidAugmentedAssignment_ListLiteral() throws Exception {
        new EvaluationTestCase.SkylarkTest().testIfErrorContains("cannot perform augmented assignment on a list or tuple expression", "def f(a, b):", "  [a, b] += []", "f(1, 2)");
    }

    @Test
    public void testInvalidAugmentedAssignment_NotAnLValue() throws Exception {
        newTest().testIfErrorContains("cannot assign to 'x + 1'", "x + 1 += 2");
    }

    @Test
    public void testAssignmentEvaluationOrder() throws Exception {
        // This order is consistent
        new EvaluationTestCase.SkylarkTest().setUp("ordinary = []", "augmented = []", "value = [1, 2]", "", "def f(record):", "  record.append('f')", "  return value", "", "def g(record):", "  record.append('g')", "  return value", "", "f(ordinary)[0] = g(ordinary)[1]", "f(augmented)[0] += g(augmented)[1]").testLookup("ordinary", MutableList.of(env, "g", "f")).testLookup("augmented", MutableList.of(env, "f", "g"));// with Python

    }

    @Test
    public void testDictComprehensions_IterationOrder() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo():", "  d = {x : x for x in ['c', 'a', 'b']}", "  s = ''", "  for a in d:", "    s += a", "  return s", "s = foo()").testLookup("s", "cab");
    }

    @Test
    public void testDotExpressionOnNonStructObject() throws Exception {
        new EvaluationTestCase.SkylarkTest().testIfExactError("object of type 'string' has no field 'field'", "x = 'a'.field");
    }

    @Test
    public void testPlusEqualsOnListMutating() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def func():", "  l1 = [1, 2]", "  l2 = l1", "  l2 += [3, 4]", "  return l1, l2", "lists = str(func())").testLookup("lists", "([1, 2, 3, 4], [1, 2, 3, 4])");
        // The same but with += after an IndexExpression
        new EvaluationTestCase.SkylarkTest().setUp("def func():", "  l = [1, 2]", "  d = {0: l}", "  d[0] += [3, 4]", "  return l, d[0]", "lists = str(func())").testLookup("lists", "([1, 2, 3, 4], [1, 2, 3, 4])");
    }

    @Test
    public void testPlusEqualsOnTuple() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def func():", "  t1 = (1, 2)", "  t2 = t1", "  t2 += (3, 4)", "  return t1, t2", "tuples = func()").testLookup("tuples", Tuple.of(Tuple.of(1, 2), Tuple.of(1, 2, 3, 4)));
    }

    @Test
    public void testPlusEqualsOnDict() throws Exception {
        new EvaluationTestCase.SkylarkTest("--incompatible_disallow_dict_plus=false").setUp("def func():", "  d = {'a' : 1}", "  d += {'b' : 2}", "  return d", "d = func()").testLookup("d", ImmutableMap.of("a", 1, "b", 2));
    }

    @Test
    public void testPlusOnDictDeprecated() throws Exception {
        new EvaluationTestCase.SkylarkTest("--incompatible_disallow_dict_plus=true").testIfErrorContains("The `+` operator for dicts is deprecated and no longer supported.", "{1: 2} + {3: 4}");
        new EvaluationTestCase.SkylarkTest("--incompatible_disallow_dict_plus=true").testIfErrorContains("The `+` operator for dicts is deprecated and no longer supported.", "def func():", "  d = {1: 2}", "  d += {3: 4}", "func()");
    }

    @Test
    public void testDictAssignmentAsLValue() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def func():", "  d = {'a' : 1}", "  d['b'] = 2", "  return d", "d = func()").testLookup("d", ImmutableMap.of("a", 1, "b", 2));
    }

    @Test
    public void testNestedDictAssignmentAsLValue() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def func():", "  d = {'a' : 1}", "  e = {'d': d}", "  e['d']['b'] = 2", "  return e", "e = func()").testLookup("e", ImmutableMap.of("d", ImmutableMap.of("a", 1, "b", 2)));
    }

    @Test
    public void testListAssignmentAsLValue() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def func():", "  a = [1, 2]", "  a[1] = 3", "  a[-2] = 4", "  return a", "a = str(func())").testLookup("a", "[4, 3]");
    }

    @Test
    public void testNestedListAssignmentAsLValue() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def func():", "  d = [1, 2]", "  e = [3, d]", "  e[1][1] = 4", "  return e", "e = str(func())").testLookup("e", "[3, [1, 4]]");
    }

    @Test
    public void testDictTupleAssignmentAsLValue() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def func():", "  d = {'a' : 1}", "  d['b'], d['c'] = 2, 3", "  return d", "d = func()").testLookup("d", ImmutableMap.of("a", 1, "b", 2, "c", 3));
    }

    @Test
    public void testDictItemPlusEqual() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def func():", "  d = {'a' : 2}", "  d['a'] += 3", "  return d", "d = func()").testLookup("d", ImmutableMap.of("a", 5));
    }

    @Test
    public void testDictAssignmentAsLValueSideEffects() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def func(d):", "  d['b'] = 2", "d = {'a' : 1}", "func(d)").testLookup("d", SkylarkDict.of(null, "a", 1, "b", 2));
    }

    @Test
    public void testAssignmentToListInDictSideEffect() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("l = [1, 2]", "d = {0: l}", "d[0].append(3)").testLookup("l", MutableList.of(null, 1, 2, 3));
    }

    @Test
    public void testUserFunctionKeywordArgs() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo(a, b, c):", "  return a + b + c", "s = foo(1, c=2, b=3)").testLookup("s", 6);
    }

    @Test
    public void testFunctionCallOrdering() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def func(): return foo() * 2", "def foo(): return 2", "x = func()").testLookup("x", 4);
    }

    @Test
    public void testFunctionCallBadOrdering() throws Exception {
        new EvaluationTestCase.SkylarkTest().testIfErrorContains("global variable 'foo' is referenced before assignment.", "def func(): return foo() * 2", "x = func()", "def foo(): return 2");
    }

    @Test
    public void testLocalVariableDefinedBelow() throws Exception {
        // returns the value before the first even number
        new EvaluationTestCase.SkylarkTest().setUp("def beforeEven(li):", "    for i in li:", "        if i % 2 == 0:", "            return a", "        else:", "            a = i", "res = beforeEven([1, 3, 4, 5])").testLookup("res", 3);
    }

    @Test
    public void testShadowisNotInitialized() throws Exception {
        /* error message */
        new EvaluationTestCase.SkylarkTest().testIfErrorContains("local variable 'gl' is referenced before assignment", "gl = 5", "def foo():", "    if False: gl = 2", "    return gl", "res = foo()");
    }

    @Test
    public void testShadowBuiltin() throws Exception {
        new EvaluationTestCase.SkylarkTest().testIfErrorContains("global variable 'len' is referenced before assignment", "x = len('abc')", "len = 2", "y = x + len");
    }

    @Test
    public void testFunctionCallRecursion() throws Exception {
        new EvaluationTestCase.SkylarkTest().testIfErrorContains("Recursion was detected when calling 'f' from 'g'", "def main():", "  f(5)", "def f(n):", "  if n > 0: g(n - 1)", "def g(n):", "  if n > 0: f(n - 1)", "main()");
    }

    @Test
    public void testTypo() throws Exception {
        new EvaluationTestCase.SkylarkTest().testIfErrorContains("name 'my_variable' is not defined (did you mean 'myVariable'?)", "myVariable = 2", "x = my_variable + 1");
    }

    @Test
    public void testNoneTrueFalseInSkylark() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("a = None", "b = True", "c = False").testLookup("a", NONE).testLookup("b", Boolean.TRUE).testLookup("c", Boolean.FALSE);
    }

    @Test
    public void testHasattrMethods() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("a = hasattr(mock, 'struct_field')", "b = hasattr(mock, 'function')", "c = hasattr(mock, 'is_empty')", "d = hasattr('str', 'replace')", "e = hasattr(mock, \'other\')\n").testLookup("a", Boolean.TRUE).testLookup("b", Boolean.TRUE).testLookup("c", Boolean.TRUE).testLookup("d", Boolean.TRUE).testLookup("e", Boolean.FALSE);
    }

    @Test
    public void testGetattrMethods() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).setUp("a = getattr(mock, 'struct_field', 'no')", "b = getattr(mock, 'function', 'no')", "c = getattr(mock, 'is_empty', 'no')", "d = getattr('str', 'replace', 'no')", "e = getattr(mock, \'other\', \'no\')\n").testLookup("a", "a").testLookup("b", "no").testLookup("c", "no").testLookup("d", "no").testLookup("e", "no");
    }

    @Test
    public void testListAnTupleConcatenationDoesNotWorkInSkylark() throws Exception {
        new EvaluationTestCase.SkylarkTest().testIfExactError("unsupported operand type(s) for +: 'list' and 'tuple'", "[1, 2] + (3, 4)");
    }

    @Test
    public void testCannotCreateMixedListInSkylark() throws Exception {
        new EvaluationTestCase.SkylarkTest().testExactOrder("['a', 'b', 1, 2]", "a", "b", 1, 2);
    }

    @Test
    public void testCannotConcatListInSkylarkWithDifferentGenericTypes() throws Exception {
        new EvaluationTestCase.SkylarkTest().testExactOrder("[1, 2] + ['a', 'b']", 1, 2, "a", "b");
    }

    @Test
    public void testConcatEmptyListWithNonEmptyWorks() throws Exception {
        new EvaluationTestCase.SkylarkTest().testExactOrder("[] + ['a', 'b']", "a", "b");
    }

    @Test
    public void testFormatStringWithTuple() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("v = '%s%s' % ('a', 1)").testLookup("v", "a1");
    }

    @Test
    public void testSingletonTuple() throws Exception {
        new EvaluationTestCase.SkylarkTest().testExactOrder("(1,)", 1);
    }

    @Test
    public void testDirFindsClassObjectFields() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.MockClassObject()).testExactOrder("dir(mock)", "field", "nset");
    }

    @Test
    public void testDirFindsJavaObjectStructFieldsAndMethods() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.Mock()).testExactOrder("dir(mock)", "function", "interrupted_struct_field", "is_empty", "legacy_method", "nullfunc_failing", "nullfunc_working", "proxy_methods_object", "return_bad", "string", "string_list", "string_list_dict", "struct_field", "struct_field_callable", "struct_field_with_extra", "value_of", "voidfunc", "with_args_and_env", "with_args_and_kwargs", "with_extra", "with_kwargs", "with_params", "with_params_and_extra");
    }

    @Test
    public void testStrNativeInfo() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.NativeInfoMock()).testEval("str(mock)", ("'struct(struct_field_callable = <built-in function foobar>, struct_field_none = None, " + "struct_field_string = \"a\")\'"));
    }

    @Test
    public void testDirNativeInfo() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("mock", new SkylarkEvaluationTest.NativeInfoMock()).testEval("dir(mock)", ("['callable_string', 'struct_field_callable', 'struct_field_none', " + "'struct_field_string', 'to_json', 'to_proto']"));
    }

    @Test
    public void testPrint() throws Exception {
        // TODO(fwe): cannot be handled by current testing suite
        setFailFast(false);
        eval("print('hello')");
        assertContainsDebug("hello");
        eval("print('a', 'b')");
        assertContainsDebug("a b");
        eval("print('a', 'b', sep='x')");
        assertContainsDebug("axb");
    }

    @Test
    public void testPrintBadKwargs() throws Exception {
        new EvaluationTestCase.SkylarkTest().testIfExactError("unexpected keywords \'end\', \'other\' in call to print(*args, sep: string = \" \")", "print(end='x', other='y')");
    }

    // Override tests in EvaluationTest incompatible with Skylark
    @SuppressWarnings("unchecked")
    @Override
    @Test
    public void testConcatLists() throws Exception {
        new EvaluationTestCase.SkylarkTest().testExactOrder("[1,2] + [3,4]", 1, 2, 3, 4).testExactOrder("(1,2)", 1, 2).testExactOrder("(1,2) + (3,4)", 1, 2, 3, 4);
        // TODO(fwe): cannot be handled by current testing suite
        // list
        Object x = eval("[1,2] + [3,4]");
        assertThat(((Iterable<Object>) (x))).containsExactly(1, 2, 3, 4).inOrder();
        // tuple
        x = eval("(1,2)");
        assertThat(((Iterable<Object>) (x))).containsExactly(1, 2).inOrder();
        assertThat(isTuple()).isTrue();
        x = eval("(1,2) + (3,4)");
        assertThat(((Iterable<Object>) (x))).containsExactly(1, 2, 3, 4).inOrder();
        assertThat(isTuple()).isTrue();
    }

    @Override
    @Test
    public void testListComprehensionsMultipleVariablesFail() throws Exception {
        new EvaluationTestCase.SkylarkTest().testIfErrorContains(("assignment length mismatch: left-hand side has length 3, but right-hand side " + "evaluates to value of length 2"), "def foo (): return [x + y for x, y, z in [(1, 2), (3, 4)]]", "foo()");
        new EvaluationTestCase.SkylarkTest().testIfErrorContains("type 'int' is not a collection", "def bar (): return [x + y for x, y in (1, 2)]", "bar()");
        new EvaluationTestCase.SkylarkTest().testIfErrorContains(("assignment length mismatch: left-hand side has length 3, but right-hand side " + "evaluates to value of length 2"), "[x + y for x, y, z in [(1, 2), (3, 4)]]");
        new EvaluationTestCase.SkylarkTest().testIfErrorContains("type 'int' is not a collection", "[x2 + y2 for x2, y2 in (1, 2)]");
        // returns [2] in Python, it's an error in Skylark
        new EvaluationTestCase.SkylarkTest().testIfErrorContains("must have at least one item", "[2 for [] in [()]]");
    }

    @Override
    @Test
    public void testNotCallInt() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("sum = 123456").testLookup("sum", 123456).testIfExactError("'int' object is not callable", "sum(1, 2, 3, 4, 5, 6)").testStatement("sum", 123456);
    }

    @Test
    public void testConditionalExpressionAtToplevel() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("x = 1 if 2 else 3").testLookup("x", 1);
    }

    @Test
    public void testConditionalExpressionInFunction() throws Exception {
        new EvaluationTestCase.SkylarkTest().setUp("def foo(a, b, c): return a+b if c else a-b\n").testStatement("foo(23, 5, 0)", 18);
    }

    @SkylarkModule(name = "SkylarkClassObjectWithSkylarkCallables", doc = "")
    static final class SkylarkClassObjectWithSkylarkCallables extends NativeInfo {
        private static final NativeProvider<SkylarkEvaluationTest.SkylarkClassObjectWithSkylarkCallables> CONSTRUCTOR = new NativeProvider<SkylarkEvaluationTest.SkylarkClassObjectWithSkylarkCallables>(SkylarkEvaluationTest.SkylarkClassObjectWithSkylarkCallables.class, "struct_with_skylark_callables") {};

        SkylarkClassObjectWithSkylarkCallables() {
            super(SkylarkEvaluationTest.SkylarkClassObjectWithSkylarkCallables.CONSTRUCTOR, ImmutableMap.of("values_only_field", "fromValues", "values_only_method", new BuiltinFunction("values_only_method", FunctionSignature.of()) {
                public String invoke() {
                    return "fromValues";
                }
            }, "collision_field", "fromValues", "collision_method", new BuiltinFunction("collision_method", FunctionSignature.of()) {
                public String invoke() {
                    return "fromValues";
                }
            }), BUILTIN);
        }

        @SkylarkCallable(name = "callable_only_field", documented = false, structField = true)
        public String getCallableOnlyField() {
            return "fromSkylarkCallable";
        }

        @SkylarkCallable(name = "callable_only_method", documented = false, structField = false)
        public String getCallableOnlyMethod() {
            return "fromSkylarkCallable";
        }

        @SkylarkCallable(name = "collision_field", documented = false, structField = true)
        public String getCollisionField() {
            return "fromSkylarkCallable";
        }

        @SkylarkCallable(name = "collision_method", documented = false, structField = false)
        public String getCollisionMethod() {
            return "fromSkylarkCallable";
        }
    }

    @Test
    public void testStructFieldDefinedOnlyInValues() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("val", new SkylarkEvaluationTest.SkylarkClassObjectWithSkylarkCallables()).setUp("v = val.values_only_field").testLookup("v", "fromValues");
    }

    @Test
    public void testStructMethodDefinedOnlyInValues() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("val", new SkylarkEvaluationTest.SkylarkClassObjectWithSkylarkCallables()).setUp("v = val.values_only_method()").testLookup("v", "fromValues");
    }

    @Test
    public void testStructFieldDefinedOnlyInSkylarkCallable() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("val", new SkylarkEvaluationTest.SkylarkClassObjectWithSkylarkCallables()).setUp("v = val.callable_only_field").testLookup("v", "fromSkylarkCallable");
    }

    @Test
    public void testStructMethodDefinedOnlyInSkylarkCallable() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("val", new SkylarkEvaluationTest.SkylarkClassObjectWithSkylarkCallables()).setUp("v = val.callable_only_method()").testLookup("v", "fromSkylarkCallable");
    }

    @Test
    public void testStructMethodDefinedInValuesAndSkylarkCallable() throws Exception {
        new EvaluationTestCase.SkylarkTest().update("val", new SkylarkEvaluationTest.SkylarkClassObjectWithSkylarkCallables()).setUp("v = val.collision_method()").testLookup("v", "fromSkylarkCallable");
    }

    @Test
    public void testStructFieldNotDefined() throws Exception {
        // TODO(bazel-team): This should probably list callable_only_method as well.
        new EvaluationTestCase.SkylarkTest().update("val", new SkylarkEvaluationTest.SkylarkClassObjectWithSkylarkCallables()).testIfExactError(("\'struct_with_skylark_callables\' object has no attribute \'nonexistent_field\'\n" + ("Available attributes: callable_only_field, collision_field, collision_method, " + "values_only_field, values_only_method")), "v = val.nonexistent_field");
    }

    @Test
    public void testStructMethodNotDefined() throws Exception {
        // TODO(bazel-team): This should probably match the error above better.
        new EvaluationTestCase.SkylarkTest().update("val", new SkylarkEvaluationTest.SkylarkClassObjectWithSkylarkCallables()).testIfExactError("type 'SkylarkClassObjectWithSkylarkCallables' has no method nonexistent_method()", "v = val.nonexistent_method()");
    }

    @Test
    public void testListComprehensionsDoNotLeakVariables() throws Exception {
        // TODO(laurentlb): This happens because the variable gets undefined after the list
        // comprehension. We should do better.
        checkEvalErrorContains("local variable 'a' is referenced before assignment.", "def foo():", "  a = 10", "  b = [a for a in range(3)]", "  return a", "x = foo()");
    }

    @Test
    public void testListComprehensionsShadowGlobalVariable() throws Exception {
        eval("a = 18", "def foo():", "  b = [a for a in range(3)]", "  return a", "x = foo()");
        assertThat(lookup("x")).isEqualTo(18);
    }

    @Test
    public void testLoadStatementWithAbsolutePath() throws Exception {
        checkEvalErrorContains("First argument of 'load' must be a label and start with either '//', ':', or '@'", "load('/tmp/foo', 'arg')");
    }

    @Test
    public void testLoadStatementWithRelativePath() throws Exception {
        checkEvalErrorContains("First argument of 'load' must be a label and start with either '//', ':', or '@'", "load('foo', 'arg')");
    }

    @Test
    public void testLoadStatementWithExternalLabel() throws Exception {
        checkEvalErrorDoesNotContain("First argument of 'load' must be a label and start with either '//', ':', or '@'", "load('@other//foo.bzl', 'arg')");
    }

    @Test
    public void testLoadStatementWithAbsoluteLabel() throws Exception {
        checkEvalErrorDoesNotContain("First argument of 'load' must be a label and start with either '//', ':', or '@'", "load('//foo.bzl', 'arg')");
    }

    @Test
    public void testLoadStatementWithRelativeLabel() throws Exception {
        checkEvalErrorDoesNotContain("First argument of 'load' must be a label and start with either '//', ':', or '@'", "load(':foo.bzl', 'arg')");
    }

    @Test
    public void testAnalysisFailureInfo() throws Exception {
        AnalysisFailure cause = new AnalysisFailure(Label.create("test", "test"), "ErrorMessage");
        AnalysisFailureInfo info = AnalysisFailureInfo.forAnalysisFailures(ImmutableList.of(cause));
        new EvaluationTestCase.SkylarkTest().update("val", info).setUp("causes = val.causes", "label = causes.to_list()[0].label", "message = causes.to_list()[0].message").testLookup("label", Label.create("test", "test")).testLookup("message", "ErrorMessage");
    }

    @Test
    public void testExperimentalFlagGuardedValue() throws Exception {
        // This test uses an arbitrary experimental flag to verify this functionality. If this
        // experimental flag were to go away, this test may be updated to use any experimental flag.
        // The flag itself is unimportant to the test.
        FlagGuardedValue val = FlagGuardedValue.onlyWhenExperimentalFlagIsTrue(EXPERIMENTAL_BUILD_SETTING_API, "foo");
        String errorMessage = "GlobalSymbol is experimental and thus unavailable with the current " + "flags. It may be enabled by setting --experimental_build_setting_api";
        new EvaluationTestCase.SkylarkTest(ImmutableMap.of("GlobalSymbol", val), "--experimental_build_setting_api=true").setUp("var = GlobalSymbol").testLookup("var", "foo");
        new EvaluationTestCase.SkylarkTest(ImmutableMap.of("GlobalSymbol", val), "--experimental_build_setting_api=false").testIfErrorContains(errorMessage, "var = GlobalSymbol");
        new EvaluationTestCase.SkylarkTest(ImmutableMap.of("GlobalSymbol", val), "--experimental_build_setting_api=false").testIfErrorContains(errorMessage, "def my_function():", "  var = GlobalSymbol");
        new EvaluationTestCase.SkylarkTest(ImmutableMap.of("GlobalSymbol", val), "--experimental_build_setting_api=false").setUp("GlobalSymbol = 'other'", "var = GlobalSymbol").testLookup("var", "other");
    }

    @Test
    public void testIncompatibleFlagGuardedValue() throws Exception {
        // This test uses an arbitrary incompatible flag to verify this functionality. If this
        // incompatible flag were to go away, this test may be updated to use any incompatible flag.
        // The flag itself is unimportant to the test.
        FlagGuardedValue val = FlagGuardedValue.onlyWhenIncompatibleFlagIsFalse(INCOMPATIBLE_NO_TARGET_OUTPUT_GROUP, "foo");
        String errorMessage = "GlobalSymbol is deprecated and will be removed soon. It may be " + "temporarily re-enabled by setting --incompatible_no_target_output_group=false";
        new EvaluationTestCase.SkylarkTest(ImmutableMap.of("GlobalSymbol", val), "--incompatible_no_target_output_group=false").setUp("var = GlobalSymbol").testLookup("var", "foo");
        new EvaluationTestCase.SkylarkTest(ImmutableMap.of("GlobalSymbol", val), "--incompatible_no_target_output_group=true").testIfErrorContains(errorMessage, "var = GlobalSymbol");
        new EvaluationTestCase.SkylarkTest(ImmutableMap.of("GlobalSymbol", val), "--incompatible_no_target_output_group=true").testIfErrorContains(errorMessage, "def my_function():", "  var = GlobalSymbol");
        new EvaluationTestCase.SkylarkTest(ImmutableMap.of("GlobalSymbol", val), "--incompatible_no_target_output_group=true").setUp("GlobalSymbol = 'other'", "var = GlobalSymbol").testLookup("var", "other");
    }
}

