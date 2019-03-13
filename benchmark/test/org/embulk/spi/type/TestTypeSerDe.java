package org.embulk.spi.type;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.embulk.EmbulkTestRuntime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static StringType.STRING;


public class TestTypeSerDe {
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    private static class HasType {
        private Type type;

        // TODO test TimestampType
        @JsonCreator
        public HasType(@JsonProperty("type")
        Type type) {
            this.type = type;
        }

        @JsonProperty("type")
        public Type getType() {
            return type;
        }
    }

    @Test
    public void testGetType() {
        TestTypeSerDe.HasType type = new TestTypeSerDe.HasType(STRING);
        String json = runtime.getModelManager().writeObject(type);
        TestTypeSerDe.HasType decoded = runtime.getModelManager().readObject(TestTypeSerDe.HasType.class, json);
        Assert.assertTrue(((STRING) == (decoded.getType())));
    }
}

