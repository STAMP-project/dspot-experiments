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


import DMNMessageType.MISSING_EXPRESSION;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;


public class ValidatorDecisionTest extends AbstractValidatorTest {
    @Test
    public void testDECISION_MISSING_EXPR_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_MISSING_EXPR.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
            Assert.assertThat(validate.get(0).toString(), validate.get(0).getMessageType(), CoreMatchers.is(MISSING_EXPRESSION));
        }
    }

    @Test
    public void testDECISION_MISSING_EXPR_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("decision/DECISION_MISSING_EXPR.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertThat(validate.get(0).toString(), validate.get(0).getMessageType(), CoreMatchers.is(MISSING_EXPRESSION));
    }

    @Test
    public void testDECISION_MISSING_EXPR_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("decision/DECISION_MISSING_EXPR.dmn", "https://github.com/kiegroup/kie-dmn", "DECISION_MISSING_EXPR"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertThat(validate.get(0).toString(), validate.get(0).getMessageType(), CoreMatchers.is(MISSING_EXPRESSION));
    }

    @Test
    public void testDECISION_MISSING_VAR_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_MISSING_VAR.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE)));
        }
    }

    @Test
    public void testDECISION_MISSING_VAR_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("decision/DECISION_MISSING_VAR.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE)));
    }

    @Test
    public void testDECISION_MISSING_VAR_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("decision/DECISION_MISSING_VAR.dmn", "https://github.com/kiegroup/kie-dmn", "DECISION_MISSING_VAR"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE)));
    }

    @Test
    public void testDECISION_MISSING_VARbis_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_MISSING_VARbis.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE)));
        }
    }

    @Test
    public void testDECISION_MISSING_VARbis_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("decision/DECISION_MISSING_VARbis.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE)));
    }

    @Test
    public void testDECISION_MISSING_VARbis_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("decision/DECISION_MISSING_VARbis.dmn", "https://github.com/kiegroup/kie-dmn", "DECISION_MISSING_VARbis"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE)));
    }

    @Test
    public void testDECISION_MISMATCH_VAR_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_MISMATCH_VAR.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH)));
        }
    }

    @Test
    public void testDECISION_MISMATCH_VAR_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("decision/DECISION_MISMATCH_VAR.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH)));
    }

    @Test
    public void testDECISION_MISMATCH_VAR_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("decision/DECISION_MISMATCH_VAR.dmn", "https://github.com/kiegroup/kie-dmn", "DECISION_MISSING_VAR"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH)));
    }

    @Test
    public void testDECISION_MULTIPLE_EXPRESSIONS_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_MULTIPLE_EXPRESSIONS.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.FAILED_XML_VALIDATION)));
        }
    }

    @Test
    public void testDECISION_MULTIPLE_EXPRESSIONS_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("decision/DECISION_MULTIPLE_EXPRESSIONS.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.FAILED_XML_VALIDATION)));
    }

    @Test
    public void testDECISION_MULTIPLE_EXPRESSIONS_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("decision/DECISION_MULTIPLE_EXPRESSIONS.dmn", "https://github.com/kiegroup/kie-dmn", "DECISION_MULTIPLE_EXPRESSIONS"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
    }

    @Test
    public void testDECISION_PERF_INDICATOR_WRONG_TYPE_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_PERF_INDICATOR_WRONG_TYPE.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
        }
    }

    @Test
    public void testDECISION_PERF_INDICATOR_WRONG_TYPE_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("decision/DECISION_PERF_INDICATOR_WRONG_TYPE.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testDECISION_PERF_INDICATOR_WRONG_TYPE_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("decision/DECISION_PERF_INDICATOR_WRONG_TYPE.dmn", "https://github.com/kiegroup/kie-dmn", "DECISION_PERF_INDICATOR_WRONG_TYPE"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testDECISION_DECISION_MAKER_WRONG_TYPE_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_DECISION_MAKER_WRONG_TYPE.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
        }
    }

    @Test
    public void testDECISION_DECISION_MAKER_WRONG_TYPE_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("decision/DECISION_DECISION_MAKER_WRONG_TYPE.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testDECISION_DECISION_MAKER_WRONG_TYPE_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("decision/DECISION_DECISION_MAKER_WRONG_TYPE.dmn", "https://github.com/kiegroup/kie-dmn", "DECISION_DECISION_MAKER_WRONG_TYPE"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testDECISION_DECISION_OWNER_WRONG_TYPE_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_DECISION_OWNER_WRONG_TYPE.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
        }
    }

    @Test
    public void testDECISION_DECISION_OWNER_WRONG_TYPE_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("decision/DECISION_DECISION_OWNER_WRONG_TYPE.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testDECISION_DECISION_OWNER_WRONG_TYPE_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("decision/DECISION_DECISION_OWNER_WRONG_TYPE.dmn", "https://github.com/kiegroup/kie-dmn", "DECISION_DECISION_MAKER_WRONG_TYPE"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testDECISION_CYCLIC_DEPENDENCY_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_CYCLIC_DEPENDENCY.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
        }
    }

    @Test
    public void testDECISION_CYCLIC_DEPENDENCY_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("decision/DECISION_CYCLIC_DEPENDENCY.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testDECISION_CYCLIC_DEPENDENCY_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("decision/DECISION_CYCLIC_DEPENDENCY.dmn", "https://github.com/kiegroup/kie-dmn", "DECISION_CYCLIC_DEPENDENCY"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testDECISION_CYCLIC_DEPENDENCY_SELF_REFERENCE_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_CYCLIC_DEPENDENCY_SELF_REFERENCE.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
            Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
        }
    }

    @Test
    public void testDECISION_CYCLIC_DEPENDENCY_SELF_REFERENCE_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("decision/DECISION_CYCLIC_DEPENDENCY_SELF_REFERENCE.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testDECISION_CYCLIC_DEPENDENCY_SELF_REFERENCE_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("decision/DECISION_CYCLIC_DEPENDENCY_SELF_REFERENCE.dmn", "https://github.com/kiegroup/kie-dmn", "DECISION_CYCLIC_DEPENDENCY_SELF_REFERENCE"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testDECISION_DEADLY_DIAMOND_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_DEADLY_DIAMOND.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
        }
    }

    @Test
    public void testDECISION_DEADLY_DIAMOND_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("decision/DECISION_DEADLY_DIAMOND.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
    }

    @Test
    public void testDECISION_DEADLY_DIAMOND_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("decision/DECISION_DEADLY_DIAMOND.dmn", "https://github.com/kiegroup/kie-dmn", "DECISION_DEADLY_DIAMOND"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
    }

    @Test
    public void testDECISION_DEADLY_KITE_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_DEADLY_KITE.dmn")) {
            final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(reader, Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
        }
    }

    @Test
    public void testDECISION_DEADLY_KITE_FileInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getFile("decision/DECISION_DEADLY_KITE.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
    }

    @Test
    public void testDECISION_DEADLY_KITE_DefinitionsInput() {
        final List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getDefinitions("decision/DECISION_DEADLY_KITE.dmn", "https://github.com/kiegroup/kie-dmn", "DECISION_DEADLY_KITE"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
    }
}

