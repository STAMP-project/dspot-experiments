/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stacktrace;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.mockito.exceptions.misusing.UnfinishedStubbingException;
import org.mockito.exceptions.misusing.UnfinishedVerificationException;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class ClickableStackTracesWhenFrameworkMisusedTest extends TestBase {
    @Mock
    private IMethods mock;

    @Test
    public void shouldPointOutMisplacedMatcher() {
        misplacedArgumentMatcherHere();
        try {
            Mockito.verify(mock).simpleMethod();
            Assert.fail();
        } catch (InvalidUseOfMatchersException e) {
            assertThat(e).hasMessageContaining("-> at ").hasMessageContaining("misplacedArgumentMatcherHere(");
        }
    }

    @Test
    public void shouldPointOutUnfinishedStubbing() {
        unfinishedStubbingHere();
        try {
            Mockito.verify(mock).simpleMethod();
            Assert.fail();
        } catch (UnfinishedStubbingException e) {
            assertThat(e).hasMessageContaining("-> at ").hasMessageContaining("unfinishedStubbingHere(");
        }
    }

    @Test
    public void shouldShowWhereIsUnfinishedVerification() throws Exception {
        unfinishedVerificationHere();
        try {
            Mockito.mock(IMethods.class);
            Assert.fail();
        } catch (UnfinishedVerificationException e) {
            assertThat(e).hasMessageContaining("unfinishedVerificationHere(");
        }
    }
}

