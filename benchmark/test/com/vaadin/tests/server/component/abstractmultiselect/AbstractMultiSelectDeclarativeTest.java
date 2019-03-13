package com.vaadin.tests.server.component.abstractmultiselect;


import com.vaadin.tests.server.component.abstractlisting.AbstractListingDeclarativeTest;
import com.vaadin.ui.AbstractMultiSelect;
import com.vaadin.ui.declarative.DesignContext;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Test;


/**
 * {@link AbstractMultiSelect} component declarative test.
 * <p>
 * Test inherits test methods from a {@link AbstractListingDeclarativeTest}
 * class providing here only common cases for {@link AbstractMultiSelect}s.
 *
 * @see AbstractListingDeclarativeTest
 * @author Vaadin Ltd
 * @param <T>
 * 		a component type
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbstractMultiSelectDeclarativeTest<T extends AbstractMultiSelect> extends AbstractListingDeclarativeTest<T> {
    @Override
    @Test
    public void dataSerialization() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        List<String> items = Arrays.asList("foo", "bar", "foobar");
        String type = "com.vaadin.SomeType";
        String attribute = "data-type";
        String design = String.format(("<%s %s=\'%s\'>\n" + (("<option item=\'foo\' selected>foo1</option>\n" + "<option item='bar'>bar1</option>") + "<option item='foobar' selected>foobar1</option></%s>")), getComponentTag(), attribute, type, getComponentTag());
        T component = getComponentClass().newInstance();
        setItems(items);
        select("foo");
        select("foobar");
        setItemCaptionGenerator(( item) -> item + "1");
        DesignContext context = readComponentAndCompare(design, component, ( ctxt) -> configureContext(type, attribute, component, ctxt));
        assertEquals(type, context.getCustomAttributes(context.getRootComponent()).get(attribute));
        context = new DesignContext();
        configureContext(type, attribute, component, context);
        testWrite(component, design, context);
    }

    @Override
    @Test
    public void valueSerialization() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        List<String> items = Arrays.asList("foo", "bar", "foobar");
        String type = "com.vaadin.SomeType";
        String attribute = "data-type";
        String design = String.format(("<%s %s=\'%s\'>\n" + (("<option item=\'foo\' selected>foo1</option>\n" + "<option item='bar'>bar1</option>") + "<option item='foobar' selected>foobar1</option></%s>")), getComponentTag(), attribute, type, getComponentTag());
        T component = getComponentClass().newInstance();
        setItems(items);
        setValue(new HashSet(Arrays.asList("foo", "foobar")));
        setItemCaptionGenerator(( item) -> item + "1");
        DesignContext context = readComponentAndCompare(design, component, ( ctxt) -> configureContext(type, attribute, component, ctxt));
        assertEquals(type, context.getCustomAttributes(context.getRootComponent()).get(attribute));
        context = new DesignContext();
        configureContext(type, attribute, component, context);
        testWrite(component, design, context);
    }

    @Override
    @Test
    public void readOnlySelection() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T component = getComponentClass().newInstance();
        List<String> items = Arrays.asList("foo", "bar", "foobar");
        String design = String.format(("<%s readonly>\n" + (("<option item=\'foo\'>foo</option>\n" + "<option item='bar'>bar</option>") + "<option item='foobar'>foobar</option>")), getComponentTag(), getComponentTag());
        setItems(items);
        setReadOnly(true);
        testRead(design, component, true);
        testWrite(design, component, true);
    }
}

