/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.client.impl.protocol;


import com.hazelcast.client.impl.clientside.ClientExceptionFactory;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientExceptionFactoryTest extends HazelcastTestSupport {
    @Parameterized.Parameter
    public Throwable throwable;

    private ClientExceptions exceptions = new ClientExceptions(true);

    private ClientExceptionFactory exceptionFactory = new ClientExceptionFactory(true);

    @Test
    public void testException() {
        ClientMessage exceptionMessage = exceptions.createExceptionMessage(throwable);
        ClientMessage responseMessage = ClientMessage.createForDecode(exceptionMessage.buffer(), 0);
        Throwable resurrectedThrowable = exceptionFactory.createException(responseMessage);
        if (!(exceptionEquals(throwable, resurrectedThrowable))) {
            TestCase.assertEquals(throwable, resurrectedThrowable);
        }
    }
}

