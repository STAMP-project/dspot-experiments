package org.ansj.app.extracting;


import DicLibrary.DEFAULT;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.ansj.app.extracting.domain.ExtractingResult;
import org.ansj.app.extracting.exception.RuleFormatException;
import org.ansj.library.DicLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.Test;


/**
 * Created by Ansj on 21/09/2017.
 */
public class ExtractingTest {
    @Test
    public void test() throws IOException, RuleFormatException {
        Extracting extracting = new Extracting(LexicalTest.class.getResourceAsStream("/rule.txt"), "utf-8");
        ExtractingResult result = null;
        result = extracting.parse("2017?2?12?????????");
        System.out.println(result.getAllResult());
        result = extracting.parse("2017?2?12??????????????1985?8?28?");
        System.out.println(result.getAllResult());
        result = extracting.parse("???????????");
        System.out.println(result.getAllResult());
    }

    @Test
    public void test1() throws RuleFormatException {
        List<String> lines = new ArrayList<>();
        // ???? ?????
        lines.add("(:bName)(:*){0,3}(???)(?|??)(??|??)	??:0;??:2;??:(??)");
        lines.add("(:bName)(\u5982\u4f55|\u600e\u4e48){0,1}(\u9632\u6cbb|\u907f\u514d)\t\u540d\u79f0:0;\u9650\u5b9a:2");
        lines.add("(:nt|\u6e05\u534e\u5927\u5b66\u554a\u554a\u554a)(\u8d1f\u8d23\u4eba)(:nr)\t\u516c\u53f8\u540d:0;\u4eba\u7269:2");
        lines.add("(\u672c\u671f\u8ba1\u63d0\u574f\u8d26\u51c6\u5907\u91d1\u989d)(:m)\tname:(\u8ba1\u63d0);num:1");
        lines.add("(\u672c\u671f\u6536\u56de\u6216\u8f6c\u56de\u574f\u8d26\u51c6\u5907\u91d1\u989d)(:m)\tname:(\u6536\u56de);num:1");
        lines.add("(:*)(\u6d4b\u5b9a)\t\u540d\u79f0:0;aaa:1");
        Extracting extracting = new Extracting(lines);
        // ???????
        DicLibrary.insertOrCreate(DEFAULT, "???", "bName", 1000);
        DicLibrary.insertOrCreate(DEFAULT, "???", "bName", 1000);
        DicLibrary.insertOrCreate(DEFAULT, "??", "bName", 1000);
        System.out.println(extracting.parse("????200").getAllResult());
        System.out.println(extracting.parse("????200??").getAllResult());
        // ????
        System.out.println(extracting.parse("?????????????").getAllResult());
        System.out.println(extracting.parse("?????????").getAllResult());
        System.out.println(extracting.parse("??????????").getAllResult());
        System.out.println(extracting.parse("?????").getAllResult());
        System.out.println(extracting.parse("???????").getAllResult());
        System.out.println(ToAnalysis.parse("??????????2138030.52???????????????0.00?"));
        System.out.println(extracting.parse("???????????").getAllResult());
        System.out.println(extracting.parse("??????????????").getAllResult());
        System.out.println(extracting.parse("??????????2138030.52???????????????0.00?").getAllResult());
    }
}

