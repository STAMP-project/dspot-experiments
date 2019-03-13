package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class Issue804 extends TestCase {
    public void test_issue() throws Exception {
        String json = "{\n" + ((((((((((((((((((((((("    \"@type\":\"com.alibaba.json.bvt.bug.Issue804$Room\",\n" + "    \"children\":[],\n") + "    \"name\":\"Room2_1\",\n") + "    \"parent\":{\n") + "        \"@type\":\"com.alibaba.json.bvt.bug.Issue804$Area\",\n") + "        \"children\":[\n") + "            {\"$ref\":\"$\"},\n") + "            {\n") + "                \"@type\":\"com.alibaba.json.bvt.bug.Issue804$Room\",\n") + "                \"children\":[],\n") + "                \"name\":\"Room_2\",\n") + "                \"parent\":{\"$ref\":\"$.parent\"}\n") + "            }\n") + "        ],\n") + "        \"name\":\"Area1\",\n") + "        \"parent\":{\n") + "            \"@type\":\"com.alibaba.json.bvt.bug.Issue804$Area\",\n") + "            \"children\":[\n") + "                {\"$ref\":\"$.parent\"}\n") + "            ],\n") + "            \"name\":\"Area0\"\n") + "        }\n") + "    }\n") + "}");
        Issue804.Room room = ((Issue804.Room) (JSON.parse(json)));
        TestCase.assertSame(room, room.parent.children.get(0));
    }

    @JSONType
    public static class Node {
        protected String name;

        protected Issue804.Node parent;

        protected List<Issue804.Node> children = new ArrayList<Issue804.Node>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Issue804.Node getParent() {
            return parent;
        }

        public void setParent(Issue804.Node parent) {
            this.parent = parent;
        }

        public List<Issue804.Node> getChildren() {
            return children;
        }

        public void setChildren(List<Issue804.Node> children) {
            this.children = children;
        }
    }

    @JSONType
    public static class Area extends Issue804.Node {}

    @JSONType
    public static class Room extends Issue804.Node {}
}

