package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.io.InputStream;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;


public class Bug_for_wuyexiong extends TestCase {
    public static class Track {
        private String name;

        private String color;

        private String _abstract;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getAbstract() {
            return _abstract;
        }

        public void setAbstract(String _abstract) {
            this._abstract = _abstract;
        }
    }

    public static class Tracks {
        private Bug_for_wuyexiong.Track[] track;

        public void setTrack(Bug_for_wuyexiong.Track[] track) {
            this.track = track;
        }

        public Bug_for_wuyexiong.Track[] getTrack() {
            return track;
        }
    }

    public void test_for_wuyexiong() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("wuyexiong.json");
        String text = IOUtils.toString(is);
        IOUtils.closeQuietly(is);
        Bug_for_wuyexiong.Tracks tracks = JSON.parseObject(text, Bug_for_wuyexiong.Tracks.class);
        Assert.assertEquals("Learn about developing mobile handset and tablet apps for Android.", tracks.getTrack()[0].getAbstract());
    }
}

