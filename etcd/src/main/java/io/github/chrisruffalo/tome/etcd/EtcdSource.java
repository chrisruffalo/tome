package io.github.chrisruffalo.tome.etcd;

import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.github.chrisruffalo.tome.core.source.Source;
import io.github.chrisruffalo.tome.core.source.SourceContext;
import io.github.chrisruffalo.tome.core.source.Value;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EtcdSource implements Source {

    private final ClientBuilder clientBuilder;

    public EtcdSource(final ClientBuilder clientBuilder) {
        this.clientBuilder = clientBuilder;
    }

    @Override
    public Optional<Value> get(SourceContext sourceContext, String propertyName) {
        final ByteSequence key = getKey(propertyName);
        try (
            final Client client = this.clientBuilder.build();
            final KV kv = client.getKVClient();
        ) {
            CompletableFuture<GetResponse> getFuture = kv.get(key);
            GetResponse response = getFuture.get();
            if (response.getCount() < 1) {
                return Optional.empty();
            }
            List<KeyValue> values = response.getKvs();
            Optional<String> stringValue = values
                    .stream()
                    .map(keyValue -> keyValue.getValue().toString())
                    .filter(value -> value != null && !value.isEmpty())
                    .findFirst();
            // map the found value (a string) into the container value
            return stringValue.map(Value::new);
        } catch (InterruptedException | ExecutionException e) {
            sourceContext.getLoggerFactory().get(this.getClass()).error(String.format("Could not complete ETCD operation for property %s", propertyName), e);
        }

        return Optional.empty();
    }

    private ByteSequence getKey(String propertyName) {
        return ByteSequence.from(propertyName.getBytes());
    }
}
