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
package com.google.devtools.build.lib.analysis;


import License.DistributionType.CLIENT;
import License.DistributionType.INTERNAL;
import License.DistributionType.WEB;
import Type.STRING;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.testing.EqualsTester;
import com.google.devtools.build.lib.analysis.util.AnalysisMock;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.bazel.LicenseCheckingModule;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.packages.License;
import com.google.devtools.build.lib.packages.RawAttributeMapper;
import com.google.devtools.build.lib.packages.Rule;
import com.google.devtools.build.lib.packages.util.MockCcSupport;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Integration tests for {@link License}.
 */
@RunWith(JUnit4.class)
public class LicensingTests extends BuildViewTestCase {
    /**
     * Filter to remove implicit dependencies of sh_* rules.
     */
    private static final Predicate<String> SH_LABEL_NAME_FILTER = new Predicate<String>() {
        @Override
        public boolean apply(String label) {
            return !(label.startsWith("//util/shell/"));
        }
    };

    private static final Predicate<Label> SH_LABEL_FILTER = new Predicate<Label>() {
        @Override
        public boolean apply(Label label) {
            return LicensingTests.SH_LABEL_NAME_FILTER.apply(("//" + (label.getPackageName())));
        }
    };

    /**
     * Filter to remove implicit dependencies of Java rules.
     */
    private static final Predicate<String> JAVA_LABEL_NAME_FILTER = new Predicate<String>() {
        @Override
        public boolean apply(String label) {
            return !(label.startsWith("//third_party/java/jdk"));
        }
    };

    public static final Predicate<Label> JAVA_LABEL_FILTER = new Predicate<Label>() {
        @Override
        public boolean apply(Label label) {
            return LicensingTests.JAVA_LABEL_NAME_FILTER.apply(("//" + (label.getPackageName())));
        }
    };

    @SuppressWarnings("unchecked")
    public static final Predicate<Label> CC_OR_JAVA_OR_SH_LABEL_FILTER = Predicates.and(AnalysisMock.get().ccSupport().labelFilter(), LicensingTests.JAVA_LABEL_FILTER, LicensingTests.SH_LABEL_FILTER);

    @Test
    public void testCollectDistribs() throws Exception {
        scratch.file("a/BUILD", "distribs(['client', 'web'])", "cc_library(name = 'alib', srcs=['a.cc'], deps=[':blib'])", "cc_library(name = 'blib', distribs=['client'])");
        scratch.file("kaboom/BUILD", "cc_library(name = 'boom', srcs=['bang.cc'])");
        assertThat(getTarget("//a:alib").getDistributions()).containsExactlyElementsIn(Sets.newHashSet(CLIENT, WEB));
        assertThat(getTarget("//a:blib").getDistributions()).containsExactlyElementsIn(Sets.newHashSet(CLIENT));
        assertThat(getTarget("//kaboom:boom").getDistributions()).containsExactlyElementsIn(Sets.newHashSet(INTERNAL));
    }

    // Same test, using package.distribs.
    @Test
    public void testCollectDistribsPackage() throws Exception {
        scratch.file("a/BUILD", "package(distribs = ['client', 'web'])", "cc_library(name = 'alib', srcs=['a.cc'], deps=[':blib'])", "cc_library(name = 'blib', distribs=['client'])");
        scratch.file("kaboom/BUILD", "cc_library(name = 'boom', srcs=['bang.cc'])");
        assertThat(getTarget("//a:alib").getDistributions()).containsExactlyElementsIn(Sets.newHashSet(CLIENT, WEB));
        assertThat(getTarget("//a:blib").getDistributions()).containsExactlyElementsIn(Sets.newHashSet(CLIENT));
        assertThat(getTarget("//kaboom:boom").getDistributions()).containsExactlyElementsIn(Sets.newHashSet(INTERNAL));
    }

    // Test for package.licenses
    @Test
    public void testCollectLicensesPackage() throws Exception {
        scratch.file("a/BUILD", "licenses(['restricted', 'reciprocal'])", "exports_files(['a'])");
        ConfiguredTarget target = getConfiguredTarget("//a:a");
        License license = getTarget(target.getLabel()).getLicense();
        assertThat(license).isEqualTo(License.parseLicense(Arrays.asList("restricted", "reciprocal")));
    }

    @Test
    public void testCollectLicenses() throws Exception {
        Predicate<Label> SOURCE_FILTER = new Predicate<Label>() {
            @Override
            public boolean apply(Label label) {
                // Remove source files from the results.
                return (AnalysisMock.get().ccSupport().labelFilter().apply(label)) && (!(label.toString().endsWith(".cc")));
            }
        };
        scratch.file("a/BUILD", "licenses(['restricted', 'reciprocal'])", "cc_library(name = 'alib', srcs=['a.cc'], deps=[':blib'])", ("cc_library(name = 'blib', srcs=['b.cc'], distribs=['web'], " + "deps=['//kaboom:boom', '//kablam:blam'])"), "exports_files(['c'])");
        scratch.file("kaboom/BUILD", "licenses(['notice'])", "cc_library(name = 'boom', srcs=['bang.cc'])");
        scratch.file("kablam/BUILD", "licenses(['restricted','exception=//a:alib'])", "cc_library(name='blam', srcs=['blam.cc'])");
        ConfiguredTarget aTarget = getConfiguredTarget("//a:alib");
        ConfiguredTarget bTarget = getConfiguredTarget("//a:blib");
        ConfiguredTarget cTarget = getConfiguredTarget("//a:c");
        ConfiguredTarget kaboomTarget = getConfiguredTarget("//kaboom:boom");
        ConfiguredTarget kablamTarget = getConfiguredTarget("//kablam:blam");
        Map<Label, License> aMap = Maps.filterKeys(LicensingTests.getTransitiveLicenses(aTarget), SOURCE_FILTER);
        Map<Label, License> aExpected = LicensingTests.licenses("//a:alib", "restricted,reciprocal", "//a:blib", "restricted,reciprocal", "//kablam:blam", "restricted,exception=//a:alib", "//kaboom:boom", "notice");
        LicensingTests.assertSameMapEntries(aExpected, aMap);
        Map<Label, License> bMap = Maps.filterKeys(LicensingTests.getTransitiveLicenses(bTarget), SOURCE_FILTER);
        Map<Label, License> bExpected = LicensingTests.licenses("//a:blib", "restricted,reciprocal", "//kablam:blam", "restricted,exception=//a:alib", "//kaboom:boom", "notice");
        LicensingTests.assertSameMapEntries(bExpected, bMap);
        Map<Label, License> kaboomMap = Maps.filterKeys(LicensingTests.getTransitiveLicenses(kaboomTarget), SOURCE_FILTER);
        LicensingTests.assertSameMapEntries(LicensingTests.licenses("//kaboom:boom", "notice"), kaboomMap);
        Map<Label, License> kablamMap = Maps.filterKeys(LicensingTests.getTransitiveLicenses(kablamTarget), SOURCE_FILTER);
        LicensingTests.assertSameMapEntries(LicensingTests.licenses("//kablam:blam", "restricted,exception=//a:alib"), kablamMap);
        License cLicense = getTarget(cTarget.getLabel()).getLicense();
        assertThat(cLicense).isEqualTo(License.parseLicense(Arrays.asList("restricted", "reciprocal")));
    }

    @Test
    public void testClientLicenseOverridesDefault() throws Exception {
        scratch.file("a/BUILD", "licenses(['restricted', 'reciprocal'])", "cc_library(name = 'alib', srcs=['a.cc'], deps=[':blib'], licenses=['unencumbered'])", "cc_library(name = 'blib', srcs=['b.cc'], distribs=['web'])", "exports_files(['c'], licenses = ['unencumbered'])");
        ConfiguredTarget aTarget = getConfiguredTarget("//a:alib");
        ConfiguredTarget bTarget = getConfiguredTarget("//a:blib");
        ConfiguredTarget cTarget = getConfiguredTarget("//a:c");
        License aLicense = getTarget(aTarget.getLabel()).getLicense();
        License bLicense = getTarget(bTarget.getLabel()).getLicense();
        License cLicense = getTarget(cTarget.getLabel()).getLicense();
        assertThat(aLicense.equals(bLicense)).isFalse();
        assertThat(aLicense).isEqualTo(License.parseLicense(Arrays.asList("unencumbered")));
        assertThat(bLicense).isEqualTo(License.parseLicense(Arrays.asList("restricted", "reciprocal")));
        assertThat(cLicense).isEqualTo(License.parseLicense(Arrays.asList("unencumbered")));
    }

    @Test
    public void testNoneLicenseHandledCorrectly() throws Exception {
        scratch.file("music/BUILD", "sh_binary(name='piano', srcs=['piano.sh'], licenses=['none'])");
        Map<Label, License> licenses = Maps.filterKeys(LicensingTests.getTransitiveLicenses(getConfiguredTarget("//music:piano")), LicensingTests.CC_OR_JAVA_OR_SH_LABEL_FILTER);
        assertThat(licenses).isEmpty();
    }

    @Test
    public void testToolLicenseAddedCorrectly() throws Exception {
        scratch.file("powertools/BUILD", "sh_binary(name = 'chainsaw',", "          srcs = ['chainsaw.sh'],", "          licenses=['restricted'],", "          output_licenses=['unencumbered'])");
        scratch.file("gr/BUILD", "licenses(['unencumbered'])", "genrule(name = 'lumberjack',", "        outs=['firewood'],", "        tools=['//powertools:chainsaw'],", "        cmd='')", "genrule(name = 'xorn',", "        outs=['jewel'],", "        srcs=['//powertools:chainsaw'],", "        cmd='')");
        ConfiguredTarget lumberjack = getConfiguredTarget("//gr:lumberjack");
        ConfiguredTarget xorn = getConfiguredTarget("//gr:xorn");
        Map<Label, License> lumberjackActual = Maps.filterKeys(LicensingTests.getTransitiveLicenses(lumberjack), LicensingTests.CC_OR_JAVA_OR_SH_LABEL_FILTER);
        Map<Label, License> xornActual = Maps.filterKeys(LicensingTests.getTransitiveLicenses(xorn), LicensingTests.CC_OR_JAVA_OR_SH_LABEL_FILTER);
        Map<Label, License> lumberjackExpected = LicensingTests.licenses("//powertools:chainsaw", "unencumbered", "//gr:lumberjack", "unencumbered");
        Map<Label, License> xornExpected = LicensingTests.licenses("//powertools:chainsaw", "restricted", "//gr:xorn", "unencumbered");
        LicensingTests.assertSameMapEntries(lumberjackExpected, lumberjackActual);
        LicensingTests.assertSameMapEntries(xornExpected, xornActual);
    }

    @Test
    public void testToolLicenseDoesNotIncludeDependencies() throws Exception {
        scratch.file("powertools/BUILD", "sh_binary(name = 'chainsaw',", "          srcs = ['chainsaw.sh'],", "          deps = [':gasoline'],", "          licenses = ['restricted'],", "          output_licenses=['unencumbered'])", "sh_binary(name = 'gasoline',", "          srcs = ['gasoline.sh'],", "          licenses = ['notice'])");
        scratch.file("gr/BUILD", "licenses(['unencumbered'])", "genrule(name = 'lumberjack',", "        outs=['firewood'],", "        tools=['//powertools:chainsaw'],", "        cmd='')");
        ConfiguredTarget lumberjack = getConfiguredTarget("//gr:lumberjack");
        Map<Label, License> actual = Maps.filterKeys(LicensingTests.getTransitiveLicenses(lumberjack), LicensingTests.CC_OR_JAVA_OR_SH_LABEL_FILTER);
        Map<Label, License> expected = LicensingTests.licenses("//powertools:chainsaw", "unencumbered", "//gr:lumberjack", "unencumbered");
        LicensingTests.assertSameMapEntries(expected, actual);
    }

    @Test
    public void testFilegroupAllowsOutputLicenseDeclaration() throws Exception {
        scratch.file("apple/BUILD", "licenses(['restricted'])", "filegroup(name='apples',", "          output_licenses=['unencumbered'],", "          srcs=['rambo', 'granny_smith'])");
        scratch.file("apfelmuss/BUILD", "licenses(['unencumbered'])", "genrule(name = 'apfelmuss',", "        outs = ['jar'],", "        tools = ['//apple:apples'],", "        cmd = 'magic')");
        ConfiguredTarget apfelmuss = getConfiguredTarget("//apfelmuss:apfelmuss");
        Map<Label, License> actual = Maps.filterKeys(LicensingTests.getTransitiveLicenses(apfelmuss), LicensingTests.CC_OR_JAVA_OR_SH_LABEL_FILTER);
        Map<Label, License> expected = LicensingTests.licenses("//apfelmuss:apfelmuss", "unencumbered", "//apple:apples", "unencumbered");
        LicensingTests.assertSameMapEntries(expected, actual);
    }

    @Test
    public void testEmptyToolLicenseWorks() throws Exception {
        scratch.file("powertools/BUILD", "sh_binary(name = 'chainsaw',", "          srcs = ['chainsaw.sh'],", "          deps = [':gasoline'],", "          licenses = ['restricted'],", "          output_licenses=[])", "sh_binary(name = 'gasoline',", "          srcs = ['gasoline.sh'],", "          licenses = ['notice'])");
        scratch.file("gr/BUILD", "genrule(name = 'lumberjack',", "        outs=['firewood'],", "        tools=['//powertools:chainsaw'],", "        cmd='')");
        ConfiguredTarget lumberjack = getConfiguredTarget("//gr:lumberjack");
        Map<Label, License> actual = Maps.filterKeys(LicensingTests.getTransitiveLicenses(lumberjack), LicensingTests.CC_OR_JAVA_OR_SH_LABEL_FILTER);
        Map<Label, License> expected = LicensingTests.licenses("//powertools:chainsaw", "");
        LicensingTests.assertSameMapEntries(expected, actual);
    }

    @Test
    public void testToolLicenseIncludesDependenciesIfNoOutputLicense() throws Exception {
        scratch.file("powertools/BUILD", "sh_binary(name = 'chainsaw',", "          srcs = ['chainsaw.sh'],", "          deps = [':gasoline'],", "          licenses = ['restricted'])", "sh_binary(name = 'gasoline',", "          srcs = ['gasoline.sh'],", "          licenses = ['notice'])");
        scratch.file("gr/BUILD", "licenses(['unencumbered'])", "genrule(name = 'lumberjack',", "        outs=['firewood'],", "        tools=['//powertools:chainsaw'],", "        cmd='')");
        ConfiguredTarget lumberjack = getConfiguredTarget("//gr:lumberjack");
        Map<Label, License> actual = Maps.filterKeys(LicensingTests.getTransitiveLicenses(lumberjack), LicensingTests.CC_OR_JAVA_OR_SH_LABEL_FILTER);
        Map<Label, License> expected = LicensingTests.licenses("//powertools:chainsaw", "restricted", "//powertools:gasoline", "notice", "//gr:lumberjack", "unencumbered");
        LicensingTests.assertSameMapEntries(expected, actual);
    }

    @Test
    public void testToolLicenseRecursively() throws Exception {
        scratch.file("powertools/BUILD", "licenses(['restricted'])", "filegroup(name = 'chainsaw',", "          srcs = ['chainsaw.sh'],", "          licenses=['restricted'],", "          output_licenses=['unencumbered'])", "filegroup(name = 'forward',", "          srcs = [':chainsaw'])");
        scratch.file("gr/BUILD", "licenses(['unencumbered'])", "genrule(name = 'lumberjack',", "        outs=['firewood'],", "        tools=['//powertools:forward'],", "        cmd='')");
        ConfiguredTarget lumberjack = getConfiguredTarget("//gr:lumberjack");
        Map<Label, License> lumberjackActual = Maps.filterKeys(LicensingTests.getTransitiveLicenses(lumberjack), LicensingTests.CC_OR_JAVA_OR_SH_LABEL_FILTER);
        Map<Label, License> lumberjackExpected = LicensingTests.licenses("//powertools:chainsaw", "unencumbered", "//powertools:forward", "restricted", "//gr:lumberjack", "unencumbered");
        LicensingTests.assertSameMapEntries(lumberjackExpected, lumberjackActual);
    }

    @Test
    public void testHostAttributeCanBeTool() throws Exception {
        scratch.file("test/BUILD", "genrule(", "    name='gen',", "    outs=['hello.txt'],", "    cmd='exit 1',", "    tools=['//test:compiler'],", ")", "sh_binary(", "    name='compiler',", "    srcs=['compiler.sh'],", "    licenses=['restricted'],", "    output_licenses=['notice'],", ")");
        ConfiguredTarget target = getConfiguredTarget("//test:gen");
        Map<Label, License> actual = Maps.filterKeys(LicensingTests.getTransitiveLicenses(target), LicensingTests.CC_OR_JAVA_OR_SH_LABEL_FILTER);
        Map<Label, License> expected = LicensingTests.licenses("//test:compiler", "notice");
        LicensingTests.assertSameMapEntries(expected, actual);
    }

    @Test
    public void testNonHostAttributeCannotBeTool() throws Exception {
        scratch.file("keyboardmonkeys/BUILD", "genrule(name='million_monkeys',", "        licenses=['restricted'],", "        output_licenses=['notice'],", "        outs=['source.sh'],", "        cmd='type furiously on typewriters')");
        scratch.file("product/BUILD", "sh_binary(name='product',", "          srcs=['//keyboardmonkeys:source.sh'])");
        ConfiguredTarget product = getConfiguredTarget("//product:product");
        Map<Label, License> actual = Maps.filterKeys(LicensingTests.getTransitiveLicenses(product), LicensingTests.CC_OR_JAVA_OR_SH_LABEL_FILTER);
        Map<Label, License> expected = LicensingTests.licenses("//keyboardmonkeys:million_monkeys", "restricted");
        LicensingTests.assertSameMapEntries(expected, actual);
    }

    @Test
    public void testCcToolchainLicenseOverride() throws Exception {
        scratch.file("c/BUILD", "filegroup(name = 'dynamic-runtime-libs-cherry', srcs = [], licenses = ['restricted'])", "cc_toolchain(", "    name = 'c',", "    toolchain_identifier = 'toolchain-identifier-k8',", "    output_licenses = ['notice'],", "    cpu = 'cherry',", "    ar_files = 'ar-cherry',", "    as_files = 'as-cherry',", "    compiler_files = 'compile-cherry',", "    dwp_files = 'dwp-cherry',", "    coverage_files = 'gcov-cherry',", "    linker_files = 'link-cherry',", "    strip_files = ':every-file',", "    objcopy_files = 'objcopy-cherry',", "    all_files = ':every-file',", "    dynamic_runtime_lib = 'dynamic-runtime-libs-cherry',", "    static_runtime_lib = 'static-runtime-libs-cherry')");
        scratch.file("c/CROSSTOOL", MockCcSupport.EMPTY_CROSSTOOL);
        ConfiguredTarget target = getConfiguredTarget("//c:c");
        Map<Label, License> expected = LicensingTests.licenses("//c:c", "notice");
        Map<Label, License> actual = LicensingTests.getTransitiveLicenses(target);
        LicensingTests.assertSameMapEntries(expected, actual);
    }

    @Test
    public void testLicenseParsing() throws Exception {
        License l1 = License.parseLicense(Arrays.asList("restricted", "exception=//a:a"));
        License l2 = License.parseLicense(Arrays.asList("restricted", "reciprocal", "exception=//a:a"));
        License l3 = License.parseLicense(Arrays.asList("by_exception_only", "reciprocal", "exception=//a:a", "exception=//b:b"));
        assertThat(l1.toString()).isEqualTo("[restricted] with exceptions [//a:a]");
        assertThat(l2.toString()).isEqualTo("[restricted, reciprocal] with exceptions [//a:a]");
        assertThat(l3.toString()).isEqualTo("[by_exception_only, reciprocal] with exceptions [//a:a, //b:b]");
    }

    @Test
    public void testDualLicensedUsesLeastRestrictive() throws Exception {
        scratch.file("user/BUILD", "sh_binary(name = 'user',", "          srcs = ['user.sh'],", "          deps = ['//used'])");
        scratch.file("used/BUILD", "licenses(['restricted', 'permissive'])", "sh_library(name = 'used',", "        srcs=['used.sh'])");
        ConfiguredTarget used = getConfiguredTarget("//used");
        Map<Label, License> usedActual = Maps.filterKeys(LicensingTests.getTransitiveLicenses(used), AnalysisMock.get().ccSupport().labelFilter());
        Label usedLabel = Label.parseAbsolute("//used", ImmutableMap.of());
        License license = usedActual.get(usedLabel);
        LicenseCheckingModule.checkCompatibility(license, EnumSet.of(DistributionType.CLIENT), getTarget("//user"), usedLabel, reporter, false);
        assertNoEvents();
    }

    /**
     * Regression test for b/9811855.
     */
    @Test
    public void testFilegroupLicenses() throws Exception {
        scratch.file("user/BUILD", "sh_binary(name = 'user',", "          srcs = ['user.sh'],", "          deps = ['//used'])");
        scratch.file("used/BUILD", "licenses(['restricted'])", "filegroup(name = 'used',", "        licenses=['unencumbered'],", "        srcs=['used.sh'])");// used.sh is not 'restricted'

        ConfiguredTarget used = getConfiguredTarget("//used");
        Map<Label, License> usedActual = Maps.filterKeys(LicensingTests.getTransitiveLicenses(used), AnalysisMock.get().ccSupport().labelFilter());
        Label usedLabel = Label.parseAbsolute("//used", ImmutableMap.of());
        License license = usedActual.get(usedLabel);
        LicenseCheckingModule.checkCompatibility(license, EnumSet.of(DistributionType.CLIENT), getTarget("//user"), usedLabel, reporter, false);
        assertNoEvents();
    }

    /**
     * Regression test for b/4159051: "Latest blaze release broke --check_licenses"
     */
    @Test
    public void testLicenseCheckingTakesOnlyOneSelectBranch() throws Exception {
        scratch.file("eden/BUILD", "config_setting(", "    name = 'config_a',", "    values = {'define': 'mode=a'})", "cc_library(name = 'eve',", "    deps = select({", "        ':config_a': ['//third_party/fruit:peach'],", "        '//conditions:default': ['//third_party/fruit:apple'],", "    }),", "    distribs = ['client'])");
        scratch.file("third_party/fruit/BUILD", "cc_library(name='apple', licenses=['restricted'])", "cc_library(name='peach', licenses=['unencumbered'])");
        useConfiguration("--check_licenses", "--define", "mode=a");
        ConfiguredTarget eve = getConfiguredTarget("//eden:eve");
        Map<Label, License> eveActual = Maps.filterKeys(LicensingTests.getTransitiveLicenses(eve), AnalysisMock.get().ccSupport().labelFilter());
        Map<Label, License> eveExpected = LicensingTests.licenses("//third_party/fruit:peach", "unencumbered");
        LicensingTests.assertSameMapEntries(eveExpected, eveActual);
        assertNoEvents();
    }

    @Test
    public void configSettingsHaveNoLicense() throws Exception {
        scratch.file("third_party/restricted/BUILD", "licenses(['by_exception_only'])", "config_setting(", "    name = 'a_setting',", "    values = {'define': 'mode=a'})");
        useConfiguration("--check_licenses", "--define", "mode=a");
        ConfiguredTarget configSetting = getConfiguredTarget("//third_party/restricted:a_setting");
        assertThat(configSetting.getProvider(LicensesProvider.class).getTransitiveLicenses()).isEmpty();
    }

    @Test
    public void testJavaToolchainOutputLicense() throws Exception {
        scratch.file("java/a/BUILD", "java_toolchain(", "  name = 'toolchain',", "  licenses = ['restricted'],", "  output_licenses = ['unencumbered'],", "  source_version = '8',", "  target_version = '8',", "  bootclasspath = [':bcp'],", "  extclasspath = [':ecp'],", "  javac = [':langtools'],", "  javabuilder = ['javabuilder'],", "  header_compiler = ['header_compiler'],", "  singlejar = ['singlejar'],", "  genclass = ['genclass'],", "  ijar = ['ijar'])", "java_binary(", "  name = 'a',", "  srcs = ['a.java'])");
        useConfiguration("--java_toolchain=//java/a:toolchain", "--check_licenses");
        ConfiguredTarget bin = getConfiguredTarget("//java/a:a");
        Map<Label, License> licenses = LicensingTests.getTransitiveLicenses(bin);
        License toolchainLicense = licenses.get(Label.parseAbsoluteUnchecked("//java/a:toolchain"));
        assertThat(toolchainLicense).isNotEqualTo(License.parseLicense(ImmutableList.of("restricted")));
        assertThat(toolchainLicense).isEqualTo(License.parseLicense(ImmutableList.of("unencumbered")));
    }

    @Test
    public void testTargetLicenseEquality() throws Exception {
        Label label1 = Label.parseAbsolute("//foo", ImmutableMap.of());
        Label label2 = Label.parseAbsolute("//bar", ImmutableMap.of());
        License restricted = License.parseLicense(ImmutableList.of("restricted"));
        License unencumbered = License.parseLicense(ImmutableList.of("unencumbered"));
        new EqualsTester().addEqualityGroup(new com.google.devtools.build.lib.analysis.LicensesProvider.TargetLicense(label1, restricted), new com.google.devtools.build.lib.analysis.LicensesProvider.TargetLicense(label1, restricted)).addEqualityGroup(new com.google.devtools.build.lib.analysis.LicensesProvider.TargetLicense(label2, restricted), new com.google.devtools.build.lib.analysis.LicensesProvider.TargetLicense(label2, restricted)).addEqualityGroup(new com.google.devtools.build.lib.analysis.LicensesProvider.TargetLicense(label2, unencumbered), new com.google.devtools.build.lib.analysis.LicensesProvider.TargetLicense(label2, unencumbered)).testEquals();
    }

    /**
     * Regression fix for https://github.com/bazelbuild/bazel/issues/7194.
     */
    @Test
    public void testStarlarkLicensesAttributeCanUseUseCustomDefault() throws Exception {
        scratch.file("foo/rules.bzl", "def _myrule_impl(ctx):", "    return []", "", "myrule = rule(", "    implementation = _myrule_impl,", "    attrs = {", "        'licenses': attr.string(default = 'custom_licenses_default'),", "    }", ")");
        scratch.file("foo/BUILD", "load('//foo:rules.bzl', 'myrule')", "myrule(name = 'hi')");
        assertThat(RawAttributeMapper.of(((Rule) (getTarget("//foo:hi")))).get("licenses", STRING)).isEqualTo("custom_licenses_default");
        assertNoEvents();
    }
}

