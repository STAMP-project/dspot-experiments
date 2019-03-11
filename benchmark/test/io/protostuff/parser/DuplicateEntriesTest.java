/**
 * ========================================================================
 */
/**
 * Copyright 2007-2011 David Yu dyuproject@gmail.com
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


import junit.framework.TestCase;


/**
 * Fail-fast exceptions thrown when detecting duplicate proto components.
 *
 * @author David Yu
 * @unknown Jul 21, 2011
 */
public class DuplicateEntriesTest extends TestCase {
    public void testDupEnum() throws Exception {
        assertDup("io/protostuff/parser/test_duplicate_enum.proto");
    }

    public void testDupNestedEnum() throws Exception {
        assertDup("io/protostuff/parser/test_duplicate_nested_enum.proto");
    }

    public void testDupMessage() throws Exception {
        assertDup("io/protostuff/parser/test_duplicate_message.proto");
    }

    public void testDupNestedMessage() throws Exception {
        assertDup("io/protostuff/parser/test_duplicate_nested_message.proto");
    }

    public void testDupRpcMethod() throws Exception {
        assertDup("io/protostuff/parser/test_duplicate_rpcmethod.proto");
    }

    public void testDupService() throws Exception {
        assertDup("io/protostuff/parser/test_duplicate_service.proto");
    }
}

