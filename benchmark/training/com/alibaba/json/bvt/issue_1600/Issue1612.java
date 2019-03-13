package com.alibaba.json.bvt.issue_1600;


import org.junit.Test;


/**
 * <p>Title: Issue1612</p>
 * <p>Description: </p>
 *
 * @author Victor
 * @version 1.0
 * @since 2017/11/27
 */
public class Issue1612 {
    @Test
    public void test() {
        RegResponse<User> userRegResponse = Issue1612.testFastJson(User.class);
        User user = userRegResponse.getResult();
        System.out.println(user);
    }
}

