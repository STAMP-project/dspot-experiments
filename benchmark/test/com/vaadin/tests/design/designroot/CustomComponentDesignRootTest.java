package com.vaadin.tests.design.designroot;


import com.vaadin.tests.server.component.customcomponent.MyPrefilledCustomComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import org.junit.Assert;
import org.junit.Test;


public class CustomComponentDesignRootTest {
    @Test
    public void customComponentReadVerticalLayoutDesign() {
        CustomComponentDesignRootForVerticalLayout r = new CustomComponentDesignRootForVerticalLayout();
        // Composition root, should be VerticalLayout
        Component compositionRoot = iterator().next();
        Assert.assertNotNull(compositionRoot);
        Assert.assertEquals(VerticalLayout.class, compositionRoot.getClass());
        Assert.assertNotNull(r.ok);
        Assert.assertNotNull(r.cancel);
        Assert.assertEquals("original", r.preInitializedField.getValue());
    }

    @Test
    public void customComponentReadCustomComponentDesign() {
        CustomComponentDesignRootForMyCustomComponent r = new CustomComponentDesignRootForMyCustomComponent();
        // Composition root, should be MyPrefilledCustomComponent
        Component compositionRoot = iterator().next();
        Assert.assertNotNull(compositionRoot);
        Assert.assertEquals(MyPrefilledCustomComponent.class, compositionRoot.getClass());
    }
}

