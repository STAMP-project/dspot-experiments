/**
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.engine.module;


import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.module.DependencyResolver;
import org.terasology.module.Module;
import org.terasology.module.ModuleRegistry;


public class ModuleDownloadListGeneratorTest {
    @Test(expected = DependencyResolutionFailedException.class)
    public void testResolverFailed() throws DependencyResolutionFailedException {
        ModuleRegistry localRegistry = buildRegistry("1.0.0", buildSimpleModule("myModule", "1.0.0"));
        DependencyResolver resolver = mockResolver(false);
        ModuleDownloadListGenerator listGenerator = new ModuleDownloadListGenerator(localRegistry, resolver);
        buildList(listGenerator);
    }

    @Test
    public void testSingleModuleNoUpdate() throws DependencyResolutionFailedException {
        ModuleRegistry localRegistry = buildRegistry("1.0.0", buildSimpleModule("myModule", "1.0.0"));
        DependencyResolver resolver = mockResolver(true, buildSimpleModule("myModule", "1.0.0"), buildEngineModule("1.0.0"));
        ModuleDownloadListGenerator listGenerator = new ModuleDownloadListGenerator(localRegistry, resolver);
        Assert.assertEquals(Collections.emptySet(), buildList(listGenerator));
    }

    @Test
    public void testSingleModuleNeedsUpdate() throws DependencyResolutionFailedException {
        Module moduleV1 = buildSimpleModule("myModule", "1.0.0");
        Module moduleV2 = buildSimpleModule("myModule", "2.0.0");
        ModuleRegistry localRegistry = buildRegistry("1.0.0", moduleV1);
        DependencyResolver resolver = mockResolver(true, moduleV2, buildEngineModule("1.0.0"));
        ModuleDownloadListGenerator listGenerator = new ModuleDownloadListGenerator(localRegistry, resolver);
        Assert.assertEquals(Collections.singleton(moduleV2), buildList(listGenerator));
    }

    @Test
    public void testMultipleModulesPartialUpdate() throws DependencyResolutionFailedException {
        Module moduleAV1 = buildSimpleModule("myModuleA", "1.0.0");
        Module moduleBV1 = buildSimpleModule("myModuleB", "1.0.0");
        Module moduleBV2 = buildSimpleModule("myModuleB", "2.0.0");
        ModuleRegistry localRegistry = buildRegistry("1.0.0", moduleAV1, moduleBV1);
        DependencyResolver resolver = mockResolver(true, moduleBV1, moduleBV2, buildEngineModule("1.0.0"));
        ModuleDownloadListGenerator listGenerator = new ModuleDownloadListGenerator(localRegistry, resolver);
        Assert.assertEquals(Collections.singleton(moduleBV2), buildList(listGenerator));
    }
}

