/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
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
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.util;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class XsdErrorTranslatorTest {
    private XsdErrorTranslator translator;

    @Test
    public void shouldOnlyRememberTheFirstValidationError() throws Exception {
        translator.error(new SAXParseException("cvc-attribute.3: The value 'abc!!!' of attribute 'name' on element 'environment' is not valid with respect to its type, 'nameType'", null));
        translator.error(new SAXParseException("cvc-elt.1: Cannot find the declaration of element 'element'", null));
        Assert.assertThat(translator.translate(), Matchers.is("\"abc!!!\" is invalid for Environment name"));
    }

    @Test
    public void shouldTranslateEvenIfSomeArgumentsAreEmpty() throws Exception {
        translator.error(new SAXParseException("cvc-attribute.3: The value '' of attribute 'name' on element 'environment' is not valid with respect to its type, 'nameType'", null));
        Assert.assertThat(translator.translate(), Matchers.is("\"\" is invalid for Environment name"));
    }

    @Test
    public void shouldHandleSingleQuotesInArguments() throws Exception {
        translator.error(new SAXParseException("cvc-attribute.3: The value 'Go's' of attribute 'name' on element 'environment' is not valid with respect to its type, 'nameType'", null));
        Assert.assertThat(translator.translate(), Matchers.is("\"Go\'s\" is invalid for Environment name"));
    }

    @Test
    public void shouldTranslateXsdErrorIfMappingDefined() throws Exception {
        translator.error(new SAXParseException("cvc-attribute.3: The value 'abc!!!' of attribute 'name' on element 'environment' is not valid with respect to its type, 'nameType'", null));
        Assert.assertThat(translator.translate(), Matchers.is("\"abc!!!\" is invalid for Environment name"));
    }

    @Test
    public void shouldReturnOriginalXsdErrorIfMappingNotDefined() throws Exception {
        translator.error(new SAXParseException("cvc-elt.1: Cannot find the declaration of element 'element'", null));
        Assert.assertThat(translator.translate(), Matchers.is("Cannot find the declaration of element 'element'"));
    }

    @Test
    public void shouldReturnOriginalErrorIfErrorMessageDoesNotContainCVCPattern() throws Exception {
        translator.error(new SAXParseException("Duplicate unique value [coverage] declared for identity constraint of element \"properties\".", null));
        Assert.assertThat(translator.translate(), Matchers.is("Duplicate unique value [coverage] declared for identity constraint of element \"properties\"."));
    }

    @Test
    public void shouldReturnOriginalErrorIfErrorMessageContainsIncompleteCVCPattern() throws SAXException {
        translator.error(new SAXParseException("cvc : starts with cvc and has colon", null));
        Assert.assertThat(translator.translate(), Matchers.is("cvc : starts with cvc and has colon"));
    }

    @Test
    public void shouldHumanizeTheNameTypeInTheErrorMessage() throws SAXException {
        translator.error(new SAXParseException("cvc-pattern-valid: Value 'abc!!' is not facet-valid with respect to pattern '[Some-Pattern]' for type 'environmentName'.", null));
        Assert.assertThat(translator.translate(), Matchers.is("Environment name is invalid. \"abc!!\" should conform to the pattern - [Some-Pattern]"));
    }

    @Test
    public void shouldHumanizeTheErrorMessageForSiteUrl() throws SAXException {
        translator.error(new SAXParseException("cvc-pattern-valid: Value 'http://10.4.5.6:8253' is not facet-valid with respect to pattern 'https?://.+' for type '#AnonType_siteUrlserverAttributeGroup'.", null));
        Assert.assertThat(translator.translate(), Matchers.is("siteUrl \"http://10.4.5.6:8253\" is invalid. It must start with \u2018http://\u2019 or \u2018https://\u2019"));
    }

    @Test
    public void shouldRemoveTypeInTheErrorMessage() throws SAXException {
        translator.error(new SAXParseException("cvc-pattern-valid: Value 'abc!!' is not facet-valid with respect to pattern '[Some-Pattern]' for type 'environmentNameType'.", null));
        Assert.assertThat(translator.translate(), Matchers.is("Environment name is invalid. \"abc!!\" should conform to the pattern - [Some-Pattern]"));
    }

    @Test
    public void shouldHumanizeTheErrorMessageForSecureSiteUrl() throws SAXException {
        translator.error(new SAXParseException("cvc-pattern-valid: Value 'http://10.4.5.6:8253' is not facet-valid with respect to pattern 'https://.+' for type '#AnonType_secureSiteUrlserverAttributeGroup'.", null));
        Assert.assertThat(translator.translate(), Matchers.is("secureSiteUrl \"http://10.4.5.6:8253\" is invalid. It must be a secure URL (should start with \u2018https://\u2019)"));
    }

    @Test
    public void shouldHumanizeAndCapitalizeRequiredAttributeErrors() throws SAXException {
        translator.error(new SAXParseException("cvc-complex-type.4: Attribute 'searchBase' must appear on element 'Ldap'.", null));
        Assert.assertThat(translator.translate(), Matchers.is("\"Search base\" is required for Ldap"));
    }

    @Test
    public void shouldDealWithPatternValidForAnonymousErrors() throws SAXException {
        translator.error(new SAXParseException("cvc-pattern-valid: Value \'Ethan\'s Work (TA)\' is not facet-valid with respect to pattern \'[^\\s]+\' for type \'#AnonType_projectIdentifiermingleType\'.", null));
        Assert.assertThat(translator.translate(), Matchers.is("Project identifier in Mingle is invalid. \"Ethan\'s Work (TA)\" should conform to the pattern - [^\\s]+"));
    }

    @Test
    public void shouldHumanizeErrorsDuringCommandSnippetValidation() throws SAXException {
        translator.error(new SAXParseException("cvc-minLength-valid: Value '  ' with length = '0' is not facet-valid with respect to minLength '1' for type '#AnonType_commandexec'.", null));
        Assert.assertThat(translator.translate(), Matchers.is("Command attribute cannot be blank in a command snippet."));
    }

    @Test
    public void shouldHumanizeErrorsDuringCommandSnippetValidationWhenInvalidTagFound() throws SAXException {
        translator.error(new SAXParseException("cvc-elt.1: Cannot find the declaration of element 'invalidTag'.", null));
        Assert.assertThat(translator.translate(), Matchers.is("Invalid XML tag \"invalidTag\" found."));
    }
}

