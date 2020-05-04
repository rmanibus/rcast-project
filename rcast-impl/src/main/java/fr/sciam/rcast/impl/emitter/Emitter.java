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

import static fr.sciam.rcast.impl.Config.INVOCATION_PREFIX;
import static fr.sciam.rcast.impl.Config.RESPONSE_PREFIX;

@ApplicationScoped
public class Emitter implements EntryAddedListener<String, Response> {

    @Inject
    HazelcastInstance instance;

    volatile Map<String, ResponseWrapper> pendingRequests = new HashMap<>();

    protected Object emit(Invocation invocation, String appName, long timeout) throws Throwable {
        ResponseWrapper wrapper = new ResponseWrapper();
        String requestId = UUID.randomUUID().toString();
        pendingRequests.put(requestId, wrapper);
        IMap<String, Response> responseIMap = getResponseMap(appName);
        UUID listenerId = responseIMap.addEntryListener(this, requestId, true);
        getInvocationMap(appName).put(requestId, invocation);
        synchronized (wrapper) {
            wrapper.wait(timeout);
        }
        responseIMap.removeEntryListener(listenerId);
        pendingRequests.remove(requestId);
        if (wrapper.getResponse() == null) {
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
        volatile Response response = null;
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
