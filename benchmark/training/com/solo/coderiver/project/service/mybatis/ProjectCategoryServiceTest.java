package com.solo.coderiver.project.service.mybatis;


import com.solo.coderiver.project.ProjectApplicationTests;
import com.solo.coderiver.project.dataobject.ProjectCategory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 *
 * @unknown solo
 * @unknown 2018/12/12 2:50 PM
 * @version 1.0
 */
public class ProjectCategoryServiceTest extends ProjectApplicationTests {
    @Autowired
    ProjectCategoryService service;

    @Test
    public void insert() {
        ProjectCategory category = new ProjectCategory();
        category.setCategoryType(11);
        category.setCategoryName("??");
        int result = service.insert(category);
        Assert.assertEquals(1, result);
    }

    @Test
    public void deleteByType() {
        int result = service.deleteByType(10);
        Assert.assertEquals(1, result);
    }

    @Test
    public void update() {
        ProjectCategory category = new ProjectCategory();
        category.setCategoryType(11);
        category.setCategoryName("??");
        int result = service.update(category);
        Assert.assertEquals(1, result);
    }

    @Test
    public void findByType() {
        ProjectCategory result = service.findByType(11);
        Assert.assertNotNull(result);
    }
}

