package com.vaadin.tests.server.component.grid;


import ContentMode.HTML;
import SelectionMode.MULTI;
import com.vaadin.data.SelectionModel.Multi;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.tests.server.component.abstractlisting.AbstractListingDeclarativeTest;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class GridDeclarativeTest extends AbstractListingDeclarativeTest<Grid> {
    @Test
    public void gridAttributes() {
        Grid<Person> grid = new Grid();
        int frozenColumns = 1;
        HeightMode heightMode = HeightMode.ROW;
        double heightByRows = 13.7;
        grid.addColumn(Person::getFirstName).setCaption("First Name");
        grid.addColumn(Person::getLastName).setId("id").setCaption("Id");
        grid.setFrozenColumnCount(frozenColumns);
        grid.setSelectionMode(MULTI);
        grid.setHeightMode(heightMode);
        grid.setHeightByRows(heightByRows);
        String design = String.format(("<%s height-mode='%s' frozen-columns='%d' rows='%s' selection-mode='%s'><table><colgroup>" + ((((("<col column-id='column0' sortable>" + "<col column-id='id' sortable>") + "</colgroup><thead>") + "<tr default><th plain-text column-ids='column0'>First Name</th>") + "<th plain-text column-ids='id'>Id</th></tr>") + "</thead></table></%s>")), getComponentTag(), heightMode.toString().toLowerCase(Locale.ROOT), frozenColumns, heightByRows, MULTI.toString().toLowerCase(Locale.ROOT), getComponentTag());
        testRead(design, grid);
        testWrite(design, grid);
    }

    @Test
    public void mergedHeaderCells() {
        Grid<Person> grid = new Grid();
        Column<Person, String> column1 = grid.addColumn(Person::getFirstName).setCaption("First Name");
        Column<Person, String> column2 = grid.addColumn(Person::getLastName).setId("id").setCaption("Id");
        Column<Person, String> column3 = grid.addColumn(Person::getEmail).setId("mail").setCaption("Mail");
        HeaderRow header = grid.addHeaderRowAt(1);
        String headerRowText1 = "foo";
        header.getCell(column1).setText(headerRowText1);
        HeaderCell cell2 = header.getCell(column2);
        HeaderCell join = header.join(cell2, header.getCell(column3));
        String headerRowText3 = "foobar";
        join.setText(headerRowText3);
        String design = String.format(("<%s><table><colgroup>" + ((((((((("<col column-id='column0' sortable>" + "<col column-id='id' sortable>") + "<col column-id='mail' sortable>") + "</colgroup><thead>") + "<tr default><th plain-text column-ids='column0'>First Name</th>") + "<th plain-text column-ids='id'>Id</th>") + "<th plain-text column-ids='mail'>Mail</th></tr>") + "<tr><th plain-text column-ids='column0'>%s</th>") + "<th colspan='2' plain-text column-ids='id,mail'>foobar</th></tr>") + "</thead></table></%s>")), getComponentTag(), headerRowText1, headerRowText3, getComponentTag());
        testRead(design, grid);
        testWrite(design, grid);
    }

    @Test
    public void mergedFooterCells() {
        Grid<Person> grid = new Grid();
        Column<Person, String> column1 = grid.addColumn(Person::getFirstName).setCaption("First Name");
        Column<Person, String> column2 = grid.addColumn(Person::getLastName).setId("id").setCaption("Id");
        Column<Person, String> column3 = grid.addColumn(Person::getEmail).setId("mail").setCaption("Mail");
        FooterRow footer = grid.addFooterRowAt(0);
        FooterCell cell1 = footer.getCell(column1);
        String footerRowText1 = "foo";
        cell1.setText(footerRowText1);
        FooterCell cell2 = footer.getCell(column2);
        FooterCell cell3 = footer.getCell(column3);
        String footerRowText2 = "foobar";
        footer.join(cell2, cell3).setHtml(footerRowText2);
        String design = String.format(("<%s><table><colgroup>" + ((((((((("<col column-id='column0' sortable>" + "<col column-id='id' sortable>") + "<col column-id='mail' sortable>") + "</colgroup><thead>") + "<tr default><th plain-text column-ids='column0'>First Name</th>") + "<th plain-text column-ids='id'>Id</th>") + "<th plain-text column-ids='mail'>Mail</th></tr></thead>") + "<tfoot><tr><td plain-text column-ids='column0'>%s</td>") + "<td colspan='2' column-ids='id,mail'>%s</td></tr></tfoot>") + "</table></%s>")), getComponentTag(), footerRowText1, footerRowText2, getComponentTag());
        testRead(design, grid);
        testWrite(design, grid);
    }

    @Test
    public void columnAttributes() {
        Grid<Person> grid = new Grid();
        String secondColumnId = "sortableColumn";
        Column<Person, String> notSortableColumn = grid.addColumn(Person::getFirstName).setCaption("First Name");
        Column<Person, String> column2 = grid.addColumn(Person::getLastName).setId(secondColumnId).setCaption("Id");
        String caption = "not-sortable-column";
        notSortableColumn.setCaption(caption);
        boolean sortable = false;
        notSortableColumn.setSortable(sortable);
        boolean editable = true;
        notSortableColumn.setEditorComponent(new TextField(), Person::setLastName);
        notSortableColumn.setEditable(editable);
        boolean resizable = false;
        notSortableColumn.setResizable(resizable);
        boolean hidable = true;
        notSortableColumn.setHidable(hidable);
        boolean hidden = true;
        notSortableColumn.setHidden(hidden);
        String hidingToggleCaption = "sortable-toggle-caption";
        column2.setHidingToggleCaption(hidingToggleCaption);
        double width = 17.3;
        column2.setWidth(width);
        double minWidth = 37.3;
        column2.setMinimumWidth(minWidth);
        double maxWidth = 63.4;
        column2.setMaximumWidth(maxWidth);
        int expandRatio = 83;
        column2.setExpandRatio(expandRatio);
        String design = String.format(("<%s><table><colgroup>" + (((((("<col column-id='column0' sortable='%s' editable resizable='%s' hidable hidden>" + "<col column-id='sortableColumn' sortable hiding-toggle-caption='%s' width='%s' min-width='%s' max-width='%s' expand='%s'>") + "</colgroup><thead>") + "<tr default><th plain-text column-ids='column0'>%s</th>") + "<th plain-text column-ids='sortableColumn'>%s</th>") + "</tr></thead>") + "</table></%s>")), getComponentTag(), sortable, resizable, hidingToggleCaption, width, minWidth, maxWidth, expandRatio, caption, "Id", getComponentTag());
        testRead(design, grid, true);
        testWrite(design, grid);
    }

    @Test
    public void headerFooterSerialization() {
        Grid<Person> grid = new Grid();
        Column<Person, String> column1 = grid.addColumn(Person::getFirstName).setCaption("First Name");
        Column<Person, String> column2 = grid.addColumn(Person::getLastName).setId("id").setCaption("Id");
        FooterRow footerRow = grid.addFooterRowAt(0);
        footerRow.getCell(column1).setText("x");
        footerRow.getCell(column2).setHtml("y");
        String design = String.format(("<%s><table><colgroup>" + ((((((("<col column-id='column0' sortable>" + "<col column-id='id' sortable></colgroup><thead>") + "<tr default><th plain-text column-ids='column0'>First Name</th>") + "<th plain-text column-ids='id'>Id</th></tr>") + "</thead><tbody></tbody>") + "<tfoot><tr><td plain-text column-ids='column0'>x</td>") + "<td column-ids='id'>y</td></tr></tfoot>") + "</table></%s>")), getComponentTag(), getComponentTag());
        testRead(design, grid);
        testWrite(design, grid, true);
    }

    /**
     * Value for single select
     */
    @Override
    @Test
    public void valueSerialization() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        valueSingleSelectSerialization();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void valueMultiSelectSerialization() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Grid<Person> grid = new Grid();
        Person person1 = createPerson("foo", "bar");
        Person person2 = createPerson("name", "last-name");
        Person person3 = createPerson("foo", "last-name");
        grid.setItems(person1, person2, person3);
        grid.addColumn(Person::getFirstName).setCaption("First Name");
        grid.addColumn(Person::getLastName).setId("id").setCaption("Id");
        Multi<Person> model = ((Multi<Person>) (grid.setSelectionMode(MULTI)));
        model.selectItems(person1, person3);
        String design = String.format(("<%s selection-mode='multi'><table><colgroup>" + (((((((("<col column-id='column0' sortable>" + "<col column-id='id' sortable></colgroup><thead>") + "<tr default><th plain-text column-ids='column0'>First Name</th>") + "<th plain-text column-ids='id'>Id</th></tr>") + "</thead><tbody>") + "<tr item='%s' selected><td>%s</td><td>%s</td></tr>") + "<tr item='%s'><td>%s</td><td>%s</td></tr>") + "<tr item='%s' selected><td>%s</td><td>%s</td></tr>") + "</tbody></table></%s>")), getComponentTag(), person1.toString(), person1.getFirstName(), person1.getLastName(), person2.toString(), person2.getFirstName(), person2.getLastName(), person3.toString(), person3.getFirstName(), person3.getLastName(), getComponentTag());
        Grid<?> readGrid = testRead(design, grid, true, true);
        assertEquals(3, readGrid.getDataProvider().size(new com.vaadin.data.provider.Query()));
        testWrite(design, grid, true);
    }

    @Test
    public void testComponentInGridHeader() {
        Grid<Person> grid = new Grid();
        Column<Person, String> column = grid.addColumn(Person::getFirstName).setCaption("First Name");
        String html = "<b>Foo</b>";
        Label component = new Label(html);
        component.setContentMode(HTML);
        // @formatter:off
        String design = String.format(("<%s><table>" + (((((("<colgroup>" + "   <col sortable column-id='column0'>") + "</colgroup>") + "<thead>") + "<tr default><th column-ids='column0'><vaadin-label>%s</vaadin-label></th></tr>") + "</thead>") + "</table></%s>")), getComponentTag(), html, getComponentTag());
        // @formatter:on
        grid.getDefaultHeaderRow().getCell(column).setComponent(component);
        testRead(design, grid, true);
        testWrite(design, grid);
    }

    @Test
    public void testComponentInGridFooter() {
        Grid<Person> grid = new Grid();
        Column<Person, String> column = grid.addColumn(Person::getFirstName).setCaption("First Name");
        String html = "<b>Foo</b>";
        Label component = new Label(html);
        component.setContentMode(HTML);
        grid.prependFooterRow().getCell(column).setComponent(component);
        grid.removeHeaderRow(grid.getDefaultHeaderRow());
        // @formatter:off
        String design = String.format(("<%s><table>" + (((((((("<colgroup>" + "   <col sortable column-id='column0'>") + "</colgroup>") + "<thead>") + "<tfoot>") + "<tr><td column-ids='column0'><vaadin-label>%s</vaadin-label></td></tr>") + "</tfoot>") + "</table>") + "</%s>")), getComponentTag(), html, getComponentTag());
        // @formatter:on
        testRead(design, grid, true);
        testWrite(design, grid);
    }

    @Test
    public void testNoHeaderRows() {
        // @formatter:off
        String design = "<vaadin-grid><table>" + ((((("<colgroup>" + "   <col sortable column-id='column0'>") + "</colgroup>") + "<thead />") + "</table>") + "</vaadin-grid>");
        // @formatter:on
        Grid<Person> grid = new Grid();
        grid.addColumn(Person::getFirstName).setCaption("First Name");
        grid.removeHeaderRow(grid.getDefaultHeaderRow());
        testWrite(design, grid);
        testRead(design, grid, true);
    }

    @Test
    public void testReadEmptyGrid() {
        String design = "<vaadin-grid />";
        testRead(design, new Grid<String>(), false);
    }

    @Test
    public void testEmptyGrid() {
        String design = "<vaadin-grid></vaadin-grid>";
        Grid<String> expected = new Grid();
        testWrite(design, expected);
        testRead(design, expected, true);
    }

    @Test(expected = DesignException.class)
    public void testMalformedGrid() {
        String design = "<vaadin-grid><vaadin-label /></vaadin-grid>";
        testRead(design, new Grid<String>());
    }

    @Test(expected = DesignException.class)
    public void testGridWithNoColGroup() {
        String design = "<vaadin-grid><table><thead><tr><th>Foo</tr></thead></table></vaadin-grid>";
        testRead(design, new Grid<String>());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHtmlEntitiesinGridHeaderFooter() {
        String id = "> id";
        String plainText = "plain-text";
        // @formatter:off
        String design = String.format(("<%s><table>" + (((((((((("<colgroup>" + "  <col sortable column-id='%s'>") + "</colgroup>") + "<thead>") + "   <tr default><th %s column-ids='%s'>&gt; Test</th>") + "</thead>") + "<tfoot>") + "<tr><td %s column-ids='%s'>&gt; Test</td></tr>") + "</tfoot>") + "<tbody />") + "</table></%s>")), getComponentTag(), id, plainText, id, plainText, id, getComponentTag());
        // @formatter:on
        Grid<Person> grid = read(design);
        String actualHeader = grid.getHeaderRow(0).getCell(id).getText();
        String actualFooter = grid.getFooterRow(0).getCell(id).getText();
        String expected = "> Test";
        assertEquals(expected, actualHeader);
        assertEquals(expected, actualFooter);
        design = design.replace(plainText, "");
        grid = read(design);
        actualHeader = grid.getHeaderRow(0).getCell(id).getHtml();
        actualFooter = grid.getFooterRow(0).getCell(id).getHtml();
        expected = "&gt; Test";
        assertEquals(expected, actualHeader);
        assertEquals(expected, actualFooter);
        grid = new Grid();
        Column<Person, String> column = grid.addColumn(Person::getFirstName).setId(id);
        HeaderRow header = grid.addHeaderRowAt(0);
        FooterRow footer = grid.addFooterRowAt(0);
        grid.removeHeaderRow(grid.getDefaultHeaderRow());
        // entities should be encoded when writing back, not interpreted as HTML
        header.getCell(column).setText("&amp; Test");
        footer.getCell(column).setText("&amp; Test");
        Element root = new Element(Tag.valueOf(getComponentTag()), "");
        grid.writeDesign(root, new DesignContext());
        assertEquals("&amp;amp; Test", root.getElementsByTag("th").get(0).html());
        assertEquals("&amp;amp; Test", root.getElementsByTag("td").get(0).html());
        header = grid.addHeaderRowAt(0);
        footer = grid.addFooterRowAt(0);
        // entities should not be encoded, this is already given as HTML
        header.getCell(id).setHtml("&amp; Test");
        footer.getCell(id).setHtml("&amp; Test");
        root = new Element(Tag.valueOf(getComponentTag()), "");
        grid.writeDesign(root, new DesignContext());
        assertEquals("&amp; Test", root.getElementsByTag("th").get(0).html());
        assertEquals("&amp; Test", root.getElementsByTag("td").get(0).html());
    }

    @Test
    public void beanItemType() throws Exception {
        Class<Person> beanClass = Person.class;
        String beanClassName = beanClass.getName();
        // @formatter:off
        String design = String.format("<%s data-item-type=\"%s\"></%s>", getComponentTag(), beanClassName, getComponentTag());
        // @formatter:on
        @SuppressWarnings("unchecked")
        Grid<Person> grid = read(design);
        assertEquals(beanClass, grid.getBeanType());
        testWrite(design, grid);
    }

    @Test
    public void beanGridDefaultColumns() {
        Grid<Person> grid = new Grid(Person.class);
        String design = write(grid, false);
        assertDeclarativeColumnCount(11, design);
        Person testPerson = new Person("the first", "the last", "The email", 64, Sex.MALE, new Address("the street", 12313, "The city", Country.SOUTH_AFRICA));
        @SuppressWarnings("unchecked")
        Grid<Person> readGrid = read(design);
        assertColumns(11, grid.getColumns(), readGrid.getColumns(), testPerson);
    }

    @Test
    public void beanGridNoColumns() {
        Grid<Person> grid = new Grid(Person.class);
        grid.setColumns();
        String design = write(grid, false);
        assertDeclarativeColumnCount(0, design);
        Person testPerson = new Person("the first", "the last", "The email", 64, Sex.MALE, new Address("the street", 12313, "The city", Country.SOUTH_AFRICA));
        @SuppressWarnings("unchecked")
        Grid<Person> readGrid = read(design);
        assertColumns(0, grid.getColumns(), readGrid.getColumns(), testPerson);
        // Can add a mapped property
        assertEquals("The email", readGrid.addColumn("email").getValueProvider().apply(testPerson));
    }

    @Test
    public void beanGridOnlyCustomColumns() {
        // Writes columns without propertyId even though name matches, reads
        // columns without propertyId mapping, can add new columns using
        // propertyId
        Grid<Person> grid = new Grid(Person.class);
        grid.setColumns();
        grid.addColumn(Person::getFirstName).setCaption("First Name");
        String design = write(grid, false);
        assertDeclarativeColumnCount(1, design);
        Person testPerson = new Person("the first", "the last", "The email", 64, Sex.MALE, new Address("the street", 12313, "The city", Country.SOUTH_AFRICA));
        @SuppressWarnings("unchecked")
        Grid<Person> readGrid = read(design);
        assertColumns(1, grid.getColumns(), readGrid.getColumns(), testPerson);
        // First name should not be mapped to the property
        Assert.assertNull(readGrid.getColumns().get(0).getValueProvider().apply(testPerson));
        // Can add a mapped property
        assertEquals("the last", readGrid.addColumn("lastName").getValueProvider().apply(testPerson));
    }

    @Test
    public void beanGridOneCustomizedColumn() {
        // Writes columns with propertyId except one without
        // Reads columns to match initial setup
        Grid<Person> grid = new Grid(Person.class);
        grid.addColumn(( person) -> ((person.getFirstName()) + " ") + (person.getLastName())).setCaption("First and Last");
        String design = write(grid, false);
        assertDeclarativeColumnCount(12, design);
        Person testPerson = new Person("the first", "the last", "The email", 64, Sex.MALE, new Address("the street", 12313, "The city", Country.SOUTH_AFRICA));
        @SuppressWarnings("unchecked")
        Grid<Person> readGrid = read(design);
        assertColumns(12, grid.getColumns(), readGrid.getColumns(), testPerson);
        // First and last name should not be mapped to anything but should exist
        Assert.assertNull(readGrid.getColumns().get(11).getValueProvider().apply(testPerson));
    }
}

