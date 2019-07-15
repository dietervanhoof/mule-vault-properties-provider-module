/*
 * (c) 2003-2018 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package be.dotsandarrows.custom.provider.api;

import static org.mule.metadata.api.model.MetadataFormat.JAVA;
import static org.mule.runtime.api.meta.Category.SELECT;
import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.api.meta.ExpressionSupport.SUPPORTED;

import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.meta.model.declaration.fluent.ConfigurationDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer;
import org.mule.runtime.extension.api.loader.ExtensionLoadingContext;
import org.mule.runtime.extension.api.loader.ExtensionLoadingDelegate;

/**
 * Declares extension for Secure Properties Configuration module
 *
 * @since 1.0
 */
public class VaultPropertiesExtensionLoadingDelegate implements ExtensionLoadingDelegate {

    public static final String EXTENSION_NAME = "Vault Properties Provider";
    public static final String CONFIG_ELEMENT = "config";

  @Override
  public void accept(ExtensionDeclarer extensionDeclarer, ExtensionLoadingContext context) {
    ConfigurationDeclarer configurationDeclarer = extensionDeclarer.named(EXTENSION_NAME)
        .describedAs(String.format("Crafted %s Extension", EXTENSION_NAME))
        .withCategory(SELECT)
        .onVersion("1.0.0")
        .fromVendor("Dots&Arrows")
        // This defines a global element in the extension with name config
        .withConfig(CONFIG_ELEMENT);

    ParameterGroupDeclarer defaultParameterGroup = configurationDeclarer.onDefaultParameterGroup();
    defaultParameterGroup
        .withRequiredParameter("token").ofType(BaseTypeBuilder.create(JAVA).stringType().build())
        .withExpressionSupport(SUPPORTED)
        .describedAs("The token to use to connect to Vault");
    defaultParameterGroup
            .withRequiredParameter("path").ofType(BaseTypeBuilder.create(JAVA).stringType().build())
            .withExpressionSupport(SUPPORTED)
            .describedAs("The path to your Vault K/V store");
    defaultParameterGroup
            .withRequiredParameter("address").ofType(BaseTypeBuilder.create(JAVA).stringType().build())
            .withExpressionSupport(SUPPORTED)
            .describedAs("The address to your Vault instance");
    defaultParameterGroup
            .withOptionalParameter("maxRetries").ofType(BaseTypeBuilder.create(JAVA).numberType().build())
            .withExpressionSupport(SUPPORTED)
            .describedAs("The maximum number of retries to connect to Vault");
    defaultParameterGroup
            .withOptionalParameter("interval").ofType(BaseTypeBuilder.create(JAVA).numberType().build())
            .withExpressionSupport(SUPPORTED)
            .describedAs("The interval between retries");
  }

}
