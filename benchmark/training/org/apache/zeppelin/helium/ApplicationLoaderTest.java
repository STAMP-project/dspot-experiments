/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.helium;


import java.io.File;
import org.apache.zeppelin.dep.DependencyResolver;
import org.apache.zeppelin.resource.LocalResourcePool;
import org.junit.Assert;
import org.junit.Test;


public class ApplicationLoaderTest {
    private File tmpDir;

    @Test
    public void loadUnloadApplication() throws Exception {
        // given
        LocalResourcePool resourcePool = new LocalResourcePool("pool1");
        DependencyResolver dep = new DependencyResolver(tmpDir.getAbsolutePath());
        ApplicationLoader appLoader = new ApplicationLoader(resourcePool, dep);
        HeliumPackage pkg1 = createPackageInfo(MockApplication1.class.getName(), "artifact1");
        ApplicationContext context1 = createContext("note1", "paragraph1", "app1");
        // when load application
        MockApplication1 app = ((MockApplication1) (getInnerApplication()));
        // then
        Assert.assertFalse(app.isUnloaded());
        Assert.assertEquals(0, app.getNumRun());
        // when unload
        app.unload();
        // then
        Assert.assertTrue(app.isUnloaded());
        Assert.assertEquals(0, app.getNumRun());
    }
}

