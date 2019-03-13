package dev.morphia.query.validation;


import dev.morphia.entities.EntityWithNoId;
import dev.morphia.entities.SimpleEntity;
import dev.morphia.mapping.MappedClass;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import java.util.ArrayList;
import org.bson.types.ObjectId;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class EntityTypeAndIdValueValidatorTest {
    @Test
    public void shouldAllowTypeThatIsAMappedEntityAndAValueWithSameClassAsIdOfMappedEntity() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("_id");
        boolean validationApplied = EntityTypeAndIdValueValidator.getInstance().apply(mappedClass, mappedField, new ObjectId(), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldNotValidateIfEntityHasNoIdField() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        MappedClass mappedClass = new MappedClass(EntityWithNoId.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("_id");
        boolean validationApplied = EntityTypeAndIdValueValidator.getInstance().apply(mappedClass, mappedField, "some non-null value", validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(false));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldRejectValueWithATypeThatDoesNotMatchTheEntityIdFieldType() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("_id");
        boolean validationApplied = EntityTypeAndIdValueValidator.getInstance().apply(mappedClass, mappedField, "some non-ObjectId value", validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
    }
}

