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


import ResourceType.DRL;
import java.io.File;
import java.util.Collection;
import org.junit.Test;
import org.kie.api.definition.KiePackage;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;


public class SerializedPackageMergeTwoSteps1Test {
    public static final String[] BINPKG = new String[]{ ((System.getProperty("java.io.tmpdir")) + (File.separator)) + "SerializedPackageMergeTwoSteps_1.bin", ((System.getProperty("java.io.tmpdir")) + (File.separator)) + "SerializedPackageMergeTwoSteps_2.bin" };

    @Test
    public void testBuildAndSerializePackagesInTwoSteps1() {
        String str1 = "package com.sample.packageA\n" + (((((("import org.drools.compiler.Person\n" + "global java.util.List list\n") + "rule R1 when\n") + "  $p : Person( name == \"John\" )\n") + "then\n") + "  list.add($p);") + "end\n");
        String str2 = "package com.sample.packageB\n" + (((((("import org.drools.compiler.Person\n" + "global java.util.List list\n") + "rule R2 when\n") + "  $p : Person()\n") + "then\n") + "  list.add($p);") + "end\n");
        // Create 2 knowledgePackages separately
        KnowledgeBuilder builder1 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder1.add(ResourceFactory.newByteArrayResource(str1.getBytes()), DRL);
        Collection<KiePackage> knowledgePackages1 = builder1.getKnowledgePackages();
        // serialize the first package to a file
        writeKnowledgePackage(knowledgePackages1, SerializedPackageMergeTwoSteps1Test.BINPKG[0]);
        KnowledgeBuilder builder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder2.add(ResourceFactory.newByteArrayResource(str2.getBytes()), DRL);
        Collection<KiePackage> knowledgePackages2 = builder2.getKnowledgePackages();
        // serialize the second package to a file
        writeKnowledgePackage(knowledgePackages2, SerializedPackageMergeTwoSteps1Test.BINPKG[1]);
    }
}

