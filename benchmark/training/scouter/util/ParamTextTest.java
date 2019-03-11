package scouter.util;


import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


/**
 * Created by gunlee on 2015. 12. 11..
 */
public class ParamTextTest {
    @Test
    public void testGetText() throws Exception {
        Map<Object, Object> args = new HashMap<Object, Object>();
        args.putAll(System.getenv());
        args.putAll(System.getProperties());
        args.put("df", "df-value");
        String text = getText(args);
        System.out.println(text);
    }
}

