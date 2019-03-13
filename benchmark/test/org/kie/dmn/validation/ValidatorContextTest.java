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
import org.kie.dmn.model.api.ContextEntry;


public class ValidatorContextTest extends AbstractValidatorTest {
    @Test
    public void testCONTEXT_MISSING_EXPR_ReaderInput() throws IOException {
        try (final Reader reader = getReader("context/CONTEXT_MISSING_EXPR.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.FAILED_XML_VALIDATION)));// this is schema validation

            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
        }
    }

    @Test
    public void testCONTEXT_MISSING_EXPR_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("context/CONTEXT_MISSING_EXPR.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.FAILED_XML_VALIDATION)));// this is schema validation

        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
    }

    @Test
    public void testCONTEXT_MISSING_EXPR_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("context/CONTEXT_MISSING_EXPR.dmn", "https://github.com/kiegroup/kie-dmn", "CONTEXT_MISSING_EXPR"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
    }

    @Test
    public void testCONTEXT_MISSING_ENTRIES_ReaderInput() throws IOException {
        try (final Reader reader = getReader("context/CONTEXT_MISSING_ENTRIES.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
        }
    }

    @Test
    public void testCONTEXT_MISSING_ENTRIES_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("context/CONTEXT_MISSING_ENTRIES.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
    }

    @Test
    public void testCONTEXT_MISSING_ENTRIES_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("context/CONTEXT_MISSING_ENTRIES.dmn", "https://github.com/kiegroup/kie-dmn", "CONTEXT_MISSING_EXPR"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
    }

    @Test
    public void testCONTEXT_ENTRY_MISSING_VARIABLE_ReaderInput() throws IOException {
        try (final Reader reader = getReader("context/CONTEXT_ENTRY_MISSING_VARIABLE.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE)));
            // check that it reports and error for the second context entry, but not for the last one
            final ContextEntry ce = ((ContextEntry) (validate.get(0).getSourceReference()));
            Assert.assertThat(getContextEntry().indexOf(ce), CoreMatchers.is(1));
        }
    }

    @Test
    public void testCONTEXT_ENTRY_MISSING_VARIABLE_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("context/CONTEXT_ENTRY_MISSING_VARIABLE.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE)));
        // check that it reports and error for the second context entry, but not for the last one
        final ContextEntry ce = ((ContextEntry) (validate.get(0).getSourceReference()));
        Assert.assertThat(getContextEntry().indexOf(ce), CoreMatchers.is(1));
    }

    @Test
    public void testCONTEXT_ENTRY_MISSING_VARIABLE_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("context/CONTEXT_ENTRY_MISSING_VARIABLE.dmn", "https://github.com/kiegroup/kie-dmn", "CONTEXT_MISSING_EXPR"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE)));
        // check that it reports and error for the second context entry, but not for the last one
        final ContextEntry ce = ((ContextEntry) (validate.get(0).getSourceReference()));
        Assert.assertThat(getContextEntry().indexOf(ce), CoreMatchers.is(1));
    }

    @Test
    public void testCONTEXT_DUP_ENTRY_ReaderInput() throws IOException {
        try (final Reader reader = getReader("context/CONTEXT_DUP_ENTRY.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.DUPLICATE_NAME)));
        }
    }

    @Test
    public void testCONTEXT_DUP_ENTRY_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("context/CONTEXT_DUP_ENTRY.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.DUPLICATE_NAME)));
    }

    @Test
    public void testCONTEXT_DUP_ENTRY_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("context/CONTEXT_DUP_ENTRY.dmn", "https://github.com/kiegroup/kie-dmn", "CONTEXT_DUP_ENTRY"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.DUPLICATE_NAME)));
    }

    @Test
    public void testCONTEXT_ENTRY_NOTYPEREF_ReaderInput() throws IOException {
        try (final Reader reader = getReader("context/CONTEXT_ENTRY_NOTYPEREF.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF)));
        }
    }

    @Test
    public void testCONTEXT_ENTRY_NOTYPEREF_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("context/CONTEXT_ENTRY_NOTYPEREF.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF)));
    }

    @Test
    public void testCONTEXT_ENTRY_NOTYPEREF_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("context/CONTEXT_ENTRY_NOTYPEREF.dmn", "https://github.com/kiegroup/kie-dmn", "CONTEXT_ENTRY_NOTYPEREF"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF)));
    }
}

