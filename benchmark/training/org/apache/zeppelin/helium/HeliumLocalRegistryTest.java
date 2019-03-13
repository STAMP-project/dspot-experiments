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


import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import static HeliumType.APPLICATION;


public class HeliumLocalRegistryTest {
    private File tmpDir;

    @Test
    public void testGetAllPackage() throws IOException {
        // given
        File r1Path = new File(tmpDir, "r1");
        HeliumLocalRegistry r1 = new HeliumLocalRegistry("r1", r1Path.getAbsolutePath());
        Assert.assertEquals(0, r1.getAll().size());
        // when
        Gson gson = new Gson();
        HeliumPackage pkg1 = new HeliumPackage(APPLICATION, "app1", "desc1", "artifact1", "classname1", new String[][]{  }, "license", "");
        FileUtils.writeStringToFile(new File(r1Path, "pkg1.json"), gson.toJson(pkg1));
        // then
        Assert.assertEquals(1, r1.getAll().size());
    }
}

