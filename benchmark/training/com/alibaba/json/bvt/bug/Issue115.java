package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Issue115 extends TestCase {
    public void test_for_issue_115() throws Exception {
        Issue115.Player2 player = new Issue115.Player2();
        Issue115.Card2 card = new Issue115.Card2();
        card.cardId = "hello";
        player.cards.put(1, card);
        player.cardGroup.put(1, card);
        String json = JSON.toJSONString(player);
        System.out.println(("json:" + json));
        Issue115.Player2 player2 = JSON.parseObject(json, Issue115.Player2.class);
    }

    static class Player2 {
        public Map cards = new HashMap();

        public Map cardGroup = new HashMap();
    }

    static class Card2 {
        public String cardId;
    }
}

