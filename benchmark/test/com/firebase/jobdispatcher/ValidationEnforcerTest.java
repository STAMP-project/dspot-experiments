/**
 * Copyright 2016 Google, Inc.
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
/**
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.firebase.jobdispatcher;


import ValidationEnforcer.ValidationException;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static RetryStrategy.DEFAULT_EXPONENTIAL;


/**
 * Tests for the {@link ValidationEnforcer} class.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 23)
public class ValidationEnforcerTest {
    private static final List<String> ERROR_LIST = Collections.singletonList("error: foo");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private JobValidator validator;

    @Mock
    private JobParameters mockJobParameters;

    @Mock
    private JobTrigger mockTrigger;

    private ValidationEnforcer enforcer;

    private final RetryStrategy retryStrategy = DEFAULT_EXPONENTIAL;

    @Test
    public void testValidate_retryStrategy() throws Exception {
        enforcer.validate(retryStrategy);
        Mockito.verify(validator).validate(retryStrategy);
    }

    @Test
    public void testValidate_jobSpec() throws Exception {
        enforcer.validate(mockJobParameters);
        Mockito.verify(validator).validate(mockJobParameters);
    }

    @Test
    public void testValidate_trigger() throws Exception {
        enforcer.validate(mockTrigger);
        Mockito.verify(validator).validate(mockTrigger);
    }

    @Test
    public void testIsValid_retryStrategy_invalid() throws Exception {
        Mockito.when(validator.validate(retryStrategy)).thenReturn(Collections.singletonList("error: foo"));
        Assert.assertFalse("isValid", enforcer.isValid(retryStrategy));
    }

    @Test
    public void testIsValid_retryStrategy_valid() throws Exception {
        Mockito.when(validator.validate(retryStrategy)).thenReturn(null);
        Assert.assertTrue("isValid", enforcer.isValid(retryStrategy));
    }

    @Test
    public void testIsValid_trigger_invalid() throws Exception {
        Mockito.when(validator.validate(mockTrigger)).thenReturn(Collections.singletonList("error: foo"));
        Assert.assertFalse("isValid", enforcer.isValid(mockTrigger));
    }

    @Test
    public void testIsValid_trigger_valid() throws Exception {
        Mockito.when(validator.validate(mockTrigger)).thenReturn(null);
        Assert.assertTrue("isValid", enforcer.isValid(mockTrigger));
    }

    @Test
    public void testIsValid_jobSpec_invalid() throws Exception {
        Mockito.when(validator.validate(mockJobParameters)).thenReturn(ValidationEnforcerTest.ERROR_LIST);
        Assert.assertFalse("isValid", enforcer.isValid(mockJobParameters));
    }

    @Test
    public void testIsValid_jobSpec_valid() throws Exception {
        Mockito.when(validator.validate(mockJobParameters)).thenReturn(null);
        Assert.assertTrue("isValid", enforcer.isValid(mockJobParameters));
    }

    @Test
    public void testEnsureValid_retryStrategy_valid() throws Exception {
        Mockito.when(validator.validate(retryStrategy)).thenReturn(null);
        enforcer.ensureValid(retryStrategy);
    }

    @Test
    public void testEnsureValid_trigger_valid() throws Exception {
        Mockito.when(validator.validate(mockTrigger)).thenReturn(null);
        enforcer.ensureValid(mockTrigger);
    }

    @Test
    public void testEnsureValid_jobSpec_valid() throws Exception {
        Mockito.when(validator.validate(mockJobParameters)).thenReturn(null);
        enforcer.ensureValid(mockJobParameters);
    }

    @Test
    public void testEnsureValid_retryStrategy_invalid() throws Exception {
        Mockito.when(validator.validate(retryStrategy)).thenReturn(ValidationEnforcerTest.ERROR_LIST);
        expectedException.expect(ValidationException.class);
        enforcer.ensureValid(retryStrategy);
    }

    @Test
    public void testEnsureValid_trigger_invalid() throws Exception {
        Mockito.when(validator.validate(mockTrigger)).thenReturn(ValidationEnforcerTest.ERROR_LIST);
        expectedException.expect(ValidationException.class);
        enforcer.ensureValid(mockTrigger);
    }

    @Test
    public void testEnsureValid_jobSpec_invalid() throws Exception {
        Mockito.when(validator.validate(mockJobParameters)).thenReturn(ValidationEnforcerTest.ERROR_LIST);
        expectedException.expect(ValidationException.class);
        enforcer.ensureValid(mockJobParameters);
    }

    @Test
    public void testValidationMessages() throws Exception {
        Mockito.when(validator.validate(mockJobParameters)).thenReturn(ValidationEnforcerTest.ERROR_LIST);
        try {
            enforcer.ensureValid(mockJobParameters);
            Assert.fail("Expected ensureValid to fail");
        } catch (ValidationEnforcer ve) {
            Assert.assertEquals("Expected ValidationException to have 1 error message", 1, ve.getErrors().size());
        }
    }
}

