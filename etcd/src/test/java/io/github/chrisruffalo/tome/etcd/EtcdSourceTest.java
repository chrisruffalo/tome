package io.github.chrisruffalo.tome.etcd;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
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

public class EtcdSourceTest {

    @RegisterExtension
    public static final EtcdClusterExtension cluster = EtcdClusterExtension.builder()
            .withNodes(1)
            .build();

    @Test
    public void testValueSource() {
        final Client client = Client.builder().endpoints(cluster.clientEndpoints()).build();

        final Configuration configuration = new DefaultConfiguration();

        // add configuration
        final Map<String,String> configMap = new HashMap<>();
        configMap.put("db.user", "prod");
        configMap.put("db.host", "ecaga-prod-11");
        configuration.addSource(new MapSource(configMap));

        final KV kv = client.getKVClient();
        kv.put(ByteSequence.from("db.url", StandardCharsets.UTF_8), ByteSequence.from("postgres://${db.user}:${db.password | 'default'}@${db.host}", StandardCharsets.UTF_8));
        configuration.addSource(new EtcdSource(client));

        // db.url is in etcd and is reseolved from there
        Assertions.assertEquals("postgres://prod:default@ecaga-prod-11", configuration.get("db.url").orElse(""));
        Assertions.assertEquals("postgres://prod:default@ecaga-prod-11", configuration.format("${db.url}"));

        // close client
        client.close();
    }

}
