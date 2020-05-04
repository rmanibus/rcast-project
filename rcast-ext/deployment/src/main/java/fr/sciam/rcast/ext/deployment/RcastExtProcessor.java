package fr.sciam.rcast.ext.deployment;

import fr.sciam.rcast.Rcast;
import fr.sciam.rcast.RegisterRcast;
import fr.sciam.rcast.ext.RcastRecorder;
import io.quarkus.arc.deployment.BeanRegistrarBuildItem;
import io.quarkus.arc.processor.BeanConfigurator;
import io.quarkus.arc.processor.ScopeInfo;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import org.jboss.jandex.*;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import io.quarkus.deployment.annotations.Record;

class RcastExtProcessor {

    private static final Logger log = Logger.getLogger(RcastExtProcessor.class);
    private static final DotName REGISTER_RCAST = DotName.createSimple(RegisterRcast.class.getName());
    private static final DotName RCAST = DotName.createSimple(Rcast.class.getName());

    private static final String FEATURE = "rcast";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    private void processInterfaces(
            CombinedIndexBuildItem indexBuildItem,
            BuildProducer<BeanRegistrarBuildItem> beanRegistrar,
            RcastRecorder recorder
    ) {

        recorder.dummy();

        Map<DotName, ClassInfo> interfaces = this.getInterfaces(indexBuildItem.getIndex());

        log.info("Rcast Interfaces: " + interfaces.keySet().size());
        for (DotName interfaze : interfaces.keySet()) {
            log.info(interfaze.toString());
        }

        beanRegistrar.produce(new BeanRegistrarBuildItem(
                registrationContext -> {
                    for (Map.Entry<DotName, ClassInfo> entry : interfaces.entrySet()) {
                        DotName rcastName = entry.getKey();
                        BeanConfigurator<Object> configurator = registrationContext.configure(rcastName);
                        configurator.addType(rcastName);
                        configurator.addQualifier(RCAST);
                        final ScopeInfo scope = new ScopeInfo(DotName.createSimple(ApplicationScoped.class.getName()), true);
                        //configurator.scope(scope);
                        configurator.param("clazz", rcastName.toString());
                        configurator.param("appName", getAnnotationParameter(entry.getValue()));
                        configurator.creator(RcastBeanCreator.class);
                        configurator.done();
                    }
                }
        ));
    }

    private String getAnnotationParameter(ClassInfo classInfo) {
        AnnotationInstance instance = classInfo.classAnnotation(REGISTER_RCAST);
        if (instance == null) {
            return "";
        }
        AnnotationValue value = instance.value("appName");
        if (value == null) {
            return "";
        }
        return value.asString();
    }

    private Map<DotName, ClassInfo> getInterfaces(IndexView index) {
        Map<DotName, ClassInfo> interfaces = new HashMap<>();
        for (AnnotationInstance annotation : index.getAnnotations(RcastExtProcessor.REGISTER_RCAST)) {
            AnnotationTarget target = annotation.target();
            ClassInfo theInfo;
            if (target.kind() == AnnotationTarget.Kind.CLASS) {
                theInfo = target.asClass();
            } else if (target.kind() == AnnotationTarget.Kind.METHOD) {
                theInfo = target.asMethod().declaringClass();
            } else {
                continue;
            }
            interfaces.put(theInfo.name(), theInfo);
        }
        return interfaces;
    }
}
