package io.protostuff.runtime;


import io.protostuff.Message;
import io.protostuff.Schema;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultIdStrategyTest {
    private final IdStrategy strategy = new DefaultIdStrategy();

    @Test
    public void privateConstructors() {
        HasSchema<DefaultIdStrategyTest.TestMessage> schema = strategy.getSchemaWrapper(DefaultIdStrategyTest.TestMessage.class, true);
        Assert.assertEquals(DefaultIdStrategyTest.TestMessage.SCHEMA, schema.getSchema());
    }

    public static class TestMessage implements Message<DefaultIdStrategyTest.TestMessage> {
        private static final Schema<DefaultIdStrategyTest.TestMessage> SCHEMA = Mockito.mock(Schema.class);

        private TestMessage() {
        }

        @Override
        public Schema<DefaultIdStrategyTest.TestMessage> cachedSchema() {
            return DefaultIdStrategyTest.TestMessage.SCHEMA;
        }
    }
}

