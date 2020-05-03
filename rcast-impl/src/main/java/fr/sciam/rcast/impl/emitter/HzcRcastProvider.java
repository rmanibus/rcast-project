package fr.sciam.rcast.impl.emitter;

import fr.sciam.rcast.RcastProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.reflect.Proxy;

import static fr.sciam.rcast.impl.Config.DEFAULT_TIMEOUT;

@ApplicationScoped
public class HzcRcastProvider implements RcastProvider {

    @Inject
    Emitter emitter;

    public <T> T getInstance(Class<T> clazz, String appName){
        return getInstance(clazz, appName, DEFAULT_TIMEOUT);
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> clazz, String appName, long timeout) {
        return (T) Proxy.newProxyInstance(
                HzcRcastProvider.class.getClassLoader(),
                new Class[]{clazz},
                new InvocationProxy(emitter, appName, clazz, timeout)
        );
    }
}
