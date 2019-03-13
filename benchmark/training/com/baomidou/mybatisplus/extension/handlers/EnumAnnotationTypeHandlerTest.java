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
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.ibatis.type.TypeHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class EnumAnnotationTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<EnumAnnotationTypeHandlerTest.SexEnum> HANDLER = new EnumAnnotationTypeHandler(EnumAnnotationTypeHandlerTest.SexEnum.class);

    @Getter
    @AllArgsConstructor
    enum SexEnum {

        MAN(1, "??"),
        WO_MAN(2, "??");
        @EnumValue
        Integer code;

        String desc;
    }

    @Test
    @Override
    public void setParameter() throws Exception {
        EnumAnnotationTypeHandlerTest.HANDLER.setParameter(preparedStatement, 1, null, INTEGER);
        Mockito.verify(preparedStatement).setNull(1, TYPE_CODE);
        EnumAnnotationTypeHandlerTest.HANDLER.setParameter(preparedStatement, 2, EnumAnnotationTypeHandlerTest.SexEnum.MAN, null);
        Mockito.verify(preparedStatement).setObject(2, 1);
        EnumAnnotationTypeHandlerTest.HANDLER.setParameter(preparedStatement, 3, EnumAnnotationTypeHandlerTest.SexEnum.WO_MAN, null);
        Mockito.verify(preparedStatement).setObject(3, 2);
    }

    @Test
    @Override
    public void getResultFromResultSetByColumnName() throws Exception {
        Mockito.when(resultSet.getObject("column")).thenReturn(null);
        Assertions.assertNull(EnumAnnotationTypeHandlerTest.HANDLER.getResult(resultSet, "column"));
        Mockito.when(resultSet.getObject("column")).thenReturn(1);
        Assertions.assertEquals(EnumAnnotationTypeHandlerTest.SexEnum.MAN, EnumAnnotationTypeHandlerTest.HANDLER.getResult(resultSet, "column"));
        Mockito.when(resultSet.getObject("column")).thenReturn(2);
        Assertions.assertEquals(EnumAnnotationTypeHandlerTest.SexEnum.WO_MAN, EnumAnnotationTypeHandlerTest.HANDLER.getResult(resultSet, "column"));
    }

    @Test
    @Override
    public void getResultFromResultSetByColumnIndex() throws Exception {
        Mockito.when(resultSet.getObject(1)).thenReturn(1);
        Assertions.assertEquals(EnumAnnotationTypeHandlerTest.SexEnum.MAN, EnumAnnotationTypeHandlerTest.HANDLER.getResult(resultSet, 1));
        Mockito.when(resultSet.getObject(2)).thenReturn(2);
        Assertions.assertEquals(EnumAnnotationTypeHandlerTest.SexEnum.WO_MAN, EnumAnnotationTypeHandlerTest.HANDLER.getResult(resultSet, 2));
        Mockito.when(resultSet.getObject(3)).thenReturn(null);
        Assertions.assertNull(EnumAnnotationTypeHandlerTest.HANDLER.getResult(resultSet, 3));
    }

    @Test
    @Override
    public void getResultFromCallableStatement() throws Exception {
        Mockito.when(callableStatement.getObject(1)).thenReturn(1);
        Assertions.assertEquals(EnumAnnotationTypeHandlerTest.SexEnum.MAN, EnumAnnotationTypeHandlerTest.HANDLER.getResult(callableStatement, 1));
        Mockito.when(callableStatement.getObject(2)).thenReturn(2);
        Assertions.assertEquals(EnumAnnotationTypeHandlerTest.SexEnum.WO_MAN, EnumAnnotationTypeHandlerTest.HANDLER.getResult(callableStatement, 2));
        Mockito.when(callableStatement.getObject(3)).thenReturn(null);
        Assertions.assertNull(EnumAnnotationTypeHandlerTest.HANDLER.getResult(callableStatement, 3));
    }
}

