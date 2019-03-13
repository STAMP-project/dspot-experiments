package org.mockserver.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.NottableString;
import org.mockserver.model.Parameter;
import org.mockserver.model.Parameters;


/**
 *
 *
 * @author jamesdbloom
 */
public class QueryParameterMatcherTest {
    @Test
    public void shouldMatchMatchingParameter() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameter.*", "parameter.*"))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
    }

    @Test
    public void shouldNotMatchMatchingParameterWhenNotApplied() {
        // given
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
        // then - not matcher
        Assert.assertFalse(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue")))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
        // and - not parameter
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter(NottableString.not("parameterTwoName"), NottableString.not("parameterTwoValue")))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
        // and - not matcher and not parameter
        Assert.assertTrue(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter(NottableString.not("parameterTwoName"), NottableString.not("parameterTwoValue"))))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
    }

    @Test
    public void shouldMatchMatchingParameterWithNotParameterAndNormalParameter() {
        // not matching parameter
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter(NottableString.not("parameterTwoName"), NottableString.not("parameterTwoValue")))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
        // not extra parameter
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"), new Parameter(NottableString.not("parameterThree"), NottableString.not("parameterThreeValueOne")))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
        // not only parameter
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter(NottableString.not("parameterThree"), NottableString.not("parameterThreeValueOne")))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
        // not all parameters (but matching)
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter(NottableString.not("parameter.*"), NottableString.not(".*")))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
        // not all parameters (but not matching name)
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter(NottableString.not("parameter.*"), NottableString.not("parameter.*")))).matches(null, new Parameters().withEntries(new Parameter("notParameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("notParameterTwoName", "parameterTwoValue"))));
        // not all parameters (but not matching value)
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter(NottableString.not("parameter.*"), NottableString.not("parameter.*")))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "notParameterOneValueOne", "notParameterOneValueTwo"), new Parameter("parameterTwoName", "notParameterTwoValue"))));
    }

    @Test
    public void shouldMatchMatchingParameterWithOnlyParameter() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter(NottableString.not("parameterThree"), NottableString.not("parameterThreeValueOne")))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterThree", "parameterThreeValueOne"))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter(NottableString.not("parameterOneName"), NottableString.not("parameterOneValueOne"), NottableString.not("parameterOneValueTwo")))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
    }

    @Test
    public void shouldMatchMatchingParameterWithOnlyParameterForEmptyList() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Parameters()).matches(null, new Parameters().withEntries(new Parameter("parameterThree", "parameterThreeValueOne"))));
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterThree", "parameterThreeValueOne"))).matches(null, new Parameters()));
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter(NottableString.not("parameterThree"), NottableString.not("parameterThreeValueOne")))).matches(null, new Parameters()));
    }

    @Test
    public void shouldNotMatchMatchingParameterWithNotParameterAndNormalParameter() {
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter(NottableString.not("parameterTwoName"), NottableString.not("parameterTwoValue")))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
    }

    @Test
    public void shouldNotMatchMatchingParameterWithOnlyNotParameter() {
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter(NottableString.not("parameterTwoName"), NottableString.not("parameterTwoValue")))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
    }

    @Test
    public void shouldNotMatchMatchingParameterWithOnlyNotParameterForBodyWithSingleParameter() {
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter(NottableString.not("parameterTwoName"), NottableString.not("parameterTwoValue")))).matches(null, new Parameters().withEntries(new Parameter("parameterTwoName", "parameterTwoValue"))));
    }

    @Test
    public void shouldMatchNullExpectation() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), null).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
    }

    @Test
    public void shouldNotMatchNullExpectationWhenNotApplied() {
        Assert.assertFalse(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), null)).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
    }

    @Test
    public void shouldMatchEmptyExpectation() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Parameters()).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
    }

    @Test
    public void shouldNotMatchEmptyExpectationWhenNotApplied() {
        Assert.assertFalse(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Parameters())).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
    }

    @Test
    public void shouldNotMatchIncorrectParameterName() {
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("INCORRECTparameterTwoName", "parameterTwoValue"))));
    }

    @Test
    public void shouldMatchIncorrectParameterNameWhenNotApplied() {
        Assert.assertTrue(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue")))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("INCORRECTparameterTwoName", "parameterTwoValue"))));
    }

    @Test
    public void shouldNotMatchIncorrectParameterValue() {
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "INCORRECTparameterTwoValue"))));
    }

    @Test
    public void shouldMatchIncorrectParameterValueWhenNotApplied() {
        Assert.assertTrue(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue")))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "INCORRECTparameterTwoValue"))));
    }

    @Test
    public void shouldNotMatchIncorrectParameterNameAndValue() {
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("INCORRECTparameterTwoName", "INCORRECTparameterTwoValue"))));
    }

    @Test
    public void shouldMatchIncorrectParameterNameAndValueWhenNotApplied() {
        Assert.assertTrue(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue")))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("INCORRECTparameterTwoName", "INCORRECTparameterTwoValue"))));
    }

    @Test
    public void shouldMatchNullParameterValue() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName"))));
    }

    @Test
    public void shouldNotMatchNullParameterValueWhenNotApplied() {
        Assert.assertFalse(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue")))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName"))));
    }

    @Test
    public void shouldMatchNullParameterValueInExpectation() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", ""))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))));
    }

    @Test
    public void shouldNotMatchMissingParameter() {
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue"))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"))));
    }

    @Test
    public void shouldMatchMissingParameterWhenNotApplied() {
        Assert.assertTrue(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"), new Parameter("parameterTwoName", "parameterTwoValue")))).matches(null, new Parameters().withEntries(new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"))));
    }

    @Test
    public void shouldMatchNullTest() {
        Assert.assertTrue(matches(null, null));
    }

    @Test
    public void shouldNotMatchNullTestWhenNotApplied() {
        Assert.assertFalse(matches(null, null));
    }

    @Test
    public void shouldMatchEmptyTest() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Parameters()).matches(null, new Parameters()));
    }

    @Test
    public void shouldNotMatchEmptyTestWhenNotApplied() {
        Assert.assertFalse(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Parameters())).matches(null, new Parameters()));
    }
}

