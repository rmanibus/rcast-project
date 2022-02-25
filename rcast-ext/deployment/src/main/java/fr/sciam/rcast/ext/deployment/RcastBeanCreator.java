package fr.sciam.rcast.ext.deployment;

import fr.sciam.rcast.RcastProvider;
import io.quarkus.arc.Arc;
import io.quarkus.arc.BeanCreator;
import org.jboss.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Map;

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
        BeanManager manager = Arc.container().beanManager();
        Bean<?> bean = manager.resolve(manager.getBeans(RcastProvider.class));

        if (bean == null) {
            log.error("Cannot find any remote provider implementation");
            return null;
        }

        RcastProvider provider = (RcastProvider) manager.getReference(bean, bean.getBeanClass(), manager.createCreationalContext(bean));

        return provider.getInstance(clazz, appName);
    }
}
