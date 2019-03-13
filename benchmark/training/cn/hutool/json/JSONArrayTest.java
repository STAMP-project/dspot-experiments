package cn.hutool.json;


import CharsetUtil.CHARSET_UTF_8;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.test.bean.Exam;
import cn.hutool.json.test.bean.JsonNode;
import cn.hutool.json.test.bean.KeyBean;
import cn.hutool.json.test.bean.Seq;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * JSONArray????
 *
 * @author Looly
 */
public class JSONArrayTest {
    @Test
    public void addTest() {
        // ??1
        JSONArray array = JSONUtil.createArray();
        // ??2
        // JSONArray array = new JSONArray();
        array.add("value1");
        array.add("value2");
        array.add("value3");
        Assert.assertEquals(array.get(0), "value1");
    }

    @Test
    public void parseTest() {
        String jsonStr = "[\"value1\", \"value2\", \"value3\"]";
        JSONArray array = JSONUtil.parseArray(jsonStr);
        Assert.assertEquals(array.get(0), "value1");
    }

    @Test
    public void parseFileTest() {
        JSONArray array = JSONUtil.readJSONArray(FileUtil.file("exam_test.json"), CHARSET_UTF_8);
        JSONObject obj0 = array.getJSONObject(0);
        Exam exam = JSONUtil.toBean(obj0, Exam.class);
        Assert.assertEquals("0", exam.getAnswerArray()[0].getSeq());
    }

    @Test
    public void toListTest() {
        String jsonStr = FileUtil.readString("exam_test.json", CHARSET_UTF_8);
        JSONArray array = JSONUtil.parseArray(jsonStr);
        List<Exam> list = array.toList(Exam.class);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(Exam.class, list.get(0).getClass());
    }

    @Test
    public void toListTest2() {
        String jsonArr = "[{\"id\":111,\"name\":\"test1\"},{\"id\":112,\"name\":\"test2\"}]";
        JSONArray array = JSONUtil.parseArray(jsonArr);
        List<JSONArrayTest.User> userList = JSONUtil.toList(array, JSONArrayTest.User.class);
        Assert.assertFalse(userList.isEmpty());
        Assert.assertEquals(JSONArrayTest.User.class, userList.get(0).getClass());
        Assert.assertEquals(Integer.valueOf(111), userList.get(0).getId());
        Assert.assertEquals(Integer.valueOf(112), userList.get(1).getId());
        Assert.assertEquals("test1", userList.get(0).getName());
        Assert.assertEquals("test2", userList.get(1).getName());
    }

    @Test
    public void toArrayTest() {
        String jsonStr = FileUtil.readString("exam_test.json", CHARSET_UTF_8);
        JSONArray array = JSONUtil.parseArray(jsonStr);
        Exam[] list = array.toArray(new Exam[0]);
        Assert.assertFalse((0 == (list.length)));
        Assert.assertEquals(Exam.class, list[0].getClass());
    }

    /**
     * ???????????????null?????????
     */
    @Test
    public void toListWithNullTest() {
        String json = "[null,{'akey':'avalue','bkey':'bvalue'}]";
        JSONArray ja = JSONUtil.parseArray(json);
        List<KeyBean> list = ja.toList(KeyBean.class);
        Assert.assertTrue((null == (list.get(0))));
        Assert.assertEquals("avalue", list.get(1).getAkey());
        Assert.assertEquals("bvalue", list.get(1).getBkey());
    }

    @Test
    public void toBeanListTest() {
        List<Map<String, String>> mapList = new ArrayList<>();
        mapList.add(JSONArrayTest.buildMap("0", "0", "0"));
        mapList.add(JSONArrayTest.buildMap("1", "1", "1"));
        mapList.add(JSONArrayTest.buildMap("+0", "+0", "+0"));
        mapList.add(JSONArrayTest.buildMap("-0", "-0", "-0"));
        JSONArray jsonArray = JSONUtil.parseArray(mapList);
        List<JsonNode> nodeList = jsonArray.toList(JsonNode.class);
        Assert.assertEquals(Long.valueOf(0L), nodeList.get(0).getId());
        Assert.assertEquals(Long.valueOf(1L), nodeList.get(1).getId());
        Assert.assertEquals(Long.valueOf(0L), nodeList.get(2).getId());
        Assert.assertEquals(Long.valueOf(0L), nodeList.get(3).getId());
        Assert.assertEquals(Integer.valueOf(0), nodeList.get(0).getParentId());
        Assert.assertEquals(Integer.valueOf(1), nodeList.get(1).getParentId());
        Assert.assertEquals(Integer.valueOf(0), nodeList.get(2).getParentId());
        Assert.assertEquals(Integer.valueOf(0), nodeList.get(3).getParentId());
        Assert.assertEquals("0", nodeList.get(0).getName());
        Assert.assertEquals("1", nodeList.get(1).getName());
        Assert.assertEquals("+0", nodeList.get(2).getName());
        Assert.assertEquals("-0", nodeList.get(3).getName());
    }

    class User {
        private Integer id;

        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return ((("User [id=" + (id)) + ", name=") + (name)) + "]";
        }
    }
}

