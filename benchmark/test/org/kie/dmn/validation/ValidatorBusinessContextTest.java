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


public class ValidatorBusinessContextTest extends AbstractValidatorTest {
    @Test
    public void testORG_UNIT_DECISION_MADE_WRONG_TYPE_ReaderInput() throws IOException {
        try (final Reader reader = getReader("businesscontext/ORG_UNIT_DECISION_MADE_WRONG_TYPE.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
        }
    }

    @Test
    public void testORG_UNIT_DECISION_MADE_WRONG_TYPE_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("businesscontext/ORG_UNIT_DECISION_MADE_WRONG_TYPE.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testORG_UNIT_DECISION_MADE_WRONG_TYPE_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("businesscontext/ORG_UNIT_DECISION_MADE_WRONG_TYPE.dmn", "https://github.com/kiegroup/kie-dmn", "ORG_UNIT_DECISION_MADE_WRONG_TYPE"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testORG_UNIT_DECISION_OWNED_WRONG_TYPE_ReaderInput() throws IOException {
        try (final Reader reader = getReader("businesscontext/ORG_UNIT_DECISION_OWNED_WRONG_TYPE.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
        }
    }

    @Test
    public void testORG_UNIT_DECISION_OWNED_WRONG_TYPE_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("businesscontext/ORG_UNIT_DECISION_OWNED_WRONG_TYPE.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testORG_UNIT_DECISION_OWNED_WRONG_TYPE_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("businesscontext/ORG_UNIT_DECISION_OWNED_WRONG_TYPE.dmn", "https://github.com/kiegroup/kie-dmn", "ORG_UNIT_DECISION_OWNED_WRONG_TYPE"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testPERF_INDICATOR_IMP_DECISION_WRONG_TYPE_ReaderInput() throws IOException {
        try (final Reader reader = getReader("businesscontext/PERF_INDICATOR_IMP_DECISION_WRONG_TYPE.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
        }
    }

    @Test
    public void testPERF_INDICATOR_IMP_DECISION_WRONG_TYPE_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("businesscontext/PERF_INDICATOR_IMP_DECISION_WRONG_TYPE.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testPERF_INDICATOR_IMP_DECISION_WRONG_TYPE_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("businesscontext/PERF_INDICATOR_IMP_DECISION_WRONG_TYPE.dmn", "https://github.com/kiegroup/kie-dmn", "PERF_INDICATOR_IMP_DECISION_WRONG_TYPE"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }
}

