/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.store;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaElementDefinition;
import uk.gov.gchq.koryphe.ValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class ElementValidatorTest {
    @Test
    public void shouldReturnTrueWhenSchemaValidateWithValidElement() {
        // Given
        final Schema schema = Mockito.mock(Schema.class);
        final String group = TestGroups.EDGE;
        final Element elm = Mockito.mock(Element.class);
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        final ElementFilter filter = Mockito.mock(ElementFilter.class);
        final boolean includeIsA = true;
        final ElementValidator validator = new ElementValidator(schema, includeIsA);
        BDDMockito.given(elm.getGroup()).willReturn(group);
        BDDMockito.given(schema.getElement(group)).willReturn(elementDef);
        BDDMockito.given(elementDef.getValidator(includeIsA)).willReturn(filter);
        BDDMockito.given(filter.test(elm)).willReturn(true);
        // When
        final boolean isValid = validator.validate(elm);
        // Then
        Assert.assertTrue(isValid);
    }

    @Test
    public void shouldReturnTrueWhenSchemaValidateWithoutIsAWithValidElement() {
        // Given
        final Schema schema = Mockito.mock(Schema.class);
        final String group = TestGroups.EDGE;
        final Element elm = Mockito.mock(Element.class);
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        final ElementFilter filter = Mockito.mock(ElementFilter.class);
        final boolean includeIsA = false;
        final ElementValidator validator = new ElementValidator(schema, includeIsA);
        BDDMockito.given(elm.getGroup()).willReturn(group);
        BDDMockito.given(schema.getElement(group)).willReturn(elementDef);
        BDDMockito.given(elementDef.getValidator(includeIsA)).willReturn(filter);
        BDDMockito.given(filter.test(elm)).willReturn(true);
        // When
        final boolean isValid = validator.validate(elm);
        // Then
        Assert.assertTrue(isValid);
    }

    @Test
    public void shouldReturnValidValidationResultWhenSchemaValidateWithValidElement() {
        // Given
        final Schema schema = Mockito.mock(Schema.class);
        final String group = TestGroups.EDGE;
        final Element elm = Mockito.mock(Element.class);
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        final ElementFilter filter = Mockito.mock(ElementFilter.class);
        final boolean includeIsA = true;
        final ElementValidator validator = new ElementValidator(schema, includeIsA);
        BDDMockito.given(elm.getGroup()).willReturn(group);
        BDDMockito.given(schema.getElement(group)).willReturn(elementDef);
        BDDMockito.given(elementDef.getValidator(includeIsA)).willReturn(filter);
        BDDMockito.given(filter.testWithValidationResult(elm)).willReturn(new ValidationResult());
        // When
        final ValidationResult result = validator.validateWithValidationResult(elm);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldReturnFalseWhenSchemaValidateWithInvalidElement() {
        // Given
        final Schema schema = Mockito.mock(Schema.class);
        final String group = TestGroups.EDGE;
        final Element elm = Mockito.mock(Element.class);
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        final ElementFilter filter = Mockito.mock(ElementFilter.class);
        final boolean includeIsA = true;
        final ElementValidator validator = new ElementValidator(schema, includeIsA);
        BDDMockito.given(elm.getGroup()).willReturn(group);
        BDDMockito.given(schema.getElement(group)).willReturn(elementDef);
        BDDMockito.given(elementDef.getValidator(includeIsA)).willReturn(filter);
        BDDMockito.given(filter.test(elm)).willReturn(false);
        // When
        final boolean isValid = validator.validate(elm);
        // Then
        Assert.assertFalse(isValid);
    }

    @Test
    public void shouldReturnFailedValidationResultWhenSchemaValidateWithInvalidElement() {
        // Given
        final Schema schema = Mockito.mock(Schema.class);
        final String group = TestGroups.EDGE;
        final Element elm = Mockito.mock(Element.class);
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        final ElementFilter filter = Mockito.mock(ElementFilter.class);
        final boolean includeIsA = true;
        final ElementValidator validator = new ElementValidator(schema, includeIsA);
        BDDMockito.given(elm.getGroup()).willReturn(group);
        BDDMockito.given(schema.getElement(group)).willReturn(elementDef);
        BDDMockito.given(elementDef.getValidator(includeIsA)).willReturn(filter);
        BDDMockito.given(filter.testWithValidationResult(elm)).willReturn(new ValidationResult("some error"));
        // When
        final ValidationResult result = validator.validateWithValidationResult(elm);
        // Then
        Assert.assertFalse(result.isValid());
        Assert.assertTrue(result.getErrorString().contains("some error"));
    }

    @Test
    public void shouldReturnFalseWhenNoSchemaElementDefinition() {
        // Given
        final Schema schema = Mockito.mock(Schema.class);
        final String group = TestGroups.EDGE;
        final Element elm = Mockito.mock(Element.class);
        final boolean includeIsA = true;
        final ElementValidator validator = new ElementValidator(schema, includeIsA);
        BDDMockito.given(elm.getGroup()).willReturn(group);
        BDDMockito.given(schema.getElement(group)).willReturn(null);
        // When
        final boolean isValid = validator.validate(elm);
        // Then
        Assert.assertFalse(isValid);
    }

    @Test
    public void shouldReturnTrueWhenViewValidateWithValidElement() {
        // Given
        final View view = Mockito.mock(View.class);
        final String group = TestGroups.EDGE;
        final Element elm = Mockito.mock(Element.class);
        final ViewElementDefinition elementDef = Mockito.mock(ViewElementDefinition.class);
        final ElementFilter filter = Mockito.mock(ElementFilter.class);
        final ElementValidator validator = new ElementValidator(view);
        BDDMockito.given(elm.getGroup()).willReturn(group);
        BDDMockito.given(view.getElement(group)).willReturn(elementDef);
        BDDMockito.given(elementDef.getPreAggregationFilter()).willReturn(filter);
        BDDMockito.given(filter.test(elm)).willReturn(true);
        // When
        final boolean isValid = validator.validate(elm);
        // Then
        Assert.assertTrue(isValid);
    }

    @Test
    public void shouldReturnFalseWhenViewValidateWithInvalidElement() {
        // Given
        final View view = Mockito.mock(View.class);
        final String group = TestGroups.EDGE;
        final Element elm = Mockito.mock(Element.class);
        final ViewElementDefinition elementDef = Mockito.mock(ViewElementDefinition.class);
        final ElementFilter filter = Mockito.mock(ElementFilter.class);
        final ElementValidator validator = new ElementValidator(view);
        BDDMockito.given(elm.getGroup()).willReturn(group);
        BDDMockito.given(view.getElement(group)).willReturn(elementDef);
        BDDMockito.given(elementDef.getPreAggregationFilter()).willReturn(filter);
        BDDMockito.given(filter.test(elm)).willReturn(false);
        // When
        final boolean isValid = validator.validate(elm);
        // Then
        Assert.assertFalse(isValid);
    }

    @Test
    public void shouldReturnFalseWhenNoViewElementDefinition() {
        // Given
        final View view = Mockito.mock(View.class);
        final String group = TestGroups.EDGE;
        final Element elm = Mockito.mock(Element.class);
        final ElementValidator validator = new ElementValidator(view);
        BDDMockito.given(elm.getGroup()).willReturn(group);
        BDDMockito.given(view.getElement(group)).willReturn(null);
        // When
        final boolean isValid = validator.validate(elm);
        // Then
        Assert.assertFalse(isValid);
    }
}

