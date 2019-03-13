package org.mockserver.matchers;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class ParameterStringMatcherTest {
    @Test
    public void shouldMatchMatchingString() {
        Assert.assertTrue(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
        Assert.assertTrue(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
    }

    @Test
    public void shouldNotMatchMatchingStringWhenNotApplied() {
        // given
        Assert.assertTrue(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
        // then - not matcher
        Assert.assertFalse(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
        // and - not parameter
        Assert.assertFalse(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
        // and - not parameter
        Assert.assertTrue(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
    }

    @Test
    public void shouldMatchMatchingStringWithNotParameterAndNormalParameter() {
        // not matching parameter
        Assert.assertFalse(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
        // not extra parameter
        Assert.assertTrue(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
        // not only parameter
        Assert.assertTrue(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
        // not all parameters (but matching)
        Assert.assertFalse(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
        // not all parameters (but not matching name)
        Assert.assertFalse(matches(null, ("" + (("notParameterOneName=parameterOneValueOne" + "&notParameterOneName=parameterOneValueTwo") + "&notParameterTwoName=parameterTwoValue"))));
        // not all parameters (but not matching value)
        Assert.assertFalse(matches(null, ("" + (("parameterOneName=notParameterOneValueOne" + "&parameterOneName=notParameterOneValueTwo") + "&parameterTwoName=notParameterTwoValue"))));
    }

    @Test
    public void shouldMatchMatchingStringWithOnlyParameter() {
        Assert.assertTrue(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
        Assert.assertFalse(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
        Assert.assertFalse(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
        Assert.assertTrue(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
    }

    @Test
    public void shouldMatchMatchingStringWithOnlyParameterForEmptyBody() {
        Assert.assertTrue(matches(null, "parameterThree=parameterThreeValueOne"));
        Assert.assertFalse(matches(null, ""));
        Assert.assertTrue(matches(null, ""));
    }

    @Test
    public void shouldNotMatchMatchingStringWithNotParameterAndNormalParameter() {
        Assert.assertFalse(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
    }

    @Test
    public void shouldNotMatchMatchingStringWithOnlyNotParameter() {
        Assert.assertFalse(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
    }

    @Test
    public void shouldNotMatchMatchingStringWithOnlyNotParameterForBodyWithSingleParameter() {
        Assert.assertFalse(matches(null, ("" + "parameterTwoName=parameterTwoValue")));
    }

    @Test
    public void shouldMatchNullExpectation() {
        Assert.assertTrue(matches(null, "some_value"));
    }

    @Test
    public void shouldNotMatchNullExpectationWhenNotApplied() {
        Assert.assertFalse(matches(null, "some_value"));
    }

    @Test
    public void shouldMatchEmptyExpectation() {
        Assert.assertTrue(matches(null, "some_value"));
    }

    @Test
    public void shouldNotMatchEmptyExpectationWhenNotApplied() {
        Assert.assertFalse(matches(null, "some_value"));
    }

    @Test
    public void shouldNotMatchIncorrectParameterName() {
        Assert.assertFalse(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&INCORRECTParameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
    }

    @Test
    public void shouldMatchIncorrectParameterNameWhenNotApplied() {
        Assert.assertTrue(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&INCORRECTParameterOneName=parameterOneValueTwo") + "&parameterTwoName=parameterTwoValue"))));
    }

    @Test
    public void shouldNotMatchIncorrectParameterValue() {
        Assert.assertFalse(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=INCORRECTParameterTwoValue"))));
    }

    @Test
    public void shouldMatchIncorrectParameterValueWhenNotApplied() {
        Assert.assertTrue(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&parameterTwoName=INCORRECTParameterTwoValue"))));
    }

    @Test
    public void shouldNotMatchIncorrectParameterNameAndValue() {
        Assert.assertFalse(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&INCORRECTParameterTwoName=INCORRECTParameterTwoValue"))));
    }

    @Test
    public void shouldMatchIncorrectParameterNameAndValueWhenNotApplied() {
        Assert.assertTrue(matches(null, ("" + (("parameterOneName=parameterOneValueOne" + "&parameterOneName=parameterOneValueTwo") + "&INCORRECTParameterTwoName=INCORRECTParameterTwoValue"))));
    }

    @Test
    public void shouldNotMatchNullParameterValue() {
        Assert.assertFalse(matches(null, ("" + ("parameterOneName=parameterValueOne" + "&parameterTwoName="))));
    }

    @Test
    public void shouldMatchNullParameterValueWhenNotApplied() {
        Assert.assertTrue(matches(null, ("" + ("parameterOneName=parameterValueOne" + "&parameterTwoName="))));
    }

    @Test
    public void shouldMatchNullParameterValueInExpectation() {
        Assert.assertTrue(matches(null, ("" + ("parameterOneName=parameterValueOne" + "&parameterTwoName=parameterTwoValue"))));
    }

    @Test
    public void shouldNotMatchMissingParameter() {
        Assert.assertFalse(matches(null, ("" + "parameterOneName=parameterValueOne")));
    }

    @Test
    public void shouldMatchMissingParameterWhenNotApplied() {
        Assert.assertTrue(matches(null, ("" + "parameterOneName=parameterValueOne")));
    }

    @Test
    public void shouldMatchNullTest() {
        Assert.assertTrue(new ParameterStringMatcher(new org.mockserver.logging.MockServerLogger(), new org.mockserver.model.Parameters()).matches(null, null));
    }

    @Test
    public void shouldNotMatchNullTestWhenNotApplied() {
        Assert.assertFalse(NotMatcher.not(new ParameterStringMatcher(new org.mockserver.logging.MockServerLogger(), new org.mockserver.model.Parameters())).matches(null, null));
    }

    @Test
    public void shouldMatchEmptyTest() {
        Assert.assertTrue(matches(null, ""));
    }

    @Test
    public void shouldNotMatchEmptyTestWhenNotApplied() {
        Assert.assertFalse(matches(null, ""));
    }
}

