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
public class ApioFormTest {
    @Test
    public void testGetDescription() {
        String description = ApioFormTest._apioForm.getDescription();
        Assert.assertThat(description, CoreMatchers.equalTo("This form can be used to create or update a person"));
    }

    @Test
    public void testGetSupportedProperties() {
        List<Property> properties = ApioFormTest._apioForm.getSupportedProperties();
        Stream<Property> stream = properties.stream();
        List<String> propertyNames = stream.map(Property::getName).map(( name) -> name.startsWith("#") ? name.substring(1) : name).collect(Collectors.toList());
        Assert.assertThat(propertyNames.size(), CoreMatchers.equalTo(7));
        Assert.assertThat(propertyNames, CoreMatchers.hasItems("address", "birthDate", "email", "familyName", "givenName", "image", "jobTitle"));
    }

    @Test
    public void testGetTitle() {
        String title = ApioFormTest._apioForm.getTitle();
        Assert.assertThat(title, CoreMatchers.equalTo("The person form"));
    }

    @Test
    public void testWrongType() throws Exception {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("The given resource is not a from");
        String json = ApioFormTest.read("SampleResource.json");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);
        new ApioForm(jsonNode);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static ApioForm _apioForm;
}

