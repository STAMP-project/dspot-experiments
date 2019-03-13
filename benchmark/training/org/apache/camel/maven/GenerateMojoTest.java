/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.maven;


import java.util.ArrayList;
import org.apache.camel.component.salesforce.api.dto.SObjectDescription;
import org.apache.camel.component.salesforce.api.dto.SObjectField;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class GenerateMojoTest {
    private static final String ACCOUNT = "Account";

    private static final String CONTACT = "Contact";

    private static final String MULTIPICKLIST = "multipicklist";

    private static final String PICKLIST = "picklist";

    private static final String PROPER_DEFAULT_MULTIPICKLIST_TYPE_ENDING = "Enum[]";

    private static final String PROPER_DEFAULT_PICKLIST_TYPE_ENDING = "Enum";

    private static final String PROPER_MULTIPICKLIST_TO_STRING_TYPE = (String.class.getName()) + "[]";

    private static final String PROPER_PICKLIST_TO_STRING_TYPE = String.class.getName();

    @Test
    public void shouldParsePicklistsToObjects() {
        // given
        final int properCountExceptions = 0;
        final GenerateMojo mojo = new GenerateMojo();
        mojo.picklistToStrings = GenerateMojoTest.createValidPicklistToStrings();
        mojo.picklistToEnums = GenerateMojoTest.createValidPicklistToEnums();
        // when
        int resultCountExceptions = 0;
        try {
            mojo.parsePicklistToEnums();
        } catch (final IllegalArgumentException e) {
            resultCountExceptions++;
        }
        try {
            mojo.parsePicklistToStrings();
        } catch (final IllegalArgumentException e) {
            resultCountExceptions++;
        }
        // then
        Assert.assertEquals(properCountExceptions, resultCountExceptions);
    }

    @Test
    public void shouldParsePicklistsToObjectsFail() {
        // given
        final int properCountExceptions = 2;
        final String[] invalidPicklistToStrings = new String[]{ "Account,DataSource" };
        final String[] invalidPicklistToEnums = new String[]{ "Con-tact.Contact_Source_Information__c" };
        final GenerateMojo mojo = new GenerateMojo();
        mojo.picklistToStrings = invalidPicklistToStrings;
        mojo.picklistToEnums = invalidPicklistToEnums;
        // when
        int resultCountExceptions = 0;
        try {
            mojo.parsePicklistToEnums();
        } catch (final IllegalArgumentException e) {
            resultCountExceptions++;
        }
        try {
            mojo.parsePicklistToStrings();
        } catch (final IllegalArgumentException e) {
            resultCountExceptions++;
        }
        // then
        Assert.assertEquals(properCountExceptions, resultCountExceptions);
    }

    @Test
    public void shouldReturnStringOrEnumTypesDefaultEnumMode() {
        // given
        final String sObjectName = GenerateMojoTest.ACCOUNT;
        final GenerateMojo mojo = new GenerateMojo();
        mojo.picklistToStrings = new String[]{ sObjectName + ".StandardPicklist", sObjectName + ".Stringified_Custom_Picklist_Type__c" };
        mojo.parsePicklistToStrings();
        Assert.assertTrue((((mojo.picklistToStrings) != null) && ((mojo.picklistToStrings.length) > 1)));
        final SObjectDescription accountDescription = new SObjectDescription();
        accountDescription.setName(GenerateMojoTest.ACCOUNT);
        accountDescription.setFields(new ArrayList());
        final SObjectField defaultPicklist = GenerateMojoTest.createField("Default_Picklist__c", GenerateMojoTest.PICKLIST);
        final SObjectField defaultMultipicklist = GenerateMojoTest.createField("Default_Multipicklist__c", GenerateMojoTest.MULTIPICKLIST);
        final SObjectField picklistToString = GenerateMojoTest.createField(mojo.picklistToStrings[0].substring(((mojo.picklistToStrings[0].indexOf('.')) + 1)), GenerateMojoTest.PICKLIST);
        final SObjectField multipicklistToString = GenerateMojoTest.createField(mojo.picklistToStrings[1].substring(((mojo.picklistToStrings[1].indexOf('.')) + 1)), GenerateMojoTest.MULTIPICKLIST);
        accountDescription.getFields().add(defaultPicklist);
        accountDescription.getFields().add(defaultMultipicklist);
        accountDescription.getFields().add(picklistToString);
        accountDescription.getFields().add(multipicklistToString);
        mojo.useStringsForPicklists = false;
        final GenerateMojo.GeneratorUtility utility = mojo.new GeneratorUtility();
        // when
        final String resultDefaultPicklistType = utility.getFieldType(accountDescription, defaultPicklist);
        final String resultDefaultMultipicklistType = utility.getFieldType(accountDescription, defaultMultipicklist);
        final String resultPicklistToStringType = utility.getFieldType(accountDescription, picklistToString);
        final String resultMultipicklistToStringType = utility.getFieldType(accountDescription, multipicklistToString);
        // then
        Assert.assertThat(resultDefaultPicklistType, CoreMatchers.endsWith(GenerateMojoTest.PROPER_DEFAULT_PICKLIST_TYPE_ENDING));
        Assert.assertThat(resultDefaultMultipicklistType, CoreMatchers.endsWith(GenerateMojoTest.PROPER_DEFAULT_MULTIPICKLIST_TYPE_ENDING));
        Assert.assertThat(resultPicklistToStringType, CoreMatchers.equalTo(GenerateMojoTest.PROPER_PICKLIST_TO_STRING_TYPE));
        Assert.assertThat(resultMultipicklistToStringType, CoreMatchers.equalTo(GenerateMojoTest.PROPER_MULTIPICKLIST_TO_STRING_TYPE));
    }

    @Test
    public void shouldReturnStringOrEnumTypesStringMode() {
        // given
        final String sObjectName = GenerateMojoTest.CONTACT;
        final GenerateMojo mojo = new GenerateMojo();
        mojo.picklistToEnums = new String[]{ sObjectName + ".Enum_Contact_Source_Information__c", sObjectName + ".Enum_Contract_Type__c" };
        mojo.parsePicklistToEnums();
        final SObjectDescription contactDescription = new SObjectDescription();
        contactDescription.setName(sObjectName);
        contactDescription.setFields(new ArrayList());
        final SObjectField stringPicklist = GenerateMojoTest.createField("Nonspecific_Picklist__c", GenerateMojoTest.PICKLIST);
        final SObjectField stringMultipicklist = GenerateMojoTest.createField("Nonspecific_Multipicklist__c", GenerateMojoTest.MULTIPICKLIST);
        final SObjectField picklistToEnum = GenerateMojoTest.createField(mojo.picklistToEnums[0].substring(((mojo.picklistToEnums[0].indexOf('.')) + 1)), GenerateMojoTest.PICKLIST);
        final SObjectField multipicklistToEnum = GenerateMojoTest.createField(mojo.picklistToEnums[1].substring(((mojo.picklistToEnums[1].indexOf('.')) + 1)), GenerateMojoTest.MULTIPICKLIST);
        contactDescription.getFields().add(stringPicklist);
        contactDescription.getFields().add(stringMultipicklist);
        contactDescription.getFields().add(picklistToEnum);
        contactDescription.getFields().add(multipicklistToEnum);
        mojo.useStringsForPicklists = true;
        final GenerateMojo.GeneratorUtility utility = mojo.new GeneratorUtility();
        // when
        final String resultStringPicklistType = utility.getFieldType(contactDescription, stringPicklist);
        final String resultStringMultipicklistType = utility.getFieldType(contactDescription, stringMultipicklist);
        final String resultPicklistToEnumType = utility.getFieldType(contactDescription, picklistToEnum);
        final String resultMultipicklistToEnumType = utility.getFieldType(contactDescription, multipicklistToEnum);
        // then
        Assert.assertThat(resultPicklistToEnumType, CoreMatchers.endsWith(GenerateMojoTest.PROPER_DEFAULT_PICKLIST_TYPE_ENDING));
        Assert.assertThat(resultMultipicklistToEnumType, CoreMatchers.endsWith(GenerateMojoTest.PROPER_DEFAULT_MULTIPICKLIST_TYPE_ENDING));
        Assert.assertThat(resultStringPicklistType, CoreMatchers.equalTo(GenerateMojoTest.PROPER_PICKLIST_TO_STRING_TYPE));
        Assert.assertThat(resultStringMultipicklistType, CoreMatchers.equalTo(GenerateMojoTest.PROPER_MULTIPICKLIST_TO_STRING_TYPE));
    }
}

