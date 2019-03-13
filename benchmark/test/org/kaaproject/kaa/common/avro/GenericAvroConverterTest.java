/**
 * Copyright 2014-2016 CyberVision, Inc.
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
package org.kaaproject.kaa.common.avro;


import BasicEndpointProfile.SCHEMA;
import org.junit.Test;


public class GenericAvroConverterTest {
    @Test(expected = RuntimeException.class)
    public void testStaticMethod() throws Exception {
        GenericAvroConverter.toJson("Expected to fail with this invalid input".getBytes(), SCHEMA..toString());
    }
}

