/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.ui.spoon.delegates;


import KettleExtensionPoint.TransBeforeDeleteSteps;
import KettleExtensionPoint.TransBeforeDeleteSteps.id;
import java.util.HashMap;
import org.eclipse.swt.widgets.Shell;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.extension.ExtensionPointPluginType;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.plugins.ClassLoadingPluginInterface;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.ui.spoon.Spoon;


public class SpoonStepsDelegateTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    public interface PluginMockInterface extends ClassLoadingPluginInterface , PluginInterface {}

    @Test
    public void testDelStepsExtensionPointCancelDelete() throws Exception {
        SpoonStepsDelegateTest.PluginMockInterface pluginInterface = Mockito.mock(SpoonStepsDelegateTest.PluginMockInterface.class);
        Mockito.when(getName()).thenReturn(id);
        Mockito.when(getMainType()).thenReturn(((Class) (ExtensionPointInterface.class)));
        Mockito.when(getIds()).thenReturn(new String[]{ TransBeforeDeleteSteps.id });
        ExtensionPointInterface extensionPoint = Mockito.mock(ExtensionPointInterface.class);
        Mockito.when(loadClass(ExtensionPointInterface.class)).thenReturn(extensionPoint);
        Mockito.doThrow(KettleException.class).when(extensionPoint).callExtensionPoint(ArgumentMatchers.any(LogChannelInterface.class), ArgumentMatchers.any(StepMeta[].class));
        PluginRegistry.addPluginType(ExtensionPointPluginType.getInstance());
        PluginRegistry.getInstance().registerPlugin(ExtensionPointPluginType.class, pluginInterface);
        SpoonStepsDelegate delegate = Mockito.mock(SpoonStepsDelegate.class);
        delegate.spoon = Mockito.mock(Spoon.class);
        Mockito.doCallRealMethod().when(delegate).delSteps(ArgumentMatchers.any(TransMeta.class), ArgumentMatchers.any(StepMeta[].class));
        TransMeta trans = Mockito.mock(TransMeta.class);
        StepMeta[] steps = new StepMeta[]{ Mockito.mock(StepMeta.class) };
        delegate.delSteps(trans, steps);
        Mockito.verify(extensionPoint, Mockito.times(1)).callExtensionPoint(ArgumentMatchers.any(), ArgumentMatchers.eq(steps));
    }

    @Test
    public void testGetStepDialogClass() throws Exception {
        SpoonStepsDelegateTest.PluginMockInterface plugin = Mockito.mock(SpoonStepsDelegateTest.PluginMockInterface.class);
        Mockito.when(getIds()).thenReturn(new String[]{ "mockPlugin" });
        Mockito.when(matches("mockPlugin")).thenReturn(true);
        Mockito.when(getName()).thenReturn("mockPlugin");
        StepMetaInterface meta = Mockito.mock(StepMetaInterface.class);
        Mockito.when(meta.getDialogClassName()).thenReturn(String.class.getName());
        Mockito.when(getClassMap()).thenReturn(new HashMap<Class<?>, String>() {
            {
                put(StepMetaInterface.class, getName());
                put(StepDialogInterface.class, StepDialogInterface.class.getName());
            }
        });
        PluginRegistry.getInstance().registerPlugin(StepPluginType.class, plugin);
        SpoonStepsDelegate delegate = Mockito.mock(SpoonStepsDelegate.class);
        Spoon spoon = Mockito.mock(Spoon.class);
        delegate.spoon = spoon;
        delegate.log = Mockito.mock(LogChannelInterface.class);
        Mockito.when(spoon.getShell()).thenReturn(Mockito.mock(Shell.class));
        Mockito.doCallRealMethod().when(delegate).getStepDialog(ArgumentMatchers.any(StepMetaInterface.class), ArgumentMatchers.any(TransMeta.class), ArgumentMatchers.any(String.class));
        TransMeta trans = Mockito.mock(TransMeta.class);
        // verify that dialog class is requested from plugin
        try {
            delegate.getStepDialog(meta, trans, "");// exception is expected here

        } catch (Throwable ignore) {
            Mockito.verify(meta, Mockito.never()).getDialogClassName();
        }
        // verify that the deprecated way is still valid
        Mockito.when(getClassMap()).thenReturn(new HashMap<Class<?>, String>() {
            {
                put(StepMetaInterface.class, getName());
            }
        });
        try {
            delegate.getStepDialog(meta, trans, "");// exception is expected here

        } catch (Throwable ignore) {
            Mockito.verify(meta, Mockito.times(1)).getDialogClassName();
        }
        // cleanup
        PluginRegistry.getInstance().removePlugin(StepPluginType.class, plugin);
    }
}

