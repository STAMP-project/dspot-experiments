package com.solo.coderiver.project.service.mybatis;


import com.solo.coderiver.project.ProjectApplicationTests;
import com.solo.coderiver.project.dataobject.ProjectMember;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 *
 * @unknown solo
 * @unknown 2018/12/13 10:39 AM
 * @version 1.0
 */
public class ProjectMemberServiceMyBatisImplTest extends ProjectApplicationTests {
    @Autowired
    ProjectMemberServiceMyBatisImpl service;

    @Test
    public void findByProjectId() {
        List<ProjectMember> list = service.findByProjectId("1541062468073593543");
        Assert.assertNotEquals(0, list.size());
    }
}

