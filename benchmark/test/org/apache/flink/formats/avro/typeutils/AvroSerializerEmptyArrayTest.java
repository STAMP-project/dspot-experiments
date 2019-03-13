/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.formats.avro.typeutils;


import java.util.ArrayList;
import java.util.List;
import org.apache.avro.reflect.Nullable;
import org.apache.flink.api.common.typeutils.SerializerTestInstance;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link AvroSerializer}.
 */
public class AvroSerializerEmptyArrayTest {
    @Test
    public void testBookSerialization() {
        try {
            AvroSerializerEmptyArrayTest.Book b = new AvroSerializerEmptyArrayTest.Book(123, "This is a test book", 26382648);
            AvroSerializer<AvroSerializerEmptyArrayTest.Book> serializer = new AvroSerializer<AvroSerializerEmptyArrayTest.Book>(AvroSerializerEmptyArrayTest.Book.class);
            SerializerTestInstance<AvroSerializerEmptyArrayTest.Book> test = new SerializerTestInstance<AvroSerializerEmptyArrayTest.Book>(serializer, AvroSerializerEmptyArrayTest.Book.class, (-1), b);
            test.testAll();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSerialization() {
        try {
            List<String> titles = new ArrayList<String>();
            List<AvroSerializerEmptyArrayTest.Book> books = new ArrayList<AvroSerializerEmptyArrayTest.Book>();
            books.add(new AvroSerializerEmptyArrayTest.Book(123, "This is a test book", 1));
            books.add(new AvroSerializerEmptyArrayTest.Book(24234234, "This is a test book", 1));
            books.add(new AvroSerializerEmptyArrayTest.Book(1234324, "This is a test book", 3));
            AvroSerializerEmptyArrayTest.BookAuthor a = new AvroSerializerEmptyArrayTest.BookAuthor(1, titles, "Test Author");
            a.books = books;
            a.bookType = AvroSerializerEmptyArrayTest.BookAuthor.BookType.journal;
            AvroSerializer<AvroSerializerEmptyArrayTest.BookAuthor> serializer = new AvroSerializer<AvroSerializerEmptyArrayTest.BookAuthor>(AvroSerializerEmptyArrayTest.BookAuthor.class);
            SerializerTestInstance<AvroSerializerEmptyArrayTest.BookAuthor> test = new SerializerTestInstance<AvroSerializerEmptyArrayTest.BookAuthor>(serializer, AvroSerializerEmptyArrayTest.BookAuthor.class, (-1), a);
            test.testAll();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Avro POJO for testing.
     */
    public static class Book {
        long bookId;

        @Nullable
        String title;

        long authorId;

        public Book() {
        }

        public Book(long bookId, String title, long authorId) {
            this.bookId = bookId;
            this.title = title;
            this.authorId = authorId;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((int) ((authorId) ^ ((authorId) >>> 32)));
            result = (prime * result) + ((int) ((bookId) ^ ((bookId) >>> 32)));
            result = (prime * result) + ((title) == null ? 0 : title.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if ((getClass()) != (obj.getClass())) {
                return false;
            }
            AvroSerializerEmptyArrayTest.Book other = ((AvroSerializerEmptyArrayTest.Book) (obj));
            if ((authorId) != (other.authorId)) {
                return false;
            }
            if ((bookId) != (other.bookId)) {
                return false;
            }
            if ((title) == null) {
                if ((other.title) != null) {
                    return false;
                }
            } else
                if (!(title.equals(other.title))) {
                    return false;
                }

            return true;
        }
    }

    /**
     * Avro POJO for testing.
     */
    public static class BookAuthor {
        enum BookType {

            book,
            article,
            journal;}

        long authorId;

        @Nullable
        List<String> bookTitles;

        @Nullable
        List<AvroSerializerEmptyArrayTest.Book> books;

        String authorName;

        AvroSerializerEmptyArrayTest.BookAuthor.BookType bookType;

        public BookAuthor() {
        }

        public BookAuthor(long authorId, List<String> bookTitles, String authorName) {
            this.authorId = authorId;
            this.bookTitles = bookTitles;
            this.authorName = authorName;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((int) ((authorId) ^ ((authorId) >>> 32)));
            result = (prime * result) + ((authorName) == null ? 0 : authorName.hashCode());
            result = (prime * result) + ((bookTitles) == null ? 0 : bookTitles.hashCode());
            result = (prime * result) + ((bookType) == null ? 0 : bookType.hashCode());
            result = (prime * result) + ((books) == null ? 0 : books.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if ((getClass()) != (obj.getClass())) {
                return false;
            }
            AvroSerializerEmptyArrayTest.BookAuthor other = ((AvroSerializerEmptyArrayTest.BookAuthor) (obj));
            if ((authorId) != (other.authorId)) {
                return false;
            }
            if ((authorName) == null) {
                if ((other.authorName) != null) {
                    return false;
                }
            } else
                if (!(authorName.equals(other.authorName))) {
                    return false;
                }

            if ((bookTitles) == null) {
                if ((other.bookTitles) != null) {
                    return false;
                }
            } else
                if (!(bookTitles.equals(other.bookTitles))) {
                    return false;
                }

            if ((bookType) != (other.bookType)) {
                return false;
            }
            if ((books) == null) {
                if ((other.books) != null) {
                    return false;
                }
            } else
                if (!(books.equals(other.books))) {
                    return false;
                }

            return true;
        }
    }
}

