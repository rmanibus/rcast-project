package fr.sciam.rcast.impl.payload;

import lombok.Getter;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Invocation implements Serializable {

    String className;
    String methodName;
    List<String> methodParametersName;
    Object[] args;

    public Invocation(Class<?> clazz, Method method, Object[] args) {
        this.className = clazz.getName();
        this.methodName = method.getName();
        this.methodParametersName = Arrays.stream(method.getParameterTypes()).map(Class::getName).collect(Collectors.toList());
        this.args = args;
    }

    public Class<?>[] getMethodParameters(){
        return
                methodParametersName.stream().map(x -> {
                    try {
                        return Class.forName(x);
                    } catch (ClassNotFoundException e) {
                        // this will throw NoSuchMethodException in getMethod()
                        return null;
                    }
                }).toArray(x -> new Class[methodParametersName.size()]);
    }

}
