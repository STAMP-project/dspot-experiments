package com.solo.coderiver.user.repository;


import com.solo.coderiver.user.UserApplicationTest;
import com.solo.coderiver.user.dataobject.WorkExperience;
import com.solo.coderiver.user.utils.DateUtils;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class WorkExperienceRepositoryTest extends UserApplicationTest {
    @Autowired
    WorkExperienceRepository repository;

    @Test
    public void save() throws Exception {
        WorkExperience work = new WorkExperience();
        work.setUserId("222222");
        work.setPosition("???????");
        work.setComment("?????????????");
        work.setCompanyName("????");
        Date startDate = DateUtils.dateParse("2016-11-10", "yyyy-MM-dd");
        Date endDate = DateUtils.dateParse("2018-5-10", "yyyy-MM-dd");
        work.setStartDate(startDate);
        work.setEndDate(endDate);
        WorkExperience result = repository.save(work);
        Assert.assertNotNull(result);
    }

    @Test
    public void findByUserId() {
        List<WorkExperience> list = repository.findByUserId("222222");
        Assert.assertNotEquals(0, list.size());
    }
}

