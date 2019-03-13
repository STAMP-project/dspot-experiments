/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
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
package com.google.devtools.common.options;


import InvocationPolicy.Builder;
import com.google.devtools.build.lib.runtime.proto.InvocationPolicyOuterClass.DisallowValues;
import com.google.devtools.build.lib.runtime.proto.InvocationPolicyOuterClass.InvocationPolicy;
import com.google.devtools.build.lib.runtime.proto.InvocationPolicyOuterClass.UseDefault;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test InvocationPolicies with the DisallowValues operation.
 */
@RunWith(JUnit4.class)
public class InvocationPolicyDisallowValuesTest extends InvocationPolicyEnforcerTestBase {
    // Useful constants
    public static final String BUILD_COMMAND = "build";

    public static final String DISALLOWED_VALUE_1 = "foo";

    public static final String DISALLOWED_VALUE_2 = "bar";

    public static final String UNFILTERED_VALUE = "baz";

    @Test
    public void testDisallowValuesAllowsValue() throws Exception {
        InvocationPolicy.Builder invocationPolicyBuilder = InvocationPolicy.newBuilder();
        invocationPolicyBuilder.addFlagPoliciesBuilder().setFlagName("test_string").getDisallowValuesBuilder().addDisallowedValues(InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_1).addDisallowedValues(InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_2);
        InvocationPolicyEnforcer enforcer = InvocationPolicyEnforcerTestBase.createOptionsPolicyEnforcer(invocationPolicyBuilder);
        parser.parse(("--test_string=" + (InvocationPolicyDisallowValuesTest.UNFILTERED_VALUE)));
        // Option should be "baz" as specified by the user.
        TestOptions testOptions = getTestOptions();
        assertThat(testOptions.testString).isEqualTo(InvocationPolicyDisallowValuesTest.UNFILTERED_VALUE);
        enforcer.enforce(parser, InvocationPolicyDisallowValuesTest.BUILD_COMMAND);
        // Still "baz" since "baz" is allowed by the policy.
        testOptions = getTestOptions();
        assertThat(testOptions.testString).isEqualTo(InvocationPolicyDisallowValuesTest.UNFILTERED_VALUE);
    }

    @Test
    public void testDisallowValuesDisallowsValue() throws Exception {
        InvocationPolicy.Builder invocationPolicyBuilder = InvocationPolicy.newBuilder();
        invocationPolicyBuilder.addFlagPoliciesBuilder().setFlagName("test_string").getDisallowValuesBuilder().addDisallowedValues(InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_1).addDisallowedValues(InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_2);
        InvocationPolicyEnforcer enforcer = InvocationPolicyEnforcerTestBase.createOptionsPolicyEnforcer(invocationPolicyBuilder);
        parser.parse(("--test_string=" + (InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_1)));
        // Option should be "foo" as specified by the user.
        TestOptions testOptions = getTestOptions();
        assertThat(testOptions.testString).isEqualTo(InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_1);
        try {
            enforcer.enforce(parser, InvocationPolicyDisallowValuesTest.BUILD_COMMAND);
            Assert.fail();
        } catch (OptionsParsingException e) {
            // expected, since foo is disallowed.
        }
    }

    @Test
    public void testDisallowValuesDisallowsMultipleValues() throws Exception {
        InvocationPolicy.Builder invocationPolicyBuilder = InvocationPolicy.newBuilder();
        invocationPolicyBuilder.addFlagPoliciesBuilder().setFlagName("test_multiple_string").getDisallowValuesBuilder().addDisallowedValues(InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_1).addDisallowedValues(InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_2);
        InvocationPolicyEnforcer enforcer = InvocationPolicyEnforcerTestBase.createOptionsPolicyEnforcer(invocationPolicyBuilder);
        parser.parse(("--test_multiple_string=" + (InvocationPolicyDisallowValuesTest.UNFILTERED_VALUE)), ("--test_multiple_string=" + (InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_2)));
        // Option should be "baz" and "bar" as specified by the user.
        TestOptions testOptions = getTestOptions();
        assertThat(testOptions.testMultipleString).containsExactly(InvocationPolicyDisallowValuesTest.UNFILTERED_VALUE, InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_2).inOrder();
        try {
            enforcer.enforce(parser, InvocationPolicyDisallowValuesTest.BUILD_COMMAND);
            Assert.fail();
        } catch (OptionsParsingException e) {
            // expected, since bar is disallowed.
        }
    }

    @Test
    public void testDisallowValuesSetsNewValue() throws Exception {
        InvocationPolicy.Builder invocationPolicyBuilder = InvocationPolicy.newBuilder();
        invocationPolicyBuilder.addFlagPoliciesBuilder().setFlagName("test_string").getDisallowValuesBuilder().addDisallowedValues(InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_1).setNewValue(InvocationPolicyDisallowValuesTest.UNFILTERED_VALUE);
        InvocationPolicyEnforcer enforcer = InvocationPolicyEnforcerTestBase.createOptionsPolicyEnforcer(invocationPolicyBuilder);
        parser.parse(("--test_string=" + (InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_1)));
        TestOptions testOptions = getTestOptions();
        assertThat(testOptions.testString).isEqualTo(InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_1);
        enforcer.enforce(parser, InvocationPolicyDisallowValuesTest.BUILD_COMMAND);
        // Should now be "baz" because the policy forces disallowed values to "baz"
        testOptions = getTestOptions();
        assertThat(testOptions.testString).isEqualTo(InvocationPolicyDisallowValuesTest.UNFILTERED_VALUE);
    }

    @Test
    public void testDisallowValuesSetsDefaultValue() throws Exception {
        InvocationPolicy.Builder invocationPolicyBuilder = InvocationPolicy.newBuilder();
        invocationPolicyBuilder.addFlagPoliciesBuilder().setFlagName("test_string").getDisallowValuesBuilder().addDisallowedValues(InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_1).getUseDefaultBuilder();
        InvocationPolicyEnforcer enforcer = InvocationPolicyEnforcerTestBase.createOptionsPolicyEnforcer(invocationPolicyBuilder);
        parser.parse(("--test_string=" + (InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_1)));
        TestOptions testOptions = getTestOptions();
        assertThat(testOptions.testString).isEqualTo(InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_1);
        enforcer.enforce(parser, InvocationPolicyDisallowValuesTest.BUILD_COMMAND);
        testOptions = getTestOptions();
        assertThat(testOptions.testString).isEqualTo(TestOptions.TEST_STRING_DEFAULT);
    }

    @Test
    public void testDisallowValuesSetsDefaultValueForRepeatableFlag() throws Exception {
        InvocationPolicy.Builder invocationPolicyBuilder = InvocationPolicy.newBuilder();
        invocationPolicyBuilder.addFlagPoliciesBuilder().setFlagName("test_multiple_string").getDisallowValuesBuilder().addDisallowedValues(InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_1).getUseDefaultBuilder();
        InvocationPolicyEnforcer enforcer = InvocationPolicyEnforcerTestBase.createOptionsPolicyEnforcer(invocationPolicyBuilder);
        parser.parse(("--test_multiple_string=" + (InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_1)));
        TestOptions testOptions = getTestOptions();
        assertThat(testOptions.testMultipleString).containsExactly(InvocationPolicyDisallowValuesTest.DISALLOWED_VALUE_1);
        enforcer.enforce(parser, InvocationPolicyDisallowValuesTest.BUILD_COMMAND);
        testOptions = getTestOptions();
        // Default for repeatable flags is always empty.
        assertThat(testOptions.testMultipleString).isEmpty();
    }

    @Test
    public void testDisallowValuesRaisesErrorIfDefaultIsDisallowedAndSetsUseDefault() throws Exception {
        InvocationPolicy.Builder invocationPolicyBuilder = InvocationPolicy.newBuilder();
        invocationPolicyBuilder.addFlagPoliciesBuilder().setFlagName("test_string").getDisallowValuesBuilder().addDisallowedValues(TestOptions.TEST_STRING_DEFAULT).getUseDefaultBuilder();
        InvocationPolicyEnforcer enforcer = InvocationPolicyEnforcerTestBase.createOptionsPolicyEnforcer(invocationPolicyBuilder);
        try {
            enforcer.enforce(parser, InvocationPolicyDisallowValuesTest.BUILD_COMMAND);
            Assert.fail();
        } catch (OptionsParsingException e) {
            assertThat(e).hasMessageThat().contains("but also specifies to use the default value");
        }
    }

    @Test
    public void testDisallowValuesSetsNewValueWhenDefaultIsDisallowed() throws Exception {
        InvocationPolicy.Builder invocationPolicyBuilder = InvocationPolicy.newBuilder();
        invocationPolicyBuilder.addFlagPoliciesBuilder().setFlagName("test_string").getDisallowValuesBuilder().addDisallowedValues(TestOptions.TEST_STRING_DEFAULT).setNewValue(InvocationPolicyDisallowValuesTest.UNFILTERED_VALUE);
        InvocationPolicyEnforcer enforcer = InvocationPolicyEnforcerTestBase.createOptionsPolicyEnforcer(invocationPolicyBuilder);
        // Option should be the default since the use didn't specify a value.
        TestOptions testOptions = getTestOptions();
        assertThat(testOptions.testString).isEqualTo(TestOptions.TEST_STRING_DEFAULT);
        enforcer.enforce(parser, InvocationPolicyDisallowValuesTest.BUILD_COMMAND);
        // Should now be "baz" because the policy set the new default to "baz"
        testOptions = getTestOptions();
        assertThat(testOptions.testString).isEqualTo(InvocationPolicyDisallowValuesTest.UNFILTERED_VALUE);
    }

    @Test
    public void testDisallowValuesDisallowsFlagDefaultButNoPolicyDefault() throws Exception {
        InvocationPolicy.Builder invocationPolicyBuilder = InvocationPolicy.newBuilder();
        // No new default is set
        invocationPolicyBuilder.addFlagPoliciesBuilder().setFlagName("test_string").getDisallowValuesBuilder().addDisallowedValues(TestOptions.TEST_STRING_DEFAULT);
        InvocationPolicyEnforcer enforcer = InvocationPolicyEnforcerTestBase.createOptionsPolicyEnforcer(invocationPolicyBuilder);
        // Option should be the default since the use didn't specify a value.
        TestOptions testOptions = getTestOptions();
        assertThat(testOptions.testString).isEqualTo(TestOptions.TEST_STRING_DEFAULT);
        try {
            enforcer.enforce(parser, InvocationPolicyDisallowValuesTest.BUILD_COMMAND);
            Assert.fail();
        } catch (OptionsParsingException e) {
            // expected.
        }
    }

    @Test
    public void testDisallowValuesDisallowsListConverterFlag() throws Exception {
        InvocationPolicy.Builder invocationPolicyBuilder = InvocationPolicy.newBuilder();
        invocationPolicyBuilder.addFlagPoliciesBuilder().setFlagName("test_list_converters").getDisallowValuesBuilder().addDisallowedValues("a");
        InvocationPolicyEnforcer enforcer = InvocationPolicyEnforcerTestBase.createOptionsPolicyEnforcer(invocationPolicyBuilder);
        parser.parse("--test_list_converters=a,b,c");
        TestOptions testOptions = getTestOptions();
        assertThat(testOptions.testListConverters).isEqualTo(Arrays.asList("a", "b", "c"));
        try {
            enforcer.enforce(parser, InvocationPolicyDisallowValuesTest.BUILD_COMMAND);
            Assert.fail();
        } catch (OptionsParsingException e) {
            assertThat(e).hasMessageThat().contains(("Flag value 'a' for option '--test_list_converters' is not allowed by invocation " + "policy"));
        }
    }

    @Test
    public void testAllowValuesWithNullDefault_DoesNotConfuseNullForDefault() throws Exception {
        InvocationPolicy.Builder invocationPolicyBuilder = InvocationPolicy.newBuilder();
        invocationPolicyBuilder.addFlagPoliciesBuilder().setFlagName("test_string_null_by_default").setDisallowValues(DisallowValues.newBuilder().addDisallowedValues("null").setUseDefault(UseDefault.newBuilder()));
        InvocationPolicyEnforcer enforcer = InvocationPolicyEnforcerTestBase.createOptionsPolicyEnforcer(invocationPolicyBuilder);
        // Check the value before invocation policy enforcement.
        parser.parse("--test_string_null_by_default=null");
        TestOptions testOptions = getTestOptions();
        assertThat(testOptions.testStringNullByDefault).isEqualTo("null");
        // Check the value afterwards.
        enforcer.enforce(parser, InvocationPolicyDisallowValuesTest.BUILD_COMMAND);
        testOptions = getTestOptions();
        assertThat(testOptions.testStringNullByDefault).isNull();
    }
}

