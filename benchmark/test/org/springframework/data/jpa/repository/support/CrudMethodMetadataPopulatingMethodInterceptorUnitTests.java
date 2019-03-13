/**
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jpa.repository.support;


import LockModeType.OPTIMISTIC;
import LockModeType.PESSIMISTIC_READ;
import java.lang.reflect.Method;
import javax.persistence.LockModeType;
import org.aopalliance.intercept.MethodInvocation;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.support.CrudMethodMetadataPostProcessor.CrudMethodMetadataPopulatingMethodInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * Unit tests for {@link CrudMethodMetadataPopulatingMethodInterceptor}.
 *
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class CrudMethodMetadataPopulatingMethodInterceptorUnitTests {
    @Mock
    MethodInvocation invocation;

    // DATAJPA-268
    @Test
    public void cleansUpBoundResources() throws Throwable {
        Method method = prepareMethodInvocation("someMethod");
        CrudMethodMetadataPopulatingMethodInterceptor interceptor = CrudMethodMetadataPopulatingMethodInterceptor.INSTANCE;
        interceptor.invoke(invocation);
        Assert.assertThat(TransactionSynchronizationManager.getResource(method), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    // DATAJPA-839
    @Test
    public void looksUpCrudMethodMetadataForEveryInvocation() throws Throwable {
        CrudMethodMetadata metadata = new CrudMethodMetadataPostProcessor().getCrudMethodMetadata();
        CrudMethodMetadataPopulatingMethodInterceptorUnitTests.expectLockModeType(metadata, OPTIMISTIC).someMethod();
        CrudMethodMetadataPopulatingMethodInterceptorUnitTests.expectLockModeType(metadata, PESSIMISTIC_READ).someOtherMethod();
    }

    interface Sample {
        @Lock(LockModeType.OPTIMISTIC)
        void someMethod();

        @Lock(LockModeType.PESSIMISTIC_READ)
        void someOtherMethod();
    }
}

