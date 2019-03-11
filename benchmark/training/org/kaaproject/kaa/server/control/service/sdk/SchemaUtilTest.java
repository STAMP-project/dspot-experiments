/**
 * Copyright 2014-2016 CyberVision, Inc.
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
package org.kaaproject.kaa.server.control.service.sdk;


import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.junit.Test;


/**
 *
 *
 * @author Bohdan Khablenko
 * @since v0.9.0
 */
public class SchemaUtilTest {
    private static final String VALID_SCHEMA_RESOURCE = "control/data/testLogSchema.json";

    private static final String INVALID_SCHEMA_RESOURCE = "control/data/KAA-946.avsc";

    @Test
    public void testCompileAvroSchema_success() throws Exception {
        URL resource = this.getClass().getClassLoader().getResource(SchemaUtilTest.VALID_SCHEMA_RESOURCE);
        String avroSchemaBody = IOUtils.toString(resource);
        this.compile(avroSchemaBody);
    }

    @Test(expected = Exception.class)
    public void testCompileAvroSchema_failure() throws Exception {
        URL resource = this.getClass().getClassLoader().getResource(SchemaUtilTest.INVALID_SCHEMA_RESOURCE);
        String avroSchemaBody = IOUtils.toString(resource);
        this.compile(avroSchemaBody);
    }
}

