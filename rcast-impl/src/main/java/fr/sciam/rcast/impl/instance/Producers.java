package fr.sciam.rcast.impl.instance;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class Producers {

    @ConfigProperty(name = "app.name")
    String appName;

    @Produces
    @ApplicationScoped
    HazelcastInstance createInstance(Config config){
        return Hazelcast.newHazelcastInstance(config);
    }

    @Produces
    Config createConfig(){
        var config = new Config();
        config.setInstanceName(appName);
        return config;
    }
}
