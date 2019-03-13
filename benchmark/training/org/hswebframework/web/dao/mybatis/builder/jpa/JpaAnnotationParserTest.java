package org.hswebframework.web.dao.mybatis.builder.jpa;


import org.hswebframework.ezorm.rdb.meta.RDBTableMetaData;
import org.junit.Assert;
import org.junit.Test;


/**
 * TODO ????
 *
 * @author zhouhao
 * @since 
 */
public class JpaAnnotationParserTest {
    @Test
    public void testParse() {
        RDBTableMetaData metaData = JpaAnnotationParser.parseMetaDataFromEntity(TestEntity.class);
        Assert.assertNotNull(metaData);
        Assert.assertEquals(metaData.getColumns().size(), 5);
    }
}

