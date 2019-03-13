/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package io.elasticjob.lite.executor.handler;


import JobProperties.JobPropertiesEnum;
import JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER;
import JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER;
import io.elasticjob.lite.executor.handler.impl.DefaultExecutorServiceHandler;
import io.elasticjob.lite.executor.handler.impl.DefaultJobExceptionHandler;
import io.elasticjob.lite.fixture.APIJsonConstants;
import io.elasticjob.lite.fixture.handler.IgnoreJobExceptionHandler;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public final class JobPropertiesTest {
    @Test
    public void assertPutInvalidKey() throws NoSuchFieldException {
        JobProperties actual = new JobProperties();
        actual.put("invalid_key", "");
        Assert.assertTrue(getMap(actual).isEmpty());
    }

    @Test
    public void assertPutNullValue() throws NoSuchFieldException {
        JobProperties actual = new JobProperties();
        actual.put(JOB_EXCEPTION_HANDLER.getKey(), null);
        Assert.assertTrue(getMap(actual).isEmpty());
    }

    @Test
    public void assertPutSuccess() throws NoSuchFieldException {
        JobProperties actual = new JobProperties();
        actual.put(JOB_EXCEPTION_HANDLER.getKey(), DefaultJobExceptionHandler.class.getCanonicalName());
        MatcherAssert.assertThat(getMap(actual).size(), Is.is(1));
    }

    @Test
    public void assertGetWhenValueIsEmpty() throws NoSuchFieldException {
        JobProperties actual = new JobProperties();
        MatcherAssert.assertThat(actual.get(JOB_EXCEPTION_HANDLER), Is.is(DefaultJobExceptionHandler.class.getCanonicalName()));
        MatcherAssert.assertThat(actual.get(EXECUTOR_SERVICE_HANDLER), Is.is(DefaultExecutorServiceHandler.class.getCanonicalName()));
    }

    @Test
    public void assertGetWhenValueIsNotEmpty() throws NoSuchFieldException {
        JobProperties actual = new JobProperties();
        actual.put(JOB_EXCEPTION_HANDLER.getKey(), IgnoreJobExceptionHandler.class.getCanonicalName());
        MatcherAssert.assertThat(actual.get(JOB_EXCEPTION_HANDLER), Is.is(IgnoreJobExceptionHandler.class.getCanonicalName()));
    }

    @Test
    public void assertJson() {
        MatcherAssert.assertThat(new JobProperties().json(), Is.is(APIJsonConstants.getJobPropertiesJson(DefaultJobExceptionHandler.class.getCanonicalName())));
    }

    @Test
    public void assertJobPropertiesEnumFromValidValue() {
        MatcherAssert.assertThat(JobPropertiesEnum.from(JOB_EXCEPTION_HANDLER.getKey()), Is.is(JOB_EXCEPTION_HANDLER));
    }

    @Test
    public void assertJobPropertiesEnumFromInvalidValue() {
        Assert.assertNull(JobPropertiesEnum.from("invalid"));
    }
}

