/**
 * ========================================================================
 */
/**
 * Copyright 2007-2010 David Yu dyuproject@gmail.com
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 * ========================================================================
 */
package io.protostuff.parser;


import EnumGroup.Value;
import Service.RpcMethod;
import java.io.File;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for annotations on messages, enums, fields, services, rpc methods, extensions
 *
 * @author David Yu
 * @unknown Dec 30, 2010
 */
public class AnnotationTest {
    @Test
    public void testIt() throws Exception {
        File f = ProtoParserTest.getFile("io/protostuff/parser/test_annotations.proto");
        Assert.assertTrue(f.exists());
        Proto proto = new Proto(f);
        ProtoUtil.loadFrom(f, proto);
        Message person = proto.getMessage("Person");
        Assert.assertNotNull(person);
        Assert.assertEquals("[ doc1]", person.getDocs().toString());
        Annotation defaultPerson = person.getAnnotation("DefaultPerson");
        Assert.assertNotNull(defaultPerson);
        Assert.assertEquals("Anonymous Coward", defaultPerson.getValue("name"));
        Field<?> age = person.getField("age");
        Assert.assertNotNull(age);
        Assert.assertEquals("[ doc2]", age.getDocs().toString());
        Annotation defaultAge = age.getAnnotation("DefaultAge");
        Assert.assertNotNull(defaultAge);
        Assert.assertTrue(defaultAge.getParams().isEmpty());
        EnumGroup gender = person.getNestedEnumGroup("Gender");
        Assert.assertNotNull(gender);
        Assert.assertEquals("[ doc3]", gender.getDocs().toString());
        Annotation defaultGender = gender.getAnnotation("DefaultGender");
        Assert.assertEquals("MALE", defaultGender.getValue("value"));
        EnumGroup.Value male = gender.getValue(0);
        Assert.assertNotNull(male);
        Assert.assertEquals("[ doc4]", male.getDocs().toString());
        Annotation maleA = male.getAnnotation("Alias");
        Assert.assertNotNull(maleA);
        Assert.assertEquals("m", maleA.getValue("value"));
        Assert.assertTrue((person == (maleA.getValue("type"))));
        EnumGroup.Value female = gender.getValue(1);
        Assert.assertNotNull(female);
        Assert.assertEquals("[ doc5]", female.getDocs().toString());
        Annotation femaleA = female.getAnnotation("Alias");
        Assert.assertNotNull(femaleA);
        Assert.assertEquals("f", femaleA.getValue("value"));
        Assert.assertTrue((person == (femaleA.getValue("type"))));
        Message listRequest = person.getNestedMessage("ListRequest");
        Assert.assertNotNull(listRequest);
        Assert.assertEquals("[ doc6]", listRequest.getDocs().toString());
        Annotation nestedMessageAnnotation = listRequest.getAnnotation("NestedMessageAnnotation");
        Assert.assertNotNull(nestedMessageAnnotation);
        Assert.assertTrue(nestedMessageAnnotation.getParams().isEmpty());
        Message response = listRequest.getNestedMessage("Response");
        Assert.assertNotNull(response);
        Assert.assertEquals("[ doc7]", response.getDocs().toString());
        Annotation deeperMessageAnnotation = response.getAnnotation("DeeperMessageAnnotation");
        Assert.assertNotNull(deeperMessageAnnotation);
        Assert.assertTrue(deeperMessageAnnotation.getParams().isEmpty());
        Field<?> personField = response.getField("person");
        Assert.assertNotNull(personField);
        Assert.assertEquals("[ doc8]", personField.getDocs().toString());
        Annotation deeperMessageFieldAnnotation = personField.getAnnotation("DeeperMessageFieldAnnotation");
        Assert.assertNotNull(deeperMessageFieldAnnotation);
        Assert.assertTrue(((deeperMessageFieldAnnotation.getParams().size()) == 2));
        Assert.assertEquals(false, deeperMessageFieldAnnotation.getValue("nullable"));
        Assert.assertEquals(Float.valueOf(1.1F), deeperMessageFieldAnnotation.getValue("version"));
        Field<?> keyField = response.getField("key");
        Assert.assertNotNull(keyField);
        Assert.assertEquals("[ doc9]", keyField.getDocs().toString());
        Annotation testNested = keyField.getAnnotation("TestNested");
        Assert.assertNotNull(testNested);
        Assert.assertTrue((person == (testNested.getValue("type"))));
        Assert.assertTrue((gender == (testNested.getValue("g"))));
        Collection<Extension> extensions = proto.getExtensions();
        Assert.assertTrue(((extensions.size()) == 1));
        Extension extendPerson = extensions.iterator().next();
        Assert.assertNotNull(extendPerson);
        Assert.assertEquals("[ doc10]", extendPerson.getDocs().toString());
        Annotation personExtras = extendPerson.getAnnotation("PersonExtras");
        Assert.assertNotNull(personExtras);
        Assert.assertTrue(personExtras.getParams().isEmpty());
        Field<?> country = extendPerson.getField("country");
        Assert.assertNotNull(country);
        Assert.assertEquals("[ doc11]", country.getDocs().toString());
        Field<?> k = extendPerson.getField("key");
        Assert.assertNotNull(k);
        Assert.assertEquals("[ doc12]", k.getDocs().toString());
        Annotation validate = country.getAnnotation("Validate");
        Assert.assertNotNull(validate);
        Assert.assertTrue(validate.getParams().isEmpty());
        Service personService = proto.getService("PersonService");
        Assert.assertNotNull(personService);
        Assert.assertEquals("[ doc13]", personService.getDocs().toString());
        Assert.assertTrue(((personService.getAnnotationMap().size()) == 2));
        Annotation someServiceAnnotation = personService.getAnnotation("SomeServiceAnnotation");
        Annotation anotherServiceAnnotation = personService.getAnnotation("AnotherServiceAnnotation");
        Assert.assertTrue(((someServiceAnnotation != null) && (someServiceAnnotation.getParams().isEmpty())));
        Assert.assertTrue(((anotherServiceAnnotation != null) && (anotherServiceAnnotation.getParams().isEmpty())));
        Service.RpcMethod put = personService.getRpcMethod("Put");
        Assert.assertNotNull(put);
        Assert.assertEquals("[ doc14]", put.getDocs().toString());
        Annotation authRequired = put.getAnnotation("AuthRequired");
        Assert.assertNotNull(authRequired);
        Assert.assertTrue(((authRequired.getParams().size()) == 1));
        Assert.assertEquals("admin", authRequired.getValue("role"));
        Service.RpcMethod list = personService.getRpcMethod("List");
        Assert.assertNotNull(list);
        Assert.assertEquals("[ doc15]", list.getDocs().toString());
        Annotation testRpc = list.getAnnotation("TestRpc");
        Assert.assertNotNull(testRpc);
        Assert.assertTrue((person == (testRpc.getValue("type"))));
        Assert.assertTrue((gender == (testRpc.getValue("g"))));
    }
}

