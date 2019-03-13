/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.skylarkinterface.processor;


import com.google.devtools.build.lib.events.Location;
import com.google.devtools.build.lib.skylarkinterface.StarlarkContext;
import com.google.devtools.build.lib.syntax.Environment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for SkylarkCallableProcessor.
 */
@RunWith(JUnit4.class)
public final class SkylarkCallableProcessorTest {
    @Test
    public void testGoldenCase() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("GoldenCase.java")).processedWith(new SkylarkCallableProcessor()).compilesWithoutError();
    }

    @Test
    public void testPrivateMethod() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("PrivateMethod.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining("@SkylarkCallable annotated methods must be public.");
    }

    @Test
    public void testStaticMethod() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("StaticMethod.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining("@SkylarkCallable annotated methods cannot be static.");
    }

    @Test
    public void testStructFieldWithArguments() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("StructFieldWithArguments.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining(("@SkylarkCallable annotated methods with structField=true must have 0 user-supplied " + ("parameters. Expected 0 extra interpreter parameters, " + "but found 1 total parameters.")));
    }

    @Test
    public void testStructFieldWithInvalidInfo() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("StructFieldWithInvalidInfo.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining(("@SkylarkCallable-annotated methods with structField=true may not also specify " + "useAst, extraPositionals, or extraKeywords"));
    }

    @Test
    public void testStructFieldWithExtraArgs() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("StructFieldWithExtraArgs.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining(("@SkylarkCallable-annotated methods with structField=true may not also specify " + "useAst, extraPositionals, or extraKeywords"));
    }

    @Test
    public void testStructFieldWithExtraKeywords() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("StructFieldWithExtraKeywords.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining(("@SkylarkCallable-annotated methods with structField=true may not also specify " + "useAst, extraPositionals, or extraKeywords"));
    }

    @Test
    public void testDocumentationMissing() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("DocumentationMissing.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining("The 'doc' string must be non-empty if 'documented' is true.");
    }

    @Test
    public void testArgumentMissing() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("ArgumentMissing.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining(("@SkylarkCallable annotated method has 0 parameters, " + ("but annotation declared 1 user-supplied parameters " + "and 0 extra interpreter parameters.")));
    }

    @Test
    public void testEnvironmentMissing() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("EnvironmentMissing.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining((("Expected parameter index 2 to be the " + (Environment.class.getCanonicalName())) + " type, matching useEnvironment, but was java.lang.String"));
    }

    @Test
    public void testContextMissing() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("ContextMissing.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining((("Expected parameter index 2 to be the " + (StarlarkContext.class.getCanonicalName())) + " type, matching useContext, but was java.lang.String"));
    }

    @Test
    public void testLocationMissing() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("LocationMissing.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining((("Expected parameter index 2 to be the " + (Location.class.getCanonicalName())) + " type, matching useLocation, but was java.lang.String"));
    }

    @Test
    public void testSkylarkInfoBeforeParams() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("SkylarkInfoBeforeParams.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining((("Expected parameter index 3 to be the " + (Location.class.getCanonicalName())) + " type, matching useLocation, but was java.lang.Integer"));
    }

    @Test
    public void testSkylarkInfoParamsWrongOrder() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("SkylarkInfoParamsWrongOrder.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining(((("Expected parameter index 1 to be the " + (Location.class.getCanonicalName())) + " type, matching useLocation, but was ") + (Environment.class.getCanonicalName())));
    }

    @Test
    public void testTooManyArguments() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("TooManyArguments.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining(("@SkylarkCallable annotated method has 2 parameters, " + ("but annotation declared 1 user-supplied parameters " + "and 0 extra interpreter parameters.")));
    }

    @Test
    public void testInvalidParamNoneDefault() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("InvalidParamNoneDefault.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining("Parameter 'a_parameter' has 'None' default value but is not noneable.");
    }

    @Test
    public void testParamTypeConflict() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("ParamTypeConflict.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining(("Parameter 'a_parameter' has both 'type' and 'allowedTypes' specified." + " Only one may be specified."));
    }

    @Test
    public void testParamNeitherNamedNorPositional() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("ParamNeitherNamedNorPositional.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining("Parameter 'a_parameter' must be either positional or named");
    }

    @Test
    public void testNonDefaultParamAfterDefault() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("NonDefaultParamAfterDefault.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining(("Positional parameter 'two' has no default value but is specified " + "after one or more positional parameters with default values"));
    }

    @Test
    public void testPositionalParamAfterNonPositional() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("PositionalParamAfterNonPositional.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining("Positional parameter 'two' is specified after one or more non-positonal parameters");
    }

    @Test
    public void testPositionalOnlyParamAfterNamed() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("PositionalOnlyParamAfterNamed.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining("Positional-only parameter 'two' is specified after one or more named parameters");
    }

    @Test
    public void testExtraKeywordsOutOfOrder() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("ExtraKeywordsOutOfOrder.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining(("Expected parameter index 1 to be the " + ("com.google.devtools.build.lib.syntax.SkylarkDict<?,?> type, matching " + "extraKeywords, but was java.lang.String")));
    }

    @Test
    public void testExtraPositionalsMissing() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("ExtraPositionalsMissing.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining(("@SkylarkCallable annotated method has 3 parameters, but annotation declared " + "1 user-supplied parameters and 3 extra interpreter parameters."));
    }

    @Test
    public void testSelfCallWithNoName() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("SelfCallWithNoName.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining("@SkylarkCallable.name must be non-empty.");
    }

    @Test
    public void testSelfCallWithStructField() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("SelfCallWithStructField.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining("@SkylarkCallable-annotated methods with selfCall=true must have structField=false");
    }

    @Test
    public void testMultipleSelfCallMethods() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("MultipleSelfCallMethods.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining("Containing class has more than one selfCall method defined.");
    }

    @Test
    public void testEnablingAndDisablingFlag() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("EnablingAndDisablingFlag.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining(("Only one of @SkylarkCallable.enablingFlag and @SkylarkCallable.disablingFlag may be " + "specified."));
    }

    @Test
    public void testEnablingAndDisablingFlag_param() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("EnablingAndDisablingFlagParam.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining(("Parameter 'two' has enableOnlyWithFlag and disableWithFlag set. " + "At most one may be set"));
    }

    @Test
    public void testConflictingMethodNames() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("ConflictingMethodNames.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining(("Containing class has more than one method with name " + "'conflicting_method' defined"));
    }

    @Test
    public void testDisabledValueParamNoToggle() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("DisabledValueParamNoToggle.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining("Parameter 'two' has valueWhenDisabled set, but is always enabled");
    }

    @Test
    public void testToggledKwargsParam() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("ToggledKwargsParam.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining("The extraKeywords parameter may not be toggled by semantic flag");
    }

    @Test
    public void testToggledParamNoDisabledValue() throws Exception {
        assertAbout(javaSource()).that(SkylarkCallableProcessorTest.getFile("ToggledParamNoDisabledValue.java")).processedWith(new SkylarkCallableProcessor()).failsToCompile().withErrorContaining(("Parameter 'two' may be disabled by semantic flag, " + "thus valueWhenDisabled must be set"));
    }
}

