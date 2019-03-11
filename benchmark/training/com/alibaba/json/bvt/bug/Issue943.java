package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 09/12/2016.
 */
public class Issue943 extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "{\n" + (((("\t\"symbols\":[\n" + "\t    {\"id\":1,\"type\":\"SCATTER\"},\n") + "\t    {\"id\":2,\"type\":\"BONUS\"}\n") + "\t]\n") + "}");
        JSONObject root = JSON.parseObject(text);
        JSONArray symbols = root.getJSONArray("symbols");
        TestCase.assertNotNull(symbols);
        TestCase.assertEquals(2, symbols.size());
        TestCase.assertEquals(1, symbols.getJSONObject(0).get("id"));
        TestCase.assertEquals("SCATTER", symbols.getJSONObject(0).get("type"));
        TestCase.assertEquals(2, symbols.getJSONObject(1).get("id"));
        TestCase.assertEquals("BONUS", symbols.getJSONObject(1).get("type"));
        Issue943.SlotConfig slotConfig = JSON.parseObject(text, Issue943.SlotConfig.class);
        TestCase.assertNotNull(slotConfig);
        TestCase.assertEquals(2, slotConfig.symbols.size());
        TestCase.assertEquals(1, slotConfig.symbols.get(0).getId());
        TestCase.assertEquals(Issue943.SymbolType.SCATTER, slotConfig.symbols.get(0).getType());
        TestCase.assertEquals(2, slotConfig.symbols.get(1).getId());
        TestCase.assertEquals(Issue943.SymbolType.BONUS, slotConfig.symbols.get(1).getType());
    }

    private static class SlotConfig {
        private List<Issue943.Symbol> symbols;

        public List<Issue943.Symbol> getSymbols() {
            return symbols;
        }

        public void setSymbols(List<Issue943.Symbol> symbols) {
            this.symbols = symbols;
        }
    }

    private static class Symbol {
        private int id;

        private Issue943.SymbolType type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Issue943.SymbolType getType() {
            return type;
        }

        public void setType(Issue943.SymbolType type) {
            this.type = type;
        }
    }

    enum SymbolType {

        NORMAL,
        WILD,
        SCATTER,
        BONUS;}
}

