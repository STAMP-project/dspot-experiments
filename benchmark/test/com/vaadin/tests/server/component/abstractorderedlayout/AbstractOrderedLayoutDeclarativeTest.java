package com.vaadin.tests.server.component.abstractorderedlayout;


import Alignment.BOTTOM_RIGHT;
import Alignment.MIDDLE_CENTER;
import Alignment.TOP_LEFT;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.server.component.DeclarativeMarginTestBase;
import com.vaadin.ui.AbstractOrderedLayout;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


/**
 * Tests declarative support for AbstractOrderedLayout.
 *
 * @author Vaadin Ltd
 */
public class AbstractOrderedLayoutDeclarativeTest extends DeclarativeMarginTestBase<AbstractOrderedLayout> {
    private List<String> defaultAlignments = Arrays.asList(":top", ":left");

    @Test
    public void testMarginsVertical() {
        testMargins("vaadin-vertical-layout", new MarginInfo(true));
    }

    @Test
    public void testMarginsHorizontal() {
        testMargins("vaadin-horizontal-layout", new MarginInfo(false));
    }

    @Test
    public void testMarginsForm() {
        testMargins("vaadin-form-layout", new MarginInfo(true, false));
    }

    @Test
    public void testSpacingVertical() {
        testSpacing("vaadin-vertical-layout", true);
    }

    @Test
    public void testSpacingHorizontal() {
        testSpacing("vaadin-horizontal-layout", true);
    }

    @Test
    public void testSpacingForm() {
        testSpacing("vaadin-form-layout", true);
    }

    @Test
    public void testExpandRatio() {
        String design = getDesign(1);
        AbstractOrderedLayout layout = getLayout(1, null);
        testRead(design, layout);
        testWrite(design, layout);
        design = getDesign(0.25F);
        layout = getLayout(0.25F, null);
        testRead(design, layout);
        testWrite(design, layout);
    }

    @Test
    public void testAlignment() {
        String design = getDesign(0, ":top", ":left");
        AbstractOrderedLayout layout = getLayout(0, TOP_LEFT);
        testRead(design, layout);
        testWrite(design, layout);
        design = getDesign(0, ":middle", ":center");
        layout = getLayout(0, MIDDLE_CENTER);
        testRead(design, layout);
        testWrite(design, layout);
        design = getDesign(0, ":bottom", ":right");
        layout = getLayout(0, BOTTOM_RIGHT);
        testRead(design, layout);
        testWrite(design, layout);
    }
}

