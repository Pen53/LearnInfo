package cn.huanzi.qch.springbootasync.rpc.domain;

public class Request {
    private static final long serialVersionUID = 3933918042687238629L;
    private String className;
    private String methodName;
    private Class<?> [] parameTypes;
    private Object [] parameters;
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameTypes() {
        return parameTypes;
    }

    public void setParameTypes(Class<?>[] parameTypes) {
        this.parameTypes = parameTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }


}
