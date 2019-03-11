package org.hswebframework.web.service;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hswebframework.ezorm.core.dsl.Query;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.validator.LogicPrimaryKey;
import org.hswebframework.web.validator.group.CreateGroup;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author zhouhao
 * @since 3.0.0-RC
 */
public class DefaultLogicPrimaryKeyValidatorTest {
    DefaultLogicPrimaryKeyValidator validator = new DefaultLogicPrimaryKeyValidator();

    @Test
    public void testSimple() {
        DefaultLogicPrimaryKeyValidator.registerQuerySuppiler(DefaultLogicPrimaryKeyValidatorTest.TestBean.class, ( bean) -> Query.<org.hswebframework.web.service.TestBean, QueryParamEntity>empty(QueryParamEntity.empty()).setSingleExecutor(( param) -> {
            Assert.assertNotNull(param.getTerms());
            Assert.assertEquals(param.getTerms().size(), 2);
            return new org.hswebframework.web.service.TestBean("test", "1");
        }));
        DefaultLogicPrimaryKeyValidatorTest.TestBean bean = new DefaultLogicPrimaryKeyValidatorTest.TestBean("test", "1");
        Assert.assertTrue(validator.validate(bean).isError());
    }

    @Test
    public void testClassAnn() {
        DefaultLogicPrimaryKeyValidator.registerQuerySuppiler(DefaultLogicPrimaryKeyValidatorTest.ClassAnnTestBean.class, ( bean) -> Query.<org.hswebframework.web.service.ClassAnnTestBean, QueryParamEntity>empty(QueryParamEntity.empty()).setSingleExecutor(( param) -> {
            Assert.assertNotNull(param.getTerms());
            Assert.assertEquals(param.getTerms().size(), 2);
            return new org.hswebframework.web.service.ClassAnnTestBean("test", "1");
        }));
        DefaultLogicPrimaryKeyValidatorTest.ClassAnnTestBean bean = new DefaultLogicPrimaryKeyValidatorTest.ClassAnnTestBean("test", "1");
        Assert.assertTrue(validator.validate(bean).isError());
    }

    @Test
    public void testGroupAnn() {
        DefaultLogicPrimaryKeyValidator.registerQuerySuppiler(DefaultLogicPrimaryKeyValidatorTest.GroupAnnTestBean.class, ( bean) -> Query.<org.hswebframework.web.service.GroupAnnTestBean, QueryParamEntity>empty(QueryParamEntity.empty()).setSingleExecutor(( param) -> {
            Assert.assertNotNull(param.getTerms());
            Assert.assertEquals(param.getTerms().size(), 2);
            return new org.hswebframework.web.service.GroupAnnTestBean("test", "1");
        }));
        DefaultLogicPrimaryKeyValidatorTest.GroupAnnTestBean bean = new DefaultLogicPrimaryKeyValidatorTest.GroupAnnTestBean("test", "1");
        Assert.assertTrue(validator.validate(bean).isPassed());
        Assert.assertTrue(validator.validate(bean, DefaultLogicPrimaryKeyValidatorTest.TestGroup.class).isError());
    }

    @Test
    public void testNestProperty() {
        DefaultLogicPrimaryKeyValidatorTest.NestTestBean nestTestBean = new DefaultLogicPrimaryKeyValidatorTest.NestTestBean(new DefaultLogicPrimaryKeyValidatorTest.TestBean("test", "1"), "test");
        DefaultLogicPrimaryKeyValidator.registerQuerySuppiler(DefaultLogicPrimaryKeyValidatorTest.NestTestBean.class, ( bean) -> Query.<org.hswebframework.web.service.NestTestBean, QueryParamEntity>empty(QueryParamEntity.empty()).setSingleExecutor(( param) -> {
            Assert.assertNotNull(param.getTerms());
            Assert.assertEquals(param.getTerms().size(), 2);
            return nestTestBean;
        }));
        Assert.assertTrue(validator.validate(nestTestBean).isError());
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @LogicPrimaryKey(groups = DefaultLogicPrimaryKeyValidatorTest.TestGroup.class)
    public class GroupAnnTestBean {
        private String name;

        private String org;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @LogicPrimaryKey({ "name", "org" })
    public class ClassAnnTestBean {
        private String name;

        private String org;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public class GroupTestBean {
        @LogicPrimaryKey(groups = CreateGroup.class)
        private String name;

        @LogicPrimaryKey
        private String org;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public class NestTestBean {
        @LogicPrimaryKey("nest.name")
        private DefaultLogicPrimaryKeyValidatorTest.TestBean nest;

        @LogicPrimaryKey
        private String org;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public class TestBean {
        @LogicPrimaryKey
        private String name;

        @LogicPrimaryKey
        private String org;
    }

    @LogicPrimaryKey(value = { "name", "org" }, groups = DefaultLogicPrimaryKeyValidatorTest.TestGroup.class)
    public interface TestGroup {}
}

