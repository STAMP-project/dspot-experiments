/**
 * Copyright 2018 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.plugin.configrepo.contract;


import junit.framework.TestCase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CRPropertyGeneratorTest extends CRBaseTest<CRPropertyGenerator> {
    private final CRPropertyGenerator invalidNoXPath;

    private final CRPropertyGenerator invalidNoSrc;

    private final CRPropertyGenerator invalidNoName;

    private String xpath = "substring-before(//report/data/all/coverage[starts-with(@type,'class')]/@value, '%')";

    private final CRPropertyGenerator propGen;

    public CRPropertyGeneratorTest() {
        propGen = new CRPropertyGenerator("coverage.class", "target/emma/coverage.xml", xpath);
        invalidNoXPath = new CRPropertyGenerator("coverage.class", "target/emma/coverage.xml", null);
        invalidNoSrc = new CRPropertyGenerator("coverage.class", null, xpath);
        invalidNoName = new CRPropertyGenerator(null, "target/emma/coverage.xml", xpath);
    }

    @Test
    public void shouldDeserializeFromAPILikeObject() {
        String json = "{\n" + ((("      \"name\": \"coverage.class\",\n" + "      \"source\": \"target/emma/coverage.xml\",\n") + "      \"xpath\": \"substring-before(//report/data/all/coverage[starts-with(@type,\'class\')]/@value, \'%\')\"\n") + "    }");
        CRPropertyGenerator deserializedValue = gson.fromJson(json, CRPropertyGenerator.class);
        Assert.assertThat(deserializedValue.getName(), Matchers.is("coverage.class"));
        Assert.assertThat(deserializedValue.getSrc(), Matchers.is("target/emma/coverage.xml"));
        Assert.assertThat(deserializedValue.getXpath(), Matchers.is("substring-before(//report/data/all/coverage[starts-with(@type,'class')]/@value, '%')"));
        ErrorCollection errors = deserializedValue.getErrors();
        TestCase.assertTrue(errors.isEmpty());
    }
}

