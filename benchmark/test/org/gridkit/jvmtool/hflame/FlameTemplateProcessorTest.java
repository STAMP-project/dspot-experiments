/**
 * Copyright 2018 Alexey Ragozin
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
package org.gridkit.jvmtool.hflame;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import org.junit.Test;
import org.w3c.dom.Document;


public class FlameTemplateProcessorTest {
    @Test
    public void simple_template_smoke_test() throws IOException {
        Document doc = loadXml("flame_template.html");
        FlameTemplateProcessor ftp = new FlameTemplateProcessor(doc);
        ftp.setDataSet("fg1", loadDataSet("hz1_dump.sjk"));
        StringWriter sw = new StringWriter();
        ftp.generate(sw);
        System.out.println(sw);
        OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream("target/test.html"), "UTF8");
        fw.append(sw.toString());
        fw.close();
    }
}

