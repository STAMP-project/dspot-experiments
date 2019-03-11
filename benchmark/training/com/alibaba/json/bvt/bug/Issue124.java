package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteDateUseDateFormat;
import com.alibaba.fastjson.JSON;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import junit.framework.TestCase;


public class Issue124 extends TestCase {
    public void test_for_issue() throws Exception {
        // final ObjectMapper mapper = new ObjectMapper();
        // mapper.setSerializationInclusion(Include.NON_NULL);
        final Random random = new Random();
        final int threadCount = 1000;
        final CountDownLatch latch = new CountDownLatch(threadCount);
        // {
        // UserInfo info = new UserInfo();
        // CheckInfoVo vo = new CheckInfoVo(100);
        // JSON.toJSONString(new SuccessReturn(info), SerializerFeature.WriteDateUseDateFormat);
        // JSON.toJSONString(new SuccessReturn(vo),
        // SerializerFeature.WriteDateUseDateFormat);
        // }
        for (int i = 0; i < threadCount; i++) {
            new Thread() {
                @Override
                public void run() {
                    Issue124.UserInfo info = new Issue124.UserInfo();
                    Issue124.CheckInfoVo vo = new Issue124.CheckInfoVo(100);
                    int r = random.nextInt();
                    try {
                        if ((r % 2) == 0) {
                            // System.out.println(mapper.writeValueAsString(info));
                            System.out.println(JSON.toJSONString(new Issue124.SuccessReturn(info), WriteDateUseDateFormat));
                        } else {
                            // System.out.println(mapper.writeValueAsString(vo));
                            System.out.println(JSON.toJSONString(new Issue124.SuccessReturn(vo), WriteDateUseDateFormat));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(0);
                    } finally {
                        latch.countDown();
                    }
                }
            }.start();
        }
        latch.await();
    }

    static class SuccessReturn {
        private int code = 0;

        private Object data;

        SuccessReturn(Object data) {
            this.data = data;
        }

        public int getCode() {
            return code;
        }

        public Object getData() {
            return data;
        }
    }

    static class CheckInfoVo {
        private final int gmMessageCount;

        public CheckInfoVo() {
            this.gmMessageCount = 0;
        }

        public CheckInfoVo(int gmMessageCount) {
            this.gmMessageCount = gmMessageCount;
        }

        public int getGmMessageCount() {
            return gmMessageCount;
        }
    }

    static class UserInfo {
        private long uid;

        private String userName;

        private String nickName;

        private int userType;

        private int avatar;

        private String updateTime;

        private int modifyNickanme;// 1????nickname 0????


        private long appid;

        private List<Integer> serverIds;

        public long getAppid() {
            return appid;
        }

        public void setAppid(long appid) {
            this.appid = appid;
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getUserType() {
            return userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }

        public int getAvatar() {
            return avatar;
        }

        public void setAvatar(int avatar) {
            this.avatar = avatar;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getModifyNickanme() {
            return modifyNickanme;
        }

        public void setModifyNickanme(int modifyNickanme) {
            this.modifyNickanme = modifyNickanme;
        }

        public List<Integer> getServerIds() {
            return serverIds;
        }

        public void setServerIds(List<Integer> serverIds) {
            this.serverIds = serverIds;
        }
    }
}

