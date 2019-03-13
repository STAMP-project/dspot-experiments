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


import ApioApiDocumentation.SupportedClass;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.talend.runtime.apio.form.Property;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
public class ApioApiDocumentationTest {
    @Test
    public void testGetDescription() {
        String description = ApioApiDocumentationTest._apioApiDocumentation.getDescription();
        Assert.assertThat(description, CoreMatchers.equalTo(("This API allows developers to try a Hypermedia API without " + "creating one")));
    }

    @Test
    public void testGetSupportedClasses1() {
        List<ApioApiDocumentation.SupportedClass> supportedClasses = ApioApiDocumentationTest._apioApiDocumentation.getSupportedClasses();
        Assert.assertThat(supportedClasses.size(), CoreMatchers.equalTo(5));
        Stream<ApioApiDocumentation.SupportedClass> supportedClassStream = supportedClasses.stream();
        ApioApiDocumentation.SupportedClass supportedClass = supportedClassStream.filter(( clazz) -> "Comment".equals(clazz.getName())).findFirst().orElseThrow(() -> new AssertionError("Unable to find 'Commerce' supported class"));
        List<Property> supportedProperties = supportedClass.getSupportedProperties();
        Stream<Property> propertyStream = supportedProperties.stream();
        List<String> propertyNames = propertyStream.map(Property::getName).collect(Collectors.toList());
        Assert.assertThat(supportedProperties.size(), CoreMatchers.equalTo(4));
        Assert.assertThat(propertyNames, CoreMatchers.hasItems("dateCreated", "dateModified", "text", "author"));
    }

    @Test
    public void testGetSupportedClasses2() {
        List<ApioApiDocumentation.SupportedClass> supportedClasses = ApioApiDocumentationTest._apioApiDocumentation.getSupportedClasses();
        Assert.assertThat(supportedClasses.size(), CoreMatchers.equalTo(5));
        Stream<ApioApiDocumentation.SupportedClass> supportedClassStream = supportedClasses.stream();
        ApioApiDocumentation.SupportedClass supportedClass = supportedClassStream.filter(( clazz) -> "BlogPosting".equals(clazz.getName())).findFirst().orElseThrow(() -> new AssertionError("Unable to find 'BlogPosting' supported class"));
        List<Property> supportedProperties = supportedClass.getSupportedProperties();
        Stream<Property> propertyStream = supportedProperties.stream();
        List<String> propertyNames = propertyStream.map(Property::getName).collect(Collectors.toList());
        Assert.assertThat(supportedProperties.size(), CoreMatchers.equalTo(8));
        Assert.assertThat(propertyNames, CoreMatchers.hasItems("dateCreated", "dateModified", "alternativeHeadline", "articleBody", "fileFormat", "headline", "creator", "comment"));
    }

    @Test
    public void testGetTitle() {
        String title = ApioApiDocumentationTest._apioApiDocumentation.getTitle();
        Assert.assertThat(title, CoreMatchers.equalTo("Apio Sample API"));
    }

    @Test
    public void testWrongType() throws Exception {
        expectedException.expect(IOException.class);
        expectedException.expectMessage(("The type of the given resource is not an instance of " + "ApiDocumentation"));
        String json = ApioApiDocumentationTest.read("SampleResource.json");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);
        new ApioApiDocumentation(jsonNode);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static ApioApiDocumentation _apioApiDocumentation;
}

