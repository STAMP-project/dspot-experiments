package com.alibaba.druid.bvt.sql;


import com.alibaba.druid.sql.parser.SymbolTable;
import com.alibaba.druid.util.FnvHash;
import junit.framework.TestCase;


public class SymbolTableTest extends TestCase {
    public void test_symbols() throws Exception {
        SymbolTable symbols = new SymbolTable(65535);
        String[] strings = new String[10];
        for (int i = 0; i < (strings.length); ++i) {
            String str = "abc" + i;
            strings[i] = str;
            symbols.addSymbol(str, FnvHash.fnv1a_64(str));
        }
        for (int i = 0; i < (strings.length); ++i) {
            String str = strings[i];
            long hash = FnvHash.fnv1a_64(str);
            String symbol = symbols.findSymbol(hash);
            TestCase.assertSame(str, symbol);
        }
        {
            byte[] bytes = "kkk#abc0#aa".getBytes();
            long hash = FnvHash.fnv1a_64(bytes, 4, 8);
            String symbol = symbols.addSymbol(bytes, 4, 8, hash);
            TestCase.assertSame(strings[0], symbol);
        }
        byte[] bytes = "xab#time:3333".getBytes();
        System.out.println(SymbolTableTest.indexOfTime(bytes, 1));
        System.out.println("xab#time:3333".indexOf("#time"));
    }
}

