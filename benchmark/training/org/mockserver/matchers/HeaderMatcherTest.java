package org.mockserver.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.Header;
import org.mockserver.model.Headers;
import org.mockserver.model.NottableString;


/**
 *
 *
 * @author jamesdbloom
 */
public class HeaderMatcherTest {
    @Test
    public void shouldMatchMatchingHeader() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("header.*", "header.*"))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
    }

    @Test
    public void shouldNotMatchMatchingHeaderWhenNotApplied() {
        // given
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
        // then - not matcher
        Assert.assertFalse(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue")))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
        // and - not header
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header(NottableString.not("headerTwoName"), NottableString.not("headerTwoValue")))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
        // and - not matcher and not header
        Assert.assertTrue(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header(NottableString.not("headerTwoName"), NottableString.not("headerTwoValue"))))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
    }

    @Test
    public void shouldMatchMatchingHeaderWithNotHeaderAndNormalHeader() {
        // not matching header
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header(NottableString.not("headerTwoName"), NottableString.not("headerTwoValue")))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
        // not extra header
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"), new Header(NottableString.not("headerThree"), NottableString.not("headerThreeValueOne")))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
        // not only header
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header(NottableString.not("headerThree"), NottableString.not("headerThreeValueOne")))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
        // not all headers (but matching)
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header(NottableString.not("header.*"), NottableString.not(".*")))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
        // not all headers (but not matching name)
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header(NottableString.not("header.*"), NottableString.not("header.*")))).matches(null, new Headers().withEntries(new Header("notHeaderOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("notHeaderTwoName", "headerTwoValue"))));
        // not all headers (but not matching value)
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header(NottableString.not("header.*"), NottableString.not("header.*")))).matches(null, new Headers().withEntries(new Header("headerOneName", "notHeaderOneValueOne", "notHeaderOneValueTwo"), new Header("headerTwoName", "notHeaderTwoValue"))));
    }

    @Test
    public void shouldMatchMatchingHeaderWithOnlyHeader() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header(NottableString.not("headerThree"), NottableString.not("headerThreeValueOne")))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerThree", "headerThreeValueOne"))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header(NottableString.not("headerOneName"), NottableString.not("headerOneValueOne"), NottableString.not("headerOneValueTwo")))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
    }

    @Test
    public void shouldMatchMatchingHeaderWithOnlyHeaderForEmptyList() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Headers()).matches(null, new Headers().withEntries(new Header("headerThree", "headerThreeValueOne"))));
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerThree", "headerThreeValueOne"))).matches(null, new Headers()));
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header(NottableString.not("headerThree"), NottableString.not("headerThreeValueOne")))).matches(null, new Headers()));
    }

    @Test
    public void shouldNotMatchMatchingHeaderWithNotHeaderAndNormalHeader() {
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header(NottableString.not("headerTwoName"), NottableString.not("headerTwoValue")))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
    }

    @Test
    public void shouldNotMatchMatchingHeaderWithOnlyNotHeader() {
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header(NottableString.not("headerTwoName"), NottableString.not("headerTwoValue")))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
    }

    @Test
    public void shouldNotMatchMatchingHeaderWithOnlyNotHeaderForBodyWithSingleHeader() {
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header(NottableString.not("headerTwoName"), NottableString.not("headerTwoValue")))).matches(null, new Headers().withEntries(new Header("headerTwoName", "headerTwoValue"))));
    }

    @Test
    public void shouldMatchNullExpectation() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), null).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
    }

    @Test
    public void shouldNotMatchNullExpectationWhenNotApplied() {
        Assert.assertFalse(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), null)).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
    }

    @Test
    public void shouldMatchEmptyExpectation() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Headers()).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
    }

    @Test
    public void shouldNotMatchEmptyExpectationWhenNotApplied() {
        Assert.assertFalse(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Headers())).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
    }

    @Test
    public void shouldNotMatchIncorrectHeaderName() {
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("INCORRECTheaderTwoName", "headerTwoValue"))));
    }

    @Test
    public void shouldMatchIncorrectHeaderNameWhenNotApplied() {
        Assert.assertTrue(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue")))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("INCORRECTheaderTwoName", "headerTwoValue"))));
    }

    @Test
    public void shouldNotMatchIncorrectHeaderValue() {
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "INCORRECTheaderTwoValue"))));
    }

    @Test
    public void shouldMatchIncorrectHeaderValueWhenNotApplied() {
        Assert.assertTrue(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue")))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "INCORRECTheaderTwoValue"))));
    }

    @Test
    public void shouldNotMatchIncorrectHeaderNameAndValue() {
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("INCORRECTheaderTwoName", "INCORRECTheaderTwoValue"))));
    }

    @Test
    public void shouldMatchIncorrectHeaderNameAndValueWhenNotApplied() {
        Assert.assertTrue(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue")))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("INCORRECTheaderTwoName", "INCORRECTheaderTwoValue"))));
    }

    @Test
    public void shouldMatchNullHeaderValue() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName"))));
    }

    @Test
    public void shouldNotMatchNullHeaderValueWhenNotApplied() {
        Assert.assertFalse(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue")))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName"))));
    }

    @Test
    public void shouldMatchNullHeaderValueInExpectation() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", ""))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))));
    }

    @Test
    public void shouldNotMatchMissingHeader() {
        Assert.assertFalse(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue"))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"))));
    }

    @Test
    public void shouldMatchMissingHeaderWhenNotApplied() {
        Assert.assertTrue(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"), new Header("headerTwoName", "headerTwoValue")))).matches(null, new Headers().withEntries(new Header("headerOneName", "headerOneValueOne", "headerOneValueTwo"))));
    }

    @Test
    public void shouldMatchNullTest() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Headers()).matches(null, new Headers()));
    }

    @Test
    public void shouldNotMatchNullTestWhenNotApplied() {
        Assert.assertFalse(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Headers())).matches(null, new Headers()));
    }

    @Test
    public void shouldMatchEmptyTest() {
        Assert.assertTrue(new MultiValueMapMatcher(new MockServerLogger(), new Headers()).matches(null, new Headers()));
    }

    @Test
    public void shouldNotMatchEmptyTestWhenNotApplied() {
        Assert.assertFalse(NotMatcher.not(new MultiValueMapMatcher(new MockServerLogger(), new Headers())).matches(null, new Headers()));
    }
}

