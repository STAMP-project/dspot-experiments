package com.alibaba.json.bvt.issue_1600;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;


public class Issue1683 extends TestCase {
    public void test_for_issue() throws Exception {
        String line = "[2, \"\u6d6a\u6f2b\u5947\u4fa0\", \"\u96e8\u5929\u4e0d\u6253\u4f1e\", 4536]";
        Issue1683.BookDO book = JSON.parseObject(line, Issue1683.BookDO.class, SupportArrayToBean);
        TestCase.assertEquals(2L, book.bookId.longValue());
        TestCase.assertEquals("????", book.bookName);
        TestCase.assertEquals("?????", book.authorName);
        TestCase.assertEquals(4536, book.wordCount.intValue());
    }

    @JSONType(orders = { "bookId", "bookName", "authorName", "wordCount" })
    public static class BookDO {
        private Long bookId;

        private String bookName;

        private String authorName;

        private Integer wordCount;

        public Long getBookId() {
            return bookId;
        }

        public void setBookId(Long bookId) {
            this.bookId = bookId;
        }

        public String getBookName() {
            return bookName;
        }

        public void setBookName(String bookName) {
            this.bookName = bookName;
        }

        public String getAuthorName() {
            return authorName;
        }

        public void setAuthorName(String authorName) {
            this.authorName = authorName;
        }

        public Integer getWordCount() {
            return wordCount;
        }

        public void setWordCount(Integer wordCount) {
            this.wordCount = wordCount;
        }
    }
}

