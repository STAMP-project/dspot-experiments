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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class MultipleInstancesResponseTypeTest extends AbstractResponseTypeTest<List<AbstractResponseTypeTest.QueryResponse>> {
    public MultipleInstancesResponseTypeTest() {
        super(new MultipleInstancesResponseType(AbstractResponseTypeTest.QueryResponse.class));
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsTheSame() throws NoSuchMethodException {
        testMatches("someQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsSubTypeOfProvidedType() throws NoSuchMethodException {
        testMatches("someSubTypedQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
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
    public void testMatchesReturnsFalseIfResponseTypeIsBoundedGenericOfProvidedType() throws NoSuchMethodException {
        testMatches("someBoundedGenericQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsMultiBoundedGenericOfProvidedType() throws NoSuchMethodException {
        testMatches("someMultiBoundedGenericQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsGenericOfOtherType() throws NoSuchMethodException {
        testMatches("someNonMatchingBoundedGenericQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsArrayOfProvidedType() throws NoSuchMethodException {
        testMatches("someArrayQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsArrayWithSubTypeOfProvidedType() throws NoSuchMethodException {
        testMatches("someSubTypedArrayQuery", AbstractResponseTypeTest.MATCHES);
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
    public void testMatchesReturnsTrueIfResponseTypeIsBoundedGenericArrayOfProvidedType() throws NoSuchMethodException {
        testMatches("someBoundedGenericArrayQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsMultiBoundedGenericArrayOfProvidedType() throws NoSuchMethodException {
        testMatches("someMultiBoundedGenericArrayQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsGenericArrayOfOtherType() throws NoSuchMethodException {
        testMatches("someNonMatchingBoundedGenericArrayQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsListOfProvidedType() throws NoSuchMethodException {
        testMatches("someListQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsSubListOfProvidedType() throws NoSuchMethodException {
        testMatches("someSubListQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsSuperListOfProvidedType() throws NoSuchMethodException {
        testMatches("someSuperListQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsBoundedGenericListOfProvidedType() throws NoSuchMethodException {
        testMatches("someBoundedGenericListQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsUnboundedGenericList() throws NoSuchMethodException {
        testMatches("someUnboundedGenericListQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsMultiBoundedGenericListOfProvidedType() throws NoSuchMethodException {
        testMatches("someMultiBoundedGenericListQuery", AbstractResponseTypeTest.MATCHES);
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
    public void testMatchesReturnsTrueIfResponseTypeIsUpperBoundedWildcardListOfProvidedType() throws NoSuchMethodException {
        testMatches("someUpperBoundedWildcardListQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsWildcardListOfOtherType() throws NoSuchMethodException {
        testMatches("someNonMatchingUpperBoundedWildcardQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsGenericUpperBoundedWildcardListOfProvidedType() throws NoSuchMethodException {
        testMatches("someGenericUpperBoundedWildcardListQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsMultiGenericUpperBoundedWildcardListOfProvidedType() throws NoSuchMethodException {
        testMatches("someMultiGenericUpperBoundedWildcardListQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsUnboundedGenericUpperBoundedWildcardList() throws NoSuchMethodException {
        testMatches("someUnboundedGenericUpperBoundedWildcardListQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsListImplementationOfProvidedType() throws NoSuchMethodException {
        testMatches("someListImplementationQuery", AbstractResponseTypeTest.MATCHES);
    }

    @SuppressWarnings("WeakerAccess")
    static class QueryResponseList extends ArrayList<AbstractResponseTypeTest.QueryResponse> {}

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsUnboundedListImplementationOfProvidedType() throws NoSuchMethodException {
        testMatches("someUnboundedListImplementationQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsBoundedListImplementationOfProvidedType() throws NoSuchMethodException {
        testMatches("someBoundedListImplementationQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsMultiUnboundedListImplementationOfProvidedType() throws NoSuchMethodException {
        testMatches("someMultiUnboundedListImplementationQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsMultiBoundedListImplementationOfProvidedType() throws NoSuchMethodException {
        testMatches("someMultiBoundedListImplementationQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsSetOfProvidedType() throws NoSuchMethodException {
        testMatches("someSetQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsStreamOfProvidedType() throws NoSuchMethodException {
        testMatches("someStreamQuery", AbstractResponseTypeTest.MATCHES);
    }

    @Test
    public void testMatchesReturnsFalseIfResponseTypeIsMapOfProvidedType() throws NoSuchMethodException {
        testMatches("someMapQuery", AbstractResponseTypeTest.DOES_NOT_MATCHES);
    }

    @Test
    public void testMatchesReturnsTrueIfResponseTypeIsFutureOfProvidedType() throws NoSuchMethodException {
        testMatches("someFutureListQuery", AbstractResponseTypeTest.MATCHES);
    }

    @SuppressWarnings("unused")
    @Test(expected = Exception.class)
    public void testConvertThrowsExceptionForSingleInstanceResponse() {
        List<AbstractResponseTypeTest.QueryResponse> result = testSubject.convert(new AbstractResponseTypeTest.QueryResponse());
    }

    @Test
    public void testConvertReturnsListOnResponseOfArrayType() {
        AbstractResponseTypeTest.QueryResponse[] testResponse = new AbstractResponseTypeTest.QueryResponse[]{ new AbstractResponseTypeTest.QueryResponse() };
        List<AbstractResponseTypeTest.QueryResponse> result = testSubject.convert(testResponse);
        Assert.assertEquals(testResponse.length, result.size());
        Assert.assertEquals(testResponse[0], result.get(0));
    }

    @Test
    public void testConvertReturnsListOnResponseOfSubTypedArrayType() {
        AbstractResponseTypeTest.SubTypedQueryResponse[] testResponse = new AbstractResponseTypeTest.SubTypedQueryResponse[]{ new AbstractResponseTypeTest.SubTypedQueryResponse() };
        List<AbstractResponseTypeTest.QueryResponse> result = testSubject.convert(testResponse);
        Assert.assertEquals(testResponse.length, result.size());
        Assert.assertEquals(testResponse[0], result.get(0));
    }

    @SuppressWarnings("unused")
    @Test(expected = Exception.class)
    public void testConvertThrowsExceptionForResponseOfDifferentArrayType() {
        AbstractResponseTypeTest.QueryResponseInterface[] testResponse = new AbstractResponseTypeTest.QueryResponseInterface[]{ new AbstractResponseTypeTest.QueryResponseInterface() {} };
        List<AbstractResponseTypeTest.QueryResponse> result = testSubject.convert(testResponse);
    }

    @Test
    public void testConvertReturnsListOnResponseOfListType() {
        List<AbstractResponseTypeTest.QueryResponse> testResponse = new ArrayList<>();
        testResponse.add(new AbstractResponseTypeTest.QueryResponse());
        List<AbstractResponseTypeTest.QueryResponse> result = testSubject.convert(testResponse);
        Assert.assertEquals(testResponse.size(), result.size());
        Assert.assertEquals(testResponse.get(0), result.get(0));
    }

    @Test
    public void testConvertReturnsListOnResponseOfSubTypedListType() {
        List<AbstractResponseTypeTest.SubTypedQueryResponse> testResponse = new ArrayList<>();
        testResponse.add(new AbstractResponseTypeTest.SubTypedQueryResponse());
        List<AbstractResponseTypeTest.QueryResponse> result = testSubject.convert(testResponse);
        Assert.assertEquals(testResponse.size(), result.size());
        Assert.assertEquals(testResponse.get(0), result.get(0));
    }

    @Test
    public void testConvertReturnsListOnResponseOfSetType() {
        Set<AbstractResponseTypeTest.QueryResponse> testResponse = new HashSet<>();
        testResponse.add(new AbstractResponseTypeTest.QueryResponse());
        List<AbstractResponseTypeTest.QueryResponse> result = testSubject.convert(testResponse);
        Assert.assertEquals(testResponse.size(), result.size());
        Assert.assertEquals(testResponse.iterator().next(), result.get(0));
    }

    @SuppressWarnings("unused")
    @Test(expected = Exception.class)
    public void testConvertThrowsExceptionForResponseOfDifferentListType() {
        List<AbstractResponseTypeTest.QueryResponseInterface> testResponse = new ArrayList<>();
        testResponse.add(new AbstractResponseTypeTest.QueryResponseInterface() {});
        List<AbstractResponseTypeTest.QueryResponse> result = testSubject.convert(testResponse);
    }

    @Test
    public void testConvertReturnsEmptyListForResponseOfDifferentListTypeIfTheListIsEmpty() {
        List<AbstractResponseTypeTest.QueryResponseInterface> testResponse = new ArrayList<>();
        List<AbstractResponseTypeTest.QueryResponse> result = testSubject.convert(testResponse);
        Assert.assertTrue(result.isEmpty());
    }
}

