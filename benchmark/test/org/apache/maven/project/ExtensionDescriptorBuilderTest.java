/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.project;


import java.util.Arrays;
import junit.framework.TestCase;


/**
 * Tests {@link ExtensionDescriptorBuilder}.
 *
 * @author Benjamin Bentmann
 */
public class ExtensionDescriptorBuilderTest extends TestCase {
    private ExtensionDescriptorBuilder builder;

    public void testEmptyDescriptor() throws Exception {
        String xml = "<extension></extension>";
        ExtensionDescriptor ed = builder.build(toStream(xml));
        TestCase.assertNotNull(ed);
        TestCase.assertNotNull(ed.getExportedPackages());
        TestCase.assertTrue(ed.getExportedPackages().isEmpty());
        TestCase.assertNotNull(ed.getExportedArtifacts());
        TestCase.assertTrue(ed.getExportedArtifacts().isEmpty());
    }

    public void testCompleteDescriptor() throws Exception {
        String xml = "<?xml version='1.0' encoding='UTF-8'?>" + ((((((((((("<extension>" + "<exportedPackages>") + "<exportedPackage>a</exportedPackage>") + "<exportedPackage>b</exportedPackage>") + "<exportedPackage>c</exportedPackage>") + "</exportedPackages>") + "<exportedArtifacts>") + "<exportedArtifact>x</exportedArtifact>") + "<exportedArtifact>y</exportedArtifact>") + "<exportedArtifact> z </exportedArtifact>") + "</exportedArtifacts>") + "</extension>");
        ExtensionDescriptor ed = builder.build(toStream(xml));
        TestCase.assertNotNull(ed);
        TestCase.assertEquals(Arrays.asList("a", "b", "c"), ed.getExportedPackages());
        TestCase.assertEquals(Arrays.asList("x", "y", "z"), ed.getExportedArtifacts());
    }
}

