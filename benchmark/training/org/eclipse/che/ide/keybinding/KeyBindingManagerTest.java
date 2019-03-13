/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.keybinding;


import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.api.keybinding.Scheme;
import org.eclipse.che.ide.util.input.CharCodeWithModifiers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


/**
 *
 *
 * @author <a href="mailto:ak@nuxeo.com">Arnaud Kervern</a>
 */
@RunWith(GwtMockitoTestRunner.class)
public class KeyBindingManagerTest {
    protected KeyBindingManager keyManager;

    protected Scheme testScheme;

    @Test
    public void testSchemeRegistration() {
        Assert.assertNull(keyManager.getScheme(testScheme.getSchemeId()));
        keyManager.addScheme(testScheme);
        Assert.assertNotNull(keyManager.getScheme(testScheme.getSchemeId()));
    }

    @Test
    public void testGlobalSchemeFallback() {
        String actionId = "fallback";
        keyManager.getGlobal().addKey(Mockito.mock(CharCodeWithModifiers.class), actionId);
        keyManager.addScheme(testScheme);
        Assert.assertTrue(keyManager.getGlobal().contains(actionId));
        // Assert Global scheme is responding to the action
        keyManager.setActive(keyManager.getGlobal().getSchemeId());
        Assert.assertNotNull(keyManager.getKeyBinding(actionId));
        // Set TestScheme as active, and assert action is still registered
        keyManager.setActive(testScheme.getSchemeId());
        Assert.assertFalse(testScheme.contains(actionId));
        Assert.assertNotNull(keyManager.getKeyBinding(actionId));
    }

    @Test
    public void testKeyBindingGetter() {
        Assert.assertEquals(2, keyManager.getSchemes().size());
        String actionId = "testAction1";
        Assert.assertNull(keyManager.getKeyBinding(actionId));
        testScheme.addKey(Mockito.mock(CharCodeWithModifiers.class), actionId);
        // Action should not be handled yet - scheme not added / selected
        Assert.assertNull(keyManager.getKeyBinding(actionId));
        keyManager.addScheme(testScheme);
        // Action should not be handled yet - scheme not selected
        Assert.assertNull(keyManager.getKeyBinding(actionId));
        keyManager.setActive(testScheme.getSchemeId());
        Assert.assertNotNull(keyManager.getKeyBinding(actionId));
    }

    @Test
    public void testActionContainsCheck() {
        String actionId = "testAction2";
        Assert.assertFalse(testScheme.contains(actionId));
        testScheme.addKey(Mockito.mock(CharCodeWithModifiers.class), actionId);
        Assert.assertTrue(testScheme.contains(actionId));
    }
}

