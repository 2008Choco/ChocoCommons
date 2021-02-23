package wtf.choco.commons.integration;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Handles the native integrations between a plugin and other plugins loaded on the server.
 * This handler is meant primarily for a plugin to better and more safely integrate with
 * other plugins natively.
 * <p>
 * There is no reason for other plugins to work with the integration handler unless work
 * with a specific native integration is required. At no point should a third party plugin
 * be registering its own integration. Third party plugins are expected to work directly
 * with the plugin's API rather than integrating through a {@link PluginIntegration}.
 * <p>
 * To use this class, an instance should be constructed in the plugin's main class and
 * integrations registered onLoad(), followed promptly by a call {@link #integrate()}.
 * At some point in the plugin's {@link JavaPlugin#onEnable()}, the integration handler
 * should invoke {@link #enableIntegrations()}. Additionally, {@link JavaPlugin#onDisable()}
 * should call {@link #disableIntegrations(boolean)}.
 * <pre>
 * public class MyPlugin extends JavaPlugin {
 *
 *     private final IntegrationHandler integrationHandler = new IntegrationHandler(this);
 *
 *     {@code @Override}
 *     public void onLoad() {
 *         this.integrationHandler.registerIntegration("exampleplugin", ExamplePluginIntegration::new);
 *         this.integrationHandler.integrate();
 *     }
 *
 *     {@code @Override}
 *     public void onEnable() {
 *         this.integrationHandler.enableIntegrations();
 *     }
 *
 *     {@code @Override}
 *     public void onDisable() {
 *         this.integrationHandler.disableIntegrations();
 *     }
 *
 * }
 * </pre>
 *
 * @author Parker Hawke - Choco
 *
 * @see PluginIntegration
 */
public class IntegrationHandler {

    private final Map<@NotNull Class<? extends PluginIntegration>, @NotNull PluginIntegration> integrations = new HashMap<>();
    private final Map<@NotNull String, @NotNull Function<@NotNull Plugin, @NotNull PluginIntegration>> integrationConstructors = new HashMap<>();

    private final Logger logger;

    /**
     * Construct an instance of the {@link IntegrationHandler}.
     *
     * @param plugin the plugin instance to which this integration handler belongs
     */
    public IntegrationHandler(@NotNull JavaPlugin plugin) {
        Preconditions.checkArgument(plugin != null, "plugin must not be null");

        this.logger = plugin.getLogger();
    }

    /**
     * Register a new plugin integration.
     *
     * @param pluginId the id of the plugin with which to integrate
     * @param integrationConstructor the function to construct an instance of the PluginIntegration
     */
    public void registerIntegrations(@NotNull String pluginId, @NotNull Function<@NotNull Plugin, @NotNull PluginIntegration> integrationConstructor) {
        Preconditions.checkArgument(pluginId != null, "pluginId must not be null");
        Preconditions.checkArgument(integrationConstructor != null, "integrationConstructor must not be null");

        this.integrationConstructors.put(pluginId, integrationConstructor);
    }

    /**
     * Get an Optional representing the plugin integration that may or may not be present on the
     * server. If the integration was not initialized (or registered), the returned Optional will
     * be empty. Otherwise, it will be populated with the integration instance.
     *
     * @param <T> the plugin integration type being fetched
     * @param integrationClass the class of the plugin integration to fetch
     *
     * @return the plugin integration. If not integrated, an empty Optional
     */
    @NotNull
    public <T extends PluginIntegration> Optional<@NotNull T> getIntegration(@NotNull Class<T> integrationClass) {
        Preconditions.checkArgument(integrationClass != null, "integrationClass must not be null");

        return Optional.ofNullable(integrationClass.cast(integrations.get(integrationClass)));
    }

    /**
     * Get an unmodifiable collection of all integrations in this handler. This only includes
     * integrations that were successfully integrated, supported, and initialized.
     *
     * @return the integrations
     */
    @NotNull
    public Collection<@NotNull ? extends PluginIntegration> getIntegrations() {
        return Collections.unmodifiableCollection(integrations.values());
    }

    /**
     * Integrate and load all integrations registered to this handler.
     */
    public void integrate() {
        this.integrationConstructors.forEach((pluginId, integrationConstructor) -> {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginId);
            if (plugin == null) {
                return;
            }

            PluginIntegration integration = integrationConstructor.apply(plugin);
            if (!integration.isSupported()) {
                return;
            }

            this.integrations.put(integration.getClass(), integration);
            integration.load();
            this.logger.info("Found and integrated with " + integration.getIntegratedPlugin().getName());
        });
    }

    /**
     * Enable all integrations integrated in this handler.
     */
    public void enableIntegrations() {
        this.integrations.values().forEach(PluginIntegration::enable);
    }

    /**
     * Disable all integrations integrated in this handler.
     *
     * @param clearIntegrations whether or not the integrations should be cleared
     */
    public void disableIntegrations(boolean clearIntegrations) {
        this.integrations.values().forEach(PluginIntegration::disable);

        if (clearIntegrations) {
            this.integrationConstructors.clear();
        }
    }

}
