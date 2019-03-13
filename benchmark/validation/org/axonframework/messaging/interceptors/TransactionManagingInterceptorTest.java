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
package org.axonframework.messaging.interceptors;


import org.axonframework.common.transaction.Transaction;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.messaging.InterceptorChain;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Rene de Waele
 */
public class TransactionManagingInterceptorTest {
    private Message<?> message;

    private InterceptorChain interceptorChain;

    private UnitOfWork<Message<?>> unitOfWork;

    private TransactionManager transactionManager;

    private Transaction transaction;

    private TransactionManagingInterceptor<Message<?>> subject;

    @Test
    public void testStartTransaction() throws Exception {
        UnitOfWork<Message<?>> unitOfWork = Mockito.spy(this.unitOfWork);
        subject.handle(unitOfWork, interceptorChain);
        Mockito.verify(transactionManager).startTransaction();
        Mockito.verify(interceptorChain).proceed();
        Mockito.verify(unitOfWork).onCommit(ArgumentMatchers.any());
        Mockito.verify(unitOfWork).onRollback(ArgumentMatchers.any());
    }

    @Test
    public void testUnitOfWorkCommit() throws Exception {
        subject.handle(unitOfWork, interceptorChain);
        unitOfWork.commit();
        Mockito.verify(transaction).commit();
    }
}

