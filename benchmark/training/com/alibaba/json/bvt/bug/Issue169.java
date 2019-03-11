package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;
import java.io.StringReader;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue169 extends TestCase {
    public void test_for_issue() throws Exception {
        StringWriter strWriter = new StringWriter();
        Issue169.SectionRequest req = new Issue169.SectionRequest();
        req.setScreenHeight(100);// ??????

        req.setScreenWidth(12);// ??????

        req.setTag("11");
        JSONWriter writer = new JSONWriter(strWriter);
        writer.startArray();
        writer.writeObject(req);
        writer.endArray();
        writer.close();
        String text = strWriter.toString();
        StringReader strReader = new StringReader(text);
        JSONReader reader = new JSONReader(strReader);
        reader.startArray();
        while (reader.hasNext()) {
            Issue169.SectionRequest vo = reader.readObject(Issue169.SectionRequest.class);
            System.out.println(((((("tag:" + (vo.getTag())) + "screenHeight:") + (vo.getScreenHeight())) + "ScreenWidth:") + (vo.getScreenWidth())));
            Assert.assertEquals(100, vo.getScreenHeight());
            Assert.assertEquals(12, vo.getScreenWidth());
            Assert.assertEquals("11", vo.getTag());
        } 
        reader.endArray();
        reader.close();
    }

    public static class SectionRequest {
        private String tag;

        private int screenHeight;

        private int screenWidth;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public int getScreenHeight() {
            return screenHeight;
        }

        public void setScreenHeight(int screenHeight) {
            this.screenHeight = screenHeight;
        }

        public int getScreenWidth() {
            return screenWidth;
        }

        public void setScreenWidth(int screenWidth) {
            this.screenWidth = screenWidth;
        }
    }
}

