/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
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
package com.github.ambry.rest;


import Container.DEFAULT_PRIVATE_CONTAINER;
import Container.DEFAULT_PUBLIC_CONTAINER;
import GetOption.None;
import JSONObject.NULL;
import RestMethod.GET;
import RestMethod.POST;
import RestServiceErrorCode.InternalServerError;
import RestServiceErrorCode.InvalidArgs;
import RestServiceErrorCode.MissingArgs;
import RestUtils.Headers;
import RestUtils.Headers.AMBRY_CONTENT_TYPE;
import RestUtils.Headers.BLOB_SIZE;
import RestUtils.Headers.GET_OPTION;
import RestUtils.Headers.OWNER_ID;
import RestUtils.Headers.PRIVATE;
import RestUtils.Headers.SERVICE_ID;
import RestUtils.Headers.TTL;
import RestUtils.Headers.USER_META_DATA_HEADER_PREFIX;
import RestUtils.MultipartPost.USER_METADATA_PART;
import RestUtils.SubResource;
import com.github.ambry.account.Container;
import com.github.ambry.protocol.GetOption;
import com.github.ambry.router.ByteRanges;
import com.github.ambry.utils.Crc32;
import com.github.ambry.utils.Pair;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.UtilsTest;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.BiConsumer;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static RestUtils.HTTP_DATE_FORMAT;
import static RestUtils.SIGNED_ID_PREFIX;


/**
 * Unit tests for {@link RestUtils}.
 */
public class RestUtilsTest {
    private static final Random RANDOM = new Random();

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    private static final String SECURE_PATH = "secure-path";

    /**
     * Tests building of {@link BlobProperties} given good input (all arguments in the number and format expected).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getBlobPropertiesGoodInputTest() throws Exception {
        JSONObject headers = new JSONObject();
        Container[] containers = new Container[]{ Container.DEFAULT_PRIVATE_CONTAINER, Container.DEFAULT_PUBLIC_CONTAINER };
        for (Container container : containers) {
            setAmbryHeadersForPut(headers, Long.toString(RestUtilsTest.RANDOM.nextInt(10000)), generateRandomString(10), container, "image/gif", generateRandomString(10));
            verifyBlobPropertiesConstructionSuccess(headers);
        }
    }

    /**
     * Tests building of {@link BlobProperties} given varied input. Some input fail (tests check the correct error code),
     * some should succeed (check for default values expected).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getBlobPropertiesVariedInputTest() throws Exception {
        String ttl = Long.toString(RestUtilsTest.RANDOM.nextInt(10000));
        String serviceId = generateRandomString(10);
        String contentType = "image/gif";
        String ownerId = generateRandomString(10);
        JSONObject headers;
        // failure required.
        // ttl not a number.
        headers = new JSONObject();
        setAmbryHeadersForPut(headers, "NaN", serviceId, DEFAULT_PUBLIC_CONTAINER, contentType, ownerId);
        verifyBlobPropertiesConstructionFailure(headers, InvalidArgs);
        // ttl < -1.
        headers = new JSONObject();
        setAmbryHeadersForPut(headers, "-2", serviceId, DEFAULT_PUBLIC_CONTAINER, contentType, ownerId);
        verifyBlobPropertiesConstructionFailure(headers, InvalidArgs);
        // serviceId missing.
        headers = new JSONObject();
        setAmbryHeadersForPut(headers, ttl, null, DEFAULT_PUBLIC_CONTAINER, contentType, ownerId);
        verifyBlobPropertiesConstructionFailure(headers, MissingArgs);
        // serviceId null.
        headers = new JSONObject();
        setAmbryHeadersForPut(headers, ttl, null, DEFAULT_PUBLIC_CONTAINER, contentType, ownerId);
        headers.put(SERVICE_ID, NULL);
        verifyBlobPropertiesConstructionFailure(headers, InvalidArgs);
        // contentType missing.
        headers = new JSONObject();
        setAmbryHeadersForPut(headers, ttl, serviceId, DEFAULT_PUBLIC_CONTAINER, null, ownerId);
        verifyBlobPropertiesConstructionFailure(headers, MissingArgs);
        // contentType null.
        headers = new JSONObject();
        setAmbryHeadersForPut(headers, ttl, serviceId, DEFAULT_PUBLIC_CONTAINER, null, ownerId);
        headers.put(AMBRY_CONTENT_TYPE, NULL);
        verifyBlobPropertiesConstructionFailure(headers, InvalidArgs);
        // too many values for some headers.
        headers = new JSONObject();
        setAmbryHeadersForPut(headers, ttl, serviceId, DEFAULT_PUBLIC_CONTAINER, contentType, ownerId);
        tooManyValuesTest(headers, TTL);
        // no internal keys for account and container
        headers = new JSONObject();
        setAmbryHeadersForPut(headers, ttl, serviceId, null, contentType, ownerId, false);
        verifyBlobPropertiesConstructionFailure(headers, InternalServerError);
        // no internal keys for account
        headers = new JSONObject();
        setAmbryHeadersForPut(headers, ttl, serviceId, DEFAULT_PUBLIC_CONTAINER, contentType, ownerId, false);
        verifyBlobPropertiesConstructionFailure(headers, InternalServerError);
        // no internal keys for container
        headers = new JSONObject();
        setAmbryHeadersForPut(headers, ttl, serviceId, null, contentType, ownerId, true);
        verifyBlobPropertiesConstructionFailure(headers, InternalServerError);
        // no failures.
        // ttl missing. Should be infinite time by default
        // ownerId missing.
        headers = new JSONObject();
        setAmbryHeadersForPut(headers, null, serviceId, DEFAULT_PUBLIC_CONTAINER, contentType, null);
        verifyBlobPropertiesConstructionSuccess(headers);
        // ttl null.
        headers = new JSONObject();
        setAmbryHeadersForPut(headers, null, serviceId, DEFAULT_PUBLIC_CONTAINER, contentType, ownerId);
        headers.put(TTL, NULL);
        verifyBlobPropertiesConstructionSuccess(headers);
        // Post with valid ttl
        headers = new JSONObject();
        setAmbryHeadersForPut(headers, "100", serviceId, DEFAULT_PUBLIC_CONTAINER, contentType, ownerId);
        verifyBlobPropertiesConstructionSuccess(headers);
        // ownerId null.
        headers = new JSONObject();
        setAmbryHeadersForPut(headers, ttl, serviceId, DEFAULT_PUBLIC_CONTAINER, contentType, null);
        headers.put(OWNER_ID, NULL);
        verifyBlobPropertiesConstructionSuccess(headers);
        // blobSize null (should be ignored)
        headers = new JSONObject();
        setAmbryHeadersForPut(headers, ttl, serviceId, DEFAULT_PUBLIC_CONTAINER, contentType, ownerId);
        headers.put(BLOB_SIZE, NULL);
        verifyBlobPropertiesConstructionSuccess(headers);
        // blobSize negative (should succeed)
        headers = new JSONObject();
        setAmbryHeadersForPut(headers, ttl, serviceId, DEFAULT_PUBLIC_CONTAINER, contentType, ownerId);
        headers.put(BLOB_SIZE, (-1));
        verifyBlobPropertiesConstructionSuccess(headers);
    }

    /**
     * Tests for {@link RestUtils#isPrivate(Map)}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void isPrivateTest() throws Exception {
        String serviceId = generateRandomString(10);
        String contentType = "image/gif";
        JSONObject headers = new JSONObject();
        // using this just to help with setting the other necessary headers - doesn't matter what the container is, the
        // PRIVATE header won't have been set.
        setAmbryHeadersForPut(headers, null, serviceId, DEFAULT_PRIVATE_CONTAINER, contentType, null);
        // isPrivate not true or false.
        headers.put(PRIVATE, "!(true || false)");
        RestRequest restRequest = createRestRequest(POST, "/", headers);
        try {
            RestUtils.isPrivate(restRequest.getArgs());
            Assert.fail("An exception was expected but none were thrown");
        } catch (RestServiceException e) {
            Assert.assertEquals("Unexpected RestServiceErrorCode", InvalidArgs, e.getErrorCode());
        }
        // isPrivate null.
        headers.put(PRIVATE, NULL);
        restRequest = createRestRequest(POST, "/", headers);
        Assert.assertFalse("isPrivate should be false because it was not set", RestUtils.isPrivate(restRequest.getArgs()));
        // isPrivate false
        headers.put(PRIVATE, "false");
        restRequest = createRestRequest(POST, "/", headers);
        Assert.assertFalse("isPrivate should be false because it was set to false", RestUtils.isPrivate(restRequest.getArgs()));
        // isPrivate true
        headers.put(PRIVATE, "true");
        restRequest = createRestRequest(POST, "/", headers);
        Assert.assertTrue("isPrivate should be true because it was set to true", RestUtils.isPrivate(restRequest.getArgs()));
    }

    /**
     * Tests for {@link RestUtils#ensureRequiredHeadersOrThrow(RestRequest, Set)}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void ensureRequiredHeadersOrThrowTest() throws Exception {
        JSONObject headers = new JSONObject();
        Set<String> requiredHeaders = new HashSet<>(Arrays.asList("required_a", "required_b", "required_c"));
        for (String requiredHeader : requiredHeaders) {
            headers.put(requiredHeader, UtilsTest.getRandomString(10));
        }
        headers.put(UtilsTest.getRandomString(10), UtilsTest.getRandomString(10));
        // success test
        RestRequest restRequest = createRestRequest(POST, "/", headers);
        RestUtils.ensureRequiredHeadersOrThrow(restRequest, requiredHeaders);
        // failure test
        // null
        headers.put(requiredHeaders.iterator().next(), NULL);
        restRequest = createRestRequest(POST, "/", headers);
        try {
            RestUtils.ensureRequiredHeadersOrThrow(restRequest, requiredHeaders);
            Assert.fail("Should have failed because one of the required headers has a bad value");
        } catch (RestServiceException e) {
            Assert.assertEquals("Unexpected RestServiceErrorCode", InvalidArgs, e.getErrorCode());
        }
        // not present
        headers.remove(requiredHeaders.iterator().next());
        restRequest = createRestRequest(POST, "/", headers);
        try {
            RestUtils.ensureRequiredHeadersOrThrow(restRequest, requiredHeaders);
            Assert.fail("Should have failed because one of the required headers is not present");
        } catch (RestServiceException e) {
            Assert.assertEquals("Unexpected RestServiceErrorCode", MissingArgs, e.getErrorCode());
        }
    }

    /**
     * Tests building of user metadata.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getUserMetadataTest() throws Exception {
        byte[] usermetadata = RestUtils.buildUserMetadata(new HashMap());
        Assert.assertArrayEquals("Unexpected user metadata", new byte[0], usermetadata);
    }

    /**
     * Tests building of User Metadata with good input
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getUserMetadataGoodInputTest() throws Exception {
        JSONObject headers = new JSONObject();
        setAmbryHeadersForPut(headers, Long.toString(RestUtilsTest.RANDOM.nextInt(10000)), generateRandomString(10), DEFAULT_PUBLIC_CONTAINER, "image/gif", generateRandomString(10));
        Map<String, String> userMetadata = new HashMap<String, String>();
        userMetadata.put(((Headers.USER_META_DATA_HEADER_PREFIX) + "key1"), "value1");
        userMetadata.put(((Headers.USER_META_DATA_HEADER_PREFIX) + "key2"), "value2");
        // changed cases
        userMetadata.put(((USER_META_DATA_HEADER_PREFIX.toUpperCase()) + "KeY3"), "value3");
        userMetadata.put(((USER_META_DATA_HEADER_PREFIX.toLowerCase()) + "kEy4"), "value4");
        RestUtilsTest.setUserMetadataHeaders(headers, userMetadata);
        verifyUserMetadataConstructionSuccess(headers, userMetadata);
    }

    /**
     * Tests building of User Metadata when the {@link RestRequest} contains an arg with name
     * {@link RestUtils.MultipartPost#USER_METADATA_PART}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getUserMetadataWithUserMetadataArgTest() throws Exception {
        byte[] original = new byte[100];
        RestUtilsTest.RANDOM.nextBytes(original);
        JSONObject headers = new JSONObject();
        headers.put(USER_METADATA_PART, ByteBuffer.wrap(original));
        RestRequest restRequest = createRestRequest(POST, "/", headers);
        byte[] rcvd = RestUtils.buildUserMetadata(restRequest.getArgs());
        Assert.assertArrayEquals("Received user metadata does not match with original", original, rcvd);
    }

    /**
     * Tests building of User Metadata with unusual input
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getUserMetadataUnusualInputTest() throws Exception {
        JSONObject headers = new JSONObject();
        setAmbryHeadersForPut(headers, Long.toString(RestUtilsTest.RANDOM.nextInt(10000)), generateRandomString(10), DEFAULT_PUBLIC_CONTAINER, "image/gif", generateRandomString(10));
        Map<String, String> userMetadataArgs = new HashMap<String, String>();
        Map<String, String> expectedUserMetadata = new HashMap<>();
        String key = (Headers.USER_META_DATA_HEADER_PREFIX) + "key1";
        String value = "value1";
        userMetadataArgs.put(key, value);
        expectedUserMetadata.put(key, value);
        // no valid prefix
        userMetadataArgs.put("key2", "value2_1");
        // valid prefix as suffix
        userMetadataArgs.put(("key3" + (Headers.USER_META_DATA_HEADER_PREFIX)), "value3");
        // empty value
        key = (Headers.USER_META_DATA_HEADER_PREFIX) + "key4";
        value = "";
        userMetadataArgs.put(key, value);
        expectedUserMetadata.put(key, value);
        // different casing
        key = (USER_META_DATA_HEADER_PREFIX.toUpperCase()) + "KeY5";
        value = "value5";
        userMetadataArgs.put(key, value);
        expectedUserMetadata.put(key, value);
        key = (USER_META_DATA_HEADER_PREFIX.toLowerCase()) + "kEy6";
        value = "value6";
        userMetadataArgs.put(key, value);
        expectedUserMetadata.put(key, value);
        // Unicode with multiple code point characters (i.e. emoji)
        key = (USER_META_DATA_HEADER_PREFIX.toLowerCase()) + "kEy7";
        userMetadataArgs.put(key, "\u00e5\u222b \ud83d\ude1d\ud83d\ude31abcd");
        // Non ascii characters should be replaced with question marks
        expectedUserMetadata.put(key, "?? ??abcd");
        RestUtilsTest.setUserMetadataHeaders(headers, userMetadataArgs);
        RestRequest restRequest = createRestRequest(POST, "/", headers);
        byte[] userMetadataByteArray = RestUtils.buildUserMetadata(restRequest.getArgs());
        Map<String, String> userMetadataMap = RestUtils.buildUserMetadata(userMetadataByteArray);
        Assert.assertEquals("Size of map unexpected ", expectedUserMetadata.size(), userMetadataMap.size());
        expectedUserMetadata.forEach(( keyToCheck, valueToCheck) -> {
            String keyInOutputMap = (Headers.USER_META_DATA_HEADER_PREFIX) + (keyToCheck.substring(USER_META_DATA_HEADER_PREFIX.length()));
            Assert.assertTrue((keyInOutputMap + " not found in user metadata map"), userMetadataMap.containsKey(keyInOutputMap));
            Assert.assertEquals((("Value for " + keyToCheck) + " didnt match expected value"), valueToCheck, userMetadataMap.get(keyInOutputMap));
        });
    }

    /**
     * Tests building of User Metadata with empty input
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getEmptyUserMetadataInputTest() throws Exception {
        JSONObject headers = new JSONObject();
        setAmbryHeadersForPut(headers, Long.toString(RestUtilsTest.RANDOM.nextInt(10000)), generateRandomString(10), DEFAULT_PUBLIC_CONTAINER, "image/gif", generateRandomString(10));
        Map<String, String> userMetadata = new HashMap<String, String>();
        RestUtilsTest.setUserMetadataHeaders(headers, userMetadata);
        RestRequest restRequest = createRestRequest(POST, "/", headers);
        byte[] userMetadataByteArray = RestUtils.buildUserMetadata(restRequest.getArgs());
        Map<String, String> userMetadataMap = RestUtils.buildUserMetadata(userMetadataByteArray);
        Assert.assertEquals("UserMetadata should have no entries ", 0, userMetadataMap.size());
    }

    /**
     * Tests deserializing user metadata from byte array
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getUserMetadataFromByteArrayComplexTest() throws Exception {
        Map<String, String> userMetadataMap = null;
        // user metadata of size 1 byte
        byte[] userMetadataByteArray = new byte[1];
        userMetadataMap = RestUtils.buildUserMetadata(userMetadataByteArray);
        Assert.assertNull("UserMetadata should have been null ", userMetadataMap);
        // user metadata with just the version
        userMetadataByteArray = new byte[4];
        ByteBuffer byteBuffer = ByteBuffer.wrap(userMetadataByteArray);
        byteBuffer.putShort(((short) (1)));
        userMetadataMap = RestUtils.buildUserMetadata(userMetadataByteArray);
        Assert.assertNull("UserMetadata should have been null ", userMetadataMap);
        // user metadata with wrong version
        userMetadataByteArray = new byte[4];
        byteBuffer = ByteBuffer.wrap(userMetadataByteArray);
        byteBuffer.putShort(((short) (3)));
        userMetadataMap = RestUtils.buildUserMetadata(userMetadataByteArray);
        Assert.assertNull("UserMetadata should have been null ", userMetadataMap);
        // 0 sized user metadata
        userMetadataByteArray = new byte[12];
        byteBuffer = ByteBuffer.wrap(userMetadataByteArray);
        byteBuffer.putShort(((short) (1)));
        byteBuffer.putInt(4);
        byteBuffer.putInt(0);
        userMetadataMap = RestUtils.buildUserMetadata(userMetadataByteArray);
        Assert.assertNull("UserMetadata should have been null ", userMetadataMap);
        // wrong size
        userMetadataByteArray = new byte[36];
        byteBuffer = ByteBuffer.wrap(userMetadataByteArray);
        byteBuffer.putShort(((short) (1)));
        String key = "key1";
        byte[] keyInBytes = key.getBytes(StandardCharsets.US_ASCII);
        int keyLength = keyInBytes.length;
        byteBuffer.putInt(21);
        byteBuffer.putInt(1);
        byteBuffer.putInt(keyLength);
        byteBuffer.put(keyInBytes);
        String value = "value1";
        byte[] valueInBytes = value.getBytes(StandardCharsets.US_ASCII);
        int valueLength = valueInBytes.length;
        byteBuffer.putInt(valueLength);
        byteBuffer.put(valueInBytes);
        Crc32 crc32 = new Crc32();
        crc32.update(userMetadataByteArray, 0, ((userMetadataByteArray.length) - 8));
        byteBuffer.putLong(crc32.getValue());
        userMetadataMap = RestUtils.buildUserMetadata(userMetadataByteArray);
        Assert.assertNull("UserMetadata should have been null ", userMetadataMap);
        // wrong total number of entries
        userMetadataByteArray = new byte[36];
        byteBuffer = ByteBuffer.wrap(userMetadataByteArray);
        byteBuffer.putShort(((short) (1)));
        byteBuffer.putInt(22);
        byteBuffer.putInt(2);
        byteBuffer.putInt(keyLength);
        byteBuffer.put(keyInBytes);
        byteBuffer.putInt(valueLength);
        byteBuffer.put(valueInBytes);
        crc32 = new Crc32();
        crc32.update(userMetadataByteArray, 0, ((userMetadataByteArray.length) - 8));
        byteBuffer.putLong(crc32.getValue());
        userMetadataMap = RestUtils.buildUserMetadata(userMetadataByteArray);
        Assert.assertNull("UserMetadata should have been null ", userMetadataMap);
        // diff key length
        userMetadataByteArray = new byte[36];
        byteBuffer = ByteBuffer.wrap(userMetadataByteArray);
        byteBuffer.putShort(((short) (1)));
        byteBuffer.putInt(22);
        byteBuffer.putInt(1);
        byteBuffer.putInt((keyLength + 1));
        byteBuffer.put(keyInBytes);
        byteBuffer.putInt(valueLength);
        byteBuffer.put(valueInBytes);
        crc32 = new Crc32();
        crc32.update(userMetadataByteArray, 0, ((userMetadataByteArray.length) - 8));
        byteBuffer.putLong(crc32.getValue());
        userMetadataMap = RestUtils.buildUserMetadata(userMetadataByteArray);
        Assert.assertNull("UserMetadata should have been null ", userMetadataMap);
        // diff value length
        userMetadataByteArray = new byte[36];
        byteBuffer = ByteBuffer.wrap(userMetadataByteArray);
        byteBuffer.putShort(((short) (1)));
        byteBuffer.putInt(22);
        byteBuffer.putInt(1);
        byteBuffer.putInt(keyLength);
        byteBuffer.put(keyInBytes);
        byteBuffer.putInt((valueLength + 1));
        byteBuffer.put(valueInBytes);
        crc32 = new Crc32();
        crc32.update(userMetadataByteArray, 0, ((userMetadataByteArray.length) - 8));
        byteBuffer.putLong(crc32.getValue());
        userMetadataMap = RestUtils.buildUserMetadata(userMetadataByteArray);
        Assert.assertNull("UserMetadata should have been null ", userMetadataMap);
        // no crc
        userMetadataByteArray = new byte[36];
        byteBuffer = ByteBuffer.wrap(userMetadataByteArray);
        byteBuffer.putShort(((short) (1)));
        byteBuffer.putInt(22);
        byteBuffer.putInt(1);
        byteBuffer.putInt(keyLength);
        byteBuffer.put(keyInBytes);
        byteBuffer.putInt(valueLength);
        byteBuffer.put(valueInBytes);
        userMetadataMap = RestUtils.buildUserMetadata(userMetadataByteArray);
        Assert.assertNull("UserMetadata should have been null ", userMetadataMap);
        // wrong crc
        userMetadataByteArray = new byte[36];
        byteBuffer = ByteBuffer.wrap(userMetadataByteArray);
        byteBuffer.putShort(((short) (1)));
        byteBuffer.putInt(22);
        byteBuffer.putInt(1);
        byteBuffer.putInt(keyLength);
        byteBuffer.put(keyInBytes);
        byteBuffer.putInt(valueLength);
        byteBuffer.put(valueInBytes);
        crc32 = new Crc32();
        crc32.update(userMetadataByteArray, 0, ((userMetadataByteArray.length) - 8));
        byteBuffer.putLong(((crc32.getValue()) - 1));
        userMetadataMap = RestUtils.buildUserMetadata(userMetadataByteArray);
        Assert.assertNull("UserMetadata should have been null ", userMetadataMap);
        // correct crc
        userMetadataByteArray = new byte[36];
        byteBuffer = ByteBuffer.wrap(userMetadataByteArray);
        byteBuffer.putShort(((short) (1)));
        byteBuffer.putInt(22);
        byteBuffer.putInt(1);
        byteBuffer.putInt(keyLength);
        byteBuffer.put(keyInBytes);
        byteBuffer.putInt(valueLength);
        byteBuffer.put(valueInBytes);
        crc32 = new Crc32();
        crc32.update(userMetadataByteArray, 0, ((userMetadataByteArray.length) - 8));
        byteBuffer.putLong(crc32.getValue());
        userMetadataMap = RestUtils.buildUserMetadata(userMetadataByteArray);
        Assert.assertEquals("Sizes don't match ", userMetadataMap.size(), 1);
        Assert.assertTrue(((("User metadata " + (Headers.USER_META_DATA_HEADER_PREFIX)) + key) + " not found in user metadata "), userMetadataMap.containsKey(((Headers.USER_META_DATA_HEADER_PREFIX) + key)));
        Assert.assertEquals(((("User metadata " + (Headers.USER_META_DATA_HEADER_PREFIX)) + key) + " value don't match "), value, userMetadataMap.get(((Headers.USER_META_DATA_HEADER_PREFIX) + key)));
    }

    /**
     * Tests {@link RestUtils#getOperationOrBlobIdFromUri(RestRequest, RestUtils.SubResource, List)}.
     *
     * @throws JSONException
     * 		
     * @throws UnsupportedEncodingException
     * 		
     * @throws URISyntaxException
     * 		
     */
    @Test
    public void getOperationOrBlobIdFromUriTest() throws UnsupportedEncodingException, URISyntaxException, JSONException {
        String baseId = "expectedOp";
        String queryString = "?queryParam1=queryValue1&queryParam2=queryParam2=queryValue2";
        String[] validIdUris = new String[]{ "/", "/" + baseId, ("/" + baseId) + "/random/extra", "", baseId, baseId + "/random/extra", ((SIGNED_ID_PREFIX) + "/") + baseId, (("/" + (SIGNED_ID_PREFIX)) + "/") + baseId };
        List<String> prefixesToTestOn = Arrays.asList("", "/media", "/toRemove", "/orNotToRemove", ("/" + (RestUtilsTest.SECURE_PATH)));
        List<String> prefixesToRemove = Arrays.asList("/media", "/toRemove", ("/" + (RestUtilsTest.SECURE_PATH)));
        String blobId = UtilsTest.getRandomString(10);
        String blobIdQuery = ((Headers.BLOB_ID) + "=") + blobId;
        // construct test cases
        Map<String, String> testCases = new HashMap<>();
        for (String validIdUri : validIdUris) {
            // the uri as is (e.g. "/expectedOp).
            testCases.put(validIdUri, validIdUri);
            // the uri with a query string (e.g. "/expectedOp?param=value").
            testCases.put((validIdUri + queryString), validIdUri);
            if ((validIdUri.length()) > 1) {
                for (RestUtils.SubResource subResource : SubResource.values()) {
                    String subResourceStr = "/" + (subResource.name());
                    // the uri with a sub-resource (e.g. "/expectedOp/BlobInfo").
                    testCases.put((validIdUri + subResourceStr), validIdUri);
                    // the uri with a sub-resource and query string (e.g. "/expectedOp/BlobInfo?param=value").
                    testCases.put(((validIdUri + subResourceStr) + queryString), validIdUri);
                }
            } else {
                testCases.put(((validIdUri + "?") + blobIdQuery), blobId);
                testCases.put((((validIdUri + queryString) + "&") + blobIdQuery), blobId);
            }
        }
        // test each case on each prefix.
        for (String prefixToTestOn : prefixesToTestOn) {
            for (Map.Entry<String, String> testCase : testCases.entrySet()) {
                String testPath = testCase.getKey();
                // skip the ones with no leading slash if prefix is not "". Otherwise they become -> "/prefixexpectedOp".
                if ((prefixToTestOn.isEmpty()) || (testPath.startsWith("/"))) {
                    String realTestPath = prefixToTestOn + testPath;
                    String expectedOutput = testCase.getValue();
                    // skip the ones where the prefix will not be removed and the expected output is the blobId
                    if ((prefixesToRemove.contains(prefixToTestOn)) || (!(expectedOutput.equals(blobId)))) {
                        expectedOutput = (prefixesToRemove.contains(prefixToTestOn)) ? expectedOutput : prefixToTestOn + expectedOutput;
                        RestRequest restRequest = createRestRequest(GET, realTestPath, null);
                        Assert.assertEquals(("Unexpected operation/blob id for: " + realTestPath), expectedOutput, RestUtils.getOperationOrBlobIdFromUri(restRequest, RestUtils.getBlobSubResource(restRequest), prefixesToRemove));
                    }
                }
            }
        }
    }

    /**
     * Tests {@link RestUtils#getBlobSubResource(RestRequest)}.
     *
     * @throws JSONException
     * 		
     * @throws UnsupportedEncodingException
     * 		
     * @throws URISyntaxException
     * 		
     */
    @Test
    public void getBlobSubResourceTest() throws UnsupportedEncodingException, URISyntaxException, JSONException {
        // sub resource null
        String queryString = "?queryParam1=queryValue1&queryParam2=queryParam2=queryValue2";
        String[] nullUris = new String[]{ "/op", "/op/", "/op/invalid", "/op/invalid/", "op", "op/", "op/invalid", "op/invalid/" };
        for (String uri : nullUris) {
            RestRequest restRequest = createRestRequest(GET, uri, null);
            Assert.assertNull(("There was no sub-resource expected in: " + uri), RestUtils.getBlobSubResource(restRequest));
            // add a sub-resource at the end as part of the query string.
            for (RestUtils.SubResource subResource : SubResource.values()) {
                String fullUri = (uri + queryString) + subResource;
                restRequest = createRestRequest(GET, fullUri, null);
                Assert.assertNull(("There was no sub-resource expected in: " + fullUri), RestUtils.getBlobSubResource(restRequest));
            }
        }
        // valid sub resource
        String[] nonNullUris = new String[]{ "/op/", "/op/random/", "op/", "op/random/" };
        for (String uri : nonNullUris) {
            for (RestUtils.SubResource subResource : SubResource.values()) {
                String fullUri = uri + subResource;
                RestRequest restRequest = createRestRequest(GET, fullUri, null);
                Assert.assertEquals(("Unexpected sub resource in uri: " + fullUri), subResource, RestUtils.getBlobSubResource(restRequest));
                // add a query-string.
                fullUri = (uri + subResource) + queryString;
                restRequest = createRestRequest(GET, fullUri, null);
                Assert.assertEquals(("Unexpected sub resource in uri: " + fullUri), subResource, RestUtils.getBlobSubResource(restRequest));
            }
        }
    }

    /**
     * Tests {@link RestUtils#getPrefixAndResourceFromUri(RestRequest, RestUtils.SubResource, List)}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getPrefixFromUriTest() throws Exception {
        String blobId = UtilsTest.getRandomString(10);
        String operation = "validOp";
        String queryString = "?queryParam1=queryValue1&queryParam2=queryParam2=queryValue2";
        String[] validIdUris = new String[]{ "/", "/" + blobId, "", blobId, ((SIGNED_ID_PREFIX) + "/") + blobId };
        String[] validOpUris = new String[]{ operation + "/random/extra", ("/" + operation) + "/random/extra" };
        List<String> prefixesToRemove = Arrays.asList(("/" + (RestUtilsTest.SECURE_PATH)), "/media", "/toRemove");
        // construct test cases and expected results
        Map<String, String> testCasesAndExpectedResults = new HashMap<>();
        for (String prefix : prefixesToRemove) {
            for (String uri : validIdUris) {
                testCasesAndExpectedResults.put(((prefix + "/") + uri), prefix);
                testCasesAndExpectedResults.put((((prefix + "/") + uri) + queryString), prefix);
                if ((uri.length()) > 1) {
                    // add sub resource and query string
                    for (RestUtils.SubResource subResource : SubResource.values()) {
                        testCasesAndExpectedResults.put(((uri + "/") + subResource), "");
                        testCasesAndExpectedResults.put((((uri + "/") + subResource) + queryString), "");
                        testCasesAndExpectedResults.put(((((prefix + "/") + uri) + "/") + subResource), prefix);
                        testCasesAndExpectedResults.put((((((prefix + "/") + uri) + "/") + subResource) + queryString), prefix);
                    }
                }
            }
            for (String uri : validOpUris) {
                testCasesAndExpectedResults.put(((prefix + "/") + uri), prefix);
                testCasesAndExpectedResults.put((((prefix + "/") + uri) + queryString), prefix);
                for (RestUtils.SubResource subResource : SubResource.values()) {
                    testCasesAndExpectedResults.put(((((prefix + "/") + uri) + "/") + subResource), prefix);
                    testCasesAndExpectedResults.put((((((prefix + "/") + uri) + "/") + subResource) + queryString), prefix);
                    testCasesAndExpectedResults.put(((uri + "/") + subResource), "");
                    testCasesAndExpectedResults.put((((uri + "/") + subResource) + queryString), "");
                }
            }
        }
        Pair<String, String> prefixAndResource;
        RestUtils.SubResource subResource;
        // verify that prefixes can be correctly extracted from uri
        for (Map.Entry<String, String> testCaseAndResult : testCasesAndExpectedResults.entrySet()) {
            String testUri = testCaseAndResult.getKey();
            String expectedOutput = testCaseAndResult.getValue();
            RestRequest restRequest = createRestRequest(GET, testUri, null);
            subResource = RestUtils.getBlobSubResource(restRequest);
            prefixAndResource = RestUtils.getPrefixAndResourceFromUri(restRequest, subResource, prefixesToRemove);
            Assert.assertEquals(("Unexpected prefix for: " + testUri), expectedOutput, prefixAndResource.getFirst());
        }
    }

    /**
     * Tests {@link RestUtils#toSecondsPrecisionInMs(long)}.
     */
    @Test
    public void toSecondsPrecisionInMsTest() {
        Assert.assertEquals(0, RestUtils.toSecondsPrecisionInMs(999));
        Assert.assertEquals(1000, RestUtils.toSecondsPrecisionInMs(1000));
        Assert.assertEquals(1000, RestUtils.toSecondsPrecisionInMs(1001));
    }

    /**
     * Tests {@link RestUtils#getTimeFromDateString(String)}.
     */
    @Test
    public void getTimeFromDateStringTest() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.ENGLISH);
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        long curTime = System.currentTimeMillis();
        Date curDate = new Date(curTime);
        String dateStr = dateFormatter.format(curDate);
        long epochTime = RestUtils.getTimeFromDateString(dateStr);
        long actualExpectedTime = (curTime / 1000L) * 1000;
        // Note http time is kept in Seconds so last three digits will be 000
        Assert.assertEquals("Time mismatch ", actualExpectedTime, epochTime);
        dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.CHINA);
        curTime = System.currentTimeMillis();
        curDate = new Date(curTime);
        dateStr = dateFormatter.format(curDate);
        // any other locale is not accepted
        Assert.assertEquals("Should have returned null", null, RestUtils.getTimeFromDateString(dateStr));
        Assert.assertEquals("Should have returned null", null, RestUtils.getTimeFromDateString("abc"));
    }

    /**
     * This tests the construction of {@link GetBlobOptions} objects with various range and sub-resource settings using
     * {@link RestUtils#buildGetBlobOptions(Map, RestUtils.SubResource, GetOption)} and
     * {@link RestUtils#buildByteRange(String)}.
     *
     * @throws RestServiceException
     * 		
     */
    @Test
    public void buildGetBlobOptionsTest() throws RestServiceException {
        // no range
        doBuildGetBlobOptionsTest(null, null, true, true);
        // valid ranges
        doBuildGetBlobOptionsTest("bytes=0-7", ByteRanges.fromOffsetRange(0, 7), true, false);
        doBuildGetBlobOptionsTest("bytes=234-56679090", ByteRanges.fromOffsetRange(234, 56679090), true, false);
        doBuildGetBlobOptionsTest("bytes=1-", ByteRanges.fromStartOffset(1), true, false);
        doBuildGetBlobOptionsTest("bytes=12345678-", ByteRanges.fromStartOffset(12345678), true, false);
        doBuildGetBlobOptionsTest("bytes=-8", ByteRanges.fromLastNBytes(8), true, false);
        doBuildGetBlobOptionsTest("bytes=-123456789", ByteRanges.fromLastNBytes(123456789), true, false);
        // bad ranges
        String[] badRanges = new String[]{ "bytes=0-abcd", "bytes=0as23-44444444", "bytes=22-7777777777777777777777777777777777777777777", "bytes=22--53", "bytes=223-34", "bytes=-34ab", "bytes=--12", "bytes=-12-", "bytes=12ab-", "bytes=---", "btes=3-5", "bytes=345", "bytes=3.14-22", "bytes=3-6.2", "bytes=", "bytes=-", "bytes= -" };
        for (String badRange : badRanges) {
            doBuildGetBlobOptionsTest(badRange, null, false, false);
        }
    }

    /**
     * Test {@link RestUtils#buildContentRangeAndLength(ByteRange, long)}.
     */
    @Test
    public void buildContentRangeAndLengthTest() throws RestServiceException {
        // good cases
        doBuildContentRangeAndLengthTest(ByteRanges.fromOffsetRange(4, 8), 12, "bytes 4-8/12", 5, true);
        doBuildContentRangeAndLengthTest(ByteRanges.fromOffsetRange(4, 12), 12, "bytes 4-11/12", 8, true);
        doBuildContentRangeAndLengthTest(ByteRanges.fromOffsetRange(4, 15), 12, "bytes 4-11/12", 8, true);
        doBuildContentRangeAndLengthTest(ByteRanges.fromStartOffset(14), 17, "bytes 14-16/17", 3, true);
        doBuildContentRangeAndLengthTest(ByteRanges.fromLastNBytes(12), 17, "bytes 5-16/17", 12, true);
        doBuildContentRangeAndLengthTest(ByteRanges.fromLastNBytes(17), 17, "bytes 0-16/17", 17, true);
        doBuildContentRangeAndLengthTest(ByteRanges.fromLastNBytes(13), 12, "bytes 0-11/12", 12, true);
        // bad cases
        doBuildContentRangeAndLengthTest(ByteRanges.fromOffsetRange(4, 12), 4, null, (-1), false);
        doBuildContentRangeAndLengthTest(ByteRanges.fromStartOffset(12), 12, null, (-1), false);
        doBuildContentRangeAndLengthTest(ByteRanges.fromStartOffset(15), 12, null, (-1), false);
    }

    /**
     * Tests {@link RestUtils#getGetOption(RestRequest, GetOption)}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getGetOptionTest() throws Exception {
        for (GetOption option : GetOption.values()) {
            JSONObject headers = new JSONObject();
            headers.put(GET_OPTION, option.toString().toLowerCase());
            RestRequest restRequest = createRestRequest(GET, "/", headers);
            Assert.assertEquals("Option returned not as expected", option, RestUtils.getGetOption(restRequest, null));
            Assert.assertEquals("Option returned not as expected", option, RestUtils.getGetOption(restRequest, option));
            Assert.assertEquals("Option returned not as expected", option, RestUtils.getGetOption(restRequest, None));
        }
        // no value defined
        RestRequest restRequest = createRestRequest(GET, "/", null);
        Assert.assertEquals("Option returned not as expected", None, RestUtils.getGetOption(restRequest, null));
        for (GetOption option : GetOption.values()) {
            Assert.assertEquals("Option returned not as expected", option, RestUtils.getGetOption(restRequest, option));
        }
        // bad value
        JSONObject headers = new JSONObject();
        headers.put(GET_OPTION, "non_existent_option");
        restRequest = createRestRequest(GET, "/", headers);
        try {
            RestUtils.getGetOption(restRequest, None);
            Assert.fail("Should have failed to get GetOption because value of header is invalid");
        } catch (RestServiceException e) {
            Assert.assertEquals("Unexpected RestServiceErrorCode", InvalidArgs, e.getErrorCode());
        }
    }

    /**
     * Tests {@link RestUtils#getHeader(Map, String, boolean)}.
     *
     * @throws RestServiceException
     * 		
     */
    @Test
    public void getHeaderTest() throws RestServiceException {
        Map<String, Object> args = new HashMap<>();
        args.put("HeaderA", "ValueA");
        args.put("HeaderB", null);
        // get HeaderA
        Assert.assertEquals("Header value does not match", args.get("HeaderA"), RestUtils.getHeader(args, "HeaderA", true));
        Assert.assertEquals("Header value does not match", args.get("HeaderA"), RestUtils.getHeader(args, "HeaderA", false));
        // get HeaderB
        Assert.assertNull("There should be no value for HeaderB", RestUtils.getHeader(args, "HeaderB", false));
        try {
            RestUtils.getHeader(args, "HeaderB", true);
            Assert.fail("Getting HeaderB as required should have failed");
        } catch (RestServiceException e) {
            Assert.assertEquals("Unexpected RestServiceErrorCode", InvalidArgs, e.getErrorCode());
        }
        // get HeaderC
        Assert.assertNull("There should be no value for HeaderC", RestUtils.getHeader(args, "HeaderB", false));
        try {
            RestUtils.getHeader(args, "Headerc", true);
            Assert.fail("Getting HeaderB as required should have failed");
        } catch (RestServiceException e) {
            Assert.assertEquals("Unexpected RestServiceErrorCode", MissingArgs, e.getErrorCode());
        }
    }

    /**
     * Tests for {@link RestUtils#getLongHeader(Map, String, boolean)}.
     *
     * @throws RestServiceException
     * 		
     */
    @Test
    public void getLongHeaderTest() throws RestServiceException {
        Map<String, Object> args = new HashMap<>();
        args.put("HeaderA", 1000L);
        args.put("HeaderB", "NotLong");
        args.put("HeaderC", "10000");
        // getLongHeader() calls getHeader() and in the interest of keeping tests short, tests for that functionality
        // are not repeated here. If that changes, these tests need to change.
        Assert.assertEquals("Header value does not match", args.get("HeaderA"), RestUtils.getLongHeader(args, "HeaderA", true));
        Assert.assertEquals("Header value does not match", Long.parseLong(args.get("HeaderC").toString()), RestUtils.getLongHeader(args, "HeaderC", true).longValue());
        try {
            RestUtils.getLongHeader(args, "HeaderB", true);
            Assert.fail("Getting HeaderB as required should have failed");
        } catch (RestServiceException e) {
            Assert.assertEquals("Unexpected RestServiceErrorCode", InvalidArgs, e.getErrorCode());
        }
        Assert.assertNull("There should no value for HeaderD", RestUtils.getLongHeader(args, "HeaderD", false));
    }

    /**
     * Tests for {@link RestUtils#setUserMetadataHeaders(byte[], RestResponseChannel)}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void setUserMetadataHeadersTest() throws Exception {
        // empty user metadata
        MockRestResponseChannel responseChannel = new MockRestResponseChannel();
        Assert.assertTrue("Should report that headers are set", RestUtils.setUserMetadataHeaders(new byte[0], responseChannel));
        Assert.assertEquals("No headers should have been set", 0, responseChannel.getResponseHeaders().size());
        JSONObject headers = new JSONObject();
        RestUtilsTest.setUserMetadataHeaders(headers, Collections.emptyMap());
        RestRequest restRequest = createRestRequest(POST, "/", headers);
        byte[] userMetadata = RestUtils.buildUserMetadata(restRequest.getArgs());
        responseChannel = new MockRestResponseChannel();
        Assert.assertTrue("Should report that headers are set", RestUtils.setUserMetadataHeaders(userMetadata, responseChannel));
        Assert.assertEquals("No headers should have been set", 0, responseChannel.getResponseHeaders().size());
        // user metadata that can be deserialized
        Map<String, String> usermetadataMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            usermetadataMap.put((((Headers.USER_META_DATA_HEADER_PREFIX) + "key") + i), ("value" + i));
        }
        RestUtilsTest.setUserMetadataHeaders(headers, usermetadataMap);
        restRequest = createRestRequest(POST, "/", headers);
        userMetadata = RestUtils.buildUserMetadata(restRequest.getArgs());
        responseChannel = new MockRestResponseChannel();
        Assert.assertTrue("Should report that headers are set", RestUtils.setUserMetadataHeaders(userMetadata, responseChannel));
        Map<String, Object> responseHeaders = responseChannel.getResponseHeaders();
        Assert.assertEquals("There is a mismatch in the numebr of headers", usermetadataMap.size(), responseHeaders.size());
        usermetadataMap.forEach(( k, v) -> Assert.assertEquals((("Value of " + k) + " not as expected"), v, responseHeaders.get(k)));
        // user metadata that cannot be deserialized
        responseChannel = new MockRestResponseChannel();
        userMetadata = TestUtils.getRandomBytes(100);
        Assert.assertFalse("Should report that headers are not set", RestUtils.setUserMetadataHeaders(userMetadata, responseChannel));
        Assert.assertEquals("No headers should have been set", 0, responseChannel.getResponseHeaders().size());
    }
}

