/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.util;


import java.io.IOException;
import java.util.Properties;
import org.junit.Test;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 11.01.2005
 */
public class PropertiesPersisterTests {
    @Test
    public void propertiesPersister() throws IOException {
        String propString = "code1=message1\ncode2:message2";
        Properties props = loadProperties(propString, false);
        String propCopy = storeProperties(props, null, false);
        loadProperties(propCopy, false);
    }

    @Test
    public void propertiesPersisterWithWhitespace() throws IOException {
        String propString = " code1\t= \tmessage1\n  code2 \t  :\t mess\\\n \t  age2";
        Properties props = loadProperties(propString, false);
        String propCopy = storeProperties(props, null, false);
        loadProperties(propCopy, false);
    }

    @Test
    public void propertiesPersisterWithHeader() throws IOException {
        String propString = "code1=message1\ncode2:message2";
        Properties props = loadProperties(propString, false);
        String propCopy = storeProperties(props, "myHeader", false);
        loadProperties(propCopy, false);
    }

    @Test
    public void propertiesPersisterWithEmptyValue() throws IOException {
        String propString = "code1=message1\ncode2:message2\ncode3=";
        Properties props = loadProperties(propString, false);
        String propCopy = storeProperties(props, null, false);
        loadProperties(propCopy, false);
    }

    @Test
    public void propertiesPersisterWithReader() throws IOException {
        String propString = "code1=message1\ncode2:message2";
        Properties props = loadProperties(propString, true);
        String propCopy = storeProperties(props, null, true);
        loadProperties(propCopy, false);
    }

    @Test
    public void propertiesPersisterWithReaderAndWhitespace() throws IOException {
        String propString = " code1\t= \tmessage1\n  code2 \t  :\t mess\\\n \t  age2";
        Properties props = loadProperties(propString, true);
        String propCopy = storeProperties(props, null, true);
        loadProperties(propCopy, false);
    }

    @Test
    public void propertiesPersisterWithReaderAndHeader() throws IOException {
        String propString = "code1\t=\tmessage1\n  code2 \t  : \t message2";
        Properties props = loadProperties(propString, true);
        String propCopy = storeProperties(props, "myHeader", true);
        loadProperties(propCopy, false);
    }

    @Test
    public void propertiesPersisterWithReaderAndEmptyValue() throws IOException {
        String propString = "code1=message1\ncode2:message2\ncode3=";
        Properties props = loadProperties(propString, true);
        String propCopy = storeProperties(props, null, true);
        loadProperties(propCopy, false);
    }
}

