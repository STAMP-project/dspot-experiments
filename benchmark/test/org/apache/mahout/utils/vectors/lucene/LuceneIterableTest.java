/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.utils.vectors.lucene;


import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.RAMDirectory;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.utils.vectors.TermInfo;
import org.apache.mahout.vectorizer.TFIDF;
import org.apache.mahout.vectorizer.Weight;
import org.junit.Test;


public final class LuceneIterableTest extends MahoutTestCase {
    private static final String[] DOCS = new String[]{ "The quick red fox jumped over the lazy brown dogs.", "Mary had a little lamb whose fleece was white as snow.", "Moby Dick is a story of a whale and a man obsessed.", "The robber wore a black fleece jacket and a baseball cap.", "The English Springer Spaniel is the best of all dogs." };

    private RAMDirectory directory;

    private final FieldType TYPE_NO_TERM_VECTORS = new FieldType();

    private final FieldType TYPE_TERM_VECTORS = new FieldType();

    @Test
    public void testIterable() throws Exception {
        IndexReader reader = DirectoryReader.open(directory);
        Weight weight = new TFIDF();
        TermInfo termInfo = new CachedTermInfo(reader, "content", 1, 100);
        LuceneIterable iterable = new LuceneIterable(reader, "id", "content", termInfo, weight);
        // TODO: do something more meaningful here
        for (Vector vector : iterable) {
            assertNotNull(vector);
            assertTrue(("vector is not an instanceof " + (NamedVector.class)), (vector instanceof NamedVector));
            assertTrue(((("vector Size: " + (vector.size())) + " is not greater than: ") + 0), ((vector.size()) > 0));
            assertTrue(getName().startsWith("doc_"));
        }
        iterable = new LuceneIterable(reader, "id", "content", termInfo, weight, 3);
        // TODO: do something more meaningful here
        for (Vector vector : iterable) {
            assertNotNull(vector);
            assertTrue(("vector is not an instanceof " + (NamedVector.class)), (vector instanceof NamedVector));
            assertTrue(((("vector Size: " + (vector.size())) + " is not greater than: ") + 0), ((vector.size()) > 0));
            assertTrue(getName().startsWith("doc_"));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testIterableNoTermVectors() throws IOException {
        RAMDirectory directory = LuceneIterableTest.createTestIndex(TYPE_NO_TERM_VECTORS);
        IndexReader reader = DirectoryReader.open(directory);
        Weight weight = new TFIDF();
        TermInfo termInfo = new CachedTermInfo(reader, "content", 1, 100);
        LuceneIterable iterable = new LuceneIterable(reader, "id", "content", termInfo, weight);
        Iterator<Vector> iterator = iterable.iterator();
        Iterators.advance(iterator, 1);
    }

    @Test
    public void testIterableSomeNoiseTermVectors() throws IOException {
        // get noise vectors
        RAMDirectory directory = LuceneIterableTest.createTestIndex(TYPE_TERM_VECTORS, new RAMDirectory(), 0);
        // get real vectors
        LuceneIterableTest.createTestIndex(TYPE_NO_TERM_VECTORS, directory, 5);
        IndexReader reader = DirectoryReader.open(directory);
        Weight weight = new TFIDF();
        TermInfo termInfo = new CachedTermInfo(reader, "content", 1, 100);
        boolean exceptionThrown;
        // 0 percent tolerance
        LuceneIterable iterable = new LuceneIterable(reader, "id", "content", termInfo, weight);
        try {
            Iterables.skip(iterable, Iterables.size(iterable));
            exceptionThrown = false;
        } catch (IllegalStateException ise) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
        // 100 percent tolerance
        iterable = new LuceneIterable(reader, "id", "content", termInfo, weight, (-1), 1.0);
        try {
            Iterables.skip(iterable, Iterables.size(iterable));
            exceptionThrown = false;
        } catch (IllegalStateException ise) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
        // 50 percent tolerance
        iterable = new LuceneIterable(reader, "id", "content", termInfo, weight, (-1), 0.5);
        Iterator<Vector> iterator = iterable.iterator();
        Iterators.advance(iterator, 5);
        try {
            Iterators.advance(iterator, Iterators.size(iterator));
            exceptionThrown = false;
        } catch (IllegalStateException ise) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}

