/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.datastore;


import DatastoreImpl.EXCEPTION_HANDLER_INTERCEPTOR;
import com.google.cloud.BaseServiceException;
import com.google.cloud.ExceptionHandler;
import junit.framework.TestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for {@link TransactionExceptionHandler}.
 */
public class TransactionExceptionHandlerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testShouldTry() {
        ExceptionHandler handler = ExceptionHandler.newBuilder().abortOn(RuntimeException.class).addInterceptors(EXCEPTION_HANDLER_INTERCEPTOR).build();
        ExceptionHandler transactionHandler = TransactionExceptionHandler.build();
        TestCase.assertFalse(handler.shouldRetry(new DatastoreException(10, "", "ABORTED", false, null), null));
        TestCase.assertFalse(handler.shouldRetry(new DatastoreException(10, "", "", false, null), null));
        TestCase.assertFalse(handler.shouldRetry(new DatastoreException(0, "", "", false, null), null));
        TestCase.assertTrue(transactionHandler.shouldRetry(new DatastoreException(10, "", "ABORTED", false, null), null));
        TestCase.assertTrue(transactionHandler.shouldRetry(new DatastoreException(10, "", "", false, null), null));
        TestCase.assertFalse(transactionHandler.shouldRetry(new DatastoreException(0, "", "", false, null), null));
        DatastoreException nestedDatastoreException = new DatastoreException(BaseServiceException.UNKNOWN_CODE, "", null, new DatastoreException(10, "", "ABORTED", false, null));
        TestCase.assertTrue(transactionHandler.shouldRetry(nestedDatastoreException, null));
        TestCase.assertFalse(handler.shouldRetry(nestedDatastoreException, null));
        DatastoreException nestedUserException = new DatastoreException(BaseServiceException.UNKNOWN_CODE, "", null, new RuntimeException(""));
        TestCase.assertFalse(transactionHandler.shouldRetry(nestedUserException, null));
        TestCase.assertFalse(handler.shouldRetry(nestedDatastoreException, null));
    }
}

