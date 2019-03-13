package cn.hutool.json;


import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.test.bean.ExamInfoDict;
import cn.hutool.json.test.bean.PerfectEvaluationProductResVo;
import cn.hutool.json.test.bean.UserInfoDict;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * JSON??????
 *
 * @author Looly?????
 */
public class JSONConvertTest {
    @Test
    public void testBean2Json() {
        UserInfoDict userInfoDict = new UserInfoDict();
        userInfoDict.setId(1);
        userInfoDict.setPhotoPath("yx.mm.com");
        userInfoDict.setRealName("????");
        ExamInfoDict examInfoDict = new ExamInfoDict();
        examInfoDict.setId(1);
        examInfoDict.setExamType(0);
        examInfoDict.setAnswerIs(1);
        ExamInfoDict examInfoDict1 = new ExamInfoDict();
        examInfoDict1.setId(2);
        examInfoDict1.setExamType(0);
        examInfoDict1.setAnswerIs(0);
        ExamInfoDict examInfoDict2 = new ExamInfoDict();
        examInfoDict2.setId(3);
        examInfoDict2.setExamType(1);
        examInfoDict2.setAnswerIs(0);
        List<ExamInfoDict> examInfoDicts = new ArrayList<ExamInfoDict>();
        examInfoDicts.add(examInfoDict);
        examInfoDicts.add(examInfoDict1);
        examInfoDicts.add(examInfoDict2);
        userInfoDict.setExamInfoDict(examInfoDicts);
        Map<String, Object> tempMap = new HashMap<String, Object>();
        tempMap.put("userInfoDict", userInfoDict);
        tempMap.put("toSendManIdCard", 1);
        JSONObject obj = JSONUtil.parseObj(tempMap);
        Assert.assertEquals(new Integer(1), obj.getInt("toSendManIdCard"));
        JSONObject examInfoDictsJson = obj.getJSONObject("userInfoDict");
        Assert.assertEquals(new Integer(1), examInfoDictsJson.getInt("id"));
        Assert.assertEquals("????", examInfoDictsJson.getStr("realName"));
        Object id = JSONUtil.getByPath(obj, "userInfoDict.examInfoDict[0].id");
        Assert.assertEquals(1, id);
    }

    @Test
    public void testJson2Bean() {
        // language=JSON
        String examJson = "{\n" + ((((((((((((((((((((((("  \"examInfoDicts\": {\n" + "    \"id\": 1,\n") + "    \"realName\": \"\u8d28\u91cf\u8fc7\u5173\",\n")// 
         + "    \"examInfoDict\": [\n") + "      {\n") + "        \"id\": 1,\n") + "        \"answerIs\": 1,\n") + "        \"examType\": 0\n")// 
         + "      },\n") + "      {\n") + "        \"id\": 2,\n") + "        \"answerIs\": 0,\n") + "        \"examType\": 0\n") + "      },\n")// 
         + "      {\n") + "        \"id\": 3,\n") + "        \"answerIs\": 0,\n") + "        \"examType\": 1\n") + "      }\n") + "    ],\n")// 
         + "    \"photoPath\": \"yx.mm.com\"\n") + "  },\n") + "  \"toSendManIdCard\": 1\n") + "}");
        JSONObject jsonObject = JSONUtil.parseObj(examJson).getJSONObject("examInfoDicts");
        UserInfoDict userInfoDict = jsonObject.toBean(UserInfoDict.class);
        Assert.assertEquals(userInfoDict.getId(), new Integer(1));
        Assert.assertEquals(userInfoDict.getRealName(), "????");
        // ============
        String jsonStr = "{\"id\":null,\"examInfoDict\":[{\"answerIs\":1, \"id\":null}]}";// JSONUtil.toJsonStr(userInfoDict1);

        JSONObject jsonObject2 = JSONUtil.parseObj(jsonStr);// .getJSONObject("examInfoDicts");

        UserInfoDict userInfoDict2 = jsonObject2.toBean(UserInfoDict.class);
        Assert.assertNull(userInfoDict2.getId());
    }

    /**
     * ??Bean?Setter??this??????????Setter?????
     */
    @Test
    public void testJson2Bean2() {
        String jsonStr = ResourceUtil.readUtf8Str("evaluation.json");
        JSONObject obj = JSONUtil.parseObj(jsonStr);
        PerfectEvaluationProductResVo vo = obj.toBean(PerfectEvaluationProductResVo.class);
        Assert.assertEquals(obj.getStr("HA001"), vo.getHA001());
        Assert.assertEquals(obj.getInt("costTotal"), vo.getCostTotal());
    }
}

