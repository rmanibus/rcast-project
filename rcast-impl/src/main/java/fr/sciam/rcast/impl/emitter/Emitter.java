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
import java.util.UUID;

import static fr.sciam.rcast.impl.Config.INVOCATION_PREFIX;
import static fr.sciam.rcast.impl.Config.RESPONSE_PREFIX;

@ApplicationScoped
public class Emitter {

    @Inject
    HazelcastInstance instance;

    protected Object emit(Invocation invocation, String appName, long timeout) throws Throwable {
        String uuid = UUID.randomUUID().toString();
        ResponseWrapper lock = new ResponseWrapper();
        getResponseMap(appName).addEntryListener(new ResponseEntryListener(lock), uuid, true);
        getInvocationMap(appName).put(uuid, invocation);
        synchronized (lock) {
            lock.wait(timeout);
        }
        if(lock.getResponse() == null){
            throw new RcastException("Request timeout");
        }
        if(lock.getResponse().getException() != null){
            throw lock.getResponse().getException();
        }
        return lock.getResponse().getResult();
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
    class ResponseWrapper{
        volatile Response response = null;
    }

    static class ResponseEntryListener implements EntryAddedListener<String, Response> {
        final ResponseWrapper lock;

        protected ResponseEntryListener(ResponseWrapper lock) {
            this.lock = lock;
        }

        @Override
        public void entryAdded(EntryEvent<String, Response> entryEvent) {
            synchronized (lock) {
                lock.setResponse(entryEvent.getValue());
                lock.notify();
            }
        }
    }

}
