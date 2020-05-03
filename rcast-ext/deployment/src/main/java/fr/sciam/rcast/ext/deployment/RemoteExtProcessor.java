package fr.sciam.rcast.ext.deployment;

import fr.sciam.rcast.Rcast;
import fr.sciam.rcast.RegisterRcast;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;

import javax.enterprise.inject.spi.configurator.BeanConfigurator;
import java.util.Map;

class RemoteExtProcessor {

    private static final DotName REGISTER_REMOTE = DotName.createSimple(RegisterRcast.class.getName());
    private static final DotName REMOTE = DotName.createSimple(Rcast.class.getName());

    private static final String FEATURE = "remote-ext";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    private void processInterfaces(
            CombinedIndexBuildItem indexBuildItem,
           BuildProducer<BeanRegistrarBuildItem> beanRegistrar
    ){
        Map<DotName, ClassInfo> interfaces = getInterfaces(indexBuildItem.getIndex(), REGISTER_REMOTE);
        beanRegistrar.produce(new BeanRegistrarBuildItem(
                new BeanRegistrar(){
                    @Override
                    public void register(RegistrationContext registrationContext){
                        for(Map.Entry<DotName, ClassInfo> entry: interfaces.entrySet()){
                            DotName remoteName = entry.getKey();
                            BeanConfigurator<Object> configurator = registrationContext.configure(remoteName);
                            configurator.addType(remoteName);
                            configurator.addQualifier(REMOTE);
                            configurator.creator(m -> {
                            // How to I call the method on my bean to create the proxy ?
                            });
                        }
                    }
                }
        ));
    }

    private Map<DotName, ClassInfo> getInterfaces(){
        ...
    }
}
