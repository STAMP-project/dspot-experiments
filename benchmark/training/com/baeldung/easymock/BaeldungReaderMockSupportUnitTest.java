package com.baeldung.easymock;


import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class BaeldungReaderMockSupportUnitTest extends EasyMockSupport {
    @TestSubject
    BaeldungReader baeldungReader = new BaeldungReader();

    @Mock
    ArticleReader mockArticleReader;

    @Mock
    IArticleWriter mockArticleWriter;

    @Test
    public void givenBaeldungReader_whenReadAndWriteSequencially_thenWorks() {
        expect(mockArticleReader.next()).andReturn(null).times(2).andThrow(new NoSuchElementException());
        expect(mockArticleWriter.write("title", "content")).andReturn("BAEL-201801");
        replayAll();
        Exception expectedException = null;
        try {
            for (int i = 0; i < 3; i++) {
                baeldungReader.readNext();
            }
        } catch (Exception exception) {
            expectedException = exception;
        }
        String articleId = baeldungReader.write("title", "content");
        verifyAll();
        Assert.assertEquals(NoSuchElementException.class, expectedException.getClass());
        Assert.assertEquals("BAEL-201801", articleId);
    }
}

