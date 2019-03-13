/**
 * Copyright 2005 JBoss Inc
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
package org.drools.modelcompiler;


import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;


public class BuildFromKJarTest {
    @Test
    public void test() {
        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "kjar-test", "1.0");
        KieRepository repo = ks.getRepository();
        repo.removeKieModule(releaseId);
        InternalKieModule kmodule = createKieModule(ks, releaseId);
        repo.addKieModule(kmodule);
        executeSession(ks, releaseId);
    }
}

