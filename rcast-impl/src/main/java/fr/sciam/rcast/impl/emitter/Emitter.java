package fr.sciam.rcast.impl.emitter;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import fr.sciam.rcast.impl.RcastException;
import fr.sciam.rcast.impl.payload.Invocation;
import fr.sciam.rcast.impl.payload.Response;
import lombok.Getter;
import lombok.Setter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static fr.sciam.rcast.impl.Config.INVOCATION_PREFIX;
import static fr.sciam.rcast.impl.Config.RESPONSE_PREFIX;

@ApplicationScoped
public class Emitter implements EntryAddedListener<String, Response> {

    @Inject
    HazelcastInstance instance;

    Map<String, ResponseWrapper> pendingRequests = new ConcurrentHashMap<>();

    protected Object emit(Invocation invocation, String appName, long timeout) throws Throwable {
        IMap<String, Response> responseIMap = getResponseMap(appName);
        IMap<String, Invocation> invocationIMap = getInvocationMap(appName);
        ResponseWrapper wrapper = new ResponseWrapper();
        String requestId = UUID.randomUUID().toString();

        UUID listenerId = responseIMap.addEntryListener(this, requestId, true);
        try{
            pendingRequests.put(requestId, wrapper);
            invocationIMap.put(requestId, invocation);
            synchronized (wrapper) {
                wrapper.wait(timeout);
            }

        }finally {
            responseIMap.removeEntryListener(listenerId);
            pendingRequests.remove(requestId);
        }

        if (wrapper.getResponse() == null) {
            invocationIMap.remove(requestId);
            throw new RcastException("Request timeout");
        }
        responseIMap.remove(requestId);
        if (wrapper.getResponse().getException() != null) {
            throw wrapper.getResponse().getException();
        }
        return wrapper.getResponse().getResult();
    }

    private IMap<String, Invocation> getInvocationMap(String appName) {
        return instance.getMap(INVOCATION_PREFIX + appName);
    }

    private IMap<String, Response> getResponseMap(String appName) {
        return instance.getMap(RESPONSE_PREFIX + appName);
    }

    @Getter
    @Setter
    static
    class ResponseWrapper {
        Response response = null;
    }


    @Override
    public void entryAdded(EntryEvent<String, Response> entryEvent) {
        ResponseWrapper wrapper = pendingRequests.get(entryEvent.getKey());
        if(wrapper == null)
            return; // too late

        synchronized (wrapper) {
            wrapper.setResponse(entryEvent.getValue());
            wrapper.notify();
        }
    }

}
