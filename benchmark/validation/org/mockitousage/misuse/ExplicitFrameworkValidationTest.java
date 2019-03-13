/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.misuse;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.mockito.exceptions.misusing.UnfinishedStubbingException;
import org.mockito.exceptions.misusing.UnfinishedVerificationException;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class ExplicitFrameworkValidationTest extends TestBase {
    @Mock
    IMethods mock;

    @SuppressWarnings({ "MockitoUsage", "CheckReturnValue" })
    @Test
    public void shouldValidateExplicitly() {
        Mockito.verify(mock);
        try {
            Mockito.validateMockitoUsage();
            Assert.fail();
        } catch (UnfinishedVerificationException e) {
        }
    }

    @SuppressWarnings({ "MockitoUsage", "CheckReturnValue" })
    @Test
    public void shouldDetectUnfinishedStubbing() {
        Mockito.when(mock.simpleMethod());
        try {
            Mockito.validateMockitoUsage();
            Assert.fail();
        } catch (UnfinishedStubbingException e) {
        }
    }

    @Test
    public void shouldDetectMisplacedArgumentMatcher() {
        ArgumentMatchers.anyObject();
        try {
            Mockito.validateMockitoUsage();
            Assert.fail();
        } catch (InvalidUseOfMatchersException e) {
        }
    }
}

