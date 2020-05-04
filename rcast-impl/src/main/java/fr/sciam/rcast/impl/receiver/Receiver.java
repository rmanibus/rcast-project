package fr.sciam.rcast.impl.receiver;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import fr.sciam.rcast.impl.payload.Invocation;
import fr.sciam.rcast.impl.payload.Response;
import io.quarkus.runtime.StartupEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import static fr.sciam.rcast.impl.Config.INVOCATION_PREFIX;
import static fr.sciam.rcast.impl.Config.RESPONSE_PREFIX;

@ApplicationScoped
public class Receiver implements EntryAddedListener<String, Invocation> {
    @Inject
    HazelcastInstance instance;

    @Inject
    Invocator invocator;

    IMap<String, Response> responseIMap;
    IMap<String, Invocation> invocationIMap;
    @PostConstruct
    protected void setup() {
        responseIMap = instance.getMap(RESPONSE_PREFIX + instance.getConfig().getInstanceName());
        invocationIMap = instance.getMap(INVOCATION_PREFIX + instance.getConfig().getInstanceName());
        invocationIMap.addEntryListener(this, true);

    }


    @Override
    public void entryAdded(EntryEvent<String, Invocation> event) {
        invocationIMap.remove(event.getKey());
        responseIMap.put(event.getKey(), invocator.invoke(event.getValue()));
    }

    void onStart(@Observes StartupEvent ev) {
        //force eager init
    }
}
