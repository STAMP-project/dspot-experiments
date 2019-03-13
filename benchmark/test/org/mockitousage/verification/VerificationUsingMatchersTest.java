/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class VerificationUsingMatchersTest extends TestBase {
    private IMethods mock;

    @Test
    public void shouldVerifyExactNumberOfInvocationsUsingMatcher() {
        mock.simpleMethod(1);
        mock.simpleMethod(2);
        mock.simpleMethod(3);
        Mockito.verify(mock, Mockito.times(3)).simpleMethod(ArgumentMatchers.anyInt());
    }

    @Test
    public void shouldVerifyUsingSameMatcher() {
        Object one = new String("1243");
        Object two = new String("1243");
        Object three = new String("1243");
        Assert.assertNotSame(one, two);
        Assert.assertEquals(one, two);
        Assert.assertEquals(two, three);
        mock.oneArg(one);
        mock.oneArg(two);
        Mockito.verify(mock).oneArg(ArgumentMatchers.same(one));
        Mockito.verify(mock, Mockito.times(2)).oneArg(two);
        try {
            Mockito.verify(mock).oneArg(ArgumentMatchers.same(three));
            Assert.fail();
        } catch (WantedButNotInvoked e) {
        }
    }

    @Test
    public void shouldVerifyUsingMixedMatchers() {
        mock.threeArgumentMethod(11, "", "01234");
        try {
            Mockito.verify(mock).threeArgumentMethod(AdditionalMatchers.and(AdditionalMatchers.geq(7), AdditionalMatchers.leq(10)), ArgumentMatchers.isA(String.class), Matchers.contains("123"));
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
        }
        mock.threeArgumentMethod(8, new Object(), "01234");
        try {
            Mockito.verify(mock).threeArgumentMethod(AdditionalMatchers.and(AdditionalMatchers.geq(7), AdditionalMatchers.leq(10)), ArgumentMatchers.isA(String.class), Matchers.contains("123"));
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
        }
        mock.threeArgumentMethod(8, "", "no match");
        try {
            Mockito.verify(mock).threeArgumentMethod(AdditionalMatchers.and(AdditionalMatchers.geq(7), AdditionalMatchers.leq(10)), ArgumentMatchers.isA(String.class), Matchers.contains("123"));
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
        }
        mock.threeArgumentMethod(8, "", "123");
        Mockito.verify(mock).threeArgumentMethod(AdditionalMatchers.and(AdditionalMatchers.geq(7), AdditionalMatchers.leq(10)), ArgumentMatchers.isA(String.class), Matchers.contains("123"));
    }
}

