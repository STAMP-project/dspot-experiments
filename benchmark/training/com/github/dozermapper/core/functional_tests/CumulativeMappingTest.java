/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.vo.cumulative.Author;
import com.github.dozermapper.core.vo.cumulative.AuthorPrime;
import com.github.dozermapper.core.vo.cumulative.Book;
import com.github.dozermapper.core.vo.cumulative.BookPrime;
import com.github.dozermapper.core.vo.cumulative.Library;
import com.github.dozermapper.core.vo.cumulative.LibraryPrime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class CumulativeMappingTest extends AbstractFunctionalTest {
    /* Domain model: Library is a list of books. A book has an author and id. An author has a name and id.
    The same is on 'Prime' side with only one exception. AuthorPrime has all the fields of Author and
    one more - salary.
     */
    @Test
    public void testMapping() {
        Library libSrc = newInstance(Library.class);
        Author author = newInstance(Author.class, new Object[]{ "The Best One", new Long(505L) });
        Book book = newInstance(Book.class, new Object[]{ new Long(141L), author });
        libSrc.setBooks(Collections.singletonList(book));
        LibraryPrime libDest = newInstance(LibraryPrime.class);
        AuthorPrime authorPrime = newInstance(AuthorPrime.class, new Object[]{ new Long(505L), "The Ultimate One", new Long(5100L) });
        BookPrime bookDest = newInstance(BookPrime.class, new Object[]{ new Long(141L), authorPrime });
        List<BookPrime> bookDests = newInstance(ArrayList.class);
        bookDests.add(bookDest);
        libDest.setBooks(bookDests);
        mapper.map(libSrc, libDest);
        Assert.assertEquals(1, libDest.getBooks().size());
        BookPrime bookPrime = ((BookPrime) (libDest.getBooks().get(0)));
        Assert.assertEquals(new Long(141L), bookPrime.getId());
        Assert.assertEquals("The Best One", bookPrime.getAuthor().getName());
        // assertEquals(new Long(5100L), book.getAuthor().getSalary()); TODO Enable this for non-cumulative recursion bug
    }
}

