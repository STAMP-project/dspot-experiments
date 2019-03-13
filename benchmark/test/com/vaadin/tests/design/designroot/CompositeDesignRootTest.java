package com.vaadin.tests.design.designroot;


import com.vaadin.tests.server.component.composite.MyPrefilledComposite;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import org.junit.Assert;
import org.junit.Test;


public class CompositeDesignRootTest {
    @Test
    public void compositeReadVerticalLayoutDesign() {
        CompositeDesignRootForVerticalLayout r = new CompositeDesignRootForVerticalLayout();
        // Composition root, should be VerticalLayout
        Component compositionRoot = iterator().next();
        Assert.assertNotNull(compositionRoot);
        Assert.assertEquals(VerticalLayout.class, compositionRoot.getClass());
        Assert.assertNotNull(r.ok);
        Assert.assertNotNull(r.cancel);
        Assert.assertEquals("original", r.preInitializedField.getValue());
    }

    @Test
    public void compositeReadCompositeDesign() {
        CompositeDesignRootForMyComposite r = new CompositeDesignRootForMyComposite();
        // Composition root, should be MyPrefilledcomposite
        Component compositionRoot = iterator().next();
        Assert.assertNotNull(compositionRoot);
        Assert.assertEquals(MyPrefilledComposite.class, compositionRoot.getClass());
    }
}

