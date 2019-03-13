/**
 * -
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
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
 * #L%
 */
package org.rapidoid.data;


import org.junit.jupiter.api.Test;
import org.rapidoid.test.TestCommons;


/**
 *
 *
 * @author Nikolche Mihajlovski
 * @since 4.4.0
 */
public class XMLTest extends TestCommons {
    @Test
    public void testXMLSerialization() {
        String xml = XML.stringify(new Person("abc", 123));
        System.out.println(xml);
        isTrue(xml.contains("<name>abc</name>"));
        isTrue(xml.contains("<age>123</age>"));
        Person p = XML.parse(xml, Person.class);
        eq(p.getName(), "abc");
        eq(p.getAge(), 123);
    }
}

