# [Vault](https://www.vaultproject.io/) properties provider Extension

## Description
A Mule extension for Mule 4 that can be used to provide properties to your mule application whilst getting the properties from [HashiCorp Vault](https://www.vaultproject.io/) instead of a file.
This library uses the [BetterCloud Vault Java Driver](https://github.com/BetterCloud/vault-java-driver).

## Usage
Install the project to your local repository with `mvn install` and add the dependency to your pom.xml file:
```xml
<dependency>
  <groupId>be.dotsandarrows</groupId>
  <artifactId>mule-vault-properties-provider-module</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <classifier>mule-plugin</classifier>
</dependency>
```

```xml
<vault-properties-provider:config
  name="Vault_Properties_Provider_Config"
  doc:name="Vault Properties Provider Config"
  token="s.azPGNwcNroSWmceZGtMtYH7U"
  path="secret/vault-test"
  address="http://localhost:8200"/>
```

Then get the property value with the `vault::` prefix:
`vault::myKey`

## Remarks
When a property changes, the changes are not detected.