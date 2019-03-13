package brave.internal;


import java.util.Map;
import org.junit.Test;


public class MapPropagationFieldsTest extends PropagationFieldsFactoryTest<MapPropagationFields> {
    @Test
    public void put_allows_arbitrary_field() {
        MapPropagationFields fields = factory.create();
        fields.put("balloon-color", "red");
        assertThat(fields.values).containsEntry("balloon-color", "red");
    }

    @Test
    public void put_idempotent() {
        MapPropagationFields fields = factory.create();
        fields.put("balloon-color", "red");
        Map<String, String> fieldsMap = fields.values;
        fields.put("balloon-color", "red");
        assertThat(fields.values).isSameAs(fieldsMap);
        fields.put("balloon-color", "blue");
        assertThat(fields.values).isNotSameAs(fieldsMap);
    }

    @Test
    public void unmodifiable() {
        MapPropagationFields fields = factory.create();
        fields.put(PropagationFieldsFactoryTest.FIELD1, "a");
        try {
            fields.values.put(PropagationFieldsFactoryTest.FIELD1, "b");
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
        }
    }
}

