/**
 * Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.genie.client.security.oauth2;


import com.netflix.genie.client.exceptions.GenieClientException;
import com.netflix.genie.test.categories.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit Tests for the Token Fetcher class.
 *
 * @author amsharma
 * @since 3.0.0
 */
@Category(UnitTest.class)
public class TokenFetcherUnitTests {
    private static final String URL = "http://url";

    private static final String CLIENT_ID = "client_id";

    private static final String CLIENT_SECRET = "client_secret";

    private static final String GRANT_TYPE = "grant_type";

    private static final String SCOPE = "scope";

    /**
     * Test the constructor with URL missing.
     *
     * @throws GenieClientException
     * 		For any problem
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyUrl() throws GenieClientException {
        new TokenFetcher(null, TokenFetcherUnitTests.CLIENT_ID, TokenFetcherUnitTests.CLIENT_SECRET, TokenFetcherUnitTests.GRANT_TYPE, TokenFetcherUnitTests.SCOPE);
    }

    /**
     * Test the constructor with client id missing.
     *
     * @throws GenieClientException
     * 		For any problem
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyClientIdl() throws GenieClientException {
        new TokenFetcher(TokenFetcherUnitTests.URL, null, TokenFetcherUnitTests.CLIENT_SECRET, TokenFetcherUnitTests.GRANT_TYPE, TokenFetcherUnitTests.SCOPE);
    }

    /**
     * Test the constructor with client secret missing.
     *
     * @throws GenieClientException
     * 		For any problem
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyClientSecret() throws GenieClientException {
        new TokenFetcher(TokenFetcherUnitTests.URL, TokenFetcherUnitTests.CLIENT_ID, null, TokenFetcherUnitTests.GRANT_TYPE, TokenFetcherUnitTests.SCOPE);
    }

    /**
     * Test the constructor with grant type missing.
     *
     * @throws GenieClientException
     * 		For any problem
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyGrantType() throws GenieClientException {
        new TokenFetcher(TokenFetcherUnitTests.URL, TokenFetcherUnitTests.CLIENT_ID, TokenFetcherUnitTests.CLIENT_SECRET, null, TokenFetcherUnitTests.SCOPE);
    }

    /**
     * Test the constructor with scope missing.
     *
     * @throws GenieClientException
     * 		For any problem
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyScope() throws GenieClientException {
        new TokenFetcher(TokenFetcherUnitTests.URL, TokenFetcherUnitTests.CLIENT_ID, TokenFetcherUnitTests.CLIENT_SECRET, TokenFetcherUnitTests.GRANT_TYPE, null);
    }

    /**
     * Test the constructor with malformed url.
     *
     * @throws GenieClientException
     * 		For any problem
     */
    @Test(expected = GenieClientException.class)
    public void testConstructorWithMalformedUrl() throws GenieClientException {
        new TokenFetcher("foo", TokenFetcherUnitTests.CLIENT_ID, TokenFetcherUnitTests.CLIENT_SECRET, TokenFetcherUnitTests.GRANT_TYPE, TokenFetcherUnitTests.SCOPE);
    }

    /**
     * Test the constructor with valid params.
     *
     * @throws GenieClientException
     * 		For any problem
     */
    @Test
    public void testConstructorWithValidParams() throws GenieClientException {
        new TokenFetcher(TokenFetcherUnitTests.URL, TokenFetcherUnitTests.CLIENT_ID, TokenFetcherUnitTests.CLIENT_SECRET, TokenFetcherUnitTests.GRANT_TYPE, TokenFetcherUnitTests.SCOPE);
    }

    /**
     * Test the getToken method for failure.
     *
     * @throws GenieClientException
     * 		For any problem
     */
    @Test
    public void testGetTokenFailure() throws GenieClientException {
        final TokenFetcher tokenFetcher = new TokenFetcher(TokenFetcherUnitTests.URL, TokenFetcherUnitTests.CLIENT_ID, TokenFetcherUnitTests.CLIENT_SECRET, TokenFetcherUnitTests.GRANT_TYPE, TokenFetcherUnitTests.SCOPE);
        try {
            tokenFetcher.getToken();
        } catch (GenieClientException ge) {
            Assert.assertTrue(((ge.getErrorCode()) == (-1)));
        }
    }
}

