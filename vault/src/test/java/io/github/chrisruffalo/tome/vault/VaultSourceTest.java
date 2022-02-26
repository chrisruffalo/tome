package io.github.chrisruffalo.tome.vault;

import com.bettercloud.vault.VaultConfig;
import io.github.chrisruffalo.tome.core.source.SourceContext;
import io.github.chrisruffalo.tome.core.source.Value;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.vault.VaultContainer;

public class VaultSourceTest {

    private static final String TOKEN = "test-token";

    @ClassRule
    public static VaultContainer<?> vaultContainer = new VaultContainer<>("vault:latest")
            .withVaultToken(TOKEN)
            .withSecretInVault("secret/testing", "top_secret=password1","db_password=dbpassword1");

    @Test
    public void someTestMethod() {
        //interact with Vault via the container's host, port and Vault token.
        VaultConfig config = new VaultConfig()
                .token(TOKEN)
                .address(String.format("%s:%d", vaultContainer.getHost(), vaultContainer.getFirstMappedPort()));

        final VaultSource source = new VaultSource(config);

        Assert.assertEquals("password1", source.get(new SourceContext(), "secret/testing:top_secret").orElse(new Value(null)));
    }

}
