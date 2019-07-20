/*
 * (c) 2003-2018 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package be.dotsandarrows.custom.provider.api;

import static be.dotsandarrows.custom.provider.api.VaultPropertiesExtensionLoadingDelegate.CONFIG_ELEMENT;
import static be.dotsandarrows.custom.provider.api.VaultPropertiesExtensionLoadingDelegate.EXTENSION_NAME;
import static org.mule.runtime.api.component.ComponentIdentifier.builder;

import com.bettercloud.vault.VaultException;
import com.sun.media.jfxmedia.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.config.api.dsl.model.ConfigurationParameters;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;

import java.net.ConnectException;
import java.util.Optional;

/**
 * Builds the provider for a custom-properties-provider:config element.
 *
 * @since 1.0
 */
public class VaultPropertiesProviderFactory implements ConfigurationPropertiesProviderFactory {
  private static final String ROLE_ID = "roleId";
  private static final String SECRET_ID = "secretId";
  private static final String PATH = "path";
  private static final String ADDRESS = "address";
  private static final String MAX_RETRIES = "maxRetries";
  private static final String INTERVAL = "interval";

  public static final String EXTENSION_NAMESPACE =
      EXTENSION_NAME.toLowerCase().replace(" ", "-");
  private static final ComponentIdentifier CUSTOM_PROPERTIES_PROVIDER =
      builder().namespace(EXTENSION_NAMESPACE).name(CONFIG_ELEMENT).build();
  private final static String CUSTOM_PROPERTIES_PREFIX = "vault::";
  private final Log logger = LogFactory.getLog(getClass());

  @Override
  public ComponentIdentifier getSupportedComponentIdentifier() {
    return CUSTOM_PROPERTIES_PROVIDER;
  }

  @Override
  public ConfigurationPropertiesProvider createProvider(ConfigurationParameters parameters,
                                                        ResourceProvider externalResourceProvider) {
    String roleId = parameters.getStringParameter(ROLE_ID);
    String secretId = parameters.getStringParameter(SECRET_ID);
    String path = parameters.getStringParameter(PATH);
    String address = parameters.getStringParameter(ADDRESS);
    String maxRetries = parameters.getStringParameter(MAX_RETRIES);
    String interval = parameters.getStringParameter(INTERVAL);
    VaultProcessor processor = null;
    logger.debug("Connecting to " + address + " with path " + path);
    try {
       processor = new VaultProcessor(address, roleId, secretId, path, maxRetries, interval);
    }
    catch (VaultException e) {
      logger.error("There was an error connecting to Vault: " + e.getMessage());
      throw new IllegalArgumentException(e.getMessage());
    }

    VaultProcessor finalProcessor = processor;
    return new ConfigurationPropertiesProvider() {
      @Override
      public Optional<ConfigurationProperty> getConfigurationProperty(String configurationAttributeKey) {
        logger.debug("Need to get property with key: " + configurationAttributeKey);
        if (configurationAttributeKey.startsWith(CUSTOM_PROPERTIES_PREFIX)) {
          String effectiveKey = configurationAttributeKey.substring(CUSTOM_PROPERTIES_PREFIX.length());
          return Optional.of(new ConfigurationProperty() {

            @Override
            public Object getSource() {
              return "Vault";
            }

            @Override
            public Object getRawValue() {
              if (finalProcessor != null)
                return finalProcessor.getProperty(effectiveKey);
              return Optional.empty();
            }

            @Override
            public String getKey() {
              return effectiveKey;
            }
          });
        }
        return Optional.empty();
      }

      @Override
      public String getDescription() {
        return "Vault properties provider";
      }
    };
  }

}
