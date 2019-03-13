package com.vaadin.tests.server.component.abstractcomponent;


import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.declarative.DesignContext;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


/**
 * Abstract test class which contains tests for declarative format for
 * properties that are common for AbstractComponent.
 * <p>
 * It's an abstract so it's not supposed to be run as is. Instead each
 * declarative test for a real component should extend it and implement abstract
 * methods to be able to test the common properties. Components specific
 * properties should be tested additionally in the subclasses implementations.
 *
 * @author Vaadin Ltd
 */
public abstract class AbstractComponentDeclarativeTestBase<T extends AbstractComponent> extends DeclarativeTestBase<T> {
    @Test
    public void emptyAbstractComponentDeserialization() throws IllegalAccessException, InstantiationException {
        String design = String.format("<%s/> ", getComponentTag());
        T component = getComponentClass().newInstance();
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void abstractComponentAttributesDeserialization() throws IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException {
        String id = "testId";
        String caption = "testCaption";
        boolean captionAsHtml = true;
        String description = "testDescription";
        boolean enabled = false;
        String error = "<div>testError</div>";
        String height = "47%";
        String width = "83px";
        String icon = "img/example.gif";
        Locale locale = new Locale("fi", "FI");
        String primaryStyle = "testPrimaryStyle";
        boolean readOnly = true;
        boolean responsive = true;
        String styleName = "testStyleName";
        boolean visible = false;
        boolean requiredIndicator = true;
        T component = getComponentClass().newInstance();
        boolean hasReadOnly = callBooleanSetter(readOnly, "setReadOnly", component);
        boolean hasRequiredIndicator = callBooleanSetter(requiredIndicator, "setRequiredIndicatorVisible", component);
        String design = String.format(("<%s id='%s' caption='%s' caption-as-html description='%s' " + ((("error='%s' enabled='false' width='%s' height='%s' " + "icon='%s' locale='%s' primary-style-name='%s' ") + "%s responsive style-name='%s' visible='false' ") + "%s/>")), getComponentTag(), id, caption, description, error, width, height, icon, locale.toString(), primaryStyle, (hasReadOnly ? "readonly" : ""), styleName, (hasRequiredIndicator ? "required-indicator-visible" : ""));
        setId(id);
        setCaption(caption);
        setCaptionAsHtml(captionAsHtml);
        setDescription(description);
        setEnabled(enabled);
        setComponentError(new com.vaadin.server.UserError(error, ContentMode.HTML, ErrorLevel.ERROR));
        setHeight(height);
        setWidth(width);
        component.setIcon(new FileResource(new File(icon)));
        setLocale(locale);
        setPrimaryStyleName(primaryStyle);
        setResponsive(responsive);
        setStyleName(styleName);
        setVisible(visible);
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void externalIcon() throws IllegalAccessException, InstantiationException {
        String url = "http://example.com/example.gif";
        String design = String.format("<%s icon='%s'/>", getComponentTag(), url);
        T component = getComponentClass().newInstance();
        component.setIcon(new ExternalResource(url));
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void themeIcon() throws IllegalAccessException, InstantiationException {
        String path = "example.gif";
        String design = String.format("<%s icon='theme://%s'/>", getComponentTag(), path);
        T component = getComponentClass().newInstance();
        component.setIcon(new ThemeResource(path));
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void sizeFullDeserialization() throws IllegalAccessException, InstantiationException {
        String design = String.format("<%s size-full/>", getComponentTag());
        T component = getComponentClass().newInstance();
        setSizeFull();
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void widthFullDeserialization() throws IllegalAccessException, InstantiationException {
        String design = String.format("<%s width-full/>", getComponentTag());
        T component = getComponentClass().newInstance();
        setWidth("100%");
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void heightFullDeserialization() throws IllegalAccessException, InstantiationException {
        String design = String.format("<%s height-full/>", getComponentTag());
        T component = getComponentClass().newInstance();
        setHeight("100%");
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void sizeUndefinedDeserialization() throws IllegalAccessException, InstantiationException {
        String design = String.format("<%s/>", getComponentTag());
        T component = getComponentClass().newInstance();
        setSizeUndefined();
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void heightUnderfinedDeserialization() throws IllegalAccessException, InstantiationException {
        String design = String.format("<%s/>", getComponentTag());
        T component = getComponentClass().newInstance();
        setHeightUndefined();
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void widthUndefinedDeserialization() throws IllegalAccessException, InstantiationException {
        String design = String.format("<%s/>", getComponentTag());
        T component = getComponentClass().newInstance();
        setWidthUndefined();
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testUnknownAttribute() {
        String value = "bar";
        String design = String.format("<%s foo='%s'/>", getComponentTag(), value);
        DesignContext context = readAndReturnContext(design);
        T label = getComponentClass().cast(context.getRootComponent());
        Assert.assertTrue("Custom attribute was preserved in custom attributes", context.getCustomAttributes(label).containsKey("foo"));
        testWrite(label, design, context);
    }
}

