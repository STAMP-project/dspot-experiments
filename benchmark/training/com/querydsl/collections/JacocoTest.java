package com.querydsl.collections;


import com.querydsl.core.alias.Alias;
import com.querydsl.core.types.dsl.EntityPathBase;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class JacocoTest {
    public static class CloneableVO {}

    public static class CloneableKlasse implements Cloneable {
        private JacocoTest.CloneableVO value;

        private Integer otherValue;

        public JacocoTest.CloneableVO getValue() {
            return value;
        }

        public void setValue(JacocoTest.CloneableVO value) {
            this.value = value;
        }

        public Integer getOtherValue() {
            return otherValue;
        }

        public void setOtherValue(Integer otherValue) {
            this.otherValue = otherValue;
        }

        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (final CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void withSimpleClass() {
        List<JacocoTest.CloneableKlasse> vos = new ArrayList<JacocoTest.CloneableKlasse>();
        for (int i = 0; i < 5; i++) {
            JacocoTest.CloneableKlasse vo = new JacocoTest.CloneableKlasse();
            vo.setOtherValue(i);
            vos.add(vo);
        }
        JacocoTest.CloneableKlasse vo = Alias.alias(JacocoTest.CloneableKlasse.class, "vo");
        Assert.assertNotNull(vo);
        CollQuery<?> query = new CollQuery<Void>();
        final EntityPathBase<JacocoTest.CloneableKlasse> fromVo = Alias.$(vo);
        Assert.assertNotNull(fromVo);
        query.from(fromVo, vos);
        query.where(Alias.$(vo.getOtherValue()).eq(1));
        List<JacocoTest.CloneableKlasse> result = query.select(Alias.$(vo)).fetch();
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(Integer.valueOf(1), result.get(0).getOtherValue());
    }
}

