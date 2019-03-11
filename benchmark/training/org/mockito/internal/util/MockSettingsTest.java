/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.creation.settings.CreationSettings;
import org.mockito.mock.MockCreationSettings;
import org.mockitoutil.TestBase;


public class MockSettingsTest extends TestBase {
    @Test
    public void public_api_for_creating_settings() throws Exception {
        // when
        MockCreationSettings<List> settings = Mockito.withSettings().name("dummy").build(List.class);
        // then
        Assert.assertEquals(List.class, settings.getTypeToMock());
        Assert.assertEquals("dummy", settings.getMockName().toString());
    }

    @Test
    public void test_without_annotations() throws Exception {
        MockCreationSettings<List> settings = withoutAnnotations().build(List.class);
        CreationSettings copy = new CreationSettings(((CreationSettings) (settings)));
        Assert.assertEquals(List.class, settings.getTypeToMock());
        Assert.assertEquals(List.class, copy.getTypeToMock());
        Assert.assertTrue(settings.isStripAnnotations());
        Assert.assertTrue(copy.isStripAnnotations());
    }
}

