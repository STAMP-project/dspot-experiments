/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.talend.wizard;


import DefinitionImageType.SVG_ICON;
import DefinitionImageType.TREE_ICON_16X16;
import DefinitionImageType.WIZARD_BANNER_75X66;
import com.liferay.talend.connection.LiferayConnectionProperties;
import com.liferay.talend.tliferayinput.TLiferayInputProperties;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.talend.components.api.wizard.ComponentWizard;


/**
 *
 *
 * @author Zolt?n Tak?cs
 */
public class LiferayConnectionWizardDefinitionTest {
    @Test
    public void testCreateWizard() {
        ComponentWizard wizard = _liferayConnectionWizardDefinition.createWizard(_liferayConnectionProperties, LiferayConnectionWizardDefinitionTest._REPOSITORY_LOCATION);
        Assert.assertThat(wizard, CoreMatchers.instanceOf(LiferayConnectionWizard.class));
        Assert.assertEquals(_liferayConnectionWizardDefinition, wizard.getDefinition());
        Assert.assertEquals(LiferayConnectionWizardDefinitionTest._REPOSITORY_LOCATION, wizard.getRepositoryLocation());
    }

    @Test
    public void testGetMenuItemName() {
        LiferayConnectionWizardDefinition definition = new LiferayConnectionWizardDefinition();
        Assert.assertEquals("Create Liferay Connection", definition.getMenuItemName());
    }

    @Test
    public void testImagePath() {
        Assert.assertNotNull(_liferayConnectionWizardDefinition.getImagePath(TREE_ICON_16X16));
        Assert.assertNotNull(_liferayConnectionWizardDefinition.getImagePath(WIZARD_BANNER_75X66));
        Assert.assertNull(_liferayConnectionWizardDefinition.getImagePath(SVG_ICON));
    }

    @Test
    public void testSupportsProperties() {
        Assert.assertTrue(_liferayConnectionWizardDefinition.supportsProperties(LiferayConnectionProperties.class));
        Assert.assertFalse(_liferayConnectionWizardDefinition.supportsProperties(TLiferayInputProperties.class));
    }

    @Test
    public void testTopLevel() {
        Assert.assertTrue(_liferayConnectionWizardDefinition.isTopLevel());
    }

    private static final String _REPOSITORY_LOCATION = "___DRI";

    private LiferayConnectionProperties _liferayConnectionProperties;

    private LiferayConnectionWizardDefinition _liferayConnectionWizardDefinition;
}

