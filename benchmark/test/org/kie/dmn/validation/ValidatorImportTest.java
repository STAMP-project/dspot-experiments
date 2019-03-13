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


import Validation.VALIDATE_COMPILATION;
import Validation.VALIDATE_MODEL;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.model.api.DMNElementReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ValidatorImportTest extends AbstractValidatorTest {
    public static final Logger LOG = LoggerFactory.getLogger(ValidatorImportTest.class);

    @Test
    public void testBaseModel_OK__ReaderInput() throws IOException {
        try (final Reader reader0 = getReader("import/Base-model.dmn");final Reader reader1 = getReader("import/Import-base-model.dmn")) {
            final List<DMNMessage> messages = // VALIDATE_SCHEMA, disabled, due to QName use not compliant.
            AbstractValidatorTest.validator.validateUsing(VALIDATE_MODEL, VALIDATE_COMPILATION).theseModels(reader0, reader1);
            Assert.assertThat(ValidatorUtil.formatMessages(messages), messages.size(), CoreMatchers.is(0));
        }
    }

    @Test
    public void testBaseModel_OK__FileInput() throws IOException {
        final List<DMNMessage> messages = // switch order for DROOLS-2936
        // VALIDATE_SCHEMA, disabled, due to QName use not compliant.
        AbstractValidatorTest.validator.validateUsing(VALIDATE_MODEL, VALIDATE_COMPILATION).theseModels(getFile("import/Import-base-model.dmn"), getFile("import/Base-model.dmn"));
        Assert.assertThat(ValidatorUtil.formatMessages(messages), messages.size(), CoreMatchers.is(0));
    }

    @Test
    public void testBaseModel_ImportModelName__FileInput() throws IOException {
        final List<DMNMessage> messages = // DROOLS-2938
        AbstractValidatorTest.validator.validateUsing(VALIDATE_MODEL).theseModels(getFile("import/Import-base-model-modelnameattribute.dmn"), getFile("import/Base-model.dmn"));
        Assert.assertThat(ValidatorUtil.formatMessages(messages), messages.size(), CoreMatchers.is(0));
    }

    @Test
    public void testBaseModel_OK__DefinitionsInput() throws IOException {
        final List<DMNMessage> messages = // VALIDATE_SCHEMA, disabled, due to QName use not compliant.
        AbstractValidatorTest.validator.validateUsing(VALIDATE_MODEL, VALIDATE_COMPILATION).theseModels(getDefinitions(Arrays.asList("import/Base-model.dmn", "import/Import-base-model.dmn"), "http://www.trisotech.com/definitions/_70df1ad5-2a33-4ede-b8b2-869988ac1d30", "Base model"), getDefinitions(Arrays.asList("import/Base-model.dmn", "import/Import-base-model.dmn"), "http://www.trisotech.com/dmn/definitions/_b33fa7d9-f501-423b-afa8-15ded7e7f493", "Import base model"));
        Assert.assertThat(ValidatorUtil.formatMessages(messages), messages.size(), CoreMatchers.is(0));
    }

    @Test
    public void testWrongImportBaseModel__ReaderInput() throws IOException {
        try (final Reader reader0 = getReader("import/Base-model.dmn");final Reader reader1 = getReader("import/Wrong-Import-base-model.dmn")) {
            final List<DMNMessage> messages = AbstractValidatorTest.validator.validateUsing(VALIDATE_MODEL).theseModels(reader0, reader1);
            Assert.assertThat(ValidatorUtil.formatMessages(messages), messages.size(), CoreMatchers.is(1));
            Assert.assertTrue(messages.stream().anyMatch(( p) -> ((p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)) && ((p.getSourceReference()) instanceof DMNElementReference)) && (((DMNElementReference) (p.getSourceReference())).getHref().equals("http://www.trisotech.com/definitions/_70df1ad5-2a33-4ede-b8b2-869988ac1d30#_1d52934e-aa4e-47c9-a011-fc989d795664"))));
        }
    }

    @Test
    public void testWrongImportBaseModel__FileInput() throws IOException {
        final List<DMNMessage> messages = AbstractValidatorTest.validator.validateUsing(VALIDATE_MODEL).theseModels(getFile("import/Base-model.dmn"), getFile("import/Wrong-Import-base-model.dmn"));
        Assert.assertThat(ValidatorUtil.formatMessages(messages), messages.size(), CoreMatchers.is(1));
        Assert.assertTrue(messages.stream().anyMatch(( p) -> ((p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)) && ((p.getSourceReference()) instanceof DMNElementReference)) && (((DMNElementReference) (p.getSourceReference())).getHref().equals("http://www.trisotech.com/definitions/_70df1ad5-2a33-4ede-b8b2-869988ac1d30#_1d52934e-aa4e-47c9-a011-fc989d795664"))));
    }

    @Test
    public void testWrongImportBaseModel__DefinitionsInput() throws IOException {
        final List<DMNMessage> messages = AbstractValidatorTest.validator.validateUsing(VALIDATE_MODEL).theseModels(getDefinitions(Arrays.asList("import/Base-model.dmn", "import/Wrong-Import-base-model.dmn"), "http://www.trisotech.com/definitions/_70df1ad5-2a33-4ede-b8b2-869988ac1d30", "Base model"), getDefinitions(Arrays.asList("import/Base-model.dmn", "import/Wrong-Import-base-model.dmn"), "http://www.trisotech.com/dmn/definitions/_719a2325-5cac-47ea-8a99-665c01d570a5", "Wrong Import base model"));
        Assert.assertTrue(messages.stream().anyMatch(( p) -> ((p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)) && ((p.getSourceReference()) instanceof DMNElementReference)) && (((DMNElementReference) (p.getSourceReference())).getHref().equals("http://www.trisotech.com/definitions/_70df1ad5-2a33-4ede-b8b2-869988ac1d30#_1d52934e-aa4e-47c9-a011-fc989d795664"))));
    }

    @Test
    public void testOnlyImportBaseModel_ReaderInput() throws IOException {
        try (final Reader reader1 = getReader("import/Only-Import-base-model.dmn")) {
            final List<DMNMessage> messages = AbstractValidatorTest.validator.validate(reader1, VALIDATE_MODEL);
            Assert.assertTrue(messages.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.IMPORT_NOT_FOUND)));
        }
    }

    @Test
    public void testOnlyImportBaseModel_FileInput() throws IOException {
        final List<DMNMessage> messages = AbstractValidatorTest.validator.validate(getFile("import/Only-Import-base-model.dmn"), VALIDATE_MODEL);
        Assert.assertTrue(messages.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.IMPORT_NOT_FOUND)));
    }

    @Test
    public void testOnlyImportBaseModel_DefinitionsInput() throws IOException {
        final List<DMNMessage> messages = AbstractValidatorTest.validator.validate(getDefinitions("import/Only-Import-base-model.dmn", "http://www.trisotech.com/dmn/definitions/_a9bfa4de-cf5c-4b2f-9011-ab576e00b162", "Only Import base model"), VALIDATE_MODEL);
        Assert.assertTrue(messages.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.IMPORT_NOT_FOUND)));
    }
}

