package com.alibaba.json.bvt.bug;


import java.util.LinkedList;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 06/12/2016.
 */
public class Issue912 extends TestCase {
    public void test_for_issue() throws Exception {
        String allMethods = "{\"mList\":[{\"className\":\"com.qa.scftemplate.contract.ISCFServiceForDyjAction\",\"methodName\":\"getArrayInt\",\"parameterSize\":1,\"parameters\":[{\"clazz\":\"[I\",\"clsList\":null,\"isGenericity\":false,\"value\":\"\"}],\"returnType\":\"[I\",\"url\":\"tcp://SCFServiceForDyj/SCFServiceForDyjActionService\"},{\"className\":\"com.qa.scftemplate.contract.ISCFServiceForDyjAction\",\"methodName\":\"getArrayPrimative\",\"parameterSize\":7,\"parameters\":[{\"clazz\":\"[I\",\"clsList\":null,\"isGenericity\":false,\"value\":\"\"},{\"clazz\":\"[F\",\"clsList\":null,\"isGenericity\":false,\"value\":\"\"},{\"clazz\":\"[S\",\"clsList\":null,\"isGenericity\":false,\"value\":\"\"},{\"clazz\":\"[D\",\"clsList\":null,\"isGenericity\":false,\"value\":\"\"},{\"clazz\":\"[J\",\"clsList\":null,\"isGenericity\":false,\"value\":\"\"},{\"clazz\":\"[B\",\"clsList\":null,\"isGenericity\":false,\"value\":\"\"},{\"clazz\":\"[C\",\"clsList\":null,\"isGenericity\":false,\"value\":\"\"}],\"returnType\":\"[Ljava.lang.String;\",\"url\":\"tcp://SCFServiceForDyj/SCFServiceForDyjActionService\"}]}";
        Issue912.JsonBean jsonBean = Issue912.getJsonData(allMethods, Issue912.JsonBean.class);
        TestCase.assertEquals(2, jsonBean.getmList().size());
        Issue912.SCFMethod m1 = jsonBean.getmList().get(0);
        TestCase.assertNotNull(m1);
    }

    public static class JsonBean {
        private List<Issue912.SCFMethod> mList;

        public List<Issue912.SCFMethod> getmList() {
            return mList;
        }

        public void setmList(List<Issue912.SCFMethod> mList) {
            this.mList = mList;
        }
    }

    public static class SCFMethod {
        public String className;

        public String url;

        public String methodName;

        public int parameterSize;

        public List<Issue912.SCFMethodParameter> parameters = new LinkedList<Issue912.SCFMethodParameter>();

        public Class<?> returnType;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public Class<?> getReturnType() {
            return returnType;
        }

        public void setReturnType(Class<?> returnType) {
            this.returnType = returnType;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public int getParameterSize() {
            return parameterSize;
        }

        public void setParameterSize(int parameterSize) {
            this.parameterSize = parameterSize;
        }

        public List<Issue912.SCFMethodParameter> getParameters() {
            return parameters;
        }

        public void setParameters(List<Issue912.SCFMethodParameter> parameters) {
            this.parameters = parameters;
        }
    }

    public static class SCFMethodParameter implements Cloneable {
        public Class<?> clazz;

        public Object value;

        public boolean isGenericity = false;

        public List<Class<?>> clsList;

        public boolean getIsGenericity() {
            return isGenericity;
        }

        public void setIsGenericity(boolean isGenericity) {
            this.isGenericity = isGenericity;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        public List<Class<?>> getClsList() {
            return clsList;
        }

        public void setClsList(List<Class<?>> clsList) {
            this.clsList = clsList;
        }
    }
}

