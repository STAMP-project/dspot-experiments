/**
 * Copyright 2007-2008, Plutext Pty Ltd.
 *
 *  This file is part of docx4j.
 *
 * docx4j is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
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
package org.docx4j.listnumbering.ind;


import java.io.FileInputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.docx4j.convert.in.FlatOpcXmlImporter;
import org.docx4j.jaxb.Context;
import org.docx4j.jaxb.JaxbValidationEventHandler;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.PPrBase.Ind;
import org.docx4j.xmlPackage.Package;
import org.junit.Assert;
import org.junit.Test;


public class ListNumberIndTest {
    static final String BASE_DIR = "src/test/java/org/docx4j/listnumbering/ind/";

    static final String[] testdocs = new String[]{ "abstract_style_with.xml", "abstract_style_without.xml", "abstract_nostyle_ppr.xml", "abstract_nostyle_noppr.xml", "override_nostyle_ppr.xml" };

    static final String[] expected = new String[]{ "11", "12", "13", null, "23" };

    @Test
    public void testGetInd() throws Exception {
        for (int i = 0; i < (ListNumberIndTest.testdocs.length); i++) {
            // Get the document
            String filename = ListNumberIndTest.testdocs[i];
            System.out.println(("Reading " + filename));
            try {
                JAXBContext jc = Context.jcXmlPackage;
                Unmarshaller u = jc.createUnmarshaller();
                u.setEventHandler(new org.docx4j.jaxb.JaxbValidationEventHandler());
                Object o = u.unmarshal(new StreamSource(new FileInputStream(((ListNumberIndTest.BASE_DIR) + filename))));
                Package wmlPackageEl = null;
                if (o instanceof Package) {
                    // MOXy
                    wmlPackageEl = ((Package) (o));
                } else {
                    wmlPackageEl = ((Package) (((JAXBElement) (o)).getValue()));
                }
                FlatOpcXmlImporter xmlPackage = new FlatOpcXmlImporter(wmlPackageEl);
                WordprocessingMLPackage wmlPackage = ((WordprocessingMLPackage) (xmlPackage.get()));
                // Get the Ind value
                NumberingDefinitionsPart ndp = wmlPackage.getMainDocumentPart().getNumberingDefinitionsPart();
                // Force initialisation of maps
                ndp.getEmulator();
                Ind ind = ndp.getInd("1", "0");
                if (ind != null) {
                    Assert.assertEquals(ind.getLeft().toString(), ListNumberIndTest.expected[i]);
                    System.out.println((("<w:ind w:left='" + (ind.getLeft().toString())) + "\n\n"));
                } else {
                    Assert.assertEquals(ind, ListNumberIndTest.expected[i]);
                    System.out.println("w:ind was null\n\n");
                }
            } catch (Exception exc) {
                exc.printStackTrace();
                throw new RuntimeException(exc);
            }
        }
    }
}

