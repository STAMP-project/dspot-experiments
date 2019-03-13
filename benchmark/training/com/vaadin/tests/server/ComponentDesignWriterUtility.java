package com.vaadin.tests.server;


import Navigator.EmptyView;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.components.colorpicker.ColorPickerGradient;
import com.vaadin.ui.components.colorpicker.ColorPickerGrid;
import com.vaadin.ui.components.colorpicker.ColorPickerHistory;
import com.vaadin.ui.components.colorpicker.ColorPickerPopup;
import com.vaadin.ui.components.colorpicker.ColorPickerPreview;
import com.vaadin.ui.components.colorpicker.ColorPickerSelect;
import com.vaadin.ui.declarative.DesignContext;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;


/**
 * Utility class for outputting the declarative syntax of Vaadin components.
 */
public class ComponentDesignWriterUtility {
    private static final Set<String> WHITE_LIST_FQNS = new HashSet<>();

    private static final Document document = new Document("");

    private static final DesignContext designContext = new DesignContext(ComponentDesignWriterUtility.document);

    static {
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add(DragAndDropWrapper.class.getName());
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add(EmptyView.class.getName());
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add(ColorPickerGradient.class.getName());
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add(ColorPickerPopup.class.getName());
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add(ColorPickerPreview.class.getName());
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add(ColorPickerGrid.class.getName());
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add(ColorPickerSelect.class.getName());
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add(ColorPickerHistory.class.getName());
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add(ColorPickerGradient.class.getName());
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add(ColorPickerPopup.class.getName());
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add(ColorPickerPreview.class.getName());
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add(ColorPickerGrid.class.getName());
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add(ColorPickerSelect.class.getName());
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add(ColorPickerHistory.class.getName());
        // ==================================================================
        // Classes that cannot be loaded
        // ==================================================================
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add("com.vaadin.server.communication.PushAtmosphereHandler$AtmosphereResourceListener");
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add("com.vaadin.server.communication.PushAtmosphereHandler");
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add("com.vaadin.server.communication.PushRequestHandler$1");
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add("com.vaadin.server.communication.PushRequestHandler$2");
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add("com.vaadin.server.LegacyVaadinPortlet");
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add("com.vaadin.server.RestrictedRenderResponse");
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add("com.vaadin.server.VaadinPortlet$VaadinGateInRequest");
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add("com.vaadin.server.VaadinPortlet$VaadinHttpAndPortletRequest");
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add("com.vaadin.server.VaadinPortlet$VaadinLiferayRequest");
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add("com.vaadin.server.VaadinPortlet$VaadinWebLogicPortalRequest");
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add("com.vaadin.server.VaadinPortlet$VaadinWebSpherePortalRequest");
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add("com.vaadin.server.VaadinPortlet");
        ComponentDesignWriterUtility.WHITE_LIST_FQNS.add("com.vaadin.server.VaadinPortletRequest");
        ComponentDesignWriterUtility.designContext.setShouldWriteDefaultValues(true);
    }

    @Test
    public void vaadin8ComponentsElementStartsWithVaadinPrefix() throws URISyntaxException {
        Assert.assertTrue(ComponentDesignWriterUtility.getVaadin8Components().stream().map(ComponentDesignWriterUtility::getDeclarativeSyntax).allMatch(( element) -> element.startsWith("<vaadin-")));
    }

    @Test
    public void vaadin7ComponentsElementStartsWithVaadinPrefix() throws URISyntaxException {
        Assert.assertTrue(ComponentDesignWriterUtility.getVaadin7Components().stream().map(ComponentDesignWriterUtility::getDeclarativeSyntax).allMatch(( element) -> element.startsWith("<vaadin7-")));
    }
}

