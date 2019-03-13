/**
 * Copyright 2008-2019 the original author or authors.
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
package org.springframework.batch.item.xml.stax;


import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import junit.framework.TestCase;
import org.springframework.batch.item.xml.EventHelper;


/**
 * Tests for {@link DefaultFragmentEventReader}.
 *
 * @author Robert Kasanicky
 * @author Mahmoud Ben Hassine
 */
public class DefaultFragmentEventReaderTests extends TestCase {
    // object under test
    private FragmentEventReader fragmentReader;

    // wrapped event fragmentReader
    private XMLEventReader eventReader;

    // test xml input
    private String xml = "<root> <fragment> <misc1/> </fragment> <misc2/> <fragment> </fragment> </root>";

    /**
     * Marked element should be wrapped with StartDocument and EndDocument
     * events.
     * Test uses redundant peek() calls before nextEvent() in important moments to assure
     * peek() has no side effects on the inner state of reader.
     */
    public void testFragmentWrapping() throws XMLStreamException {
        TestCase.assertTrue(fragmentReader.hasNext());
        moveCursorBeforeFragmentStart();
        fragmentReader.markStartFragment();// mark the fragment

        TestCase.assertTrue(EventHelper.startElementName(eventReader.peek()).equals("fragment"));
        // StartDocument inserted before StartElement
        TestCase.assertTrue(fragmentReader.peek().isStartDocument());
        TestCase.assertTrue(fragmentReader.nextEvent().isStartDocument());
        // StartElement follows in the next step
        TestCase.assertTrue(EventHelper.startElementName(fragmentReader.nextEvent()).equals("fragment"));
        moveCursorToNextElementEvent();// misc1 start

        fragmentReader.nextEvent();// skip it

        moveCursorToNextElementEvent();// misc1 end

        fragmentReader.nextEvent();// skip it

        moveCursorToNextElementEvent();// move to end of fragment

        // expected EndElement, peek first which should have no side effect
        TestCase.assertTrue(EventHelper.endElementName(fragmentReader.nextEvent()).equals("fragment"));
        // inserted EndDocument
        TestCase.assertTrue(fragmentReader.peek().isEndDocument());
        TestCase.assertTrue(fragmentReader.nextEvent().isEndDocument());
        // now the reader should behave like the document has finished
        TestCase.assertTrue(((fragmentReader.peek()) == null));
        TestCase.assertFalse(fragmentReader.hasNext());
        try {
            fragmentReader.nextEvent();
            TestCase.fail("nextEvent should simulate behavior as if document ended");
        } catch (NoSuchElementException expected) {
            // expected
        }
    }

    /**
     * When fragment is marked as processed the cursor is moved after the end of
     * the fragment.
     */
    public void testMarkFragmentProcessed() throws XMLStreamException {
        moveCursorBeforeFragmentStart();
        fragmentReader.markStartFragment();// mark the fragment start

        // read only one event to move inside the fragment
        XMLEvent startFragment = fragmentReader.nextEvent();
        TestCase.assertTrue(startFragment.isStartDocument());
        fragmentReader.markFragmentProcessed();// mark fragment as processed

        fragmentReader.nextEvent();// skip whitespace

        // the next element after fragment end is <misc2/>
        XMLEvent misc2 = fragmentReader.nextEvent();
        TestCase.assertTrue(EventHelper.startElementName(misc2).equals("misc2"));
    }

    /**
     * Cursor is moved to the end of the fragment as usually even
     * if nothing was read from the event reader after beginning
     * of fragment was marked.
     */
    public void testMarkFragmentProcessedImmediatelyAfterMarkFragmentStart() throws Exception {
        moveCursorBeforeFragmentStart();
        fragmentReader.markStartFragment();
        fragmentReader.markFragmentProcessed();
        fragmentReader.nextEvent();// skip whitespace

        // the next element after fragment end is <misc2/>
        XMLEvent misc2 = fragmentReader.nextEvent();
        TestCase.assertTrue(EventHelper.startElementName(misc2).equals("misc2"));
    }
}

