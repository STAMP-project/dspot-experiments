package com.ql.util.express.bugfix;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.match.QLPattern;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class TestCompileMemory {
    @Test
    public void test() throws Exception {
        QLPattern.printStackDepth = true;
        List<String> expressList = new ArrayList<String>();
        String demo = "fieldList = formDO.getFieldList();\n" + ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("submitBtn = formDO.getSubmitBtn();\n" + "leaf = parentCat.leaf;\n") + "if (parentCat.getCatId() == 0){\n") + "    firstLevel = true;\n") + "} else {\n") + "    firstLevel = false;\n") + "}\n") + "\n") + "mustLeaf = parentCat.getNamePaths()!=null&&parentCat.getNamePaths().length == 2;\n") + "leafFeatureList = new HashMap ();\n") + "leafFeatureList.put(\"CBU_DEFAULT_SKUPRICE\", \"1\");\n") + "leafFeatureList.put(\"CBU_ISSUPPORTONLINE\", \"1\");\n") + "leafFeatureList.put(\"CBU_MIXED_BATCH\", \"1\");\n") + "leafFeatureList.put(\"CBU_SUPPORT_WHOLESALE\", \"1\");\n") + "leafFeatureList.put(\"CBU_TEMPLATE\", \"1\");\n") + "leafFeatureList.put(\"CBU_USE_SPU\", \"1\");\n") + "firstLevelFeatureList = new HashMap ();\n") + "firstLevelFeatureList.put(\"CBU_TRADE_TYPE\", \"1\");\n") + "\n") + "\n") + "\n") + "for (int i = 0; i < fieldList.size(); i++)\n") + "{\n") + "    field = fieldList.get(i);\n") + "    name = field.getName();\n") + "    if (leaf == true){\n") + "        field.setDisabled(true);\n") + "    }\n") + "    if (name.equals(\"namePaths\")){\n") + "        field.setValue(parentCat.getNamePathsStr());\n") + "    }\n") + "\n") + "    if (name.equals(\"sortOrder\")){\n") + "        field.setValue(-1);\n") + "        field.setOptions(subCatList);\n") + "    }\n") + "    if (name.equals(\"CBU_OPERATOR\")){\n") + "        field.setValue(operator);\n") + "    }\n") + "    if (name.equals(\"gmtModified\")){\n") + "        field.setValue(gmtModified);\n") + "    }\n") + "    if (name.equals(\"leaf\")){\n") + "        if (firstLevel == true){\n") + "            field.setDisabled(true);\n") + "            field.setValue(false);\n") + "        } else {\n") + "            field.setDisabled(false);\n") + "        }\n") + "        if (mustLeaf){\n") + "            field.setDisabled(true);\n") + "            field.setValue(true);\n") + "        }\n") + "    }\n") + "\n") + "    val = leafFeatureList.get(name);\n") + "    if (val!=null&&val.equals(\"1\")){\n") + "        if (mustLeaf){\n") + "            field.setVisible(true);\n") + "        } else {\n") + "            field.setVisible(false);\n") + "        }\n") + "\n") + "    }\n") + "    val = firstLevelFeatureList.get(name);\n") + "    if (val!=null&&val.equals(\"1\")){\n") + "        if (firstLevel == false){\n") + "            field.setVisible(false);\n") + "        }\n") + "    }\n") + "    if (hasPermission == false){\n") + "        field.disabled = true;\n") + "    }\n") + "\n") + "}\n") + "if (hasPermission == false){\n") + "    submitBtn.getSubmitBtn().setVisible(false);\n") + "    field.setDisabled(true);\n") + "    applyHref = submitBtn.getSubmitBtn().getHref();\n") + "    globalerror.setUiType(\"globalError\");\n") + "    globalerror.setValue(\"\u65e0\u64cd\u4f5c\u6743\u9650\uff0c\u8bf7\u70b9\u51fb\u7533\u8bf7\");\n") + "    globalerror.setHref(applyHref);\n") + "    fieldList.add(globalerror);\n") + "} else if (leaf == true){\n") + "    submitBtn.getSubmitBtn().setDisabled(true);\n") + "    globalerror.setUiType(\"globalError\");\n") + "    globalerror.setValue(\"\u53f6\u5b50\u7c7b\u76ee\u4e0b\u4e0d\u80fd\u6dfb\u52a0\u5b50\u7c7b\u76ee\");\n") + "    fieldList.add(globalerror)\n") + "} else {\n") + "    submitBtn.getSubmitBtn().setDisabled(false);\n") + "    submitBtn.getSubmitBtn().setVisible(true);\n") + "}\n") + "formDO.setFieldList(fieldList);\n") + "return formDO;");
        expressList.add(demo);
        demo = "max(1,max(2,max(3,max(4,max(5,max(6,7))))))";
        expressList.add(demo);
        demo = "for(i=0;i<100;i++){System.out.println(11111)}";
        expressList.add(demo);
        for (String express : expressList) {
            ExpressRunner runner2 = new ExpressRunner();
            InstructionSet result2 = runner2.parseInstructionSet(express);
            System.out.println(((express + " \u7f16\u8bd1\u7ed3\u679c\u5982\u4e0b:\n") + result2));
        }
        QLPattern.printStackDepth = false;
    }
}

