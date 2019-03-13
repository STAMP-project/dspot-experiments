/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.oxm.jibx;


import java.io.ByteArrayInputStream;
import javax.xml.transform.stream.StreamSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.oxm.AbstractUnmarshallerTests;


/**
 * NOTE: These tests fail under Eclipse/IDEA because JiBX binding does
 * not occur by default. The Gradle build should succeed, however.
 *
 * @author Arjen Poutsma
 * @author Sam Brannen
 */
@Deprecated
public class JibxUnmarshallerTests extends AbstractUnmarshallerTests<JibxMarshaller> {
    protected static final String INPUT_STRING_WITH_SPECIAL_CHARACTERS = "<tns:flights xmlns:tns=\"http://samples.springframework.org/flight\">" + "<tns:flight><tns:airline>Air Libert\u00e9</tns:airline><tns:number>42</tns:number></tns:flight></tns:flights>";

    @Test
    @Override
    public void unmarshalPartialStaxSourceXmlStreamReader() throws Exception {
        // JiBX does not support reading XML fragments, hence the override here
    }

    @Test
    public void unmarshalStreamSourceInputStreamUsingNonDefaultEncoding() throws Exception {
        String encoding = "ISO-8859-1";
        unmarshaller.setEncoding(encoding);
        StreamSource source = new StreamSource(new ByteArrayInputStream(JibxUnmarshallerTests.INPUT_STRING_WITH_SPECIAL_CHARACTERS.getBytes(encoding)));
        Object flights = unmarshaller.unmarshal(source);
        testFlights(flights);
        FlightType flight = ((Flights) (flights)).getFlight(0);
        Assert.assertEquals("Airline is invalid", "Air Libert\u00e9", flight.getAirline());
    }
}

