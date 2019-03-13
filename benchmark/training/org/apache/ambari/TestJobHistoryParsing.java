/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.TestCase;


/**
 *
 */
public class TestJobHistoryParsing extends TestCase {
    static final char LINE_DELIMITER_CHAR = '.';

    static final char[] charsToEscape = new char[]{ '"', '=', TestJobHistoryParsing.LINE_DELIMITER_CHAR };

    private static final char DELIMITER = ' ';

    private static final String ID = "WORKFLOW_ID";

    private static final String NAME = "WORKFLOW_NAME";

    private static final String NODE = "WORKFLOW_NODE_NAME";

    private static final String ADJ = "WORKFLOW_ADJACENCIES";

    private static final String ID_PROP = "mapreduce.workflow.id";

    private static final String NAME_PROP = "mapreduce.workflow.name";

    private static final String NODE_PROP = "mapreduce.workflow.node.name";

    private static final String ADJ_PROP = "mapreduce.workflow.adjacency";

    public void test1() {
        Map<String, String[]> adj = new HashMap<String, String[]>();
        adj.put("10", new String[]{ "20", "30" });
        adj.put("20", new String[]{ "30" });
        adj.put("30", new String[]{  });
        test("id_0-1", "something.name", "10", adj);
    }

    public void test2() {
        Map<String, String[]> adj = new HashMap<String, String[]>();
        adj.put("1=0", new String[]{ "2 0", "3\"0." });
        adj.put("2 0", new String[]{ "3\"0." });
        adj.put("3\"0.", new String[]{  });
        test("id_= 0-1", "something.name", "1=0", adj);
    }

    public void test3() {
        String s = "`~!@#$%^&*()-_=+[]{}|,.<>/?;:\'\"";
        test(s, s, s, new HashMap<String, String[]>());
    }

    public void test4() {
        Map<String, String[]> adj = new HashMap<String, String[]>();
        adj.put("X", new String[]{  });
        test("", "jobName", "X", adj);
    }

    private static class ParsedLine {
        static final String KEY = "(\\w+)";

        static final String VALUE = "([^\"\\\\]*+(?:\\\\.[^\"\\\\]*+)*+)";

        static final Pattern keyValPair = Pattern.compile((((((TestJobHistoryParsing.ParsedLine.KEY) + "=") + "\"") + (TestJobHistoryParsing.ParsedLine.VALUE)) + "\""));

        Map<String, String> props = new HashMap<String, String>();

        private String type;

        ParsedLine(String fullLine) {
            int firstSpace = fullLine.indexOf(" ");
            if (firstSpace < 0) {
                firstSpace = fullLine.length();
            }
            if (firstSpace == 0) {
                return;// This is a junk line of some sort

            }
            type = fullLine.substring(0, firstSpace);
            String propValPairs = fullLine.substring((firstSpace + 1));
            Matcher matcher = TestJobHistoryParsing.ParsedLine.keyValPair.matcher(propValPairs);
            while (matcher.find()) {
                String key = matcher.group(1);
                String value = matcher.group(2);
                props.put(key, value);
            } 
        }

        protected String getType() {
            return type;
        }

        protected String get(String key) {
            return props.get(key);
        }
    }
}

