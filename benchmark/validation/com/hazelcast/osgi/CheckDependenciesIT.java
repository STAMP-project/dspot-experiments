/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.osgi;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.jar.Manifest;
import org.apache.felix.utils.manifest.Clause;
import org.apache.felix.utils.manifest.Parser;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class CheckDependenciesIT extends HazelcastTestSupport {
    private static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";

    private static final String[] WHITELIST_PREFIXES = new String[]{ // everything from the Java package is OK - it's part of the Java SE platform
    "java.", // with the "javax" package we have to be more specific - do not use just "javax."
    // as it contains e.g. javax.servlet which is not part of the SE platform!
    "javax.crypto", "javax.management", "javax.net.ssl", "javax.script", "javax.security.auth", "javax.transaction.xa", "javax.xml", // these 2 XML-related packages are part of the platform since Java SE 6
    "org.xml.sax", "org.w3c.dom" };

    @Test
    public void testNoMandatoryDependencyDeclared() throws IOException, InterruptedException {
        Manifest manifest = getHazelcastManifest();
        String packages = manifest.getMainAttributes().getValue("Import-Package");
        Clause[] clauses = Parser.parseHeader(packages);
        for (Clause clause : clauses) {
            String name = clause.getName();
            String resolution = clause.getDirective("resolution");
            checkImport(name, resolution);
        }
    }
}

