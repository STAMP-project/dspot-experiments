package org.ansj.app.crf;


import java.util.List;
import org.ansj.app.crf.pojo.Element;
import org.junit.Test;


public class ConfigTest {
    @Test
    public void wordAlertTest() {
        List<Element> wordAlert = Config.wordAlert("??1?");
        System.out.println(wordAlert);
    }
}

