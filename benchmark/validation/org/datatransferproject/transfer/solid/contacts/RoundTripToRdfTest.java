/**
 * Copyright 2018 The Data Transfer Project Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datatransferproject.transfer.solid.contacts;


import ezvcard.Ezvcard;
import ezvcard.VCard;
import java.io.StringWriter;
import org.apache.jena.rdf.model.Model;
import org.junit.Test;


public class RoundTripToRdfTest {
    @Test
    public void testFromRDF() {
        VCard vcard = SolidContactsExport.parsePerson(getPersonResource(TestData.RDF_TEST_DATA1));
        assertWithMessage("Formatted Name is correct").that(vcard.getFormattedName().getValue()).isEqualTo("Cool Kid 1");
        assertWithMessage("Note is correct").that(vcard.getNotes().get(0).getValue()).isEqualTo("This is a note for Cool Kid 1");
        assertWithMessage("One email found").that(vcard.getEmails()).hasSize(1);
        assertWithMessage("email is correct").that(vcard.getEmails().get(0).getValue()).isEqualTo("a@b.com");
    }

    @Test
    public void testFromVcard() {
        for (VCard vcardInput : Ezvcard.parse(TestData.VCARD_TEXT).all()) {
            Model personModel = SolidContactsImport.getPersonModel(vcardInput);
            StringWriter stringWriter = new StringWriter();
            personModel.write(stringWriter, "TURTLE");
            String rdf = stringWriter.toString();
            VCard vcardOutput = SolidContactsExport.parsePerson(getPersonResource(rdf));
            checkEquality(vcardInput, vcardOutput);
        }
    }
}

