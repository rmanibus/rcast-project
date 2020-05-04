package fr.sciam.rcast.ext.deployment;

import fr.sciam.rcast.RcastProvider;
import io.quarkus.arc.Arc;
import io.quarkus.arc.BeanCreator;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import java.util.Map;
import java.util.Set;

import org.jboss.logging.Logger;

public class RcastBeanCreator implements BeanCreator<Object> {

    private static final Logger log = Logger.getLogger(RcastBeanCreator.class);

    @Override
    public Object create(CreationalContext<Object> creationalContext, Map<String, Object> param) {
        Class<?> clazz = null;

        try {
            clazz = Class.forName((String) param.get("clazz"));
        } catch (ClassNotFoundException e) {
            log.error("cannot find class " + param.get("clazz"));
        }
        String appName = (String) param.get("appName");
        Set<Bean<?>> beans = Arc.container().beanManager().getBeans(RcastProvider.class);
        for(Bean<?> bean : beans){
            log.info("Provider: " + bean.getName() + " " + bean.getBeanClass().getName());
        }
        RcastProvider provider = Arc.container().instance(RcastProvider.class).get();
        if(provider == null){
            log.error("Cannot find any remote provider implementation");
            return null;
        }
        return provider.getInstance(clazz, appName);
    }
}
