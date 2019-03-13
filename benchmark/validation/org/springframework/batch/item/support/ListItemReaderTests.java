/**
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.item.support;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;


public class ListItemReaderTests extends TestCase {
    ListItemReader<String> reader = new ListItemReader(Arrays.asList(new String[]{ "a", "b", "c" }));

    public void testNext() throws Exception {
        TestCase.assertEquals("a", reader.read());
        TestCase.assertEquals("b", reader.read());
        TestCase.assertEquals("c", reader.read());
        TestCase.assertEquals(null, reader.read());
    }

    public void testChangeList() throws Exception {
        List<String> list = new ArrayList<>(Arrays.asList(new String[]{ "a", "b", "c" }));
        reader = new ListItemReader(list);
        TestCase.assertEquals("a", reader.read());
        list.clear();
        TestCase.assertEquals(0, list.size());
        TestCase.assertEquals("b", reader.read());
    }
}

