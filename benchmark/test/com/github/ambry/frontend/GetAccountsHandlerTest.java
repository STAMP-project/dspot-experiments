/**
 * Copyright 2019 LinkedIn Corp. All rights reserved.
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
 */
package com.github.ambry.frontend;


import RestServiceErrorCode.BadRequest;
import RestServiceErrorCode.InvalidArgs;
import RestServiceErrorCode.NotFound;
import TestUtils.ThrowingRunnable;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.account.Account;
import com.github.ambry.account.AccountCollectionSerde;
import com.github.ambry.account.InMemAccountService;
import com.github.ambry.rest.MockRestResponseChannel;
import com.github.ambry.rest.RestRequest;
import com.github.ambry.rest.RestResponseChannel;
import com.github.ambry.rest.RestServiceErrorCode;
import com.github.ambry.rest.RestTestUtils;
import com.github.ambry.router.ReadableStreamChannel;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.ThrowingBiConsumer;
import com.github.ambry.utils.ThrowingConsumer;
import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;

import static com.github.ambry.frontend.FrontendTestSecurityServiceFactory.Mode.PostProcessRequest;
import static com.github.ambry.frontend.FrontendTestSecurityServiceFactory.Mode.ProcessRequest;


/**
 * Tests for {@link GetAccountsHandler}.
 */
public class GetAccountsHandlerTest {
    private final FrontendTestSecurityServiceFactory securityServiceFactory;

    private final InMemAccountService accountService;

    private final GetAccountsHandler handler;

    public GetAccountsHandlerTest() {
        FrontendMetrics metrics = new FrontendMetrics(new MetricRegistry());
        securityServiceFactory = new FrontendTestSecurityServiceFactory();
        accountService = new InMemAccountService(false, true);
        handler = new GetAccountsHandler(securityServiceFactory.getSecurityService(), accountService, metrics);
    }

    /**
     * Test valid request cases.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void validRequestsTest() throws Exception {
        Account account = accountService.createAndAddRandomAccount();
        ThrowingBiConsumer<RestRequest, Collection<Account>> testAction = ( request, expectedAccounts) -> {
            RestResponseChannel restResponseChannel = new MockRestResponseChannel();
            ReadableStreamChannel channel = sendRequestGetResponse(request, restResponseChannel);
            assertNotNull("There should be a response", channel);
            Assert.assertNotNull("Date has not been set", restResponseChannel.getHeader(RestUtils.Headers.DATE));
            assertEquals("Content-type is not as expected", RestUtils.JSON_CONTENT_TYPE, restResponseChannel.getHeader(RestUtils.Headers.CONTENT_TYPE));
            assertEquals("Content-length is not as expected", channel.getSize(), Integer.parseInt(((String) (restResponseChannel.getHeader(RestUtils.Headers.CONTENT_LENGTH)))));
            assertEquals("Accounts do not match", new HashSet<>(expectedAccounts), new HashSet<>(AccountCollectionSerde.fromJson(RestTestUtils.getJsonizedResponseBody(channel))));
        };
        testAction.accept(createRestRequest(null, null), accountService.getAllAccounts());
        testAction.accept(createRestRequest(account.getName(), null), Collections.singleton(account));
        testAction.accept(createRestRequest(null, Short.toString(account.getId())), Collections.singleton(account));
    }

    /**
     * Test bad request cases.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void badRequestsTest() throws Exception {
        Account existingAccount = accountService.createAndAddRandomAccount();
        Account nonExistentAccount = accountService.generateRandomAccount();
        ThrowingBiConsumer<RestRequest, RestServiceErrorCode> testAction = ( request, expectedErrorCode) -> {
            TestUtils.assertException(.class, () -> sendRequestGetResponse(request, new MockRestResponseChannel()), ( e) -> assertEquals("Unexpected error code", expectedErrorCode, e.getErrorCode()));
        };
        // cannot supply both ID and name
        testAction.accept(createRestRequest(existingAccount.getName(), Short.toString(existingAccount.getId())), BadRequest);
        // non-numerical ID
        testAction.accept(createRestRequest(null, "ABC"), InvalidArgs);
        // account that doesn't exist
        testAction.accept(createRestRequest(nonExistentAccount.getName(), null), NotFound);
        testAction.accept(createRestRequest(null, Short.toString(nonExistentAccount.getId())), NotFound);
    }

    /**
     * Tests the case where the {@link SecurityService} denies the request.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void securityServiceDenialTest() throws Exception {
        IllegalStateException injectedException = new IllegalStateException("@@expected");
        TestUtils.ThrowingRunnable testAction = () -> sendRequestGetResponse(createRestRequest(null, null), new MockRestResponseChannel());
        ThrowingConsumer<IllegalStateException> errorChecker = ( e) -> assertEquals("Wrong exception", injectedException, e);
        securityServiceFactory.exceptionToReturn = injectedException;
        securityServiceFactory.mode = ProcessRequest;
        TestUtils.assertException(IllegalStateException.class, testAction, errorChecker);
        securityServiceFactory.mode = PostProcessRequest;
        TestUtils.assertException(IllegalStateException.class, testAction, errorChecker);
        securityServiceFactory.exceptionToThrow = injectedException;
        securityServiceFactory.exceptionToReturn = null;
        securityServiceFactory.mode = ProcessRequest;
        TestUtils.assertException(IllegalStateException.class, testAction, errorChecker);
        securityServiceFactory.mode = PostProcessRequest;
        TestUtils.assertException(IllegalStateException.class, testAction, errorChecker);
    }
}

