package org.hswebframework.web.service.form.simple;


import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.hswebframework.ezorm.rdb.executor.SqlExecutor;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.entity.form.DynamicFormColumnBindEntity;
import org.hswebframework.web.entity.form.DynamicFormColumnEntity;
import org.hswebframework.web.entity.form.DynamicFormEntity;
import org.hswebframework.web.service.form.DatabaseRepository;
import org.hswebframework.web.service.form.DynamicFormColumnService;
import org.hswebframework.web.service.form.DynamicFormOperationService;
import org.hswebframework.web.service.form.DynamicFormService;
import org.hswebframework.web.tests.SimpleWebApplicationTests;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * TODO ????
 *
 * @author zhouhao
 */
public class SimpleDynamicFormServiceTest extends SimpleWebApplicationTests {
    @Autowired
    private DynamicFormService dynamicFormService;

    @Autowired
    private DynamicFormColumnService dynamicFormColumnService;

    @Autowired
    private DynamicFormOperationService dynamicFormOperationService;

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private SqlExecutor sqlExecutor;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testDeploy() throws SQLException {
        DynamicFormEntity form = entityFactory.newInstance(DynamicFormEntity.class);
        form.setName("test");
        form.setDatabaseTableName("f_test");
        form.setTriggers(("[" + (((("{\"trigger\":\"select.wrapper.done\""// ??? ????????????????
         + ",\"language\":\"groovy\"") + ",\"script\":\"println(\'wrapper done:\'+instance);return true;\"") + "}") + "]")));
        form.setCorrelations(("[" + ("{\"target\":\"s_dyn_form\",\"alias\":\"form\",\"condition\":\"form.u_id=f_test.id\"}" + "]")));
        DynamicFormColumnEntity column_name = entityFactory.newInstance(DynamicFormColumnEntity.class);
        column_name.setName("??");
        column_name.setColumnName("name");
        column_name.setJavaType("string");
        column_name.setJdbcType(JDBCType.VARCHAR.getName());
        column_name.setLength(32);
        column_name.setValidator(Arrays.asList("{\"type\":\"NotBlank\",\"groups\":[\"create\"],\"message\":\"\u59d3\u540d\u4e0d\u80fd\u4e3a\u7a7a\"}"));
        DynamicFormColumnEntity column_age = entityFactory.newInstance(DynamicFormColumnEntity.class);
        column_age.setName("??");
        column_age.setColumnName("age");
        column_age.setJavaType("int");
        column_age.setJdbcType(JDBCType.NUMERIC.getName());
        column_age.setPrecision(4);
        column_age.setScale(0);
        // Stream.of(column_id, column_name, column_age).forEach(dynamicFormColumnService::insert);
        DynamicFormColumnBindEntity bindEntity = new DynamicFormColumnBindEntity();
        bindEntity.setForm(form);
        bindEntity.setColumns(Arrays.asList(column_name, column_age));
        String id = dynamicFormService.saveOrUpdate(bindEntity);
        dynamicFormService.deploy(id);
        dynamicFormOperationService.insert(form.getId(), new HashMap<String, Object>() {
            {
                put("name", "??");
                put("age", 10);
            }
        });
        databaseRepository.getDefaultDatabase().getTable("s_dyn_form");
        List<Object> objects = dynamicFormOperationService.select(form.getId(), new QueryParamEntity().includes("*", "form.*"));
        Assert.assertTrue(((objects.size()) == 1));
        System.out.println(objects);
        System.out.println(dynamicFormService.select());
        System.out.println(sqlExecutor.list("select * from s_dyn_form"));
    }
}

