package com.vaadin.tests.server.component.abstractsingleselect;


import com.vaadin.tests.design.DeclarativeTestBaseBase;
import com.vaadin.tests.server.component.abstractlisting.AbstractListingDeclarativeTest;
import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.declarative.DesignContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


/**
 * {@link AbstractSingleSelect} component declarative test.
 * <p>
 * Test inherits test methods from a {@link AbstractListingDeclarativeTest}
 * class providing here only common cases for {@link AbstractSingleSelect}s.
 *
 * @author Vaadin Ltd
 * @param <T>
 * 		a component type
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractSingleSelectDeclarativeTest<T extends AbstractSingleSelect> extends AbstractListingDeclarativeTest<T> {
    @Override
    @Test
    public void dataSerialization() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        List<String> items = Arrays.asList("foo", "bar", "foobar");
        String type = "com.vaadin.SomeType";
        String attribute = "data-type";
        String design = String.format(("<%s %s=\'%s\'>\n" + (("<option item=\'foo\'>foo</option>\n" + "<option item='bar' selected>bar</option>") + "<option item='foobar'>foobar</option></%s>")), getComponentTag(), attribute, type, getComponentTag());
        T component = getComponentClass().newInstance();
        setItems(items);
        setSelectedItem("bar");
        DesignContext context = readComponentAndCompare(design, component);
        assertEquals(type, context.getCustomAttributes(context.getRootComponent()).get(attribute));
        context = new DesignContext();
        context.setCustomAttribute(component, attribute, type);
        context.setShouldWriteDataDelegate(DeclarativeTestBaseBase.ALWAYS_WRITE_DATA);
        testWrite(component, design, context);
    }

    @Override
    @Test
    public void valueSerialization() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        List<String> items = Arrays.asList("foo", "bar", "foobar");
        String type = "com.vaadin.SomeType";
        String attribute = "data-type";
        String design = String.format(("<%s  %s=\'%s\'>\n" + (("<option item=\'foo\'>foo</option>\n" + "<option item='bar' selected>bar</option>") + "<option item='foobar'>foobar</option></%s>")), getComponentTag(), attribute, type, getComponentTag());
        T component = getComponentClass().newInstance();
        setItems(items);
        setValue("bar");
        DesignContext context = readComponentAndCompare(design, component);
        assertEquals(type, context.getCustomAttributes(context.getRootComponent()).get(attribute));
        context = new DesignContext();
        context.setCustomAttribute(component, attribute, type);
        context.setShouldWriteDataDelegate(DeclarativeTestBaseBase.ALWAYS_WRITE_DATA);
        testWrite(component, design, context);
    }

    @Test
    public void dataWithCaptionGeneratorSerialization() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        List<String> items = Arrays.asList("foo", "bar", "foobar");
        T component = getComponentClass().newInstance();
        Method setItemCaptionGenerator = getItemCaptionGeneratorMethod(component);
        if (setItemCaptionGenerator == null) {
            return;
        }
        String design = String.format(("<%s>\n" + (("<option item=\'foo\'>foo1</option>\n" + "<option item='bar' selected>bar1</option>") + "<option item='foobar'>foobar1</option></%s>")), getComponentTag(), getComponentTag());
        setItems(items);
        setValue("bar");
        ItemCaptionGenerator generator = ( item) -> item + "1";
        setItemCaptionGenerator.invoke(component, generator);
        testRead(design, component);
        testWrite(design, component, true);
    }

    @Override
    @Test
    public void readOnlySelection() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T component = getComponentClass().newInstance();
        List<String> items = Arrays.asList("foo", "bar", "foobar");
        String design = String.format(("<%s readonly>\n" + (("<option item=\'foo\'>foo</option>\n" + "<option item='bar'>bar</option>") + "<option item='foobar'>foobar</option>")), getComponentTag(), getComponentTag());
        setItems(items);
        setReadOnly(true);
        testRead(design, component);
        testWrite(design, component, true);
    }
}

