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
package org.axonframework.messaging.responsetypes;


import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


public class InstanceResponseTypeTest extends AbstractResponseTypeTest<AbstractResponseTypeTest.QueryResponse> {
    public InstanceResponseTypeTest() {
        super(new InstanceResponseType(AbstractResponseTypeTest.QueryResponse.class));
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsTheSame() throws NoSuchMethodException {
        testMatches("someQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsSubTypeOfProvidedType() throws NoSuchMethodException {
        testMatches("someSubTypedQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsSuperTypeOfProvidedType() throws NoSuchMethodException {
        testMatches("someSuperTypedQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsUnboundedGeneric() throws NoSuchMethodException {
        testMatches("someUnboundedGenericQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsBoundedGenericOfProvidedType() throws NoSuchMethodException {
        testMatches("someBoundedGenericQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsMultiBoundedGenericOfProvidedType() throws NoSuchMethodException {
        testMatches("someMultiBoundedGenericQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsGenericOfOtherType() throws NoSuchMethodException {
        testMatches("someNonMatchingBoundedGenericQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsArrayOfProvidedType() throws NoSuchMethodException {
        testMatches("someArrayQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsArrayWithSubTypeOfProvidedType() throws NoSuchMethodException {
        testMatches("someSubTypedArrayQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsArrayWithSuperTypeOfProvidedType() throws NoSuchMethodException {
        testMatches("someSuperTypedArrayQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsUnboundedGenericArray() throws NoSuchMethodException {
        testMatches("someUnboundedGenericArrayQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsBoundedGenericArrayOfProvidedType() throws NoSuchMethodException {
        testMatches("someBoundedGenericArrayQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsMultiBoundedGenericArrayOfProvidedType() throws NoSuchMethodException {
        testMatches("someMultiBoundedGenericArrayQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsGenericArrayOfOtherType() throws NoSuchMethodException {
        testMatches("someNonMatchingBoundedGenericArrayQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsListOfProvidedType() throws NoSuchMethodException {
        testMatches("someListQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsSubListOfProvidedType() throws NoSuchMethodException {
        testMatches("someSubListQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsSuperListOfProvidedType() throws NoSuchMethodException {
        testMatches("someSuperListQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsBoundedGenericListOfProvidedType() throws NoSuchMethodException {
        testMatches("someBoundedGenericListQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsUnboundedGenericList() throws NoSuchMethodException {
        testMatches("someUnboundedGenericListQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsMultiBoundedGenericListOfProvidedType() throws NoSuchMethodException {
        testMatches("someMultiBoundedGenericListQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsGenericListOfOtherType() throws NoSuchMethodException {
        testMatches("someNonMatchingBoundedGenericListQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsUnboundedWildcardList() throws NoSuchMethodException {
        testMatches("someUnboundedWildcardListQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsLowerBoundedWildcardList() throws NoSuchMethodException {
        testMatches("someLowerBoundedWildcardListQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsUpperBoundedWildcardListOfProvidedType() throws NoSuchMethodException {
        testMatches("someUpperBoundedWildcardListQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsWildcardListOfOtherType() throws NoSuchMethodException {
        testMatches("someNonMatchingUpperBoundedWildcardQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsGenericUpperBoundedWildcardListOfProvidedType() throws NoSuchMethodException {
        testMatches("someGenericUpperBoundedWildcardListQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsMultiGenericUpperBoundedWildcardListOfProvidedType() throws NoSuchMethodException {
        testMatches("someMultiGenericUpperBoundedWildcardListQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsUnboundedGenericUpperBoundedWildcardList() throws NoSuchMethodException {
        testMatches("someUnboundedGenericUpperBoundedWildcardListQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsSetOfProvidedType() throws NoSuchMethodException {
        testMatches("someSetQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsStreamOfProvidedType() throws NoSuchMethodException {
        testMatches("someStreamQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsMapOfProvidedType() throws NoSuchMethodException {
        testMatches("someMapQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsFutureOfProvidedType() throws NoSuchMethodException {
        testMatches("someFutureQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testConvertReturnsSingleResponseAsIs() {
        AbstractResponseTypeTest.QueryResponse testResponse = new AbstractResponseTypeTest.QueryResponse();
        AbstractResponseTypeTest.QueryResponse result = testSubject.convert(testResponse);
        Assert.assertEquals(testResponse, result);
    }

    @Test
    public void testConvertReturnsSingleResponseAsIsForSubTypedResponse() {
        AbstractResponseTypeTest.SubTypedQueryResponse testResponse = new AbstractResponseTypeTest.SubTypedQueryResponse();
        AbstractResponseTypeTest.QueryResponse result = testSubject.convert(testResponse);
        Assert.assertEquals(testResponse, result);
    }

    @SuppressWarnings("unused")
    @Test(expected = Exception.class)
    public void testConvertThrowsClassCastExceptionForDifferentSingleInstanceResponse() {
        AbstractResponseTypeTest.QueryResponse result = testSubject.convert(new AbstractResponseTypeTest.QueryResponseInterface() {});
    }

    @SuppressWarnings("unused")
    @Test(expected = Exception.class)
    public void testConvertThrowsClassCastExceptionForMultipleInstanceResponse() {
        AbstractResponseTypeTest.QueryResponse result = testSubject.convert(new ArrayList<AbstractResponseTypeTest.QueryResponse>());
    }
}

