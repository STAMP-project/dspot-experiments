package com.vaadin.data;


import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Slider;
import java.util.Collections;
import org.junit.Test;


public class BinderComponentTest extends BinderTestBase<Binder<String>, String> {
    enum TestValues {

        TRUE,
        FALSE,
        FILE_NOT_FOUND;}

    @Test
    public void slider_bind_null() {
        double minValue = 10.5;
        double initialValue = 28.2;
        Slider slider = new Slider();
        slider.setResolution(1);
        slider.setMin(minValue);
        testFieldNullRepresentation(initialValue, slider);
    }

    @Test
    public void colorpicker_bind_null() {
        testFieldNullRepresentation(new Color(123, 254, 213), new ColorPicker());
    }

    @Test
    public void richtextarea_bind_null() {
        testFieldNullRepresentation("Test text", new RichTextArea());
    }

    @Test
    public void checkbox_bind_null() {
        testFieldNullRepresentation(true, new CheckBox());
    }

    @Test
    public void checkboxgroup_bind_null() {
        CheckBoxGroup<BinderComponentTest.TestValues> checkBoxGroup = new CheckBoxGroup();
        checkBoxGroup.setItems(BinderComponentTest.TestValues.values());
        testFieldNullRepresentation(Collections.singleton(BinderComponentTest.TestValues.FILE_NOT_FOUND), checkBoxGroup);
    }
}

