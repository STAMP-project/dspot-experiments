package com.querydsl.collections;


import com.querydsl.core.Tuple;
import com.querydsl.core.types.MappingProjection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("serial")
public class MappingProjectionTest extends AbstractQueryTest {
    public class ResultPart {}

    public class ResultObject {}

    @Test
    public void test() {
        final MappingProjection<MappingProjectionTest.ResultPart> key = new MappingProjection<MappingProjectionTest.ResultPart>(MappingProjectionTest.ResultPart.class, cat.name) {
            @Override
            protected MappingProjectionTest.ResultPart map(Tuple row) {
                return new MappingProjectionTest.ResultPart();
            }
        };
        List<MappingProjectionTest.ResultObject> list = query().from(cat, cats).select(new MappingProjection<MappingProjectionTest.ResultObject>(MappingProjectionTest.ResultObject.class, key) {
            @Override
            protected MappingProjectionTest.ResultObject map(Tuple row) {
                MappingProjectionTest.ResultPart consolidationKey = row.get(key);
                return new MappingProjectionTest.ResultObject();
            }
        }).fetch();
        Assert.assertEquals(cats.size(), list.size());
    }
}

