package com.solo.coderiver.project.repository;


import com.solo.coderiver.project.ProjectApplicationTests;
import com.solo.coderiver.project.dataobject.ProjectCategory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class ProjectCategoryRepositoryTest extends ProjectApplicationTests {
    @Autowired
    ProjectCategoryRepository repository;

    @Test
    public void save() {
        ProjectCategory category = new ProjectCategory();
        category.setCategoryName("??");
        category.setCategoryType(1);
        ProjectCategory save = repository.save(category);
        Assert.assertNotNull(save);
    }

    @Test
    public void findByCategoryType() {
        ProjectCategory result = repository.findByCategoryType(1);
        Assert.assertNotNull(result);
    }
}

