/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.skylark;


import AdvertisedProviderSet.ANY;
import AspectParameters.EMPTY;
import BuildType.LABEL_LIST;
import BuildType.NODEP_LABEL_LIST;
import BuildType.OUTPUT;
import BuildType.OUTPUT_LIST;
import ExecutionPlatformConstraintsAllowed.PER_TARGET;
import FileTypeSet.ANY_FILE;
import NoTransition.INSTANCE;
import RuleClass.ConfiguredTargetFactory;
import StarlarkSemantics.DEFAULT_SEMANTICS;
import StructProvider.STRUCT;
import Type.INTEGER;
import Type.INTEGER_LIST;
import Type.STRING;
import Type.STRING_DICT;
import Type.STRING_LIST;
import Type.STRING_LIST_DICT;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.analysis.skylark.SkylarkAttr;
import com.google.devtools.build.lib.analysis.skylark.SkylarkAttr.Descriptor;
import com.google.devtools.build.lib.analysis.skylark.SkylarkRuleContext;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.packages.AdvertisedProviderSet;
import com.google.devtools.build.lib.packages.Attribute;
import com.google.devtools.build.lib.packages.ImplicitOutputsFunction;
import com.google.devtools.build.lib.packages.PredicateWithMessage;
import com.google.devtools.build.lib.packages.RequiredProviders;
import com.google.devtools.build.lib.packages.RuleClass;
import com.google.devtools.build.lib.packages.SkylarkDefinedAspect;
import com.google.devtools.build.lib.packages.SkylarkInfo;
import com.google.devtools.build.lib.packages.SkylarkProvider;
import com.google.devtools.build.lib.packages.StructImpl;
import com.google.devtools.build.lib.skylark.util.SkylarkTestCase;
import com.google.devtools.build.lib.syntax.ClassObject;
import com.google.devtools.build.lib.syntax.EvalException;
import com.google.devtools.build.lib.syntax.EvalUtils;
import com.google.devtools.build.lib.syntax.SkylarkList.MutableList;
import com.google.devtools.build.lib.syntax.SkylarkList.Tuple;
import com.google.devtools.build.lib.syntax.StarlarkSemantics;
import com.google.devtools.build.lib.syntax.util.EvaluationTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import java.util.Collection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for SkylarkRuleClassFunctions.
 */
@RunWith(JUnit4.class)
public class SkylarkRuleClassFunctionsTest extends SkylarkTestCase {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCannotOverrideBuiltInAttribute() throws Exception {
        ev.setFailFast(false);
        evalAndExport("def impl(ctx):", "  return", "r = rule(impl, attrs = {'tags': attr.string_list()})");
        ev.assertContainsError("There is already a built-in attribute 'tags' which cannot be overridden");
    }

    @Test
    public void testCannotOverrideBuiltInAttributeName() throws Exception {
        ev.setFailFast(false);
        evalAndExport("def impl(ctx):", "  return", "r = rule(impl, attrs = {'name': attr.string()})");
        ev.assertContainsError("There is already a built-in attribute 'name' which cannot be overridden");
    }

    @Test
    public void testImplicitArgsAttribute() throws Exception {
        ev.setFailFast(false);
        evalAndExport("def _impl(ctx):", "  pass", "exec_rule = rule(implementation = _impl, executable = True)", "non_exec_rule = rule(implementation = _impl)");
        assertThat(getRuleClass("exec_rule").hasAttr("args", STRING_LIST)).isTrue();
        assertThat(getRuleClass("non_exec_rule").hasAttr("args", STRING_LIST)).isFalse();
    }

    @Test
    public void testAttrWithOnlyType() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.string_list()");
        assertThat(attr.getType()).isEqualTo(STRING_LIST);
    }

    @Test
    public void testOutputListAttr() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.output_list()");
        assertThat(attr.getType()).isEqualTo(OUTPUT_LIST);
    }

    @Test
    public void testIntListAttr() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.int_list()");
        assertThat(attr.getType()).isEqualTo(INTEGER_LIST);
    }

    @Test
    public void testOutputAttr() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.output()");
        assertThat(attr.getType()).isEqualTo(OUTPUT);
    }

    @Test
    public void testStringDictAttr() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.string_dict(default = {'a': 'b'})");
        assertThat(attr.getType()).isEqualTo(STRING_DICT);
    }

    @Test
    public void testStringListDictAttr() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.string_list_dict(default = {'a': ['b', 'c']})");
        assertThat(attr.getType()).isEqualTo(STRING_LIST_DICT);
    }

    @Test
    public void testAttrAllowedFileTypesAnyFile() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.label_list(allow_files = True)");
        assertThat(attr.getAllowedFileTypesPredicate()).isEqualTo(ANY_FILE);
    }

    @Test
    public void testAttrAllowedFileTypesWrongType() throws Exception {
        checkErrorContains("allow_files should be a boolean or a string list", "attr.label_list(allow_files = 18)");
    }

    @Test
    public void testDisableDeprecatedParams() throws Exception {
        ev = createEvaluationTestCase(DEFAULT_SEMANTICS.toBuilder().incompatibleDisableDeprecatedAttrParams(true).build());
        ev.initialize();
        // Verify 'single_file' deprecation.
        EvalException expected = MoreAsserts.assertThrows(EvalException.class, () -> eval("attr.label(single_file = True)"));
        assertThat(expected).hasMessageThat().contains("'single_file' is no longer supported. use allow_single_file instead.");
        Attribute attr = buildAttribute("a1", "attr.label(allow_single_file = ['.xml'])");
        assertThat(attr.isSingleArtifact()).isTrue();
        // Verify 'non_empty' deprecation.
        expected = MoreAsserts.assertThrows(EvalException.class, () -> eval("attr.string_list(non_empty=True)"));
        assertThat(expected).hasMessageThat().contains("'non_empty' is no longer supported. use allow_empty instead.");
        attr = buildAttribute("a2", "attr.string_list(allow_empty=False)");
        assertThat(attr.isNonEmpty()).isTrue();
    }

    @Test
    public void testAttrAllowedSingleFileTypesWrongType() throws Exception {
        checkErrorContains("allow_single_file should be a boolean or a string list", "attr.label(allow_single_file = 18)");
    }

    @Test
    public void testAttrWithList() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.label_list(allow_files = ['.xml'])");
        assertThat(attr.getAllowedFileTypesPredicate().apply("a.xml")).isTrue();
        assertThat(attr.getAllowedFileTypesPredicate().apply("a.txt")).isFalse();
        assertThat(attr.isSingleArtifact()).isFalse();
    }

    @Test
    public void testAttrSingleFileWithList() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.label(allow_single_file = ['.xml'])");
        assertThat(attr.getAllowedFileTypesPredicate().apply("a.xml")).isTrue();
        assertThat(attr.getAllowedFileTypesPredicate().apply("a.txt")).isFalse();
        assertThat(attr.isSingleArtifact()).isTrue();
    }

    @Test
    public void testAttrWithProviders() throws Exception {
        Attribute attr = buildAttribute("a1", "b = provider()", "attr.label_list(allow_files = True, providers = ['a', b])");
        assertThat(attr.getRequiredProviders().isSatisfiedBy(SkylarkRuleClassFunctionsTest.set(SkylarkRuleClassFunctionsTest.legacy("a"), SkylarkRuleClassFunctionsTest.declared("b")))).isTrue();
        assertThat(attr.getRequiredProviders().isSatisfiedBy(SkylarkRuleClassFunctionsTest.set(SkylarkRuleClassFunctionsTest.legacy("a")))).isFalse();
    }

    @Test
    public void testAttrWithProvidersOneEmpty() throws Exception {
        Attribute attr = buildAttribute("a1", "b = provider()", "attr.label_list(allow_files = True, providers = [['a', b],[]])");
        assertThat(attr.getRequiredProviders().acceptsAny()).isTrue();
    }

    @Test
    public void testAttrWithProvidersList() throws Exception {
        Attribute attr = buildAttribute("a1", "b = provider()", "attr.label_list(allow_files = True, providers = [['a', b], ['c']])");
        assertThat(attr.getRequiredProviders().isSatisfiedBy(SkylarkRuleClassFunctionsTest.set(SkylarkRuleClassFunctionsTest.legacy("a"), SkylarkRuleClassFunctionsTest.declared("b")))).isTrue();
        assertThat(attr.getRequiredProviders().isSatisfiedBy(SkylarkRuleClassFunctionsTest.set(SkylarkRuleClassFunctionsTest.legacy("c")))).isTrue();
        assertThat(attr.getRequiredProviders().isSatisfiedBy(SkylarkRuleClassFunctionsTest.set(SkylarkRuleClassFunctionsTest.legacy("a")))).isFalse();
    }

    @Test
    public void testAttrWithWrongProvidersList() throws Exception {
        checkAttributeError(("element in 'providers' is of unexpected type. Either all elements should be providers," + (" or all elements should be lists of providers," + " but got list with an element of type int.")), "attr.label_list(allow_files = True,  providers = [['a', 1], ['c']])");
        checkAttributeError(("element in 'providers' is of unexpected type. Either all elements should be providers," + (" or all elements should be lists of providers," + " but got an element of type string.")), "b = provider()", "attr.label_list(allow_files = True,  providers = [['a', b], 'c'])");
        checkAttributeError(("element in 'providers' is of unexpected type. Either all elements should be providers," + (" or all elements should be lists of providers," + " but got an element of type string.")), "c = provider()", "attr.label_list(allow_files = True,  providers = [['a', b], c])");
    }

    @Test
    public void testLabelListWithAspects() throws Exception {
        evalAndExport("def _impl(target, ctx):", "   pass", "my_aspect = aspect(implementation = _impl)", "a = attr.label_list(aspects = [my_aspect])");
        SkylarkAttr.Descriptor attr = ((SkylarkAttr.Descriptor) (ev.lookup("a")));
        SkylarkDefinedAspect aspect = ((SkylarkDefinedAspect) (ev.lookup("my_aspect")));
        assertThat(aspect).isNotNull();
        assertThat(attr.build("xxx").getAspectClasses()).containsExactly(aspect.getAspectClass());
    }

    @Test
    public void testLabelWithAspects() throws Exception {
        evalAndExport("def _impl(target, ctx):", "   pass", "my_aspect = aspect(implementation = _impl)", "a = attr.label(aspects = [my_aspect])");
        SkylarkAttr.Descriptor attr = ((SkylarkAttr.Descriptor) (ev.lookup("a")));
        SkylarkDefinedAspect aspect = ((SkylarkDefinedAspect) (ev.lookup("my_aspect")));
        assertThat(aspect).isNotNull();
        assertThat(attr.build("xxx").getAspectClasses()).containsExactly(aspect.getAspectClass());
    }

    @Test
    public void testLabelListWithAspectsError() throws Exception {
        checkErrorContains("expected type 'Aspect' for 'aspects' element but got type 'int' instead", "def _impl(target, ctx):", "   pass", "my_aspect = aspect(implementation = _impl)", "attr.label_list(aspects = [my_aspect, 123])");
    }

    @Test
    public void testAspectExtraDeps() throws Exception {
        evalAndExport("def _impl(target, ctx):", "   pass", "my_aspect = aspect(_impl,", "   attrs = { '_extra_deps' : attr.label(default = Label('//foo/bar:baz')) }", ")");
        SkylarkDefinedAspect aspect = ((SkylarkDefinedAspect) (ev.lookup("my_aspect")));
        Attribute attribute = Iterables.getOnlyElement(aspect.getAttributes());
        assertThat(attribute.getName()).isEqualTo("$extra_deps");
        assertThat(attribute.getDefaultValue(null)).isEqualTo(/* defaultToMain= */
        /* repositoryMapping= */
        Label.parseAbsolute("//foo/bar:baz", false, ImmutableMap.of()));
    }

    @Test
    public void testAspectParameter() throws Exception {
        evalAndExport("def _impl(target, ctx):", "   pass", "my_aspect = aspect(_impl,", "   attrs = { 'param' : attr.string(values=['a', 'b']) }", ")");
        SkylarkDefinedAspect aspect = ((SkylarkDefinedAspect) (ev.lookup("my_aspect")));
        Attribute attribute = Iterables.getOnlyElement(aspect.getAttributes());
        assertThat(attribute.getName()).isEqualTo("param");
    }

    @Test
    public void testAspectParameterRequiresValues() throws Exception {
        checkErrorContains(("Aspect parameter attribute 'param' must have type 'string' and use the 'values' " + "restriction."), "def _impl(target, ctx):", "   pass", "my_aspect = aspect(_impl,", "   attrs = { 'param' : attr.string(default = 'c') }", ")");
    }

    @Test
    public void testAspectParameterBadType() throws Exception {
        checkErrorContains(("Aspect parameter attribute 'param' must have type 'string' and use the 'values' " + "restriction."), "def _impl(target, ctx):", "   pass", "my_aspect = aspect(_impl,", "   attrs = { 'param' : attr.label(default = Label('//foo/bar:baz')) }", ")");
    }

    @Test
    public void testAspectParameterAndExtraDeps() throws Exception {
        evalAndExport("def _impl(target, ctx):", "   pass", "my_aspect = aspect(_impl,", "   attrs = { 'param' : attr.string(values=['a', 'b']),", "             '_extra' : attr.label(default = Label('//foo/bar:baz')) }", ")");
        SkylarkDefinedAspect aspect = ((SkylarkDefinedAspect) (ev.lookup("my_aspect")));
        assertThat(aspect.getAttributes()).hasSize(2);
        assertThat(aspect.getParamAttributes()).containsExactly("param");
    }

    @Test
    public void testAspectNoDefaultValueAttribute() throws Exception {
        checkErrorContains("Aspect attribute '_extra_deps' has no default value", "def _impl(target, ctx):", "   pass", "my_aspect = aspect(_impl,", "   attrs = { '_extra_deps' : attr.label() }", ")");
    }

    @Test
    public void testAspectAddToolchain() throws Exception {
        scratch.file("test/BUILD", "toolchain_type(name = 'my_toolchain_type')");
        evalAndExport("def _impl(ctx): pass", "a1 = aspect(_impl, toolchains=['//test:my_toolchain_type'])");
        SkylarkDefinedAspect a = ((SkylarkDefinedAspect) (lookup("a1")));
        assertThat(a.getRequiredToolchains()).containsExactly(BuildViewTestCase.makeLabel("//test:my_toolchain_type"));
    }

    @Test
    public void testNonLabelAttrWithProviders() throws Exception {
        checkErrorContains(("unexpected keyword 'providers', for call to method " + ("string(default = '', doc = '', mandatory = False, values = []) " + "of 'attr (a language module)'")), "attr.string(providers = ['a'])");
    }

    private static final ConfiguredTargetFactory<Object, Object, Exception> DUMMY_CONFIGURED_TARGET_FACTORY = ( ruleContext) -> {
        throw new IllegalStateException();
    };

    @Test
    public void testAttrAllowedRuleClassesSpecificRuleClasses() throws Exception {
        Attribute attr = buildAttribute("a", "attr.label_list(allow_rules = ['java_binary'], allow_files = True)");
        assertThat(attr.getAllowedRuleClassesPredicate().apply(ruleClass("java_binary"))).isTrue();
        assertThat(attr.getAllowedRuleClassesPredicate().apply(ruleClass("genrule"))).isFalse();
    }

    @Test
    public void testAttrDefaultValue() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.string(default = 'some value')");
        assertThat(attr.getDefaultValueUnchecked()).isEqualTo("some value");
    }

    @Test
    public void testLabelAttrDefaultValueAsString() throws Exception {
        Attribute sligleAttr = buildAttribute("a1", "attr.label(default = '//foo:bar')");
        assertThat(sligleAttr.getDefaultValueUnchecked()).isEqualTo(/* defaultToMain= */
        /* repositoryMapping= */
        Label.parseAbsolute("//foo:bar", false, ImmutableMap.of()));
        Attribute listAttr = buildAttribute("a2", "attr.label_list(default = ['//foo:bar', '//bar:foo'])");
        assertThat(listAttr.getDefaultValueUnchecked()).isEqualTo(ImmutableList.of(/* defaultToMain= */
        /* repositoryMapping= */
        Label.parseAbsolute("//foo:bar", false, ImmutableMap.of()), /* defaultToMain= */
        /* repositoryMapping= */
        Label.parseAbsolute("//bar:foo", false, ImmutableMap.of())));
        Attribute dictAttr = buildAttribute("a3", "attr.label_keyed_string_dict(default = {'//foo:bar': 'my value'})");
        assertThat(dictAttr.getDefaultValueUnchecked()).isEqualTo(ImmutableMap.of(/* defaultToMain= */
        /* repositoryMapping= */
        Label.parseAbsolute("//foo:bar", false, ImmutableMap.of()), "my value"));
    }

    @Test
    public void testLabelAttrDefaultValueAsStringBadValue() throws Exception {
        checkErrorContains(("invalid label '/foo:bar' in parameter 'default' of attribute 'label': " + "invalid target name '/foo:bar'"), "attr.label(default = '/foo:bar')");
        checkErrorContains(("invalid label '/bar:foo' in element 1 of parameter 'default' of attribute " + "'label_list': invalid target name '/bar:foo'"), "attr.label_list(default = ['//foo:bar', '/bar:foo'])");
        checkErrorContains("invalid label '/bar:foo' in dict key element: invalid target name '/bar:foo'", "attr.label_keyed_string_dict(default = {'//foo:bar': 'a', '/bar:foo': 'b'})");
    }

    @Test
    public void testAttrDefaultValueBadType() throws Exception {
        checkErrorContains(("expected value of type 'string' for parameter 'default', for call to method " + ("string(default = '', doc = '', mandatory = False, values = []) " + "of 'attr (a language module)'")), "attr.string(default = 1)");
    }

    @Test
    public void testAttrMandatory() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.string(mandatory=True)");
        assertThat(attr.isMandatory()).isTrue();
        assertThat(attr.isNonEmpty()).isFalse();
    }

    @Test
    public void testAttrNonEmpty() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.string_list(non_empty=True)");
        assertThat(attr.isNonEmpty()).isTrue();
        assertThat(attr.isMandatory()).isFalse();
    }

    @Test
    public void testAttrAllowEmpty() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.string_list(allow_empty=False)");
        assertThat(attr.isNonEmpty()).isTrue();
        assertThat(attr.isMandatory()).isFalse();
    }

    @Test
    public void testAttrBadKeywordArguments() throws Exception {
        checkErrorContains(("unexpected keyword 'bad_keyword', for call to method " + ("string(default = '', doc = '', mandatory = False, values = []) of " + "'attr (a language module)'")), "attr.string(bad_keyword = '')");
    }

    @Test
    public void testAttrCfg() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.label(cfg = 'host', allow_files = True)");
        assertThat(attr.getConfigurationTransition().isHostTransition()).isTrue();
    }

    @Test
    public void testAttrCfgTarget() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.label(cfg = 'target', allow_files = True)");
        assertThat(attr.getConfigurationTransition()).isEqualTo(INSTANCE);
    }

    @Test
    public void incompatibleDataTransition() throws Exception {
        ev = createEvaluationTestCase(DEFAULT_SEMANTICS.toBuilder().incompatibleDisallowDataTransition(true).build());
        ev.initialize();
        EvalException expected = MoreAsserts.assertThrows(EvalException.class, () -> eval("attr.label(cfg = 'data')"));
        assertThat(expected).hasMessageThat().contains("Using cfg = \"data\" on an attribute is a noop and no longer supported");
    }

    @Test
    public void testAttrValues() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.string(values = ['ab', 'cd'])");
        PredicateWithMessage<Object> predicate = attr.getAllowedValues();
        assertThat(predicate.apply("ab")).isTrue();
        assertThat(predicate.apply("xy")).isFalse();
    }

    @Test
    public void testAttrIntValues() throws Exception {
        Attribute attr = buildAttribute("a1", "attr.int(values = [1, 2])");
        PredicateWithMessage<Object> predicate = attr.getAllowedValues();
        assertThat(predicate.apply(2)).isTrue();
        assertThat(predicate.apply(3)).isFalse();
    }

    @Test
    public void testAttrDoc() throws Exception {
        // We don't actually store the doc in the attr definition; right now it's just meant to be
        // extracted by documentation generating tools. So we don't have anything to assert and we just
        // verify that no exceptions were thrown from building them.
        buildAttribute("a1", "attr.bool(doc='foo')");
        buildAttribute("a2", "attr.int(doc='foo')");
        buildAttribute("a3", "attr.int_list(doc='foo')");
        buildAttribute("a4", "attr.label(doc='foo')");
        buildAttribute("a5", "attr.label_keyed_string_dict(doc='foo')");
        buildAttribute("a6", "attr.label_list(doc='foo')");
        buildAttribute("a7", "attr.license(doc='foo')");
        buildAttribute("a8", "attr.output(doc='foo')");
        buildAttribute("a9", "attr.output_list(doc='foo')");
        buildAttribute("a10", "attr.string(doc='foo')");
        buildAttribute("a11", "attr.string_dict(doc='foo')");
        buildAttribute("a12", "attr.string_list(doc='foo')");
        buildAttribute("a13", "attr.string_list_dict(doc='foo')");
    }

    @Test
    public void testNoAttrLicense() throws Exception {
        ev = createEvaluationTestCase(DEFAULT_SEMANTICS.toBuilder().incompatibleNoAttrLicense(true).build());
        ev.initialize();
        EvalException expected = MoreAsserts.assertThrows(EvalException.class, () -> eval("attr.license()"));
        assertThat(expected).hasMessageThat().contains("type 'attr (a language module)' has no method license()");
    }

    @Test
    public void testAttrDocValueBadType() throws Exception {
        checkErrorContains(("expected value of type 'string' for parameter 'doc', for call to method " + ("string(default = '', doc = '', mandatory = False, values = []) " + "of 'attr (a language module)'")), "attr.string(doc = 1)");
    }

    @Test
    public void testRuleImplementation() throws Exception {
        evalAndExport("def impl(ctx): return None", "rule1 = rule(impl)");
        RuleClass c = getRuleClass();
        assertThat(c.getConfiguredTargetFunction().getName()).isEqualTo("impl");
    }

    @Test
    public void testRuleDoc() throws Exception {
        evalAndExport("def impl(ctx): return None", "rule1 = rule(impl, doc='foo')");
    }

    @Test
    public void testLateBoundAttrWorksWithOnlyLabel() throws Exception {
        checkEvalError(("expected value of type 'string' for parameter 'default', for call to method " + ("string(default = '', doc = '', mandatory = False, values = []) " + "of 'attr (a language module)'")), "def attr_value(cfg): return 'a'", "attr.string(default=attr_value)");
    }

    private static final Label FAKE_LABEL = Label.parseAbsoluteUnchecked("//fake/label.bzl");

    @Test
    public void testRuleAddAttribute() throws Exception {
        evalAndExport("def impl(ctx): return None", "r1 = rule(impl, attrs={'a1': attr.string()})");
        RuleClass c = getRuleClass();
        assertThat(c.hasAttr("a1", STRING)).isTrue();
    }

    @Test
    public void testExportAliasedName() throws Exception {
        // When there are multiple names aliasing the same SkylarkExportable, the first one to be
        // declared should be used. Make sure we're not using lexicographical order, hash order,
        // non-deterministic order, or anything else.
        // Having more names improves the chance that non-determinism will be caught.
        evalAndExport("def _impl(ctx): pass", "d = rule(implementation = _impl)", "a = d", "b = d", "c = d", "e = d", "f = d", "foo = d", "bar = d", "baz = d", "x = d", "y = d", "z = d");
        String dName = ((com.google.devtools.build.lib.analysis.skylark.SkylarkRuleClassFunctions.SkylarkRuleFunction) (lookup("d"))).getRuleClass().getName();
        String fooName = ((com.google.devtools.build.lib.analysis.skylark.SkylarkRuleClassFunctions.SkylarkRuleFunction) (lookup("foo"))).getRuleClass().getName();
        assertThat(dName).isEqualTo("d");
        assertThat(fooName).isEqualTo("d");
    }

    @Test
    public void testOutputToGenfiles() throws Exception {
        evalAndExport("def impl(ctx): pass", "r1 = rule(impl, output_to_genfiles=True)");
        RuleClass c = getRuleClass();
        assertThat(c.hasBinaryOutput()).isFalse();
    }

    @Test
    public void testRuleAddMultipleAttributes() throws Exception {
        evalAndExport("def impl(ctx): return None", "r1 = rule(impl,", "     attrs = {", "            'a1': attr.label_list(allow_files=True),", "            'a2': attr.int()", "})");
        RuleClass c = getRuleClass();
        assertThat(c.hasAttr("a1", LABEL_LIST)).isTrue();
        assertThat(c.hasAttr("a2", INTEGER)).isTrue();
    }

    @Test
    public void testRuleAttributeFlag() throws Exception {
        evalAndExport("def impl(ctx): return None", "r1 = rule(impl, attrs = {'a1': attr.string(mandatory=True)})");
        RuleClass c = getRuleClass();
        assertThat(c.getAttributeByName("a1").isMandatory()).isTrue();
    }

    @Test
    public void testRuleOutputs() throws Exception {
        evalAndExport("def impl(ctx): return None", "r1 = rule(impl, outputs = {'a': 'a.txt'})");
        RuleClass c = getRuleClass();
        ImplicitOutputsFunction function = c.getDefaultImplicitOutputsFunction();
        assertThat(function.getImplicitOutputs(ev.getEventHandler(), null)).containsExactly("a.txt");
    }

    @Test
    public void testRuleUnknownKeyword() throws Exception {
        registerDummyUserDefinedFunction();
        checkErrorContains("unexpected keyword 'bad_keyword', for call to function rule(", "rule(impl, bad_keyword = 'some text')");
    }

    @Test
    public void testRuleImplementationMissing() throws Exception {
        checkErrorContains("parameter 'implementation' has no default value, for call to function rule(", "rule(attrs = {})");
    }

    @Test
    public void testRuleBadTypeForAdd() throws Exception {
        registerDummyUserDefinedFunction();
        checkErrorContains(("expected value of type 'dict or NoneType' for parameter 'attrs', " + "for call to function rule("), "rule(impl, attrs = 'some text')");
    }

    @Test
    public void testRuleBadTypeInAdd() throws Exception {
        registerDummyUserDefinedFunction();
        checkErrorContains("expected <String, Descriptor> type for 'attrs' but got <string, string> instead", "rule(impl, attrs = {'a1': 'some text'})");
    }

    @Test
    public void testRuleBadTypeForDoc() throws Exception {
        registerDummyUserDefinedFunction();
        checkErrorContains("expected value of type 'string' for parameter 'doc', for call to function rule(", "rule(impl, doc = 1)");
    }

    @Test
    public void testLabel() throws Exception {
        Object result = evalRuleClassCode("Label('//foo/foo:foo')");
        assertThat(result).isInstanceOf(Label.class);
        assertThat(result.toString()).isEqualTo("//foo/foo:foo");
    }

    @Test
    public void testLabelSameInstance() throws Exception {
        Object l1 = evalRuleClassCode("Label('//foo/foo:foo')");
        // Implicitly creates a new pkgContext and environment, yet labels should be the same.
        Object l2 = evalRuleClassCode("Label('//foo/foo:foo')");
        assertThat(l1).isSameAs(l2);
    }

    @Test
    public void testLabelNameAndPackage() throws Exception {
        Object result = evalRuleClassCode("Label('//foo/bar:baz').name");
        assertThat(result).isEqualTo("baz");
        // NB: implicitly creates a new pkgContext and environments, yet labels should be the same.
        result = evalRuleClassCode("Label('//foo/bar:baz').package");
        assertThat(result).isEqualTo("foo/bar");
    }

    @Test
    public void testRuleLabelDefaultValue() throws Exception {
        evalAndExport(("def impl(ctx): return None\n" + ("r1 = rule(impl, attrs = {'a1': " + "attr.label(default = Label('//foo:foo'), allow_files=True)})")));
        RuleClass c = getRuleClass();
        Attribute a = c.getAttributeByName("a1");
        assertThat(a.getDefaultValueUnchecked()).isInstanceOf(Label.class);
        assertThat(a.getDefaultValueUnchecked().toString()).isEqualTo("//foo:foo");
    }

    @Test
    public void testIntDefaultValue() throws Exception {
        evalAndExport("def impl(ctx): return None", "r1 = rule(impl, attrs = {'a1': attr.int(default = 40+2)})");
        RuleClass c = getRuleClass();
        Attribute a = c.getAttributeByName("a1");
        assertThat(a.getDefaultValueUnchecked()).isEqualTo(42);
    }

    @Test
    public void testFileTypeIsDisabled() throws Exception {
        StarlarkSemantics semantics = DEFAULT_SEMANTICS.toBuilder().incompatibleDisallowFileType(true).build();
        EvalException expected = MoreAsserts.assertThrows(EvalException.class, () -> evalRuleClassCode(semantics, "FileType(['.css'])"));
        assertThat(expected).hasMessageThat().contains("FileType function is not available.");
    }

    @Test
    public void testRuleInheritsBaseRuleAttributes() throws Exception {
        evalAndExport("def impl(ctx): return None", "r1 = rule(impl)");
        RuleClass c = getRuleClass();
        assertThat(c.hasAttr("tags", STRING_LIST)).isTrue();
        assertThat(c.hasAttr("visibility", NODEP_LABEL_LIST)).isTrue();
        assertThat(c.hasAttr("deprecation", STRING)).isTrue();
        assertThat(c.hasAttr(":action_listener", LABEL_LIST)).isTrue();// required for extra actions

    }

    @Test
    public void testSimpleTextMessagesBooleanFields() throws Exception {
        checkTextMessage("struct(name=True).to_proto()", "name: true");
        checkTextMessage("struct(name=False).to_proto()", "name: false");
    }

    @Test
    public void testStructRestrictedOverrides() throws Exception {
        checkErrorContains("cannot override built-in struct function 'to_json'", "struct(to_json='foo')");
        checkErrorContains("cannot override built-in struct function 'to_proto'", "struct(to_proto='foo')");
    }

    @Test
    public void testSimpleTextMessages() throws Exception {
        checkTextMessage("struct(name='value').to_proto()", "name: \"value\"");
        checkTextMessage("struct(name=['a', 'b']).to_proto()", "name: \"a\"", "name: \"b\"");
        checkTextMessage("struct(name=123).to_proto()", "name: 123");
        checkTextMessage("struct(name=[1, 2, 3]).to_proto()", "name: 1", "name: 2", "name: 3");
        checkTextMessage("struct(a=struct(b='b')).to_proto()", "a {", "  b: \"b\"", "}");
        checkTextMessage("struct(a=[struct(b='x'), struct(b='y')]).to_proto()", "a {", "  b: \"x\"", "}", "a {", "  b: \"y\"", "}");
        checkTextMessage("struct(a=struct(b=struct(c='c'))).to_proto()", "a {", "  b {", "    c: \"c\"", "  }", "}");
    }

    @Test
    public void testProtoFieldsOrder() throws Exception {
        checkTextMessage("struct(d=4, b=2, c=3, a=1).to_proto()", "a: 1", "b: 2", "c: 3", "d: 4");
    }

    @Test
    public void testTextMessageEscapes() throws Exception {
        checkTextMessage("struct(name=\'a\"b\').to_proto()", "name: \"a\\\"b\"");
        checkTextMessage("struct(name=\'a\\\'b\').to_proto()", "name: \"a\'b\"");
        checkTextMessage("struct(name=\'a\\nb\').to_proto()", "name: \"a\\nb\"");
        // struct(name="a\\\"b") -> name: "a\\\"b"
        checkTextMessage("struct(name=\'a\\\\\\\"b\').to_proto()", "name: \"a\\\\\\\"b\"");
    }

    @Test
    public void testTextMessageInvalidElementInListStructure() throws Exception {
        checkErrorContains(("Invalid text format, expected a struct, a string, a bool, or " + "an int but got a list for list element in struct field 'a'"), "struct(a=[['b']]).to_proto()");
    }

    @Test
    public void testTextMessageInvalidStructure() throws Exception {
        checkErrorContains(("Invalid text format, expected a struct, a string, a bool, or an int " + "but got a function for struct field 'a'"), "struct(a=rule).to_proto()");
    }

    @Test
    public void testJsonBooleanFields() throws Exception {
        checkJson("struct(name=True).to_json()", "{\"name\":true}");
        checkJson("struct(name=False).to_json()", "{\"name\":false}");
    }

    @Test
    public void testJsonDictFields() throws Exception {
        checkJson("struct(config={}).to_json()", "{\"config\":{}}");
        checkJson("struct(config={'key': 'value'}).to_json()", "{\"config\":{\"key\":\"value\"}}");
        checkErrorContains("Keys must be a string but got a int for struct field 'config'", "struct(config={1:2}).to_json()");
        checkErrorContains("Keys must be a string but got a int for dict value 'foo'", "struct(config={'foo':{1:2}}).to_json()");
        checkErrorContains("Keys must be a string but got a bool for struct field 'config'", "struct(config={True: False}).to_json()");
    }

    @Test
    public void testJsonEncoding() throws Exception {
        checkJson("struct(name='value').to_json()", "{\"name\":\"value\"}");
        checkJson("struct(name=['a', 'b']).to_json()", "{\"name\":[\"a\",\"b\"]}");
        checkJson("struct(name=123).to_json()", "{\"name\":123}");
        checkJson("struct(name=[1, 2, 3]).to_json()", "{\"name\":[1,2,3]}");
        checkJson("struct(a=struct(b='b')).to_json()", "{\"a\":{\"b\":\"b\"}}");
        checkJson("struct(a=[struct(b='x'), struct(b='y')]).to_json()", "{\"a\":[{\"b\":\"x\"},{\"b\":\"y\"}]}");
        checkJson("struct(a=struct(b=struct(c='c'))).to_json()", "{\"a\":{\"b\":{\"c\":\"c\"}}}");
    }

    @Test
    public void testJsonEscapes() throws Exception {
        checkJson("struct(name=\'a\"b\').to_json()", "{\"name\":\"a\\\"b\"}");
        checkJson("struct(name=\'a\\\'b\').to_json()", "{\"name\":\"a\'b\"}");
        checkJson("struct(name=\'a\\\\b\').to_json()", "{\"name\":\"a\\\\b\"}");
        checkJson("struct(name=\'a\\nb\').to_json()", "{\"name\":\"a\\nb\"}");
        checkJson("struct(name=\'a\\rb\').to_json()", "{\"name\":\"a\\rb\"}");
        checkJson("struct(name=\'a\\tb\').to_json()", "{\"name\":\"a\\tb\"}");
    }

    @Test
    public void testJsonNestedListStructure() throws Exception {
        checkJson("struct(a=[['b']]).to_json()", "{\"a\":[[\"b\"]]}");
    }

    @Test
    public void testJsonInvalidStructure() throws Exception {
        checkErrorContains(("Invalid text format, expected a struct, a string, a bool, or an int but got a " + "function for struct field 'a'"), "struct(a=rule).to_json()");
    }

    @Test
    public void testLabelAttrWrongDefault() throws Exception {
        checkErrorContains(("expected value of type 'Label or string or LateBoundDefault or function or NoneType' " + "for parameter 'default', for call to method label("), "attr.label(default = 123)");
    }

    @Test
    public void testLabelGetRelative() throws Exception {
        assertThat(eval("Label('//foo:bar').relative('baz')").toString()).isEqualTo("//foo:baz");
        assertThat(eval("Label('//foo:bar').relative('//baz:qux')").toString()).isEqualTo("//baz:qux");
    }

    @Test
    public void testLabelGetRelativeSyntaxError() throws Exception {
        checkErrorContains("invalid target name 'bad//syntax': target names may not contain '//' path separators", "Label('//foo:bar').relative('bad//syntax')");
    }

    @Test
    public void testLicenseAttributesNonconfigurable() throws Exception {
        scratch.file("test/BUILD");
        scratch.file("test/rule.bzl", "def _impl(ctx):", "  return", "some_rule = rule(", "  implementation = _impl,", "  attrs = {", "    'licenses': attr.license()", "  }", ")");
        scratch.file("third_party/foo/BUILD", "load('//test:rule.bzl', 'some_rule')", "some_rule(", "    name='r',", "    licenses = ['unencumbered']", ")");
        invalidatePackages();
        // Should succeed without a "licenses attribute is potentially configurable" loading error:
        createRuleContext("//third_party/foo:r");
    }

    @Test
    public void testStructCreation() throws Exception {
        // TODO(fwe): cannot be handled by current testing suite
        eval("x = struct(a = 1, b = 2)");
        assertThat(lookup("x")).isInstanceOf(ClassObject.class);
    }

    @Test
    public void testStructFields() throws Exception {
        // TODO(fwe): cannot be handled by current testing suite
        eval("x = struct(a = 1, b = 2)");
        ClassObject x = ((ClassObject) (lookup("x")));
        assertThat(x.getValue("a")).isEqualTo(1);
        assertThat(x.getValue("b")).isEqualTo(2);
    }

    @Test
    public void testStructEquality() throws Exception {
        assertThat(((Boolean) (eval("struct(a = 1, b = 2) == struct(b = 2, a = 1)")))).isTrue();
        assertThat(((Boolean) (eval("struct(a = 1) == struct(a = 1, b = 2)")))).isFalse();
        assertThat(((Boolean) (eval("struct(a = 1, b = 2) == struct(a = 1)")))).isFalse();
        // Compare a recursive object to itself to make sure reference equality is checked
        assertThat(((Boolean) (eval("s = (struct(a = 1, b = [])); s.b.append(s); s == s")))).isTrue();
        assertThat(((Boolean) (eval("struct(a = 1, b = 2) == struct(a = 1, b = 3)")))).isFalse();
        assertThat(((Boolean) (eval("struct(a = 1) == [1]")))).isFalse();
        assertThat(((Boolean) (eval("[1] == struct(a = 1)")))).isFalse();
        assertThat(((Boolean) (eval("struct() == struct()")))).isTrue();
        assertThat(((Boolean) (eval("struct() == struct(a = 1)")))).isFalse();
        eval("foo = provider(); bar = provider()");
        assertThat(((Boolean) (eval("struct(a = 1) == foo(a = 1)")))).isFalse();
        assertThat(((Boolean) (eval("foo(a = 1) == struct(a = 1)")))).isFalse();
        assertThat(((Boolean) (eval("foo(a = 1) == bar(a = 1)")))).isFalse();
        assertThat(((Boolean) (eval("foo(a = 1) == foo(a = 1)")))).isTrue();
    }

    @Test
    public void testStructIncomparability() throws Exception {
        checkErrorContains("Cannot compare structs", "struct(a = 1) < struct(a = 2)");
        checkErrorContains("Cannot compare structs", "struct(a = 1) > struct(a = 2)");
        checkErrorContains("Cannot compare structs", "struct(a = 1) <= struct(a = 2)");
        checkErrorContains("Cannot compare structs", "struct(a = 1) >= struct(a = 2)");
    }

    @Test
    public void testStructAccessingFieldsFromSkylark() throws Exception {
        eval("x = struct(a = 1, b = 2)", "x1 = x.a", "x2 = x.b");
        assertThat(lookup("x1")).isEqualTo(1);
        assertThat(lookup("x2")).isEqualTo(2);
    }

    @Test
    public void testStructAccessingUnknownField() throws Exception {
        checkErrorContains(("\'struct\' object has no attribute \'c\'\n" + "Available attributes: a, b"), "x = struct(a = 1, b = 2)", "y = x.c");
    }

    @Test
    public void testStructAccessingUnknownFieldWithArgs() throws Exception {
        checkErrorContains("type 'struct' has no method c()", "x = struct(a = 1, b = 2)", "y = x.c()");
    }

    @Test
    public void testStructAccessingNonFunctionFieldWithArgs() throws Exception {
        checkErrorContains("'int' object is not callable", "x = struct(a = 1, b = 2)", "x1 = x.a(1)");
    }

    @Test
    public void testStructAccessingFunctionFieldWithArgs() throws Exception {
        eval("def f(x): return x+5", "x = struct(a = f, b = 2)", "x1 = x.a(1)");
        assertThat(lookup("x1")).isEqualTo(6);
    }

    @Test
    public void testStructPosArgs() throws Exception {
        checkErrorContains("expected no more than 0 positional arguments, but got 1", "x = struct(1, b = 2)");
    }

    @Test
    public void testStructConcatenationFieldNames() throws Exception {
        // TODO(fwe): cannot be handled by current testing suite
        eval("x = struct(a = 1, b = 2)", "y = struct(c = 1, d = 2)", "z = x + y\n");
        StructImpl z = ((StructImpl) (lookup("z")));
        assertThat(z.getFieldNames()).isEqualTo(ImmutableSet.of("a", "b", "c", "d"));
    }

    @Test
    public void testStructConcatenationFieldValues() throws Exception {
        // TODO(fwe): cannot be handled by current testing suite
        eval("x = struct(a = 1, b = 2)", "y = struct(c = 1, d = 2)", "z = x + y\n");
        StructImpl z = ((StructImpl) (lookup("z")));
        assertThat(z.getValue("a")).isEqualTo(1);
        assertThat(z.getValue("b")).isEqualTo(2);
        assertThat(z.getValue("c")).isEqualTo(1);
        assertThat(z.getValue("d")).isEqualTo(2);
    }

    @Test
    public void testStructConcatenationCommonFields() throws Exception {
        checkErrorContains("Cannot use '+' operator on provider instances with overlapping field(s): a", "x = struct(a = 1, b = 2)", "y = struct(c = 1, a = 2)", "z = x + y\n");
    }

    @Test
    public void testConditionalStructConcatenation() throws Exception {
        // TODO(fwe): cannot be handled by current testing suite
        eval("def func():", "  x = struct(a = 1, b = 2)", "  if True:", "    x += struct(c = 1, d = 2)", "  return x", "x = func()");
        StructImpl x = ((StructImpl) (lookup("x")));
        assertThat(x.getValue("a")).isEqualTo(1);
        assertThat(x.getValue("b")).isEqualTo(2);
        assertThat(x.getValue("c")).isEqualTo(1);
        assertThat(x.getValue("d")).isEqualTo(2);
    }

    @Test
    public void testGetattrNoAttr() throws Exception {
        checkErrorContains("\'struct\' object has no attribute \'b\'\nAvailable attributes: a", "s = struct(a='val')", "getattr(s, 'b')");
    }

    @Test
    public void testGetattr() throws Exception {
        eval("s = struct(a='val')", "x = getattr(s, 'a')", "y = getattr(s, 'b', 'def')", "z = getattr(s, 'b', default = 'def')", "w = getattr(s, 'a', default='ignored')");
        assertThat(lookup("x")).isEqualTo("val");
        assertThat(lookup("y")).isEqualTo("def");
        assertThat(lookup("z")).isEqualTo("def");
        assertThat(lookup("w")).isEqualTo("val");
    }

    @Test
    public void testHasattr() throws Exception {
        eval("s = struct(a=1)", "x = hasattr(s, 'a')", "y = hasattr(s, \'b\')\n");
        assertThat(lookup("x")).isEqualTo(true);
        assertThat(lookup("y")).isEqualTo(false);
    }

    @Test
    public void testStructStr() throws Exception {
        assertThat(eval("str(struct(x = 2, y = 3, z = 4))")).isEqualTo("struct(x = 2, y = 3, z = 4)");
    }

    @Test
    public void testStructsInSets() throws Exception {
        eval("depset([struct(a='a')])");
    }

    @Test
    public void testStructsInDicts() throws Exception {
        eval("d = {struct(a = 1): 'aa', struct(b = 2): 'bb'}");
        assertThat(eval("d[struct(a = 1)]")).isEqualTo("aa");
        assertThat(eval("d[struct(b = 2)]")).isEqualTo("bb");
        assertThat(eval("str([d[k] for k in d])")).isEqualTo("[\"aa\", \"bb\"]");
        checkErrorContains("unhashable type: 'struct'", "{struct(a = []): 'foo'}");
    }

    @Test
    public void testStructMembersAreImmutable() throws Exception {
        checkErrorContains("cannot assign to 's.x'", "s = struct(x = 'a')", "s.x = \'b\'\n");
    }

    @Test
    public void testStructDictMembersAreMutable() throws Exception {
        eval("s = struct(x = {'a' : 1})", "s.x[\'b\'] = 2\n");
        assertThat(getValue("x")).isEqualTo(ImmutableMap.of("a", 1, "b", 2));
    }

    @Test
    public void testNsetGoodCompositeItem() throws Exception {
        eval("def func():", "  return depset([struct(a='a')])", "s = func()");
        Collection<Object> result = toCollection();
        assertThat(result).hasSize(1);
        assertThat(result.iterator().next()).isInstanceOf(StructImpl.class);
    }

    @Test
    public void testNsetBadMutableItem() throws Exception {
        checkEvalError("depsets cannot contain mutable items", "depset([([],)])");
        checkEvalError("depsets cannot contain mutable items", "depset([struct(a=[])])");
    }

    @Test
    public void testStructMutabilityShallow() throws Exception {
        assertThat(EvalUtils.isImmutable(SkylarkRuleClassFunctionsTest.makeStruct("a", 1))).isTrue();
    }

    @Test
    public void testStructMutabilityDeep() throws Exception {
        assertThat(EvalUtils.isImmutable(Tuple.<Object>of(SkylarkRuleClassFunctionsTest.makeList(null)))).isTrue();
        assertThat(EvalUtils.isImmutable(SkylarkRuleClassFunctionsTest.makeStruct("a", SkylarkRuleClassFunctionsTest.makeList(null)))).isTrue();
        assertThat(EvalUtils.isImmutable(SkylarkRuleClassFunctionsTest.makeBigStruct(null))).isTrue();
        assertThat(EvalUtils.isImmutable(Tuple.<Object>of(SkylarkRuleClassFunctionsTest.makeList(ev.getEnvironment())))).isFalse();
        assertThat(EvalUtils.isImmutable(SkylarkRuleClassFunctionsTest.makeStruct("a", SkylarkRuleClassFunctionsTest.makeList(ev.getEnvironment())))).isFalse();
        assertThat(EvalUtils.isImmutable(SkylarkRuleClassFunctionsTest.makeBigStruct(ev.getEnvironment()))).isFalse();
    }

    @Test
    public void declaredProviders() throws Exception {
        evalAndExport("data = provider()", "d = data(x = 1, y ='abc')", "d_x = d.x", "d_y = d.y");
        assertThat(lookup("d_x")).isEqualTo(1);
        assertThat(lookup("d_y")).isEqualTo("abc");
        SkylarkProvider dataConstructor = ((SkylarkProvider) (lookup("data")));
        StructImpl data = ((StructImpl) (lookup("d")));
        assertThat(data.getProvider()).isEqualTo(dataConstructor);
        assertThat(dataConstructor.isExported()).isTrue();
        assertThat(dataConstructor.getPrintableName()).isEqualTo("data");
        assertThat(dataConstructor.getKey()).isEqualTo(new SkylarkProvider.SkylarkKey(SkylarkRuleClassFunctionsTest.FAKE_LABEL, "data"));
    }

    @Test
    public void declaredProvidersConcatSuccess() throws Exception {
        evalAndExport("data = provider()", "dx = data(x = 1)", "dy = data(y = 'abc')", "dxy = dx + dy", "x = dxy.x", "y = dxy.y");
        assertThat(lookup("x")).isEqualTo(1);
        assertThat(lookup("y")).isEqualTo("abc");
        SkylarkProvider dataConstructor = ((SkylarkProvider) (lookup("data")));
        StructImpl dx = ((StructImpl) (lookup("dx")));
        assertThat(dx.getProvider()).isEqualTo(dataConstructor);
        StructImpl dy = ((StructImpl) (lookup("dy")));
        assertThat(dy.getProvider()).isEqualTo(dataConstructor);
    }

    @Test
    public void declaredProvidersConcatError() throws Exception {
        evalAndExport("data1 = provider()", "data2 = provider()");
        checkEvalError("Cannot use '+' operator on instances of different providers (data1 and data2)", "d1 = data1(x = 1)", "d2 = data2(y = 2)", "d = d1 + d2");
    }

    @Test
    public void declaredProvidersWithFieldsConcatSuccess() throws Exception {
        evalAndExport("data = provider(fields=['f1', 'f2'])", "d1 = data(f1 = 4)", "d2 = data(f2 = 5)", "d3 = d1 + d2", "f1 = d3.f1", "f2 = d3.f2");
        assertThat(lookup("f1")).isEqualTo(4);
        assertThat(lookup("f2")).isEqualTo(5);
    }

    @Test
    public void declaredProvidersWithFieldsConcatError() throws Exception {
        evalAndExport("data1 = provider(fields=['f1', 'f2'])", "data2 = provider(fields=['f3'])");
        checkEvalError("Cannot use '+' operator on instances of different providers (data1 and data2)", "d1 = data1(f1=1, f2=2)", "d2 = data2(f3=3)", "d = d1 + d2");
    }

    @Test
    public void declaredProvidersWithOverlappingFieldsConcatError() throws Exception {
        evalAndExport("data = provider(fields=['f1', 'f2'])");
        checkEvalError("Cannot use '+' operator on provider instances with overlapping field(s): f1", "d1 = data(f1 = 4)", "d2 = data(f1 = 5)", "d1 + d2");
    }

    @Test
    public void structsAsDeclaredProvidersTest() throws Exception {
        evalAndExport("data = struct(x = 1)");
        StructImpl data = ((StructImpl) (lookup("data")));
        assertThat(STRUCT.isExported()).isTrue();
        assertThat(data.getProvider()).isEqualTo(STRUCT);
        assertThat(data.getProvider().getKey()).isEqualTo(STRUCT.getKey());
    }

    @Test
    public void declaredProvidersDoc() throws Exception {
        evalAndExport("data1 = provider(doc='foo')");
    }

    @Test
    public void declaredProvidersBadTypeForDoc() throws Exception {
        checkErrorContains(("expected value of type 'string' for parameter 'doc', for call to function " + "provider(doc = '', fields = None)"), "provider(doc = 1)");
    }

    @Test
    public void aspectAllAttrs() throws Exception {
        evalAndExport("def _impl(target, ctx):", "   pass", "my_aspect = aspect(_impl, attr_aspects=['*'])");
        SkylarkDefinedAspect myAspect = ((SkylarkDefinedAspect) (lookup("my_aspect")));
        assertThat(myAspect.getDefinition(EMPTY).propagateAlong("foo")).isTrue();
    }

    @Test
    public void aspectRequiredAspectProvidersSingle() throws Exception {
        evalAndExport("def _impl(target, ctx):", "   pass", "cc = provider()", "my_aspect = aspect(_impl, required_aspect_providers=['java', cc])");
        SkylarkDefinedAspect myAspect = ((SkylarkDefinedAspect) (lookup("my_aspect")));
        RequiredProviders requiredProviders = myAspect.getDefinition(EMPTY).getRequiredProvidersForAspects();
        assertThat(requiredProviders.isSatisfiedBy(ANY)).isTrue();
        assertThat(requiredProviders.isSatisfiedBy(AdvertisedProviderSet.EMPTY)).isFalse();
        assertThat(requiredProviders.isSatisfiedBy(AdvertisedProviderSet.builder().addSkylark(SkylarkRuleClassFunctionsTest.declared("cc")).addSkylark("java").build())).isTrue();
        assertThat(requiredProviders.isSatisfiedBy(AdvertisedProviderSet.builder().addSkylark("cc").build())).isFalse();
    }

    @Test
    public void aspectRequiredAspectProvidersAlternatives() throws Exception {
        evalAndExport("def _impl(target, ctx):", "   pass", "cc = provider()", "my_aspect = aspect(_impl, required_aspect_providers=[['java'], [cc]])");
        SkylarkDefinedAspect myAspect = ((SkylarkDefinedAspect) (lookup("my_aspect")));
        RequiredProviders requiredProviders = myAspect.getDefinition(EMPTY).getRequiredProvidersForAspects();
        assertThat(requiredProviders.isSatisfiedBy(ANY)).isTrue();
        assertThat(requiredProviders.isSatisfiedBy(AdvertisedProviderSet.EMPTY)).isFalse();
        assertThat(requiredProviders.isSatisfiedBy(AdvertisedProviderSet.builder().addSkylark("java").build())).isTrue();
        assertThat(requiredProviders.isSatisfiedBy(AdvertisedProviderSet.builder().addSkylark(SkylarkRuleClassFunctionsTest.declared("cc")).build())).isTrue();
        assertThat(requiredProviders.isSatisfiedBy(AdvertisedProviderSet.builder().addSkylark("prolog").build())).isFalse();
    }

    @Test
    public void aspectRequiredAspectProvidersEmpty() throws Exception {
        evalAndExport("def _impl(target, ctx):", "   pass", "my_aspect = aspect(_impl, required_aspect_providers=[])");
        SkylarkDefinedAspect myAspect = ((SkylarkDefinedAspect) (lookup("my_aspect")));
        RequiredProviders requiredProviders = myAspect.getDefinition(EMPTY).getRequiredProvidersForAspects();
        assertThat(requiredProviders.isSatisfiedBy(ANY)).isFalse();
        assertThat(requiredProviders.isSatisfiedBy(AdvertisedProviderSet.EMPTY)).isFalse();
    }

    @Test
    public void aspectRequiredAspectProvidersDefault() throws Exception {
        evalAndExport("def _impl(target, ctx):", "   pass", "my_aspect = aspect(_impl)");
        SkylarkDefinedAspect myAspect = ((SkylarkDefinedAspect) (lookup("my_aspect")));
        RequiredProviders requiredProviders = myAspect.getDefinition(EMPTY).getRequiredProvidersForAspects();
        assertThat(requiredProviders.isSatisfiedBy(ANY)).isFalse();
        assertThat(requiredProviders.isSatisfiedBy(AdvertisedProviderSet.EMPTY)).isFalse();
    }

    @Test
    public void aspectProvides() throws Exception {
        evalAndExport("def _impl(target, ctx):", "   pass", "y = provider()", "my_aspect = aspect(_impl, provides = ['x', y])");
        SkylarkDefinedAspect myAspect = ((SkylarkDefinedAspect) (lookup("my_aspect")));
        AdvertisedProviderSet advertisedProviders = myAspect.getDefinition(EMPTY).getAdvertisedProviders();
        assertThat(advertisedProviders.canHaveAnyProvider()).isFalse();
        assertThat(advertisedProviders.getSkylarkProviders()).containsExactly(SkylarkRuleClassFunctionsTest.legacy("x"), SkylarkRuleClassFunctionsTest.declared("y"));
    }

    @Test
    public void aspectProvidesError() throws Exception {
        ev.setFailFast(false);
        evalAndExport("def _impl(target, ctx):", "   pass", "y = provider()", "my_aspect = aspect(_impl, provides = ['x', 1])");
        MoreAsserts.assertContainsEvent(ev.getEventCollector(), (" Illegal argument: element in 'provides' is of unexpected type." + " Should be list of providers, but got item of type int. "));
    }

    @Test
    public void aspectDoc() throws Exception {
        evalAndExport("def _impl(target, ctx):", "   pass", "my_aspect = aspect(_impl, doc='foo')");
    }

    @Test
    public void aspectBadTypeForDoc() throws Exception {
        registerDummyUserDefinedFunction();
        checkErrorContains("expected value of type 'string' for parameter 'doc', for call to function aspect(", "aspect(impl, doc = 1)");
    }

    @Test
    public void fancyExports() throws Exception {
        evalAndExport("def _impla(target, ctx): pass", "p, (a, p1) = [", "   provider(),", "   [ aspect(_impla),", "     provider() ]", "]");
        SkylarkProvider p = ((SkylarkProvider) (lookup("p")));
        SkylarkDefinedAspect a = ((SkylarkDefinedAspect) (lookup("a")));
        SkylarkProvider p1 = ((SkylarkProvider) (lookup("p1")));
        assertThat(p.getPrintableName()).isEqualTo("p");
        assertThat(p.getKey()).isEqualTo(new SkylarkProvider.SkylarkKey(SkylarkRuleClassFunctionsTest.FAKE_LABEL, "p"));
        assertThat(p1.getPrintableName()).isEqualTo("p1");
        assertThat(p1.getKey()).isEqualTo(new SkylarkProvider.SkylarkKey(SkylarkRuleClassFunctionsTest.FAKE_LABEL, "p1"));
        assertThat(a.getAspectClass()).isEqualTo(new com.google.devtools.build.lib.packages.SkylarkAspectClass(SkylarkRuleClassFunctionsTest.FAKE_LABEL, "a"));
    }

    @Test
    public void multipleTopLevels() throws Exception {
        evalAndExport("p = provider()", "p1 = p");
        SkylarkProvider p = ((SkylarkProvider) (lookup("p")));
        SkylarkProvider p1 = ((SkylarkProvider) (lookup("p1")));
        assertThat(p).isEqualTo(p1);
        assertThat(p.getKey()).isEqualTo(new SkylarkProvider.SkylarkKey(SkylarkRuleClassFunctionsTest.FAKE_LABEL, "p"));
        assertThat(p1.getKey()).isEqualTo(new SkylarkProvider.SkylarkKey(SkylarkRuleClassFunctionsTest.FAKE_LABEL, "p"));
    }

    @Test
    public void providerWithFields() throws Exception {
        evalAndExport("p = provider(fields = ['x', 'y'])", "p1 = p(x = 1, y = 2)", "x = p1.x", "y = p1.y");
        SkylarkProvider p = ((SkylarkProvider) (lookup("p")));
        SkylarkInfo p1 = ((SkylarkInfo) (lookup("p1")));
        assertThat(p1.getProvider()).isEqualTo(p);
        assertThat(lookup("x")).isEqualTo(1);
        assertThat(lookup("y")).isEqualTo(2);
    }

    @Test
    public void providerWithFieldsDict() throws Exception {
        evalAndExport("p = provider(fields = { 'x' : 'I am x', 'y' : 'I am y'})", "p1 = p(x = 1, y = 2)", "x = p1.x", "y = p1.y");
        SkylarkProvider p = ((SkylarkProvider) (lookup("p")));
        SkylarkInfo p1 = ((SkylarkInfo) (lookup("p1")));
        assertThat(p1.getProvider()).isEqualTo(p);
        assertThat(lookup("x")).isEqualTo(1);
        assertThat(lookup("y")).isEqualTo(2);
    }

    @Test
    public void providerWithFieldsOptional() throws Exception {
        evalAndExport("p = provider(fields = ['x', 'y'])", "p1 = p(y = 2)", "y = p1.y");
        SkylarkProvider p = ((SkylarkProvider) (lookup("p")));
        SkylarkInfo p1 = ((SkylarkInfo) (lookup("p1")));
        assertThat(p1.getProvider()).isEqualTo(p);
        assertThat(lookup("y")).isEqualTo(2);
    }

    @Test
    public void providerWithFieldsOptionalError() throws Exception {
        ev.setFailFast(false);
        evalAndExport("p = provider(fields = ['x', 'y'])", "p1 = p(y = 2)", "x = p1.x");
        MoreAsserts.assertContainsEvent(ev.getEventCollector(), " 'p' object has no attribute 'x'");
    }

    @Test
    public void providerWithExtraFieldsError() throws Exception {
        ev.setFailFast(false);
        evalAndExport("p = provider(fields = ['x', 'y'])", "p1 = p(x = 1, y = 2, z = 3)");
        MoreAsserts.assertContainsEvent(ev.getEventCollector(), "unexpected keyword 'z' in call to p(*, x = ?, y = ?)");
    }

    @Test
    public void providerWithEmptyFieldsError() throws Exception {
        ev.setFailFast(false);
        evalAndExport("p = provider(fields = [])", "p1 = p(x = 1, y = 2, z = 3)");
        MoreAsserts.assertContainsEvent(ev.getEventCollector(), "unexpected keywords 'x', 'y', 'z' in call to p()");
    }

    @Test
    public void starTheOnlyAspectArg() throws Exception {
        checkEvalError("'*' must be the only string in 'attr_aspects' list", "def _impl(target, ctx):", "   pass", "aspect(_impl, attr_aspects=['*', 'foo'])");
    }

    @Test
    public void testMandatoryConfigParameterForExecutableLabels() throws Exception {
        scratch.file("third_party/foo/extension.bzl", "def _main_rule_impl(ctx):", "    pass", "my_rule = rule(_main_rule_impl,", "    attrs = { ", "        'exe' : attr.label(executable = True, allow_files = True),", "    },", ")");
        scratch.file("third_party/foo/BUILD", "load(':extension.bzl', 'my_rule')", "my_rule(name = 'main', exe = ':tool.sh')");
        AssertionError expected = MoreAsserts.assertThrows(AssertionError.class, () -> createRuleContext("//third_party/foo:main"));
        assertThat(expected).hasMessageThat().contains("cfg parameter is mandatory when executable=True is provided.");
    }

    @Test
    public void testRuleAddToolchain() throws Exception {
        scratch.file("test/BUILD", "toolchain_type(name = 'my_toolchain_type')");
        evalAndExport("def impl(ctx): return None", "r1 = rule(impl, toolchains=['//test:my_toolchain_type'])");
        RuleClass c = getRuleClass();
        assertThat(c.getRequiredToolchains()).containsExactly(BuildViewTestCase.makeLabel("//test:my_toolchain_type"));
    }

    @Test
    public void testRuleAddExecutionConstraints() throws Exception {
        registerDummyUserDefinedFunction();
        scratch.file("test/BUILD", "toolchain_type(name = 'my_toolchain_type')");
        evalAndExport("r1 = rule(", "  implementation = impl,", "  toolchains=['//test:my_toolchain_type'],", "  exec_compatible_with=['//constraint:cv1', '//constraint:cv2'],", ")");
        RuleClass c = getRuleClass();
        assertThat(c.getExecutionPlatformConstraints()).containsExactly(BuildViewTestCase.makeLabel("//constraint:cv1"), BuildViewTestCase.makeLabel("//constraint:cv2"));
    }

    @Test
    public void testTargetsCanAddExecutionPlatformConstraints() throws Exception {
        registerDummyUserDefinedFunction();
        scratch.file("test/BUILD", "toolchain_type(name = 'my_toolchain_type')");
        evalAndExport("r1 = rule(impl, ", "  toolchains=['//test:my_toolchain_type'],", "  execution_platform_constraints_allowed=True,", ")");
        RuleClass c = getRuleClass();
        assertThat(c.executionPlatformConstraintsAllowed()).isEqualTo(PER_TARGET);
    }

    @Test
    public void testRuleFunctionReturnsNone() throws Exception {
        scratch.file("test/rule.bzl", "def _impl(ctx):", "  pass", "foo_rule = rule(", "  implementation = _impl,", "  attrs = {'params': attr.string_list()},", ")");
        // Custom rule should return None
        // Native rule should return None
        scratch.file("test/BUILD", "load(':rule.bzl', 'foo_rule')", "r = foo_rule(name='foo')", "c = cc_library(name='cc')", "", "foo_rule(", "    name='check',", "    params = [type(r), type(c)]", ")");
        invalidatePackages();
        SkylarkRuleContext context = createRuleContext("//test:check");
        @SuppressWarnings("unchecked")
        MutableList<Object> params = ((MutableList<Object>) (getValue("params")));
        assertThat(params.get(0)).isEqualTo("NoneType");
        assertThat(params.get(1)).isEqualTo("NoneType");
    }

    @Test
    public void testTypeOfStruct() throws Exception {
        eval("p = type(struct)", "s = type(struct())");
        assertThat(lookup("p")).isEqualTo("Provider");
        assertThat(lookup("s")).isEqualTo("struct");
    }
}

