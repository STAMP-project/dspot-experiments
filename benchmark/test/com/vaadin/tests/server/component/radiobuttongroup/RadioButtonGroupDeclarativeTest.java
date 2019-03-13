package com.vaadin.tests.server.component.radiobuttongroup;


import com.vaadin.tests.server.component.abstractsingleselect.AbstractSingleSelectDeclarativeTest;
import com.vaadin.ui.RadioButtonGroup;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


/**
 * Declarative support test for RadioButtonGroup.
 * <p>
 * Only {@link RadioButtonGroup#setHtmlContentAllowed(boolean)} is tested here
 * explicitly. All other tests are in the super class (
 * {@link AbstractSingleSelectDeclarativeTest}).
 *
 * @see AbstractSingleSelectDeclarativeTest
 * @author Vaadin Ltd
 */
@SuppressWarnings("rawtypes")
public class RadioButtonGroupDeclarativeTest extends AbstractSingleSelectDeclarativeTest<RadioButtonGroup> {
    private static final String SIMPLE_HTML = "<span>foo</span>";

    private static final String HTML = "<div class='wrapper'><div>bar</div></div>";

    private static final String HTML_ENTITIES = "<b>a & b</b>";

    @Test
    public void serializeDataWithHtmlContentAllowed() {
        RadioButtonGroup<String> group = new RadioButtonGroup();
        List<String> items = Arrays.asList("foo", "bar", "foobar");
        String design = String.format(("<%s html-content-allowed>\n" + (("<option item=\'foo\'>%s</option>\n" + "<option item='bar'>%s</option>") + "<option item='foobar'>%s</option>")), getComponentTag(), RadioButtonGroupDeclarativeTest.SIMPLE_HTML, RadioButtonGroupDeclarativeTest.HTML, RadioButtonGroupDeclarativeTest.HTML_ENTITIES.replace("&", "&amp;"), getComponentTag());
        group.setItems(items);
        group.setHtmlContentAllowed(true);
        group.setItemCaptionGenerator(( item) -> generateCaption(item, items));
        testRead(design, group, true);
        testWrite(design, group, true);
    }
}

