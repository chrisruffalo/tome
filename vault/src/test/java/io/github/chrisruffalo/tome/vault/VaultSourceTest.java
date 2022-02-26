package io.github.chrisruffalo.tome.vault;

import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.configuration.DefaultConfiguration;
import io.github.chrisruffalo.tome.core.source.MapSource;
import io.github.chrisruffalo.tome.core.source.PrefixedSource;
import io.github.chrisruffalo.tome.core.source.SourceContext;
import io.github.chrisruffalo.tome.core.source.Value;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.vault.VaultContainer;

import java.util.HashMap;
import java.util.Map;

public class VaultSourceTest {

    private static final String TOKEN = "test-token";

    @ClassRule
    public static VaultContainer<?> vaultContainer = new VaultContainer<>("vault:latest")
            .withVaultToken(TOKEN)
            .withSecretInVault("secret/testing", "top_secret=password1","db_password=dbpassword1");

    @Test
    public void someTestMethod() throws VaultException {
        //interact with Vault via the container's host, port and Vault token.
        VaultConfig config = new VaultConfig()
                .token(TOKEN)
                .address(String.format("http://%s:%d", vaultContainer.getHost(), vaultContainer.getFirstMappedPort()))
                .build();

        final VaultSource source = new VaultSource(config);

        Assert.assertEquals("password1", source.get(new SourceContext(), "secret/testing:top_secret").orElse(new Value(null)).toString());
    }


    @Test
    public void testComposition() throws VaultException {
        final Configuration configuration = new DefaultConfiguration();

        // add configuration
        final Map<String,String> configMap = new HashMap<>();
        configMap.put("vault.host", vaultContainer.getHost());
        configMap.put("vault.port", Integer.toString(vaultContainer.getFirstMappedPort()));
        configMap.put("vault.url", "http://${vault.host}:${vault.port}");
        configMap.put("vault.token", TOKEN);
        configMap.put("db.user", "prod");
        configMap.put("db.host", "ecaga-prod-11");
        configMap.put("db.url", "postgres://${db.user}:${vault:secret/testing:top_secret | 'default'}@${db.host}");
        configuration.addSource(new MapSource(configMap));

        // use previous configuration to load
        VaultConfig config = new VaultConfig()
                .token(configuration.get("vault.token").orElse(""))
                .address(configuration.get("vault.url").orElse(""))
                .build();

        final VaultSource source = new VaultSource(config);
        configuration.addSource(new PrefixedSource("vault:", source));

        // resolve out host
        Assert.assertEquals("postgres://prod:password1@ecaga-prod-11", configuration.get("db.url").orElse(""));
    }
}
