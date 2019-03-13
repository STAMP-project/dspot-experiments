/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.restassured.test.osgi;


import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;


/**
 * This test aims to prove that xml-path is available as a valid OSGi bundle.
 */
@RunWith(PaxExam.class)
public class XmlPathOSGiITest {
    @Test
    public void getUUIDParsesAStringResultToUUID() throws Exception {
        final String UUID_XML = "<some>\n" + (("  <thing id=\"1\">db24eeeb-7fe5-41d3-8f06-986b793ecc91</thing>\n" + "  <thing id=\"2\">d69ded28-d75c-460f-9cbe-1412c60ed4cc</thing>\n") + "</some>");
        final UUID uuid = from(UUID_XML).getUUID("some.thing[0]");
        Assert.assertThat(uuid, Matchers.equalTo(UUID.fromString("db24eeeb-7fe5-41d3-8f06-986b793ecc91")));
    }
}

