package io.github.chrisruffalo.tome.etcd;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.test.EtcdClusterExtension;
import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.configuration.DefaultConfiguration;
import io.github.chrisruffalo.tome.core.source.MapSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EtcdSourceTest {

    @RegisterExtension
    public static final EtcdClusterExtension cluster = EtcdClusterExtension.builder()
            .withNodes(1)
            .build();

    @Test
    public void testValueSource() throws ExecutionException, InterruptedException {
        final ClientBuilder clientBuilder = Client.builder().endpoints(cluster.clientEndpoints());

        final Configuration configuration = new DefaultConfiguration();

        // add configuration
        final Map<String,String> configMap = new HashMap<>();
        configMap.put("db.user", "prod");
        configMap.put("db.host", "ecaga-prod-11");
        configuration.addSource(new MapSource(configMap));

        try (
            final Client client = clientBuilder.build();
            final KV kv = client.getKVClient();
        ) {
            final CompletableFuture<PutResponse> future = kv.put(ByteSequence.from("db.url", StandardCharsets.UTF_8), ByteSequence.from("postgres://${db.user}:${db.password | 'default'}@${db.host}", StandardCharsets.UTF_8));
            future.join();
        }

        configuration.addSource(new EtcdSource(clientBuilder));

        // db.url is in etcd and is reseolved from there
        Assertions.assertEquals("postgres://prod:default@ecaga-prod-11", configuration.get("db.url").orElse(""));
        Assertions.assertEquals("postgres://prod:default@ecaga-prod-11", configuration.format("${db.url}"));
    }

}
