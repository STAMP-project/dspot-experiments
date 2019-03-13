package com.vaadin.v7.tests.server.component.abstractfield;


import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.AbstractField;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class AbsFieldValidatorsTest {
    AbstractField<Object> field = new AbstractField<Object>() {
        @Override
        public Class getType() {
            return Object.class;
        }
    };

    Validator validator = EasyMock.createMock(Validator.class);

    Validator validator2 = EasyMock.createMock(Validator.class);

    @Test
    public void testAddValidator() {
        Assert.assertNotNull(field.getValidators());
        Assert.assertEquals(0, field.getValidators().size());
        field.addValidator(validator);
        Assert.assertEquals(1, field.getValidators().size());
        Assert.assertTrue(field.getValidators().contains(validator));
        field.addValidator(validator2);
        Assert.assertEquals(2, field.getValidators().size());
        Assert.assertTrue(field.getValidators().contains(validator));
        Assert.assertTrue(field.getValidators().contains(validator2));
    }

    @Test
    public void testRemoveValidator() {
        field.addValidator(validator);
        field.addValidator(validator2);
        field.removeValidator(validator);
        Assert.assertNotNull(field.getValidators());
        Assert.assertEquals(1, field.getValidators().size());
        Assert.assertFalse(field.getValidators().contains(validator));
        Assert.assertTrue(field.getValidators().contains(validator2));
        field.removeValidator(validator2);
        Assert.assertNotNull(field.getValidators());
        Assert.assertEquals(0, field.getValidators().size());
        Assert.assertFalse(field.getValidators().contains(validator));
        Assert.assertFalse(field.getValidators().contains(validator2));
    }

    @Test
    public void testRemoveAllValidators() {
        field.addValidator(validator);
        field.addValidator(validator2);
        field.removeAllValidators();
        Assert.assertNotNull(field.getValidators());
        Assert.assertEquals(0, field.getValidators().size());
        Assert.assertFalse(field.getValidators().contains(validator));
        Assert.assertFalse(field.getValidators().contains(validator2));
    }

    @Test
    public void nonImmediateFieldWithValidator() {
        field.setImmediate(false);
        field.addValidator(validator);
        Assert.assertFalse("field should be non-immediate because explicitly set", field.isImmediate());
    }
}

