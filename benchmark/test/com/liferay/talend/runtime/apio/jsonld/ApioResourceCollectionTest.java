/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.talend.runtime.apio.jsonld;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.talend.runtime.apio.operation.Operation;
import java.io.IOException;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 *
 * @author Zolt?n Tak?cs
 */
public class ApioResourceCollectionTest {
    @Test
    public void testGetContextVocabulary() {
        JsonNode jsonNode = ApioResourceCollectionTest._apioJsonLDResource.getContextJsonNode();
        String vocabulary = ApioResourceCollectionTest._apioJsonLDResource.getVocabulary(jsonNode);
        Assert.assertThat(vocabulary, CoreMatchers.equalTo("http://schema.org/"));
    }

    @Test
    public void testGetResourceActualPage() {
        String actualPage = ApioResourceCollectionTest._apioJsonLDResource.getResourceActualPage();
        Assert.assertThat(actualPage, CoreMatchers.equalTo(ApioResourceCollectionTest._DEFAULT_PAGE));
    }

    @Test
    public void testGetResourceCollectionType() {
        String resourceType = ApioResourceCollectionTest._apioJsonLDResource.getResourceCollectionType();
        Assert.assertThat(resourceType, CoreMatchers.equalTo("Person"));
    }

    @Test
    public void testGetResourceElementFieldNames() {
        List<String> fieldNames = ApioResourceCollectionTest._apioJsonLDResource.getResourceElementFieldNames();
        Assert.assertThat(fieldNames, CoreMatchers.hasItems("familyName", "givenName"));
    }

    @Test
    public void testGetResourceFirstPage() {
        String firstPage = ApioResourceCollectionTest._apioJsonLDResource.getResourceFirstPage();
        Assert.assertThat(firstPage, CoreMatchers.equalTo(ApioResourceCollectionTest._DEFAULT_PAGE));
    }

    @Test
    public void testGetResourceLastPage() {
        String lastPage = ApioResourceCollectionTest._apioJsonLDResource.getResourceLastPage();
        Assert.assertThat(lastPage, CoreMatchers.equalTo(ApioResourceCollectionTest._DEFAULT_PAGE));
    }

    @Test
    public void testGetResourceNextPage() {
        String nextPage = ApioResourceCollectionTest._apioJsonLDResource.getResourceNextPage();
        Assert.assertThat(nextPage, CoreMatchers.equalTo(ApioResourceCollectionTest._EMPTY));
    }

    @Test
    public void testGetResourceOperations() {
        List<Operation> operations = ApioResourceCollectionTest._apioJsonLDResource.getResourceOperations();
        Assert.assertThat(operations.size(), CoreMatchers.equalTo(1));
        Operation operation = operations.get(0);
        String method = operation.getMethod();
        String expects = operation.getExpects();
        Assert.assertThat(method, CoreMatchers.equalTo("POST"));
        Assert.assertThat(expects, CoreMatchers.equalTo("https://apiosample.wedeploy.io/f/c/people"));
    }

    @Test
    public void testGetResourcePreviousPage() {
        String previousPage = ApioResourceCollectionTest._apioJsonLDResource.getResourcePreviousPage();
        Assert.assertThat(previousPage, CoreMatchers.equalTo(ApioResourceCollectionTest._EMPTY));
    }

    @Test
    public void testIsSingleModel() {
        Assert.assertThat(ApioResourceCollectionTest._apioJsonLDResource.isSingleModel(), CoreMatchers.is(false));
    }

    @Test
    public void testNumberOfItems() {
        int numberOfItems = ApioResourceCollectionTest._apioJsonLDResource.getNumberOfItems();
        Assert.assertThat(numberOfItems, CoreMatchers.equalTo(10));
    }

    @Test
    public void testTotalItems() {
        int totalItems = ApioResourceCollectionTest._apioJsonLDResource.getTotalItems();
        Assert.assertThat(totalItems, CoreMatchers.equalTo(10));
    }

    @Test
    public void testWrongType() throws Exception {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("The type of the given resource is not a Collection");
        String json = ApioResourceCollectionTest.read("SampleResource.json");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);
        new ApioResourceCollection(jsonNode);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String _DEFAULT_PAGE = "https://apiosample.wedeploy.io/p/people?page=1&per_page=30";

    private static final String _EMPTY = "";

    private static ApioResourceCollection _apioJsonLDResource;
}

