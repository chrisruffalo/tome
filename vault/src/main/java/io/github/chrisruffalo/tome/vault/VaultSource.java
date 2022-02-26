package io.github.chrisruffalo.tome.vault;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.LogicalResponse;
import io.github.chrisruffalo.tome.core.source.Source;
import io.github.chrisruffalo.tome.core.source.SourceContext;
import io.github.chrisruffalo.tome.core.source.Value;

import java.util.Optional;

/**
 * Using a given vault config connect to a vault and read values, requires a special
 * property name like "/path/to/property/parent:propertyName" to get the value from
 * the map at the vault level.
 */
public class VaultSource implements Source {

    private final VaultConfig vaultConfig;

    public VaultSource(final VaultConfig config) {
        this.vaultConfig = config;
    }

    @Override
    public Optional<Value> get(SourceContext sourceContext, String propertyName) {

        if(!propertyName.contains(":")) {
            return Optional.empty();
        }

        final String[] split = propertyName.split(":");
        final String path = split[0];
        final String key = split[1];

        // no value found because the path is not helpful
        if (path == null || path.isEmpty() || key == null || key.isEmpty()) {
            return Optional.empty();
        }

        final Vault vault = new Vault(this.vaultConfig);

        try {
            // perform logical read from vault path
            final LogicalResponse response = vault.logical().read(path);

            // extract value from key
            if(response.getData().containsKey(key)) {
                return Optional.of(new Value(response.getData().get(key)));
            }
        } catch (VaultException e) {
            sourceContext.getLogger().error(String.format("Could not complete vault operation for property path %s, key %s", path, key), e);
        }

        return Optional.empty();
    }
}
