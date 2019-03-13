/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.validation;


import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;


public class ValidatorTypeRefTest extends AbstractValidatorTest {
    @Test
    public void testTYPEREF_NO_FEEL_TYPE_ReaderInput() throws IOException {
        try (final Reader reader = getReader("typeref/TYPEREF_NO_FEEL_TYPE.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.TYPE_REF_NOT_FOUND)));
        }
    }

    @Test
    public void testTYPEREF_NO_FEEL_TYPE_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("typeref/TYPEREF_NO_FEEL_TYPE.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.TYPE_REF_NOT_FOUND)));
    }

    @Test
    public void testTYPEREF_NO_FEEL_TYPE_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("typeref/TYPEREF_NO_FEEL_TYPE.dmn", "https://github.com/kiegroup/kie-dmn", "TYPEREF_NO_FEEL_TYPE"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.TYPE_REF_NOT_FOUND)));
    }

    @Test
    public void testTYPEREF_NO_NS_ReaderInput() throws IOException {
        try (final Reader reader = getReader("typeref/TYPEREF_NO_NS.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.FAILED_XML_VALIDATION)));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND)));
        }
    }

    @Test
    public void testTYPEREF_NO_NS_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("typeref/TYPEREF_NO_NS.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.FAILED_XML_VALIDATION)));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND)));
    }

    @Test
    public void testTYPEREF_NO_NS_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("typeref/TYPEREF_NO_NS.dmn", "https://github.com/kiegroup/kie-dmn", "TYPEREF_NO_NS"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND)));
    }

    @Test
    public void testTYPEREF_NOT_FEEL_NOT_DEF_ReaderInput() throws IOException {
        try (final Reader reader = getReader("typeref/TYPEREF_NOT_FEEL_NOT_DEF.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.INVALID_NAME)));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND)));
        }
    }

    @Test
    public void testTYPEREF_NOT_FEEL_NOT_DEF_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("typeref/TYPEREF_NOT_FEEL_NOT_DEF.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.INVALID_NAME)));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND)));
    }

    @Test
    public void testTYPEREF_NOT_FEEL_NOT_DEF_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("typeref/TYPEREF_NOT_FEEL_NOT_DEF.dmn", "https://github.com/kiegroup/kie-dmn", "TYPEREF_NOT_FEEL_NOT_DEF"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.INVALID_NAME)));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND)));
    }

    @Test
    public void testTYPEREF_NOT_FEEL_NOT_DEF_valid_ResourceInput() throws IOException {
        // DROOLS-1433
        // the assumption is that the following document TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn should NOT contain any DMNMessageTypeId.TYPEREF_NOT_FEEL_NOT_DEF at all
        // the test also highlight typically in a DMN model many nodes would not define a typeRef, resulting in a large number of false negative
        try (final Reader reader = getReader("typeref/TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
        }
    }

    @Test
    public void testTYPEREF_NOT_FEEL_NOT_DEF_valid_FileInput() {
        // DROOLS-1433
        // the assumption is that the following document TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn should NOT contain any DMNMessageTypeId.TYPEREF_NOT_FEEL_NOT_DEF at all
        // the test also highlight typically in a DMN model many nodes would not define a typeRef, resulting in a large number of false negative
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("typeref/TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
    }

    @Test
    public void testTYPEREF_NOT_FEEL_NOT_DEF_valid_DefinitionsInput() {
        // DROOLS-1433
        // the assumption is that the following document TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn should NOT contain any DMNMessageTypeId.TYPEREF_NOT_FEEL_NOT_DEF at all
        // the test also highlight typically in a DMN model many nodes would not define a typeRef, resulting in a large number of false negative
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("typeref/TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn", "https://github.com/kiegroup/kie-dmn", "TYPEREF_NOT_FEEL_NOT_DEF_valid"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
    }

    @Test
    public void testBKM_WITH_NO_TYPEREF_IS_OK_DefinitionsInput() {
        // DROOLS-2631
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("typeref/BKM_WITH_NO_TYPEREF_IS_OK.dmn", "http://www.trisotech.com/dmn/definitions/_7e8d7561-657a-4729-b2a9-5a6279df6d5d", "Drawing 1"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
    }
}

