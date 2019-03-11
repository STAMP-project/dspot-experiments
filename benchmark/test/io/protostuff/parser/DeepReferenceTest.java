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


import java.io.File;
import junit.framework.TestCase;


/**
 * Test for deep references in the message's fields
 *
 * @author David Yu
 * @unknown Jun 20, 2010
 */
public class DeepReferenceTest extends TestCase {
    public void testIt() throws Exception {
        File f = ProtoParserTest.getFile("test_deep_reference.proto");
        TestCase.assertTrue(f.exists());
        Proto proto = new Proto(f);
        ProtoUtil.loadFrom(f, proto);
        TestCase.assertTrue(((proto.getImportedProtos().size()) == 2));
        Proto importedProto = proto.getImportedProto(ProtoParserTest.getFile("test_imported_inner.proto"));
        TestCase.assertNotNull(importedProto);
        Proto jpImportedProto = proto.getImportedProto(ProtoParserTest.getFile("test_java_package_imported_inner.proto"));
        TestCase.assertNotNull(jpImportedProto);
        Message request = proto.getMessage("Request");
        TestCase.assertNotNull(request);
        Message requestInner = request.getNestedMessage("Inner");
        TestCase.assertNotNull(requestInner);
        Message requestDeeper = requestInner.getNestedMessage("Deeper");
        TestCase.assertNotNull(requestDeeper);
        Message response = proto.getMessage("Response");
        TestCase.assertNotNull(response);
        Message responseInner = response.getNestedMessage("Inner");
        TestCase.assertNotNull(responseInner);
        Message responseDeeper = responseInner.getNestedMessage("Deeper");
        TestCase.assertNotNull(responseDeeper);
        Message foo = importedProto.getMessage("Foo");
        TestCase.assertNotNull(foo);
        Message fooInner = foo.getNestedMessage("Inner");
        TestCase.assertNotNull(fooInner);
        Message fooDeeper = fooInner.getNestedMessage("Deeper");
        TestCase.assertNotNull(fooDeeper);
        Message bar = importedProto.getMessage("Bar");
        TestCase.assertNotNull(bar);
        Message barInner = bar.getNestedMessage("Inner");
        TestCase.assertNotNull(barInner);
        Message barDeeper = barInner.getNestedMessage("Deeper");
        TestCase.assertNotNull(barDeeper);
        Message jpFoo = jpImportedProto.getMessage("JPFoo");
        TestCase.assertNotNull(jpFoo);
        Message jpFooInner = jpFoo.getNestedMessage("Inner");
        TestCase.assertNotNull(jpFooInner);
        Message jpFooDeeper = jpFooInner.getNestedMessage("Deeper");
        TestCase.assertNotNull(jpFooDeeper);
        Message jpBar = jpImportedProto.getMessage("JPBar");
        TestCase.assertNotNull(jpBar);
        Message jpBarInner = jpBar.getNestedMessage("Inner");
        TestCase.assertNotNull(jpBarInner);
        Message jpBarDeeper = jpBarInner.getNestedMessage("Deeper");
        TestCase.assertNotNull(jpBarDeeper);
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("foo1", request)) == foo));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("foo2", request)) == foo));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("foo3", request)) == jpFoo));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner1", request)) == fooInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner2", request)) == fooInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner3", request)) == jpFooInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper1", request)) == fooDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper2", request)) == fooDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper3", request)) == jpFooDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("foo1", requestInner)) == foo));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("foo2", requestInner)) == foo));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("foo3", requestInner)) == jpFoo));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner1", requestInner)) == fooInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner2", requestInner)) == fooInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner3", requestInner)) == jpFooInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper1", requestInner)) == fooDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper2", requestInner)) == fooDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper3", requestInner)) == jpFooDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("foo1", requestDeeper)) == foo));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("foo2", requestDeeper)) == foo));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("foo3", requestDeeper)) == jpFoo));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner1", requestDeeper)) == fooInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner2", requestDeeper)) == fooInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner3", requestDeeper)) == jpFooInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper1", requestDeeper)) == fooDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper2", requestDeeper)) == fooDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper3", requestDeeper)) == jpFooDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("bar1", response)) == bar));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("bar2", response)) == bar));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("bar3", response)) == jpBar));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner1", response)) == barInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner2", response)) == barInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner3", response)) == jpBarInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper1", response)) == barDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper2", response)) == barDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper3", response)) == jpBarDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("bar1", responseInner)) == bar));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("bar2", responseInner)) == bar));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("bar3", responseInner)) == jpBar));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner1", responseInner)) == barInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner2", responseInner)) == barInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner3", responseInner)) == jpBarInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper1", responseInner)) == barDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper2", responseInner)) == barDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper3", responseInner)) == jpBarDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("bar1", responseDeeper)) == bar));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("bar2", responseDeeper)) == bar));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("bar3", responseDeeper)) == jpBar));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner1", responseDeeper)) == barInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner2", responseDeeper)) == barInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("inner3", responseDeeper)) == jpBarInner));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper1", responseDeeper)) == barDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper2", responseDeeper)) == barDeeper));
        TestCase.assertTrue(((DeepReferenceTest.getMessageField("deeper3", responseDeeper)) == jpBarDeeper));
    }
}

