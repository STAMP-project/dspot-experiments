/**
 * Protocol Buffers - Google's data interchange format
 */
/**
 * Copyright 2008 Google Inc.  All rights reserved.
 */
/**
 * https://developers.google.com/protocol-buffers/
 */
/**
 *
 */
/**
 * Redistribution and use in source and binary forms, with or without
 */
/**
 * modification, are permitted provided that the following conditions are
 */
/**
 * met:
 */
/**
 *
 */
/**
 * * Redistributions of source code must retain the above copyright
 */
/**
 * notice, this list of conditions and the following disclaimer.
 */
/**
 * * Redistributions in binary form must reproduce the above
 */
/**
 * copyright notice, this list of conditions and the following disclaimer
 */
/**
 * in the documentation and/or other materials provided with the
 */
/**
 * distribution.
 */
/**
 * * Neither the name of Google Inc. nor the names of its
 */
/**
 * contributors may be used to endorse or promote products derived from
 */
/**
 * this software without specific prior written permission.
 */
/**
 *
 */
/**
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 */
/**
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 */
/**
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 */
/**
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 */
/**
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 */
/**
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 */
/**
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 */
/**
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 */
/**
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 */
/**
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 */
/**
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.google.protobuf;


import TestAllTypes.NestedMessage;
import TextFormatParseInfoTree.Builder;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import junit.framework.TestCase;
import protobuf_unittest.UnittestProto.TestAllTypes;


/**
 * Test @{link TextFormatParseInfoTree}.
 */
public class TextFormatParseInfoTreeTest extends TestCase {
    private static final Descriptor DESCRIPTOR = TestAllTypes.getDescriptor();

    private static final FieldDescriptor OPTIONAL_INT32 = TextFormatParseInfoTreeTest.DESCRIPTOR.findFieldByName("optional_int32");

    private static final FieldDescriptor OPTIONAL_BOOLEAN = TextFormatParseInfoTreeTest.DESCRIPTOR.findFieldByName("optional_boolean");

    private static final FieldDescriptor REPEATED_INT32 = TextFormatParseInfoTreeTest.DESCRIPTOR.findFieldByName("repeated_int32");

    private static final FieldDescriptor OPTIONAL_NESTED_MESSAGE = TextFormatParseInfoTreeTest.DESCRIPTOR.findFieldByName("optional_nested_message");

    private static final FieldDescriptor REPEATED_NESTED_MESSAGE = TextFormatParseInfoTreeTest.DESCRIPTOR.findFieldByName("repeated_nested_message");

    private static final FieldDescriptor FIELD_BB = NestedMessage.getDescriptor().findFieldByName("bb");

    private static final TextFormatParseLocation LOC0 = TextFormatParseLocation.create(1, 2);

    private static final TextFormatParseLocation LOC1 = TextFormatParseLocation.create(2, 3);

    private Builder rootBuilder;

    public void testBuildEmptyParseTree() {
        TextFormatParseInfoTree tree = rootBuilder.build();
        TestCase.assertTrue(tree.getLocations(null).isEmpty());
    }

    public void testGetLocationReturnsSingleLocation() {
        rootBuilder.setLocation(TextFormatParseInfoTreeTest.OPTIONAL_INT32, TextFormatParseInfoTreeTest.LOC0);
        TextFormatParseInfoTree root = rootBuilder.build();
        TestCase.assertEquals(TextFormatParseInfoTreeTest.LOC0, root.getLocation(TextFormatParseInfoTreeTest.OPTIONAL_INT32, 0));
        TestCase.assertEquals(1, root.getLocations(TextFormatParseInfoTreeTest.OPTIONAL_INT32).size());
    }

    public void testGetLocationsReturnsNoParseLocationsForUnknownField() {
        TestCase.assertTrue(rootBuilder.build().getLocations(TextFormatParseInfoTreeTest.OPTIONAL_INT32).isEmpty());
        rootBuilder.setLocation(TextFormatParseInfoTreeTest.OPTIONAL_BOOLEAN, TextFormatParseInfoTreeTest.LOC0);
        TextFormatParseInfoTree root = rootBuilder.build();
        TestCase.assertTrue(root.getLocations(TextFormatParseInfoTreeTest.OPTIONAL_INT32).isEmpty());
        TestCase.assertEquals(TextFormatParseInfoTreeTest.LOC0, root.getLocations(TextFormatParseInfoTreeTest.OPTIONAL_BOOLEAN).get(0));
    }

    public void testGetLocationThrowsIllegalArgumentExceptionForUnknownField() {
        rootBuilder.setLocation(TextFormatParseInfoTreeTest.REPEATED_INT32, TextFormatParseInfoTreeTest.LOC0);
        TextFormatParseInfoTree root = rootBuilder.build();
        try {
            root.getNestedTree(TextFormatParseInfoTreeTest.OPTIONAL_INT32, 0);
            TestCase.fail("Did not detect unknown field");
        } catch (IllegalArgumentException expected) {
            // pass
        }
    }

    public void testGetLocationThrowsIllegalArgumentExceptionForInvalidIndex() {
        TextFormatParseInfoTree root = rootBuilder.setLocation(TextFormatParseInfoTreeTest.OPTIONAL_INT32, TextFormatParseInfoTreeTest.LOC0).build();
        try {
            root.getLocation(TextFormatParseInfoTreeTest.OPTIONAL_INT32, 1);
            TestCase.fail("Invalid index not detected");
        } catch (IllegalArgumentException expected) {
            // pass
        }
        try {
            root.getLocation(TextFormatParseInfoTreeTest.OPTIONAL_INT32, (-1));
            TestCase.fail("Negative index not detected");
        } catch (IllegalArgumentException expected) {
            // pass
        }
    }

    public void testGetLocationsReturnsMultipleLocations() {
        rootBuilder.setLocation(TextFormatParseInfoTreeTest.REPEATED_INT32, TextFormatParseInfoTreeTest.LOC0);
        rootBuilder.setLocation(TextFormatParseInfoTreeTest.REPEATED_INT32, TextFormatParseInfoTreeTest.LOC1);
        TextFormatParseInfoTree root = rootBuilder.build();
        TestCase.assertEquals(TextFormatParseInfoTreeTest.LOC0, root.getLocation(TextFormatParseInfoTreeTest.REPEATED_INT32, 0));
        TestCase.assertEquals(TextFormatParseInfoTreeTest.LOC1, root.getLocation(TextFormatParseInfoTreeTest.REPEATED_INT32, 1));
        TestCase.assertEquals(2, root.getLocations(TextFormatParseInfoTreeTest.REPEATED_INT32).size());
    }

    public void testGetNestedTreeThrowsIllegalArgumentExceptionForUnknownField() {
        rootBuilder.setLocation(TextFormatParseInfoTreeTest.REPEATED_INT32, TextFormatParseInfoTreeTest.LOC0);
        TextFormatParseInfoTree root = rootBuilder.build();
        try {
            root.getNestedTree(TextFormatParseInfoTreeTest.OPTIONAL_NESTED_MESSAGE, 0);
            TestCase.fail("Did not detect unknown field");
        } catch (IllegalArgumentException expected) {
            // pass
        }
    }

    public void testGetNestedTreesReturnsNoParseInfoTreesForUnknownField() {
        rootBuilder.setLocation(TextFormatParseInfoTreeTest.REPEATED_INT32, TextFormatParseInfoTreeTest.LOC0);
        TextFormatParseInfoTree root = rootBuilder.build();
        TestCase.assertTrue(root.getNestedTrees(TextFormatParseInfoTreeTest.OPTIONAL_NESTED_MESSAGE).isEmpty());
    }

    public void testGetNestedTreeThrowsIllegalArgumentExceptionForInvalidIndex() {
        rootBuilder.setLocation(TextFormatParseInfoTreeTest.REPEATED_INT32, TextFormatParseInfoTreeTest.LOC0);
        rootBuilder.getBuilderForSubMessageField(TextFormatParseInfoTreeTest.OPTIONAL_NESTED_MESSAGE);
        TextFormatParseInfoTree root = rootBuilder.build();
        try {
            root.getNestedTree(TextFormatParseInfoTreeTest.OPTIONAL_NESTED_MESSAGE, 1);
            TestCase.fail("Submessage index that is too large not detected");
        } catch (IllegalArgumentException expected) {
            // pass
        }
        try {
            rootBuilder.build().getNestedTree(TextFormatParseInfoTreeTest.OPTIONAL_NESTED_MESSAGE, (-1));
            TestCase.fail("Invalid submessage index (-1) not detected");
        } catch (IllegalArgumentException expected) {
            // pass
        }
    }

    public void testGetNestedTreesReturnsSingleTree() {
        rootBuilder.getBuilderForSubMessageField(TextFormatParseInfoTreeTest.OPTIONAL_NESTED_MESSAGE);
        TextFormatParseInfoTree root = rootBuilder.build();
        TestCase.assertEquals(1, root.getNestedTrees(TextFormatParseInfoTreeTest.OPTIONAL_NESTED_MESSAGE).size());
        TextFormatParseInfoTree subtree = root.getNestedTrees(TextFormatParseInfoTreeTest.OPTIONAL_NESTED_MESSAGE).get(0);
        TestCase.assertNotNull(subtree);
    }

    public void testGetNestedTreesReturnsMultipleTrees() {
        TextFormatParseInfoTree.Builder subtree1Builder = rootBuilder.getBuilderForSubMessageField(TextFormatParseInfoTreeTest.REPEATED_NESTED_MESSAGE);
        subtree1Builder.getBuilderForSubMessageField(TextFormatParseInfoTreeTest.FIELD_BB);
        subtree1Builder.getBuilderForSubMessageField(TextFormatParseInfoTreeTest.FIELD_BB);
        TextFormatParseInfoTree.Builder subtree2Builder = rootBuilder.getBuilderForSubMessageField(TextFormatParseInfoTreeTest.REPEATED_NESTED_MESSAGE);
        subtree2Builder.getBuilderForSubMessageField(TextFormatParseInfoTreeTest.FIELD_BB);
        TextFormatParseInfoTree root = rootBuilder.build();
        TestCase.assertEquals(2, root.getNestedTrees(TextFormatParseInfoTreeTest.REPEATED_NESTED_MESSAGE).size());
        TestCase.assertEquals(2, root.getNestedTrees(TextFormatParseInfoTreeTest.REPEATED_NESTED_MESSAGE).get(0).getNestedTrees(TextFormatParseInfoTreeTest.FIELD_BB).size());
        TestCase.assertEquals(1, root.getNestedTrees(TextFormatParseInfoTreeTest.REPEATED_NESTED_MESSAGE).get(1).getNestedTrees(TextFormatParseInfoTreeTest.FIELD_BB).size());
    }
}

