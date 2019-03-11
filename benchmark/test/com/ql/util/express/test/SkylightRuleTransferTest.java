package com.ql.util.express.test;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;


/**
 * Created by tianqiao on 16/10/27.
 */
public class SkylightRuleTransferTest {
    static Pattern pattern = Pattern.compile("[\\s]+");

    static Pattern pattern2 = Pattern.compile("(rule|RULE)[\\s]+([\\S]+)[\\s]+(name|NAME)[\\s]+([\\S]+)[\\s]+");

    public class Rule {
        public String name;

        public String code;

        public String content;

        public String ql;

        public Rule(String content) throws Exception {
            this.content = content;
            praseContent();
        }

        private void praseContent() throws Exception {
            Matcher matcher = SkylightRuleTransferTest.pattern2.matcher(content);
            if (matcher.find()) {
                this.code = matcher.group(2);
                this.name = matcher.group(4);
                this.ql = matcher.replaceFirst("");
            } else {
                System.out.println("???????");
                throw new Exception("???????");
            }
        }
    }

    @Test
    public void helloWorld() throws Exception {
        String skylight = "rule test name \u6d4b\u8bd5 for(i=0;i<10;i++){\nsum=sum+i;\n}\nreturn sum;\n";
        SkylightRuleTransferTest.Rule rule = new SkylightRuleTransferTest.Rule(skylight);
        System.out.println(("code:" + (rule.code)));
        System.out.println(("name:" + (rule.name)));
        System.out.println(("ql\u811a\u672c:\n" + (rule.ql)));
    }
}

