/**
 * ================================================================================
 */
/**
 * Copyright (c) 2011, David Yu
 */
/**
 * All rights reserved.
 */
/**
 * --------------------------------------------------------------------------------
 */
/**
 * Redistribution and use in source and binary forms, with or without
 */
/**
 * modification, are permitted provided that the following conditions are met:
 */
/**
 * 1. Redistributions of source code must retain the above copyright notice,
 */
/**
 * this list of conditions and the following disclaimer.
 */
/**
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 */
/**
 * this list of conditions and the following disclaimer in the documentation
 */
/**
 * and/or other materials provided with the distribution.
 */
/**
 * 3. Neither the name of protostuff nor the names of its contributors may be used
 */
/**
 * to endorse or promote products derived from this software without
 */
/**
 * specific prior written permission.
 */
/**
 *
 */
/**
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 */
/**
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 */
/**
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 */
/**
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 */
/**
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 */
/**
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 */
/**
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 */
/**
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 */
/**
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 */
/**
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 */
/**
 * POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * ================================================================================
 */
package io.protostuff.parser;


import java.io.File;
import junit.framework.TestCase;


/**
 * Test for references configured via options and annotations.
 *
 * @author David Yu
 * @unknown Dec 22, 2011
 */
public class ConfiguredReferenceTest extends TestCase {
    public void testIt() throws Exception {
        File f = ProtoParserTest.getFile("io/protostuff/parser/test_option_annotation_reference.proto");
        TestCase.assertTrue(f.exists());
        Proto proto = new Proto(f);
        ProtoUtil.loadFrom(f, proto);
        Message car = proto.getMessage("Car");
        TestCase.assertNotNull(car);
        EnumGroup condition = car.getNestedEnumGroup("Condition");
        TestCase.assertNotNull(condition);
        Message part = car.getNestedMessage("Part");
        TestCase.assertNotNull(part);
        Message tire = part.getNestedMessage("Tire");
        TestCase.assertNotNull(tire);
        Message person = ((Message) (proto.findReference("Person", proto.getPackageName())));
        TestCase.assertNotNull(person);
        Message listRequest = person.getNestedMessage("ListRequest");
        TestCase.assertNotNull(listRequest);
        EnumGroup importedGender = ((EnumGroup) (proto.findReference("Person.Gender", proto.getPackageName())));
        TestCase.assertNotNull(importedGender);
        EnumGroup localGender = proto.getEnumGroup("Gender");
        TestCase.assertNotNull(localGender);
        ConfiguredReferenceTest.verifyCarAnnotations(car, part, tire, localGender, importedGender, condition, person, listRequest);
        ConfiguredReferenceTest.verifyCarOptions(car, part, tire, localGender, importedGender, condition, person, listRequest);
        ConfiguredReferenceTest.verifyPart(part);
        ConfiguredReferenceTest.verifyCondition(condition);
        Service carService = proto.getService("CarService");
        TestCase.assertNotNull(carService);
        ConfiguredReferenceTest.verifyCarService(carService, car, condition);
    }
}

