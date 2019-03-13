/**
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.handlers;


import JdbcType.INTEGER;
import JdbcType.INTEGER.TYPE_CODE;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class EnumTypeHandlerTest extends BaseTypeHandlerTest {
    private static final EnumTypeHandler<EnumTypeHandlerTest.SexEnum> SEX_ENUM_ENUM_TYPE_HANDLER = new EnumTypeHandler(EnumTypeHandlerTest.SexEnum.class);

    private static final EnumTypeHandler<EnumTypeHandlerTest.GradeEnum> GRADE_ENUM_ENUM_TYPE_HANDLER = new EnumTypeHandler(EnumTypeHandlerTest.GradeEnum.class);

    @Getter
    @AllArgsConstructor
    enum SexEnum implements IEnum<Integer> {

        MAN(1, "1"),
        WO_MAN(2, "2");
        Integer code;

        String desc;

        @Override
        public Integer getValue() {
            return this.code;
        }
    }

    @Getter
    @AllArgsConstructor
    enum GradeEnum {

        PRIMARY(1, "??"),
        SECONDARY(2, "??"),
        HIGH(3, "??");
        @EnumValue
        private final int code;

        private final String desc;
    }

    @Test
    @Override
    public void setParameter() throws Exception {
        EnumTypeHandlerTest.SEX_ENUM_ENUM_TYPE_HANDLER.setParameter(preparedStatement, 1, EnumTypeHandlerTest.SexEnum.MAN, null);
        Mockito.verify(preparedStatement).setObject(1, 1);
        EnumTypeHandlerTest.SEX_ENUM_ENUM_TYPE_HANDLER.setParameter(preparedStatement, 2, EnumTypeHandlerTest.SexEnum.WO_MAN, null);
        Mockito.verify(preparedStatement).setObject(2, 2);
        EnumTypeHandlerTest.SEX_ENUM_ENUM_TYPE_HANDLER.setParameter(preparedStatement, 3, null, INTEGER);
        Mockito.verify(preparedStatement).setNull(3, TYPE_CODE);
        EnumTypeHandlerTest.GRADE_ENUM_ENUM_TYPE_HANDLER.setParameter(preparedStatement, 4, EnumTypeHandlerTest.GradeEnum.PRIMARY, null);
        Mockito.verify(preparedStatement).setObject(4, 1);
        EnumTypeHandlerTest.GRADE_ENUM_ENUM_TYPE_HANDLER.setParameter(preparedStatement, 5, EnumTypeHandlerTest.GradeEnum.SECONDARY, null);
        Mockito.verify(preparedStatement).setObject(5, 2);
        EnumTypeHandlerTest.GRADE_ENUM_ENUM_TYPE_HANDLER.setParameter(preparedStatement, 6, null, INTEGER);
        Mockito.verify(preparedStatement).setNull(6, TYPE_CODE);
    }

    @Test
    @Override
    public void getResultFromResultSetByColumnName() throws Exception {
        Mockito.when(resultSet.getObject("column")).thenReturn(null);
        Assertions.assertNull(EnumTypeHandlerTest.SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
        Mockito.when(resultSet.getObject("column")).thenReturn(1);
        Assertions.assertEquals(EnumTypeHandlerTest.SexEnum.MAN, EnumTypeHandlerTest.SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
        Mockito.when(resultSet.getObject("column")).thenReturn(2);
        Assertions.assertEquals(EnumTypeHandlerTest.SexEnum.WO_MAN, EnumTypeHandlerTest.SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
        Mockito.when(resultSet.getObject("column")).thenReturn(null);
        Assertions.assertNull(EnumTypeHandlerTest.GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
        Mockito.when(resultSet.getObject("column")).thenReturn(1);
        Assertions.assertEquals(EnumTypeHandlerTest.GradeEnum.PRIMARY, EnumTypeHandlerTest.GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
        Mockito.when(resultSet.getObject("column")).thenReturn(2);
        Assertions.assertEquals(EnumTypeHandlerTest.GradeEnum.SECONDARY, EnumTypeHandlerTest.GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
    }

    @Test
    @Override
    public void getResultFromResultSetByColumnIndex() throws Exception {
        Mockito.when(resultSet.getObject(1)).thenReturn(1);
        Assertions.assertEquals(EnumTypeHandlerTest.SexEnum.MAN, EnumTypeHandlerTest.SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 1));
        Mockito.when(resultSet.getObject(2)).thenReturn(2);
        Assertions.assertEquals(EnumTypeHandlerTest.SexEnum.WO_MAN, EnumTypeHandlerTest.SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 2));
        Mockito.when(resultSet.getObject(3)).thenReturn(null);
        Assertions.assertNull(EnumTypeHandlerTest.SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 3));
        Mockito.when(resultSet.getObject(4)).thenReturn(1);
        Assertions.assertEquals(EnumTypeHandlerTest.GradeEnum.PRIMARY, EnumTypeHandlerTest.GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 4));
        Mockito.when(resultSet.getObject(5)).thenReturn(2);
        Assertions.assertEquals(EnumTypeHandlerTest.GradeEnum.SECONDARY, EnumTypeHandlerTest.GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 5));
        Mockito.when(resultSet.getObject(6)).thenReturn(null);
        Assertions.assertNull(EnumTypeHandlerTest.GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 6));
    }

    @Test
    @Override
    public void getResultFromCallableStatement() throws Exception {
        Mockito.when(callableStatement.getObject(1)).thenReturn(1);
        Assertions.assertEquals(EnumTypeHandlerTest.SexEnum.MAN, EnumTypeHandlerTest.SEX_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 1));
        Mockito.when(callableStatement.getObject(2)).thenReturn(2);
        Assertions.assertEquals(EnumTypeHandlerTest.SexEnum.WO_MAN, EnumTypeHandlerTest.SEX_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 2));
        Mockito.when(callableStatement.getObject(3)).thenReturn(null);
        Assertions.assertNull(EnumTypeHandlerTest.SEX_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 3));
        Mockito.when(callableStatement.getObject(4)).thenReturn(1);
        Assertions.assertEquals(EnumTypeHandlerTest.GradeEnum.PRIMARY, EnumTypeHandlerTest.GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 4));
        Mockito.when(callableStatement.getObject(5)).thenReturn(2);
        Assertions.assertEquals(EnumTypeHandlerTest.GradeEnum.SECONDARY, EnumTypeHandlerTest.GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 5));
        Mockito.when(callableStatement.getObject(6)).thenReturn(null);
        Assertions.assertNull(EnumTypeHandlerTest.GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 6));
    }
}

