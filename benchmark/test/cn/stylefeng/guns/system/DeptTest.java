package cn.stylefeng.guns.system;


import cn.stylefeng.guns.base.BaseJunit;
import cn.stylefeng.guns.modular.system.entity.Dept;
import cn.stylefeng.guns.modular.system.mapper.DeptMapper;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;


/**
 * ??????
 *
 * @author fengshuonan
 * @unknown 2017-04-27 17:05
 */
public class DeptTest extends BaseJunit {
    @Resource
    DeptMapper deptMapper;

    @Test
    public void addDeptTest() {
        Dept dept = new Dept();
        dept.setFullName("??fullname");
        dept.setSort(5);
        dept.setPid(1L);
        dept.setSimpleName("??");
        dept.setDescription("??tips");
        dept.setVersion(1);
        Integer insert = deptMapper.insert(dept);
        Assert.assertEquals(insert, new Integer(1));
    }

    @Test
    public void updateTest() {
        Dept dept = this.deptMapper.selectById(24);
        dept.setDescription("??");
        deptMapper.updateById(dept);
    }

    @Test
    public void deleteTest() {
        Dept dept = this.deptMapper.selectById(24);
        Integer integer = deptMapper.deleteById(dept);
        Assert.assertTrue((integer > 0));
    }
}

