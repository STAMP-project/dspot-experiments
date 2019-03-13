package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONPOJOBuilder;
import com.alibaba.fastjson.annotation.JSONType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import junit.framework.TestCase;


/**
 * Created by wenshao on 19/12/2016.
 */
public class Issue955 extends TestCase {
    public void test_checkObject() {
        Issue955.Art origin = makeOrigin();
        JSONObject articleObj = ((JSONObject) (JSON.toJSON(origin)));
        JSONObject dataObj = new JSONObject();
        dataObj.put("art", articleObj);
        Issue955.Art other = dataObj.getObject("art", Issue955.Art.class);// return null;

        assertSame(origin, other);// test failed

    }

    public void test_checkArray() throws Exception {
        Issue955.Art origin = makeOrigin();
        JSONObject object = ((JSONObject) (JSON.toJSON(origin)));
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(object);
        Issue955.Art other = JSON.parseObject(jsonArray.getString(0), Issue955.Art.class);
        assertSame(origin, other);// test passed

        other = jsonArray.getObject(0, Issue955.Art.class);// return = null;

        assertSame(origin, other);// test failed

    }

    @JSONType(builder = Issue955.Art.Builder.class)
    public static class Art {
        private String id;

        private String date;

        private boolean isSupported;

        public String getId() {
            return id;
        }

        public long getDatetime() throws ParseException {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return (format.parse(date).getTime()) / 1000;
        }

        @JSONField(name = "isSupported")
        public int isSupported() {
            return isSupported ? 1 : 0;
        }

        @JSONPOJOBuilder
        public static final class Builder {
            private final Issue955.Art article = new Issue955.Art();

            public Builder() {
            }

            @JSONField(name = "id")
            public Issue955.Art.Builder withId(String id) {
                article.id = id;
                return this;
            }

            @JSONField(name = "datetime")
            public Issue955.Art.Builder withDateTime(long dateTime) {
                if (dateTime > 0)
                    article.date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date((dateTime * 1000)));

                return this;
            }

            @JSONField(name = "isSupported")
            public Issue955.Art.Builder withSupported(int supported) {
                article.isSupported = supported == 1;
                return this;
            }

            public Issue955.Art build() {
                return article;
            }
        }
    }
}

