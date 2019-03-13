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
package org.apache.camel.language;


import java.io.File;
import org.apache.camel.LanguageTestSupport;
import org.junit.Test;


/**
 * Unit test for File Language.
 */
public class FileLanguageExtSingleTest extends LanguageTestSupport {
    private File file;

    @Test
    public void testFileNoSingleExt() throws Exception {
        assertExpression("${file:name}", (("test" + (File.separator)) + "bye.def.txt"));
        assertExpression("${file:name.noext}", (("test" + (File.separator)) + "bye"));
        assertExpression("${file:name.noext.single}", (("test" + (File.separator)) + "bye.def"));
        assertExpression("${file:name.ext}", "def.txt");
        assertExpression("${file:name.ext.single}", "txt");
        assertExpression("${file:onlyname.noext}", "bye");
        assertExpression("${file:onlyname.noext.single}", "bye.def");
    }
}

