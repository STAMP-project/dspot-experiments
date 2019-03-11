package com.solo.coderiver.comments.service.impl;


import CommentsTypeEnum.USER;
import com.solo.coderiver.comments.CommentsApplicationTest;
import com.solo.coderiver.comments.dto.CommentsInfoDTO;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class CommentsInfoServiceImplTest extends CommentsApplicationTest {
    @Autowired
    CommentsInfoServiceImpl service;

    @Test
    public void save() {
        CommentsInfoDTO info = new CommentsInfoDTO();
        info.setId("abc123");
        info.setType(USER.getCode());
        info.setOwnerId("1541062468073593543");
        info.setFromId("333333");
        info.setFromName("??");
        info.setContent("???????");
        info.setLikeNum(3);
        CommentsInfoDTO info1 = new CommentsInfoDTO();
        info1.setPid("abc123");
        info1.setId("abc456");
        info1.setType(USER.getCode());
        info1.setOwnerId("1541062468073593543");
        info1.setFromId("222222");
        info1.setFromName("??");
        info1.setContent("????????????");
        info1.setLikeNum(2);
        CommentsInfoDTO result = service.save(info1);
        Assert.assertNotNull(result);
    }

    @Test
    public void findByOwnerId() {
        List<CommentsInfoDTO> result = service.findByOwnerId("1541062468073593543");
        Assert.assertNotEquals(0, result.size());
    }
}

