package com.solo.coderiver.project.repository;


import com.solo.coderiver.project.ProjectApplicationTests;
import com.solo.coderiver.project.dataobject.ProjectInfo;
import com.solo.coderiver.project.utils.KeyUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class ProjectInfoRepositoryTest extends ProjectApplicationTests {
    @Autowired
    ProjectInfoRepository repository;

    @Test
    public void save() {
        ProjectInfo info = new ProjectInfo();
        info.setProjectId(KeyUtils.genUniqueKey());
        info.setProjectName("????");
        info.setCategoryType(1);
        info.setCategoryName("??");
        info.setProjectAvatar("http://xxxxx.png");
        info.setProjectDifficulty(3.5F);
        info.setProjectIntroduce("????????");
        info.setProjectProgress(2);
        info.setProjectCreatorId("222222");
        ProjectInfo save = repository.save(info);
        Assert.assertNotNull(save);
    }

    @Test
    public void findByProjectId() {
        ProjectInfo info = repository.findByProjectId("1539259206662512790");
        Assert.assertNotNull(info);
    }
}

