/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cluster;


import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.function.Function;
import javax.jms.Message;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.event.CatalogEvent;
import org.geoserver.catalog.event.CatalogModifyEvent;
import org.geoserver.cluster.impl.handlers.DocumentFile;
import org.geoserver.cluster.server.events.StyleModifyEvent;
import org.geoserver.data.test.MockData;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Style;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests related with styles events.
 */
public final class JmsStylesTest extends GeoServerSystemTestSupport {
    private static final String TEST_STYLE_NAME = "test_style";

    private static final String TEST_STYLE_FILE = "/test_style.sld";

    private static final String TEST_WORKSPACE_NAME = MockData.DEFAULT_PREFIX;

    private static final String CATALOG_ADD_EVENT_HANDLER_KEY = "JMSCatalogAddEventHandlerSPI";

    private static final String CATALOG_MODIFY_EVENT_HANDLER_KEY = "JMSCatalogModifyEventHandlerSPI";

    private static final String CATALOG_STYLES_FILE_EVENT_HANDLER_KEY = "JMSCatalogStylesFileHandlerSPI";

    private static final String CATALOG_REMOVE_EVENT_HANDLER_KEY = "JMSCatalogRemoveEventHandlerSPI";

    private WorkspaceInfo testWorkspace;

    private JMSEventHandler<String, DocumentFile> styleFileHandler;

    private JMSEventHandler<String, CatalogEvent> addEventHandler;

    private JMSEventHandler<String, CatalogEvent> modifyEventHandler;

    private JMSEventHandler<String, CatalogEvent> removeEventHandler;

    @Test
    public void testAddStyle() throws Exception {
        // add the test to the style catalog
        getTestData().addStyle(JmsStylesTest.TEST_STYLE_NAME, JmsStylesTest.TEST_STYLE_FILE, this.getClass(), getCatalog());
        // waiting for a catalog add event and a style file event
        List<Message> messages = JmsEventsListener.getMessagesByHandlerKey(5000, ( selected) -> (selected.size()) >= 2, JmsStylesTest.CATALOG_ADD_EVENT_HANDLER_KEY, JmsStylesTest.CATALOG_STYLES_FILE_EVENT_HANDLER_KEY);
        // let's check if the new added style was correctly published
        Assert.assertThat(messages.size(), Is.is(2));
        // checking that the correct style file was published
        List<DocumentFile> styleFile = JmsEventsListener.getMessagesForHandler(messages, JmsStylesTest.CATALOG_STYLES_FILE_EVENT_HANDLER_KEY, styleFileHandler);
        Assert.assertThat(styleFile.size(), Is.is(1));
        Assert.assertThat(styleFile.get(0).getResourceName(), Is.is("test_style.sld"));
        // checking that the correct style was published
        List<CatalogEvent> styleAddEvent = JmsEventsListener.getMessagesForHandler(messages, JmsStylesTest.CATALOG_ADD_EVENT_HANDLER_KEY, addEventHandler);
        Assert.assertThat(styleAddEvent.size(), Is.is(1));
        Assert.assertThat(styleAddEvent.get(0).getSource(), CoreMatchers.instanceOf(StyleInfo.class));
        StyleInfo styleInfo = ((StyleInfo) (styleAddEvent.get(0).getSource()));
        Assert.assertThat(styleInfo.getName(), Is.is(JmsStylesTest.TEST_STYLE_NAME));
        Assert.assertThat(styleInfo.getWorkspace(), CoreMatchers.nullValue());
    }

    @Test
    public void testAddStyleToWorkspace() throws Exception {
        // add the test to the style catalog
        getTestData().addStyle(testWorkspace, JmsStylesTest.TEST_STYLE_NAME, JmsStylesTest.TEST_STYLE_FILE, this.getClass(), getCatalog());
        // waiting for a catalog add event and a style file event
        List<Message> messages = JmsEventsListener.getMessagesByHandlerKey(5000, ( selected) -> (selected.size()) >= 2, JmsStylesTest.CATALOG_ADD_EVENT_HANDLER_KEY, JmsStylesTest.CATALOG_STYLES_FILE_EVENT_HANDLER_KEY);
        // let's check if the new added style was correctly published
        Assert.assertThat(messages.size(), Is.is(2));
        // checking that the correct style file was published
        List<DocumentFile> styleFile = JmsEventsListener.getMessagesForHandler(messages, JmsStylesTest.CATALOG_STYLES_FILE_EVENT_HANDLER_KEY, styleFileHandler);
        Assert.assertThat(styleFile.size(), Is.is(1));
        Assert.assertThat(styleFile.get(0).getResourceName(), Is.is("test_style.sld"));
        // checking that the correct style was published
        List<CatalogEvent> styleAddEvent = JmsEventsListener.getMessagesForHandler(messages, JmsStylesTest.CATALOG_ADD_EVENT_HANDLER_KEY, addEventHandler);
        Assert.assertThat(styleAddEvent.size(), Is.is(1));
        Assert.assertThat(styleAddEvent.get(0).getSource(), CoreMatchers.instanceOf(StyleInfo.class));
        StyleInfo styleInfo = ((StyleInfo) (styleAddEvent.get(0).getSource()));
        Assert.assertThat(styleInfo.getName(), Is.is(JmsStylesTest.TEST_STYLE_NAME));
        Assert.assertThat(styleInfo.getWorkspace(), Is.is(testWorkspace));
    }

    @Test
    public void testModifyStyleWorkspace() throws Exception {
        // add the test to the style catalog
        addTestStyle();
        // modify the style associated file
        StyleInfo styleInfo = getCatalog().getStyleByName(JmsStylesTest.TEST_STYLE_NAME);
        Assert.assertThat(styleInfo, CoreMatchers.notNullValue());
        getCatalog().getResourcePool().writeStyle(styleInfo, JmsStylesTest.class.getResourceAsStream("/test_style_modified.sld"));
        // modify the style workspace
        styleInfo.setWorkspace(testWorkspace);
        getCatalog().save(styleInfo);
        // waiting for a catalog modify event and a style file event
        List<Message> messages = JmsEventsListener.getMessagesByHandlerKey(5000, ( selected) -> (selected.size()) >= 1, JmsStylesTest.CATALOG_MODIFY_EVENT_HANDLER_KEY);
        Assert.assertThat(messages.size(), Is.is(1));
        // checking that the correct catalog style was published
        List<CatalogEvent> styleModifiedEvent = JmsEventsListener.getMessagesForHandler(messages, JmsStylesTest.CATALOG_MODIFY_EVENT_HANDLER_KEY, addEventHandler);
        Assert.assertThat(styleModifiedEvent.size(), Is.is(1));
        Assert.assertThat(styleModifiedEvent.get(0).getSource(), CoreMatchers.instanceOf(StyleInfo.class));
        StyleInfo modifiedStyle = ((StyleInfo) (styleModifiedEvent.get(0).getSource()));
        Assert.assertThat(modifiedStyle.getName(), Is.is(JmsStylesTest.TEST_STYLE_NAME));
        // check that the catalog modify event contains the correct workspace
        WorkspaceInfo workspace = searchPropertyNewValue(((CatalogModifyEvent) (styleModifiedEvent.get(0))), "workspace", WorkspaceInfo.class);
        Assert.assertThat(workspace, Is.is(testWorkspace));
        // check that the correct file style was published
        Assert.assertThat(styleModifiedEvent.get(0), CoreMatchers.instanceOf(StyleModifyEvent.class));
        byte[] fileContent = getFile();
        Assert.assertThat(fileContent, CoreMatchers.notNullValue());
        Assert.assertThat(fileContent.length, CoreMatchers.not(0));
        // parse the published style file and check the opacity value
        Style style = parseStyleFile(styleInfo, new ByteArrayInputStream(fileContent));
        RasterSymbolizer symbolizer = ((RasterSymbolizer) (style.featureTypeStyles().get(0).rules().get(0).symbolizers().get(0)));
        Assert.assertThat(symbolizer.getOpacity().evaluate(null), Is.is("0.5"));
    }

    @Test
    public void testRemoveStyle() throws Exception {
        // add the test to the style catalog
        addTestStyle();
        // remove style from catalog
        StyleInfo style = getCatalog().getStyleByName(JmsStylesTest.TEST_STYLE_NAME);
        Assert.assertThat(style, CoreMatchers.notNullValue());
        getCatalog().remove(style);
        // waiting for a catalog remove event
        List<Message> messages = JmsEventsListener.getMessagesByHandlerKey(5000, ( selected) -> (selected.size()) >= 1, JmsStylesTest.CATALOG_REMOVE_EVENT_HANDLER_KEY);
        Assert.assertThat(messages.size(), Is.is(1));
        // checking that the correct style was published
        List<CatalogEvent> styleRemoveEvent = JmsEventsListener.getMessagesForHandler(messages, JmsStylesTest.CATALOG_REMOVE_EVENT_HANDLER_KEY, addEventHandler);
        Assert.assertThat(styleRemoveEvent.size(), Is.is(1));
        Assert.assertThat(styleRemoveEvent.get(0).getSource(), CoreMatchers.instanceOf(StyleInfo.class));
        StyleInfo removedStyle = ((StyleInfo) (styleRemoveEvent.get(0).getSource()));
        Assert.assertThat(removedStyle.getName(), Is.is(JmsStylesTest.TEST_STYLE_NAME));
    }
}

