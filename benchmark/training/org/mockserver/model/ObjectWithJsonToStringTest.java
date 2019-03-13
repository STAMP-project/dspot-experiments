package org.mockserver.model;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class ObjectWithJsonToStringTest {
    private class TestObject extends ObjectWithJsonToString {
        private String stringField = "stringField";

        private int intField = 100;

        public String getStringField() {
            return stringField;
        }

        public void setStringField(String stringField) {
            this.stringField = stringField;
        }

        public int getIntField() {
            return intField;
        }

        public void setIntField(int intField) {
            this.intField = intField;
        }
    }

    @Test
    public void shouldConvertObjectToJSON() {
        MatcherAssert.assertThat(new ObjectWithJsonToStringTest.TestObject().toString(), Is.is((((((("{" + (NEW_LINE)) + "  \"stringField\" : \"stringField\",") + (NEW_LINE)) + "  \"intField\" : 100") + (NEW_LINE)) + "}")));
    }
}

