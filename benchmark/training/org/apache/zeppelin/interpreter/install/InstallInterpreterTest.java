package org.apache.zeppelin.interpreter.install;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/* Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
public class InstallInterpreterTest {
    private File tmpDir;

    private InstallInterpreter installer;

    private File interpreterBaseDir;

    @Test
    public void testList() {
        Assert.assertEquals(2, installer.list().size());
    }

    @Test
    public void install() {
        Assert.assertEquals(0, interpreterBaseDir.listFiles().length);
        installer.install("intp1");
        Assert.assertTrue(new File(interpreterBaseDir, "intp1").isDirectory());
    }

    @Test
    public void installAll() {
        installer.installAll();
        Assert.assertTrue(new File(interpreterBaseDir, "intp1").isDirectory());
        Assert.assertTrue(new File(interpreterBaseDir, "intp2").isDirectory());
    }
}

