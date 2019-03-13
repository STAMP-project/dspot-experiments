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
package org.apache.camel.dataformat.avro;


import org.apache.camel.CamelException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class AvroMarshalAndUnmarshallTest extends CamelTestSupport {
    @Test
    public void testMarshalAndUnmarshalWithDataFormat() throws Exception {
        marshalAndUnmarshal("direct:in", "direct:back");
    }

    @Test
    public void testMarshalAndUnmarshalWithDSL1() throws Exception {
        marshalAndUnmarshal("direct:marshal", "direct:unmarshalA");
    }

    @Test
    public void testMarshalAndUnmarshalWithDSL2() throws Exception {
        marshalAndUnmarshal("direct:marshal", "direct:unmarshalB");
    }

    @Test
    public void testMarshalAndUnmarshalWithDSL3() throws Exception {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:unmarshalC").unmarshal().avro(new CamelException("wrong schema")).to("mock:reverse");
                }
            });
            fail("Expect the exception here");
        } catch (Exception ex) {
            // expected
        }
    }
}

