package com.vaadin.tests.server.component.abstractlisting;


import com.vaadin.server.SerializablePredicate;
import com.vaadin.tests.server.component.abstractcomponent.AbstractComponentDeclarativeTestBase;
import com.vaadin.ui.AbstractListing;
import com.vaadin.ui.IconGenerator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


/**
 * {@link AbstractListing} component declarative test.
 * <p>
 * Test ignores comparison for {@link ItemCaptionGenerator},
 * {@link IconGenerator} and {@link SerializablePredicate} "properties" since
 * they are functions and it doesn't matter which implementation is chosen. But
 * test checks generated item captions, item icon generation and enabled items
 * generations if they are available in the component as public methods.
 * <p>
 * Common {@link AbstractComponent} properties are tested in
 * {@link AbstractComponentDeclarativeTestBase}
 *
 * @see AbstractComponentDeclarativeTestBase
 * @author Vaadin Ltd
 * @param <T>
 * 		a component type
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractListingDeclarativeTest<T extends AbstractListing> extends AbstractComponentDeclarativeTestBase<T> {
    private static final String EXTERNAL_URL = "http://example.com/example.gif";

    private static final String FILE_PATH = "img/example.gif";

    private static final String THEME_PATH = "example.gif";

    @Test
    public void itemIconsSerialization() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T component = getComponentClass().newInstance();
        Method setIconGenerator = getIconGeneratorMethod(component);
        if (setIconGenerator == null) {
            return;
        }
        List<String> items = Arrays.asList("foo", "bar", "foobar", "barfoo");
        String design = String.format(("<%s>\n" + (((("<option item=\'foo\' icon=\'%s\'>foo</option>\n" + "<option item='bar' icon='%s'>bar</option>") + "<option item='foobar' icon='theme://%s'>foobar</option>") + "<option item='barfoo'>barfoo</option>") + "</%s>")), getComponentTag(), AbstractListingDeclarativeTest.EXTERNAL_URL, AbstractListingDeclarativeTest.FILE_PATH, AbstractListingDeclarativeTest.THEME_PATH, getComponentTag());
        setItems(items);
        IconGenerator generator = ( item) -> generateIcons(item, items);
        setIconGenerator.invoke(component, generator);
        testRead(design, component);
        testWrite(design, component, true);
    }

    @Test
    public void enabledItemsSerialization() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T component = getComponentClass().newInstance();
        Method setEnabledITemsGenerator = getEnabledItemsProviderMethod(component);
        if (setEnabledITemsGenerator == null) {
            return;
        }
        List<String> items = Arrays.asList("foo", "bar", "foobar");
        String design = String.format(("<%s>\n" + (("<option item=\'foo\'>foo</option>\n" + "<option item='bar' disabled>bar</option>") + "<option item='foobar'>foobar</option>")), getComponentTag(), getComponentTag());
        setItems(items);
        SerializablePredicate predicate = ( item) -> !(item.equals("bar"));
        setEnabledITemsGenerator.invoke(component, predicate);
        testRead(design, component);
        testWrite(design, component, true);
    }
}

