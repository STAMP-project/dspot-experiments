package org.springside.examples.bootapi.service;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springside.examples.bootapi.domain.Account;
import org.springside.examples.bootapi.domain.Book;
import org.springside.examples.bootapi.domain.Message;
import org.springside.examples.bootapi.repository.BookDao;
import org.springside.examples.bootapi.repository.MessageDao;
import org.springside.modules.test.log.LogbackListAppender;


public class BookBorrowServiceTest {
    private BookBorrowService service;

    private BookDao mockBookDao;

    private MessageDao mockMessageDao;

    private LogbackListAppender appender;

    @Test
    public void applyBorrowRequest() {
        Book book = new Book(1L);
        book.status = Book.STATUS_IDLE;
        book.owner = new Account(1L);
        Mockito.when(mockBookDao.findOne(1L)).thenReturn(book);
        service.applyBorrowRequest(1L, new Account(3L));
        Mockito.verify(mockBookDao).save(Mockito.any(Book.class));
        Mockito.verify(mockMessageDao).save(Mockito.any(Message.class));
    }

    @Test
    public void applyBorrowRequestWithError() {
        // ???????
        Book book = new Book(1L);
        book.status = Book.STATUS_IDLE;
        book.owner = new Account(1L);
        Mockito.when(mockBookDao.findOne(1L)).thenReturn(book);
        try {
            service.applyBorrowRequest(1L, new Account(1L));
            fail("should fail here");
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("User shouldn't borrower the book which is himeself");
            assertThat(appender.getLastMessage()).contains("user id:1,book id:1");
        }
        // ??BookDao????
        Mockito.verify(mockBookDao, Mockito.never()).save(Mockito.any(Book.class));
        // ??????
        book.status = Book.STATUS_REQUEST;
        try {
            service.applyBorrowRequest(1L, new Account(3L));
            fail("should fail here");
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("The book is not idle");
            assertThat(appender.getLastMessage()).contains("user id:3,book id:1,status:request");
        }
        Mockito.verify(mockBookDao, Mockito.never()).save(Mockito.any(Book.class));
    }
}

