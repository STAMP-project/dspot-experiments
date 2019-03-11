/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.view;


import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;


/**
 * view.xml related tests.
 */
public class ViewXmlTest {
    @Test
    public void testValidateViewXmls() throws Exception {
        List<File> viewXmlFiles = new LinkedList<File>();
        File ambariViewsDir = new File(".");// ambari-views

        File xsdFile = new File("./target/classes/view.xsd");
        // validate each of the view.xml files under ambari-views
        for (File file : getViewXmlFiles(viewXmlFiles, ambariViewsDir.listFiles())) {
            validateViewXml(file, xsdFile);
        }
    }
}

