package com.vaadin.v7.tests.server.component.abstractselect;


import DeclarativeTestBaseBase.ALWAYS_WRITE_DATA;
import com.vaadin.server.Resource;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.ListSelect;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for reading the properties of selection components.
 *
 * @author Vaadin Ltd
 */
public class AbstractSelectDeclarativeTest extends DeclarativeTestBase<AbstractSelect> {
    @Test
    public void testReadSingleSelectNewItemsAllowed() {
        testRead(getDesignSingleSelectNewItemsAllowed(), getExpectedSingleSelectNewItemsAllowed());
    }

    @Test
    public void testWriteSingleSelectNewItemsAllowed() {
        testWrite(getDesignSingleSelectNewItemsAllowed(), getExpectedSingleSelectNewItemsAllowed());
    }

    @Test
    public void testReadMultiSelect() {
        testRead(getDesignMultiSelect(), getExpectedMultiSelect());
    }

    @Test
    public void testWriteMultiSelect() {
        testWrite(getDesignMultiSelect(), getExpectedMultiSelect());
    }

    @Test
    public void testReadInlineData() {
        testRead(getDesignForInlineData(), getExpectedComponentForInlineData());
    }

    @Test(expected = DesignException.class)
    public void testReadMultipleValuesForSingleSelect() {
        testRead(("<vaadin7-list-select>" + (("<option selected>1</option>" + "<option selected>2</option>") + "</vaadin7-list-select>")), null);
    }

    @Test
    public void testReadMultipleValuesForMultiSelect() {
        ListSelect ls = new ListSelect();
        ls.setMultiSelect(true);
        ls.addItem("1");
        ls.addItem("2");
        ls.select("1");
        ls.select("2");
        testRead(("<vaadin7-list-select multi-select>" + (("<option selected>1</option>" + "<option selected>2</option>") + "</vaadin7-list-select>")), ls);
    }

    @Test
    public void testReadSingleValueForMultiSelect() {
        ListSelect ls = new ListSelect();
        ls.setMultiSelect(true);
        ls.addItem("1");
        ls.addItem("2");
        ls.select("1");
        testRead(("<vaadin7-list-select multi-select>" + (("<option selected>1</option>" + "<option>2</option>") + "</vaadin7-list-select>")), ls);
    }

    @Test
    public void testReadSingleValueForSingleSelect() {
        ListSelect ls = new ListSelect();
        ls.setMultiSelect(false);
        ls.addItem("1");
        ls.addItem("2");
        ls.select("1");
        testRead(("<vaadin7-list-select>" + (("<option selected>1</option>" + "<option>2</option>") + "</vaadin7-list-select>")), ls);
    }

    @Test
    public void testWriteInlineDataIgnored() {
        // No data is written by default
        testWrite(stripOptionTags(getDesignForInlineData()), getExpectedComponentForInlineData());
    }

    @Test
    public void testWriteInlineData() {
        testWrite(getDesignForInlineData(), getExpectedComponentForInlineData(), true);
    }

    @Test
    public void testReadAttributesSingleSelect() {
        Element design = createDesignWithAttributesSingleSelect();
        ComboBox cb = new ComboBox();
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("icon", Resource.class, null);
        container.addContainerProperty("name", String.class, null);
        cb.setContainerDataSource(container);
        cb.readDesign(design, new DesignContext());
        Assert.assertTrue("Adding new items should be allowed.", cb.isNewItemsAllowed());
        assertEquals("Wrong item caption mode.", AbstractSelect.ItemCaptionMode.PROPERTY, cb.getItemCaptionMode());
        assertEquals("Wrong item caption property id.", "name", cb.getItemCaptionPropertyId());
        assertEquals("Wrong item icon property id.", "icon", cb.getItemIconPropertyId());
        Assert.assertTrue("Null selection should be allowed.", cb.isNullSelectionAllowed());
        assertEquals("Wrong null selection item id.", "No items selected", cb.getNullSelectionItemId());
    }

    @Test
    public void testReadAttributesMultiSelect() {
        Element design = createDesignWithAttributesMultiSelect();
        ListSelect ls = new ListSelect();
        ls.readDesign(design, new DesignContext());
        Assert.assertTrue("Multi select should be allowed.", ls.isMultiSelect());
        assertEquals("Wrong caption mode.", AbstractSelect.ItemCaptionMode.EXPLICIT, ls.getItemCaptionMode());
        Assert.assertFalse("Null selection should not be allowed.", ls.isNullSelectionAllowed());
    }

    @Test
    public void testWriteAttributesSingleSelect() {
        ComboBox cb = createSingleSelectWithOnlyAttributes();
        Element e = new Element(Tag.valueOf("vaadin-combo-box"), "");
        cb.writeDesign(e, new DesignContext());
        assertEquals("Wrong caption for the combo box.", "A combo box", e.attr("caption"));
        Assert.assertTrue("Adding new items should be allowed.", "".equals(e.attr("new-items-allowed")));
        assertEquals("Wrong item caption mode.", "icon_only", e.attr("item-caption-mode"));
        assertEquals("Wrong item icon property id.", "icon", e.attr("item-icon-property-id"));
        Assert.assertTrue("Null selection should be allowed.", (("".equals(e.attr("null-selection-allowed"))) || ("true".equals(e.attr("null-selection-allowed")))));
        assertEquals("Wrong null selection item id.", "No item selected", e.attr("null-selection-item-id"));
    }

    @Test
    public void testWriteMultiListSelect() {
        ListSelect ls = createMultiSelect();
        Element e = new Element(Tag.valueOf("vaadin-list-select"), "");
        ls.writeDesign(e, new DesignContext());
        assertEquals("Null selection should not be allowed.", "false", e.attr("null-selection-allowed"));
        Assert.assertTrue("Multi select should be allowed.", (("".equals(e.attr("multi-select"))) || ("true".equals(e.attr("multi-select")))));
    }

    @Test
    public void testHtmlEntities() {
        String design = "<vaadin7-combo-box>" + (("  <option item-id=\"one\">&gt; One</option>" + "  <option>&gt; Two</option>") + "</vaadin7-combo-box>");
        AbstractSelect read = read(design);
        assertEquals("> One", read.getItemCaption("one"));
        AbstractSelect underTest = new ComboBox();
        underTest.addItem("> One");
        Element root = new Element(Tag.valueOf("vaadin-combo-box"), "");
        DesignContext dc = new DesignContext();
        dc.setShouldWriteDataDelegate(ALWAYS_WRITE_DATA);
        underTest.writeDesign(root, dc);
        assertEquals("&gt; One", root.getElementsByTag("option").first().html());
    }
}

