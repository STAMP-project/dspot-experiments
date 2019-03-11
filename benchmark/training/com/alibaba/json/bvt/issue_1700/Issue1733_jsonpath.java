package com.alibaba.json.bvt.issue_1700;


import com.alibaba.fastjson.JSONPath;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class Issue1733_jsonpath extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1733_jsonpath.Order order = new Issue1733_jsonpath.Order();
        order.books.add(new Issue1733_jsonpath.Book(10, "??"));
        order.books.add(new Issue1733_jsonpath.Book(50, "??"));
        order.books.add(new Issue1733_jsonpath.Book(60, "??"));
        String path = "$.books[price>20 && category = '??']";
        List result = ((List) (JSONPath.eval(order, path)));
        TestCase.assertSame(1, result.size());
        TestCase.assertSame(order.books.get(1), result.get(0));
    }

    public void test_for_issue_or() throws Exception {
        Issue1733_jsonpath.Order order = new Issue1733_jsonpath.Order();
        order.books.add(new Issue1733_jsonpath.Book(10, "??"));
        order.books.add(new Issue1733_jsonpath.Book(50, "??"));
        order.books.add(new Issue1733_jsonpath.Book(60, "??"));
        String path = "$.books[price>20||category = '??']";
        List result = ((List) (JSONPath.eval(order, path)));
        TestCase.assertEquals(2, result.size());
        TestCase.assertSame(order.books.get(1), result.get(0));
        TestCase.assertSame(order.books.get(2), result.get(1));
    }

    public void test_for_issue_or_1() throws Exception {
        Issue1733_jsonpath.Order order = new Issue1733_jsonpath.Order();
        order.books.add(new Issue1733_jsonpath.Book(10, "??"));
        order.books.add(new Issue1733_jsonpath.Book(50, "??"));
        order.books.add(new Issue1733_jsonpath.Book(60, "??"));
        String path = "$.books[category = '??' ||category = '??']";
        List result = ((List) (JSONPath.eval(order, path)));
        TestCase.assertEquals(2, result.size());
        TestCase.assertSame(order.books.get(0), result.get(0));
        TestCase.assertSame(order.books.get(1), result.get(1));
    }

    public static class Order {
        public List<Issue1733_jsonpath.Book> books = new ArrayList<Issue1733_jsonpath.Book>();
    }

    public static class Book {
        public BigDecimal price;

        public String category;

        public Book() {
        }

        public Book(int price, String category) {
            this(new BigDecimal(price), category);
        }

        public Book(BigDecimal price, String category) {
            this.price = price;
            this.category = category;
        }
    }
}

