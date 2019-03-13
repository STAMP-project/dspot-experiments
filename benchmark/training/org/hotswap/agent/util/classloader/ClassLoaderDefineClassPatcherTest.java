/**
 * Copyright 2013-2019 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package org.hotswap.agent.util.classloader;


import PluginManager.PLUGIN_PACKAGE;
import java.net.URL;
import java.net.URLClassLoader;
import org.hotswap.agent.plugin.jvm.AnonymousClassInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by bubnik on 29.10.13.
 */
public class ClassLoaderDefineClassPatcherTest {
    @Test
    public void testPatch() throws Exception {
        ClassLoader appClassLoader = new URLClassLoader(new URL[]{  }, getClass().getClassLoader());
        Assert.assertEquals("Class created in parent classloader", getClass().getClassLoader(), appClassLoader.loadClass(AnonymousClassInfo.class.getName()).getClassLoader());
        new ClassLoaderDefineClassPatcher().patch(getClass().getClassLoader(), PLUGIN_PACKAGE.replace(".", "/"), appClassLoader, null);
        Assert.assertEquals("Class created in app classloader", appClassLoader, appClassLoader.loadClass(AnonymousClassInfo.class.getName()).getClassLoader());
    }
}

