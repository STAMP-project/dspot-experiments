/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import org.drools.compiler.CommonTestMethodBase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kie.api.runtime.KieSession;


public class EventAccessorRestoreTest extends CommonTestMethodBase {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private File kbaseFile = null;

    @Test
    public void testDeserialization() {
        try {
            FileInputStream fis = new FileInputStream(kbaseFile);
            KieSession knowledgeSession = loadSession(fis);
            ArrayList list = new ArrayList();
            knowledgeSession.setGlobal("list", list);
            knowledgeSession.insert(30);
            knowledgeSession.fireAllRules();
            Assert.assertEquals(1, list.size());
            Assert.assertEquals("Tick", list.get(0).getClass().getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

