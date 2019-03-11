package jadx.tests.integration.inline;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TestIssue86 extends IntegrationTest {
    public static class TestCls {
        private static final String SERVER_ERR = "server-err";

        private static final String NOT_FOUND = "not-found";

        private static final String LIST_TAG = "list-tag";

        private static final String TEMP_TAG = "temp-tag";

        private static final String MIN_TAG = "min-tag";

        private static final String MAX_TAG = "max-tag";

        private static final String MILLIS_TAG = "millis-tag";

        private static final String WEATHER_TAG = "weather-tag";

        private static final String DESC_TAG = "desc-tag";

        private List<TestIssue86.TestCls.Day> test(String response) {
            List<TestIssue86.TestCls.Day> reportList = new ArrayList<>();
            try {
                System.out.println(response);
                if ((response != null) && ((response.startsWith(TestIssue86.TestCls.SERVER_ERR)) || (response.startsWith(TestIssue86.TestCls.NOT_FOUND)))) {
                    return reportList;
                }
                TestIssue86.TestCls.JSONObject jsonObj = new TestIssue86.TestCls.JSONObject(response);
                TestIssue86.TestCls.JSONArray days = jsonObj.getJSONArray(TestIssue86.TestCls.LIST_TAG);
                for (int i = 0; i < (days.length()); i++) {
                    TestIssue86.TestCls.JSONObject c = days.getJSONObject(i);
                    long millis = c.getLong(TestIssue86.TestCls.MILLIS_TAG);
                    TestIssue86.TestCls.JSONObject temp = c.getJSONObject(TestIssue86.TestCls.TEMP_TAG);
                    String max = temp.getString(TestIssue86.TestCls.MAX_TAG);
                    String min = temp.getString(TestIssue86.TestCls.MIN_TAG);
                    TestIssue86.TestCls.JSONArray weather = c.getJSONArray(TestIssue86.TestCls.WEATHER_TAG);
                    String weatherDesc = weather.getJSONObject(0).getString(TestIssue86.TestCls.DESC_TAG);
                    TestIssue86.TestCls.Day d = new TestIssue86.TestCls.Day();
                    d.setMilis(millis);
                    d.setMinTmp(min);
                    d.setMaxTmp(max);
                    d.setWeatherDesc(weatherDesc);
                    reportList.add(d);
                }
            } catch (TestIssue86.TestCls.JSONException e) {
                e.printStackTrace();
            }
            return reportList;
        }

        private static class Day {
            public void setMilis(long milis) {
            }

            public void setMinTmp(String min) {
            }

            public void setMaxTmp(String max) {
            }

            public void setWeatherDesc(String weatherDesc) {
            }
        }

        private static class JSONObject {
            public JSONObject(String response) {
            }

            public TestIssue86.TestCls.JSONArray getJSONArray(String tag) throws TestIssue86.TestCls.JSONException {
                return null;
            }

            public TestIssue86.TestCls.JSONObject getJSONObject(String tag) throws TestIssue86.TestCls.JSONException {
                return null;
            }

            public String getString(String tag) throws TestIssue86.TestCls.JSONException {
                return null;
            }

            public long getLong(String tag) throws TestIssue86.TestCls.JSONException {
                return 0;
            }
        }

        private class JSONArray {
            public TestIssue86.TestCls.JSONObject getJSONObject(int i) throws TestIssue86.TestCls.JSONException {
                return null;
            }

            public int length() {
                return 0;
            }
        }

        private class JSONException extends Exception {
            private static final long serialVersionUID = -4358405506584551910L;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestIssue86.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("response.startsWith(NOT_FOUND)"));
    }
}

