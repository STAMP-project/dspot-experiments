/**
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.beans.factory.parsing;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rob Harrop
 * @author Chris Beams
 * @since 2.0
 */
public class ParseStateTests {
    @Test
    public void testSimple() throws Exception {
        ParseStateTests.MockEntry entry = new ParseStateTests.MockEntry();
        ParseState parseState = new ParseState();
        parseState.push(entry);
        Assert.assertEquals("Incorrect peek value.", entry, parseState.peek());
        parseState.pop();
        Assert.assertNull("Should get null on peek()", parseState.peek());
    }

    @Test
    public void testNesting() throws Exception {
        ParseStateTests.MockEntry one = new ParseStateTests.MockEntry();
        ParseStateTests.MockEntry two = new ParseStateTests.MockEntry();
        ParseStateTests.MockEntry three = new ParseStateTests.MockEntry();
        ParseState parseState = new ParseState();
        parseState.push(one);
        Assert.assertEquals(one, parseState.peek());
        parseState.push(two);
        Assert.assertEquals(two, parseState.peek());
        parseState.push(three);
        Assert.assertEquals(three, parseState.peek());
        parseState.pop();
        Assert.assertEquals(two, parseState.peek());
        parseState.pop();
        Assert.assertEquals(one, parseState.peek());
    }

    @Test
    public void testSnapshot() throws Exception {
        ParseStateTests.MockEntry entry = new ParseStateTests.MockEntry();
        ParseState original = new ParseState();
        original.push(entry);
        ParseState snapshot = original.snapshot();
        original.push(new ParseStateTests.MockEntry());
        Assert.assertEquals("Snapshot should not have been modified.", entry, snapshot.peek());
    }

    private static class MockEntry implements ParseState.Entry {}
}

