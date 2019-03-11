/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.messaging;


import java.util.Arrays;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 * @author Nakul Mishra
 */
public class DefaultInterceptorChainTest {
    private UnitOfWork<Message<?>> unitOfWork;

    private MessageHandler<Message<?>> mockHandler;

    @Test
    @SuppressWarnings("unchecked")
    public void testChainWithDifferentProceedCalls() throws Exception {
        MessageHandlerInterceptor interceptor1 = ( unitOfWork, interceptorChain) -> {
            unitOfWork.transformMessage(( m) -> new GenericMessage<>("testing"));
            return interceptorChain.proceed();
        };
        MessageHandlerInterceptor interceptor2 = ( unitOfWork, interceptorChain) -> interceptorChain.proceed();
        unitOfWork.transformMessage(( m) -> new GenericMessage<>("original"));
        DefaultInterceptorChain testSubject = new DefaultInterceptorChain(unitOfWork, Arrays.asList(interceptor1, interceptor2), mockHandler);
        String actual = ((String) (testSubject.proceed()));
        Assert.assertSame("Result", actual);
        Mockito.verify(mockHandler).handle(ArgumentMatchers.argThat(( x) -> (x != null) && (x.getPayload().equals("testing"))));
    }
}

