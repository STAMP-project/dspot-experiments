package com.alibaba.json.bvt.issue_2000;


import AccessLevel.DEVELOPER;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.Permissions;
import org.gitlab4j.api.models.Visibility;


public class Issue2040 extends TestCase {
    final ParserConfig config = new ParserConfig();

    public void test_for_issue_2040() throws Exception {
        Issue2040.Model model = JSON.parseObject("{\"accessLevel\":30,\"visibility\":\"PUBLIC\"}", Issue2040.Model.class, config);
        TestCase.assertSame(DEVELOPER, model.accessLevel);
    }

    public void test_for_issue_2040_2() throws Exception {
        String json = "{\n" + ((((("      \"project_access\": null,\n" + "      \"group_access\": {\n") + "        \"access_level\": 50,\n") + "        \"notification_level\": 3\n") + "      }\n") + "    }");
        ObjectMapper objectMapper = new ObjectMapper();
        // Permissions permissions = objectMapper.readValue(json, Permissions.class);
        Permissions permissions = JSON.parseObject(json, Permissions.class, config);
        System.out.println(JSON.toJSONString(permissions));
    }

    public static class Model {
        public AccessLevel accessLevel;

        public Visibility visibility;
    }
}

