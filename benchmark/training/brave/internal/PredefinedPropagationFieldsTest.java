package brave.internal;


import org.junit.Test;


public class PredefinedPropagationFieldsTest extends PropagationFieldsFactoryTest<PredefinedPropagationFields> {
    @Test
    public void put_ignore_if_not_defined() {
        PropagationFields.put(context, "balloon-color", "red", factory.type());
        assertThat(toMap()).isEmpty();
    }

    @Test
    public void put_ignore_if_not_defined_index() {
        PredefinedPropagationFields fields = factory.create();
        fields.put(4, "red");
        assertThat(fields).isEqualToComparingFieldByField(factory.create());
    }

    @Test
    public void put_idempotent() {
        PredefinedPropagationFields fields = factory.create();
        fields.put("foo", "red");
        String[] fieldsArray = fields.values;
        fields.put("foo", "red");
        assertThat(fields.values).isSameAs(fieldsArray);
        fields.put("foo", "blue");
        assertThat(fields.values).isNotSameAs(fieldsArray);
    }

    @Test
    public void get_ignore_if_not_defined_index() {
        PredefinedPropagationFields fields = factory.create();
        assertThat(fields.get(4)).isNull();
    }

    @Test
    public void toMap_one_index() {
        PredefinedPropagationFields fields = factory.create();
        fields.put(1, "a");
        assertThat(fields.toMap()).hasSize(1).containsEntry(PropagationFieldsFactoryTest.FIELD2, "a");
    }

    @Test
    public void toMap_two_index() {
        PredefinedPropagationFields fields = factory.create();
        fields.put(0, "1");
        fields.put(1, "a");
        assertThat(fields.toMap()).hasSize(2).containsEntry(PropagationFieldsFactoryTest.FIELD1, "1").containsEntry(PropagationFieldsFactoryTest.FIELD2, "a");
    }
}

