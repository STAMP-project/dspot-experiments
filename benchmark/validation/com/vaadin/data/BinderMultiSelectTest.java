package com.vaadin.data;


import com.vaadin.tests.data.bean.BeanWithEnums;
import com.vaadin.tests.data.bean.TestEnum;
import com.vaadin.ui.CheckBoxGroup;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class BinderMultiSelectTest extends BinderTestBase<Binder<BeanWithEnums>, BeanWithEnums> {
    public static class TestEnumSetToStringConverter implements Converter<Set<TestEnum>, String> {
        @Override
        public Result<String> convertToModel(Set<TestEnum> value, ValueContext context) {
            return Result.ok(value.stream().map(TestEnum::name).collect(Collectors.joining(",")));
        }

        @Override
        public Set<TestEnum> convertToPresentation(String value, ValueContext context) {
            return Stream.of(value.split(",")).filter(( string) -> !(string.isEmpty())).map(TestEnum::valueOf).collect(Collectors.toSet());
        }
    }

    private final Binder<AtomicReference<String>> converterBinder = new Binder();

    private CheckBoxGroup<TestEnum> select;

    @Test
    public void beanBound_bindSelectByShortcut_selectionUpdated() {
        item.setEnums(Collections.singleton(TestEnum.ONE));
        binder.setBean(item);
        binder.bind(select, BeanWithEnums::getEnums, BeanWithEnums::setEnums);
        Assert.assertEquals(Collections.singleton(TestEnum.ONE), select.getSelectedItems());
    }

    @Test
    public void beanBound_bindSelect_selectionUpdated() {
        item.setEnums(Collections.singleton(TestEnum.TWO));
        binder.setBean(item);
        binder.forField(select).bind(BeanWithEnums::getEnums, BeanWithEnums::setEnums);
        Assert.assertEquals(Collections.singleton(TestEnum.TWO), select.getSelectedItems());
    }

    @Test
    public void selectBound_bindBeanWithoutEnums_selectedItemNotPresent() {
        bindEnum();
        Assert.assertTrue(select.getSelectedItems().isEmpty());
    }

    @Test
    public void selectBound_bindBean_selectionUpdated() {
        item.setEnums(Collections.singleton(TestEnum.ONE));
        bindEnum();
        Assert.assertEquals(Collections.singleton(TestEnum.ONE), select.getSelectedItems());
    }

    @Test
    public void bound_setSelection_beanValueUpdated() {
        bindEnum();
        select.select(TestEnum.TWO);
        Assert.assertEquals(Collections.singleton(TestEnum.TWO), item.getEnums());
    }

    @Test
    public void bound_setSelection_beanValueIsACopy() {
        bindEnum();
        select.select(TestEnum.TWO);
        Set<TestEnum> enums = item.getEnums();
        binder.setBean(new BeanWithEnums());
        select.select(TestEnum.ONE);
        Assert.assertEquals(Collections.singleton(TestEnum.TWO), enums);
    }

    @Test
    public void bound_deselect_beanValueUpdatedToNull() {
        item.setEnums(Collections.singleton(TestEnum.ONE));
        bindEnum();
        select.deselect(TestEnum.ONE);
        Assert.assertTrue(item.getEnums().isEmpty());
    }

    @Test
    public void unbound_changeSelection_beanValueNotUpdated() {
        item.setEnums(Collections.singleton(TestEnum.ONE));
        bindEnum();
        binder.removeBean();
        select.select(TestEnum.TWO);
        Assert.assertEquals(Collections.singleton(TestEnum.ONE), item.getEnums());
    }

    @Test
    public void withConverter_load_selectUpdated() {
        converterBinder.readBean(new AtomicReference("TWO"));
        Assert.assertEquals(Collections.singleton(TestEnum.TWO), select.getSelectedItems());
    }

    @Test
    public void withConverter_save_referenceUpdated() {
        select.select(TestEnum.ONE);
        select.select(TestEnum.TWO);
        AtomicReference<String> reference = new AtomicReference<>("");
        converterBinder.writeBeanIfValid(reference);
        Assert.assertEquals("ONE,TWO", reference.get());
    }

    @Test
    public void withValidator_validate_validatorUsed() {
        binder.forField(select).withValidator(( selection) -> ((selection.size()) % 2) == 1, "Must select odd number of items").bind(BeanWithEnums::getEnums, BeanWithEnums::setEnums);
        binder.setBean(item);
        Assert.assertFalse(binder.validate().isOk());
        select.select(TestEnum.TWO);
        Assert.assertTrue(binder.validate().isOk());
    }
}

