/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.maven;


import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.function.Function;
import org.apache.camel.component.salesforce.api.dto.SObjectDescription;
import org.apache.camel.component.salesforce.internal.client.RestClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class CamelSalesforceMojoOutputTest {
    private static final String TEST_CALCULATED_FORMULA_FILE = "complex_calculated_formula.json";

    private static final String TEST_CASE_FILE = "case.json";

    @Parameterized.Parameter(1)
    public SObjectDescription description;

    @Parameterized.Parameter(4)
    public Function<String, String> fileNameAdapter = Function.identity();

    @Parameterized.Parameter(0)
    public String json;

    @Parameterized.Parameter(3)
    public GenerateMojo mojo;

    @Parameterized.Parameter(2)
    public Set<String> sources;

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testProcessDescription() throws Exception {
        final File pkgDir = temp.newFolder();
        final GenerateMojo.GeneratorUtility utility = mojo.new GeneratorUtility();
        final RestClient client = CamelSalesforceMojoOutputTest.mockRestClient();
        mojo.descriptions = new ObjectDescriptions(client, 0, null, null, null, null, mojo.getLog());
        mojo.processDescription(pkgDir, description, utility);
        for (final String source : sources) {
            final String expected = fileNameAdapter.apply(source);
            final File generatedFile = new File(pkgDir, source);
            final String generatedContent = FileUtils.readFileToString(generatedFile, StandardCharsets.UTF_8);
            final String expectedContent = IOUtils.toString(CamelSalesforceMojoOutputTest.class.getResource(("/generated/" + expected)), StandardCharsets.UTF_8);
            Assert.assertEquals(((("Generated source file in " + source) + " must be equal to the one present in test/resources/") + expected), expectedContent, generatedContent);
        }
    }
}

