package org.mockserver.validator.jsonschema;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.slf4j.Logger;


/**
 *
 *
 * @author jamesdbloom
 */
public class JsonSchemaValidatorTest {
    public static final String JSON_SCHEMA = ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "    \"type\": \"object\",") + (NEW_LINE)) + "    \"properties\": {") + (NEW_LINE)) + "        \"enumField\": {") + (NEW_LINE)) + "            \"enum\": [ \"one\", \"two\" ]") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        \"arrayField\": {") + (NEW_LINE)) + "            \"type\": \"array\",") + (NEW_LINE)) + "            \"minItems\": 1,") + (NEW_LINE)) + "            \"items\": {") + (NEW_LINE)) + "                \"type\": \"string\"") + (NEW_LINE)) + "            },") + (NEW_LINE)) + "            \"uniqueItems\": true") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        \"stringField\": {") + (NEW_LINE)) + "            \"type\": \"string\",") + (NEW_LINE)) + "            \"minLength\": 5,") + (NEW_LINE)) + "            \"maxLength\": 6") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        \"booleanField\": {") + (NEW_LINE)) + "            \"type\": \"boolean\"") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        \"objectField\": {") + (NEW_LINE)) + "            \"type\": \"object\",") + (NEW_LINE)) + "            \"properties\": {") + (NEW_LINE)) + "                \"stringField\": {") + (NEW_LINE)) + "                    \"type\": \"string\",") + (NEW_LINE)) + "                    \"minLength\": 1,") + (NEW_LINE)) + "                    \"maxLength\": 3") + (NEW_LINE)) + "                }") + (NEW_LINE)) + "            },") + (NEW_LINE)) + "            \"required\": [ \"stringField\" ]") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"additionalProperties\" : false,") + (NEW_LINE)) + "    \"required\": [ \"enumField\", \"arrayField\" ]") + (NEW_LINE)) + "}";

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Mock
    protected Logger logger;

    @Test
    public void shouldMatchJson() {
        Assert.assertThat(isValid("{arrayField: [ \"one\" ], enumField: \"one\"}"), Is.is(""));
    }

    @Test
    public void shouldHandleJsonMissingRequiredFields() {
        // then
        Assert.assertThat(isValid("{}"), Is.is((("1 error:" + (NEW_LINE)) + " - object has missing required properties ([\"arrayField\",\"enumField\"])")));
    }

    @Test
    public void shouldHandleJsonTooFewItems() {
        // then
        Assert.assertThat(isValid("{arrayField: [ ],         enumField: \\\"one\\\"}"), Is.is(("JsonParseException - Unexpected character (\'\\\' (code 92)): expected a valid value (number, String, array, object, \'true\', \'false\' or \'null\')\n" + " at [Source: (String)\"{arrayField: [ ],         enumField: \\\"one\\\"}\"; line: 1, column: 39]")));
    }

    @Test
    public void shouldHandleJsonTooLongString() {
        Assert.assertThat(isValid("{arrayField: [ \\\"one\\\" ], enumField: \\\"one\\\", stringField: \\\"1234567\\\"}"), Is.is(("JsonParseException - Unexpected character (\'\\\' (code 92)): expected a valid value (number, String, array, object, \'true\', \'false\' or \'null\')\n" + " at [Source: (String)\"{arrayField: [ \\\"one\\\" ], enumField: \\\"one\\\", stringField: \\\"1234567\\\"}\"; line: 1, column: 17]")));
    }

    @Test
    public void shouldHandleJsonIncorrectEnum() {
        Assert.assertThat(isValid("{arrayField: [ \\\"one\\\" ], enumField: \\\"four\\\"}"), Is.is(("JsonParseException - Unexpected character (\'\\\' (code 92)): expected a valid value (number, String, array, object, \'true\', \'false\' or \'null\')\n" + " at [Source: (String)\"{arrayField: [ \\\"one\\\" ], enumField: \\\"four\\\"}\"; line: 1, column: 17]")));
    }

    @Test
    public void shouldHandleJsonExtraField() {
        Assert.assertThat(isValid("{arrayField: [ \\\"one\\\" ], enumField: \\\"one\\\", extra: \\\"field\\\"}"), Is.is(("JsonParseException - Unexpected character (\'\\\' (code 92)): expected a valid value (number, String, array, object, \'true\', \'false\' or \'null\')\n" + " at [Source: (String)\"{arrayField: [ \\\"one\\\" ], enumField: \\\"one\\\", extra: \\\"field\\\"}\"; line: 1, column: 17]")));
    }

    @Test
    public void shouldHandleJsonIncorrectSubField() {
        Assert.assertThat(isValid("{arrayField: [ \\\"one\\\" ], enumField: \\\"one\\\", objectField: {stringField: \\\"1234\\\"} }"), Is.is(("JsonParseException - Unexpected character (\'\\\' (code 92)): expected a valid value (number, String, array, object, \'true\', \'false\' or \'null\')\n" + " at [Source: (String)\"{arrayField: [ \\\"one\\\" ], enumField: \\\"one\\\", objectField: {stringField: \\\"1234\\\"} }\"; line: 1, column: 17]")));
    }

    @Test
    public void shouldHandleJsonMissingSubField() {
        Assert.assertThat(isValid("{arrayField: [ \\\"one\\\" ], enumField: \\\"one\\\", objectField: { } }"), Is.is(("JsonParseException - Unexpected character (\'\\\' (code 92)): expected a valid value (number, String, array, object, \'true\', \'false\' or \'null\')\n" + " at [Source: (String)\"{arrayField: [ \\\"one\\\" ], enumField: \\\"one\\\", objectField: { } }\"; line: 1, column: 17]")));
    }

    @Test
    public void shouldHandleJsonMultipleErrors() {
        Assert.assertThat(isValid("{arrayField: [ ],  stringField: \\\"1234\\\"}"), Is.is(("JsonParseException - Unexpected character (\'\\\' (code 92)): expected a valid value (number, String, array, object, \'true\', \'false\' or \'null\')\n" + " at [Source: (String)\"{arrayField: [ ],  stringField: \\\"1234\\\"}\"; line: 1, column: 34]")));
    }

    @Test
    public void shouldHandleIllegalJson() {
        // then
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Schema must either be a path reference to a *.json file or a json string");
        // given
        isValid("illegal_json");
    }

    @Test
    public void shouldHandleNullExpectation() {
        // then
        exception.expect(NullPointerException.class);
        // given
        isValid("some_value");
    }

    @Test
    public void shouldHandleEmptyExpectation() {
        // then
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Schema must either be a path reference to a *.json file or a json string");
        // given
        isValid("some_value");
    }

    @Test
    public void shouldHandleNullTest() {
        // given
        Assert.assertThat(new JsonSchemaValidator(new org.mockserver.logging.MockServerLogger(), JsonSchemaValidatorTest.JSON_SCHEMA).isValid(null), Is.is(""));
    }

    @Test
    public void shouldHandleEmptyTest() {
        // given
        Assert.assertThat(isValid(""), Is.is(""));
    }
}

