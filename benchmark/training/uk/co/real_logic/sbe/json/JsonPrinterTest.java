/**
 * Copyright 2013-2019 Real Logic Ltd.
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
package uk.co.real_logic.sbe.json;


import java.nio.ByteBuffer;
import junit.framework.TestCase;
import org.junit.Test;
import uk.co.real_logic.sbe.EncodedCarTestBase;
import uk.co.real_logic.sbe.ir.Ir;


public class JsonPrinterTest extends EncodedCarTestBase {
    private static final int SCHEMA_BUFFER_CAPACITY = 16 * 1024;

    private static final int MSG_BUFFER_CAPACITY = 4 * 1024;

    @Test
    public void exampleMessagePrintedAsJson() throws Exception {
        final ByteBuffer encodedSchemaBuffer = ByteBuffer.allocate(JsonPrinterTest.SCHEMA_BUFFER_CAPACITY);
        JsonPrinterTest.encodeSchema(encodedSchemaBuffer);
        final ByteBuffer encodedMsgBuffer = ByteBuffer.allocate(JsonPrinterTest.MSG_BUFFER_CAPACITY);
        EncodedCarTestBase.encodeTestMessage(encodedMsgBuffer);
        encodedSchemaBuffer.flip();
        final Ir ir = JsonPrinterTest.decodeIr(encodedSchemaBuffer);
        final JsonPrinter printer = new JsonPrinter(ir);
        final String result = printer.print(encodedMsgBuffer);
        TestCase.assertEquals(("{\n" + (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("    \"serialNumber\": 1234,\n" + "    \"modelYear\": 2013,\n") + "    \"available\": \"T\",\n") + "    \"code\": \"A\",\n") + "    \"someNumbers\": [0, 1, 2, 3, 4],\n") + "    \"vehicleCode\": \"abcdef\",\n") + "    \"extras\": { \"sunRoof\": false, \"sportsPack\": true, \"cruiseControl\": true },\n") + "    \"engine\": \n") + "    {\n") + "        \"capacity\": 2000,\n") + "        \"numCylinders\": 4,\n") + "        \"maxRpm\": 9000,\n") + "        \"manufacturerCode\": \"123\",\n") + "        \"fuel\": \"Petrol\"\n") + "    },\n") + "    \"fuelFigures\": [\n") + "    {\n") + "        \"speed\": 30,\n") + "        \"mpg\": 35.9\n") + "    },\n") + "    {\n") + "        \"speed\": 55,\n") + "        \"mpg\": 49.0\n") + "    },\n") + "    {\n") + "        \"speed\": 75,\n") + "        \"mpg\": 40.0\n") + "    }],\n") + "    \"performanceFigures\": [\n") + "    {\n") + "        \"octaneRating\": 95,\n") + "        \"acceleration\": [\n") + "        {\n") + "            \"mph\": 30,\n") + "            \"seconds\": 4.0\n") + "        },\n") + "        {\n") + "            \"mph\": 60,\n") + "            \"seconds\": 7.5\n") + "        },\n") + "        {\n") + "            \"mph\": 100,\n") + "            \"seconds\": 12.2\n") + "        }]\n") + "    },\n") + "    {\n") + "        \"octaneRating\": 99,\n") + "        \"acceleration\": [\n") + "        {\n") + "            \"mph\": 30,\n") + "            \"seconds\": 3.8\n") + "        },\n") + "        {\n") + "            \"mph\": 60,\n") + "            \"seconds\": 7.1\n") + "        },\n") + "        {\n") + "            \"mph\": 100,\n") + "            \"seconds\": 11.8\n") + "        }]\n") + "    }],\n") + "    \"manufacturer\": \"Honda\",\n") + "    \"model\": \"Civic VTi\",\n") + "    \"activationCode\": \"\"\n") + "}")), result);
    }
}

