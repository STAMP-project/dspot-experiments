package com.solo.coderiver.user.repository;


import com.solo.coderiver.user.UserApplicationTest;
import com.solo.coderiver.user.dataobject.TeamApply;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class TeamApplyRepositoryTest extends UserApplicationTest {
    @Autowired
    TeamApplyRepository repository;

    @Test
    public void save() {
        TeamApply teamApply = new TeamApply();
        teamApply.setProjectId("1539259206662512790");
        teamApply.setProjectCreatorId("222222");
        teamApply.setProjectName("????");
        teamApply.setApplicantsId("555555");
        teamApply.setApplicantsName("??");
        teamApply.setApplicantsExperience(2);
        teamApply.setApplicantsRole(2);
        TeamApply save = repository.save(teamApply);
        Assert.assertNotNull(save);
    }

    @Test
    public void findByProjectCreatorId() {
        List<TeamApply> list = repository.findByProjectCreatorId("222222");
        Assert.assertNotEquals(0, list.size());
    }

    @Test
    public void findByApplicantsId() {
        List<TeamApply> list = repository.findByApplicantsId("555555");
        Assert.assertNotEquals(0, list.size());
    }
}

