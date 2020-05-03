package fr.sciam.rcast.impl.emitter;

import fr.sciam.rcast.impl.payload.Invocation;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@AllArgsConstructor
public class InvocationProxy implements InvocationHandler {

    private final Emitter emitter;
    private final String appName;
    private final Class<?> clazz;
    private final long timeout;
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return emitter.emit(new Invocation(clazz, method, args), appName, timeout);
    }
}
