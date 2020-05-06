package fr.sciam.rcast.impl.receiver;

import fr.sciam.rcast.impl.RcastException;
import fr.sciam.rcast.impl.payload.Invocation;
import fr.sciam.rcast.impl.payload.Response;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ApplicationScoped
public class Invocator {

    @Inject
    BeanManager manager;

    Response invoke(Invocation invocation){
        try{
            Object instance = getInstance(invocation.getClassName());
            Method method = getMethod(instance, invocation.getMethodName(), invocation.getMethodParameters());
            return doInvoke(instance, method, invocation.getArgs());
        }catch (RcastException e){
            return new Response(null, e);
        }
    }

    private Object getInstance(String className){
        try {
            Bean<?> bean = manager.resolve(manager.getBeans(Class.forName(className), Default.Literal.INSTANCE));
            if (bean == null){
                throw new RcastException("no bean found for " + className);
            }
            return manager.getReference(bean, bean.getBeanClass(), manager.createCreationalContext(bean));
        } catch (ClassNotFoundException e) {
            throw new RcastException("no class found for " + className);
        }
    }

    private Method getMethod(Object instance, String methodName, Class<?>[] methodParameters){
        try {
            return instance.getClass().getMethod(methodName, methodParameters);
        } catch (NoSuchMethodException e) {
            throw new RcastException("no method found");
        }
    }

    private Response doInvoke( Object instance, Method method, Object[] args){
        try {
            return new Response(method.invoke(instance, args), null);
        } catch (IllegalAccessException e) {
            throw new RcastException("illegal access: " + e.getMessage());
        } catch (InvocationTargetException e) {
            return new Response(null, e.getCause());
        }
    }
}
