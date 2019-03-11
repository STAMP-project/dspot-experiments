/**
 * Copyright 2016 KairosDB Authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.kairosdb.core.http.rest.json;


import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.testing.BeanValidationHelper;


public class NewMetricRequestTest {
    @Test
    public void testNullNameInvalid() {
        NewMetricRequest request = new NewMetricRequest(null, new HashMap<String, String>());
        request.addDataPoint(new DataPointRequest(5, "value"));
        Set<ConstraintViolation<NewMetricRequest>> violations = BeanValidationHelper.VALIDATOR.validate(request);
        List<String> violationMessages = BeanValidationHelper.messagesFor(violations);
        Assert.assertThat(violationMessages.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(violationMessages.get(0), CoreMatchers.equalTo("name may not be null"));
    }

    @Test
    public void testEmptyNameInvalid() {
        NewMetricRequest request = new NewMetricRequest("", new HashMap<String, String>());
        request.addDataPoint(new DataPointRequest(5, "value"));
        Set<ConstraintViolation<NewMetricRequest>> violations = BeanValidationHelper.VALIDATOR.validate(request);
        List<String> violationMessages = BeanValidationHelper.messagesFor(violations);
        Assert.assertThat(violationMessages.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(violationMessages.get(0), CoreMatchers.equalTo("name may not be empty"));
    }

    @Test
    public void testNullValueInvalid() {
        NewMetricRequest request = new NewMetricRequest("metric1", new HashMap<String, String>());
        request.addDataPoint(new DataPointRequest(5, null));
        Set<ConstraintViolation<NewMetricRequest>> violations = BeanValidationHelper.VALIDATOR.validate(request);
        List<String> violationMessages = BeanValidationHelper.messagesFor(violations);
        Assert.assertThat(violationMessages.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(violationMessages.get(0), CoreMatchers.equalTo("datapoints[0].value may not be null"));
    }

    @Test
    public void testEmptyValueInvalid() {
        NewMetricRequest request = new NewMetricRequest("metric1", new HashMap<String, String>());
        request.addDataPoint(new DataPointRequest(5, ""));
        Set<ConstraintViolation<NewMetricRequest>> violations = BeanValidationHelper.VALIDATOR.validate(request);
        List<String> violationMessages = BeanValidationHelper.messagesFor(violations);
        Assert.assertThat(violationMessages.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(violationMessages.get(0), CoreMatchers.equalTo("datapoints[0].value may not be empty"));
    }

    @Test
    public void testTimestampZeroInvalid() {
        NewMetricRequest request = new NewMetricRequest("metric1", new HashMap<String, String>());
        request.addDataPoint(new DataPointRequest(0, "value"));
        Set<ConstraintViolation<NewMetricRequest>> violations = BeanValidationHelper.VALIDATOR.validate(request);
        List<String> violationMessages = BeanValidationHelper.messagesFor(violations);
        Assert.assertThat(violationMessages.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(violationMessages.get(0), CoreMatchers.equalTo("datapoints[0].timestamp must be greater than or equal to 1"));
    }
}

