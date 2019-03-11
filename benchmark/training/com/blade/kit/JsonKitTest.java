package com.blade.kit;


import com.blade.model.TestBean;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
2017/6/6
 */
public class JsonKitTest {
    @Test
    public void test1() throws Exception {
        TestBean testBean = new TestBean();
        setAge(20);
        setName("jack");
        setPrice(2.31);
        setSex(false);
        setOtherList(new String[]{ "a", "b" });
        String text = JsonKit.toString(testBean);
        System.out.println(text);
        TestBean bean = JsonKit.formJson(text, TestBean.class);
        System.out.println(bean);
    }

    @Test
    public void test2() {
        TestBean testBean = new TestBean();
        setDateTime(LocalDateTime.now());
        System.out.println(JsonKit.toString(testBean));
    }

    @Test
    public void testLocal() {
        Map<String, Object> result = new HashMap<>(8);
        result.put("date1", new Date());
        result.put("date2", LocalDate.now());
        result.put("date3", LocalDateTime.now());
        System.out.println(JsonKit.toString(result));
    }

    @Test
    public void test3() {
        TestBean testBean = new TestBean();
        setName("\"hello\"_world");
        System.out.println(JsonKit.toString(testBean));
    }
}

