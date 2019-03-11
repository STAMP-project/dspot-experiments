/**
 * Copyright 2006-2009 the original author or authors.
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
package org.springframework.batch.support.transaction;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;


/**
 *
 *
 * @author Dave Syer
 */
public class ConcurrentTransactionAwareProxyTests {
    private static Log logger = LogFactory.getLog(ConcurrentTransactionAwareProxyTests.class);

    private PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();

    int outerMax = 20;

    int innerMax = 30;

    private ExecutorService executor;

    private CompletionService<List<String>> completionService;

    @Test(expected = Throwable.class)
    public void testConcurrentTransactionalSet() throws Exception {
        Set<String> set = TransactionAwareProxyFactory.createTransactionalSet();
        testSet(set);
    }

    @Test
    public void testConcurrentTransactionalAppendOnlySet() throws Exception {
        Set<String> set = TransactionAwareProxyFactory.createAppendOnlyTransactionalSet();
        testSet(set);
    }

    @Test
    public void testConcurrentTransactionalAppendOnlyList() throws Exception {
        List<String> list = TransactionAwareProxyFactory.createAppendOnlyTransactionalList();
        testList(list, false);
    }

    @Test
    public void testConcurrentTransactionalAppendOnlyMap() throws Exception {
        Map<Long, Map<String, String>> map = TransactionAwareProxyFactory.createAppendOnlyTransactionalMap();
        testMap(map);
    }

    @Test(expected = ExecutionException.class)
    public void testConcurrentTransactionalMap() throws Exception {
        Map<Long, Map<String, String>> map = TransactionAwareProxyFactory.createTransactionalMap();
        testMap(map);
    }

    @Test
    public void testTransactionalContains() throws Exception {
        final Map<Long, Map<String, String>> map = TransactionAwareProxyFactory.createAppendOnlyTransactionalMap();
        boolean result = execute(new org.springframework.transaction.support.TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                return map.containsKey("foo");
            }
        });
        Assert.assertFalse(result);
    }
}

