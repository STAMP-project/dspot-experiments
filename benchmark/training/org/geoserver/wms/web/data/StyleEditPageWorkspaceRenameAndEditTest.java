/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.web.data;


import java.io.File;
import java.io.FileReader;
import java.util.logging.Level;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.catalog.CatalogException;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geotools.styling.Style;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test is extremely brittle, and doesn't play well with others
 */
public class StyleEditPageWorkspaceRenameAndEditTest extends GeoServerWicketTestSupport {
    StyleInfo buildingsStyle;

    StyleEditPage edit;

    private static final String STYLE_TO_MOVE_NAME = "testStyle";

    private static final String STYLE_TO_MOVE_FILENAME = "testMoveStyle.sld";

    private static final String STYLE_TO_MOVE_FILENAME_UPDATED = "testMoveStyleUpdated.sld";

    StyleInfo styleInfoToMove;

    /* Test that a user can update the .sld file contents and move the style into a workspace in a single edit. */
    @Test
    public void testMoveWorkspaceAndEdit() throws Exception {
        // add catalog listener so we can validate the style modified event
        final boolean[] gotValidEvent = new boolean[]{ false };
        getCatalog().addListener(new CatalogListener() {
            @Override
            public void handleAddEvent(CatalogAddEvent event) throws CatalogException {
                // not interest, ignore this events
            }

            @Override
            public void handleRemoveEvent(CatalogRemoveEvent event) throws CatalogException {
                // not interest, ignore this events
            }

            @Override
            public void handleModifyEvent(CatalogModifyEvent event) throws CatalogException {
                // not interest, ignore this events
            }

            @Override
            public void handlePostModifyEvent(CatalogPostModifyEvent event) throws CatalogException {
                Assert.assertThat(event, CoreMatchers.notNullValue());
                Assert.assertThat(event.getSource(), CoreMatchers.notNullValue());
                if (!((event.getSource()) instanceof StyleInfo)) {
                    // only interested in style info events
                    return;
                }
                try {
                    // get the associated style and check that you got the correct
                    // content
                    StyleInfo styleInfo = ((StyleInfo) (event.getSource()));
                    Assert.assertThat(styleInfo, CoreMatchers.notNullValue());
                    Style style = getCatalog().getResourcePool().getStyle(styleInfo);
                    Assert.assertThat(style, CoreMatchers.notNullValue());
                    Assert.assertThat(style.featureTypeStyles().size(), CoreMatchers.is(2));
                    // ok everything looks good
                    gotValidEvent[0] = true;
                } catch (Exception exception) {
                    LOGGER.log(Level.SEVERE, "Error handling catalog modified style event.", exception);
                }
            }

            @Override
            public void reloaded() {
                // not interest, ignore this events
            }
        });
        edit = new StyleEditPage(styleInfoToMove);
        tester.startPage(edit);
        // Before the edit, the style should have one <FeatureTypeStyle>
        Assert.assertEquals(1, styleInfoToMove.getStyle().featureTypeStyles().size());
        FormTester form = tester.newFormTester("styleForm", false);
        // Update the workspace (select "sf" from the dropdown)
        DropDownChoice<WorkspaceInfo> typeDropDown = ((DropDownChoice<WorkspaceInfo>) (tester.getComponentFromLastRenderedPage("styleForm:context:panel:workspace")));
        for (int wsIdx = 0; wsIdx < (typeDropDown.getChoices().size()); wsIdx++) {
            WorkspaceInfo ws = typeDropDown.getChoices().get(wsIdx);
            if ("sf".equalsIgnoreCase(ws.getName())) {
                form.select("context:panel:workspace", wsIdx);
                break;
            }
        }
        // Update the raw style contents (the new style has TWO <FeatureTypeStyle> entries).
        File styleFile = new File(getClass().getResource(StyleEditPageWorkspaceRenameAndEditTest.STYLE_TO_MOVE_FILENAME_UPDATED).toURI());
        String updatedSld = IOUtils.toString(new FileReader(styleFile)).replaceAll("\r\n", "\n").replaceAll("\r", "\n");
        form.setValue("styleEditor:editorContainer:editorParent:editor", updatedSld);
        // Submit the form and verify that both the new workspace and new rawStyle saved.
        form.submit();
        StyleInfo si = getCatalog().getStyleByName(getCatalog().getWorkspaceByName("sf"), StyleEditPageWorkspaceRenameAndEditTest.STYLE_TO_MOVE_NAME);
        Assert.assertNotNull(si);
        Assert.assertNotNull(si.getWorkspace());
        Assert.assertEquals("sf", si.getWorkspace().getName());
        Assert.assertEquals(2, si.getStyle().featureTypeStyles().size());
        // check the correct style modified event was published
        Assert.assertThat(gotValidEvent[0], CoreMatchers.is(true));
    }
}

