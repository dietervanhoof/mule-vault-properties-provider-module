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

import com.sun.media.jfxmedia.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.config.api.dsl.model.ConfigurationParameters;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;

import java.util.Optional;

/**
 * Builds the provider for a custom-properties-provider:config element.
 *
 * @since 1.0
 */
public class VaultPropertiesProviderFactory implements ConfigurationPropertiesProviderFactory {

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

    // This is how you can access the configuration parameter of the <custom-properties-provider:config> element.
    String token = parameters.getStringParameter("token");
    String path = parameters.getStringParameter("path");
    String address = parameters.getStringParameter("address");
    String maxRetries = parameters.getStringParameter("maxRetries");
    String interval = parameters.getStringParameter("interval");

    logger.debug("Connecting to " + address + " with path " + path);
    VaultProcessor processor = new VaultProcessor();
    processor.setAddress(address);
    processor.setToken(token);
    processor.setPath(path);
    processor.setInterval(interval);
    processor.setMaxRetries(maxRetries);
    processor.getProperties();
    return new ConfigurationPropertiesProvider() {
      @Override
      public Optional<ConfigurationProperty> getConfigurationProperty(String configurationAttributeKey) {
        // TODO change implementation to discover properties values from your custom source
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
              return processor.getProperty(effectiveKey);
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
