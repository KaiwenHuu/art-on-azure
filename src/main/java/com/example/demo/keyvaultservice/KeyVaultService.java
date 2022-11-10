package com.example.demo.keyvaultservice;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;

public class KeyVaultService {

  private static SecretClient secretClient = new SecretClientBuilder()
      .vaultUrl("https://instartgram-key-vault.vault.azure.net/").credential(new DefaultAzureCredentialBuilder().build())
      .buildClient();

  public String getSecret(String string) {
    String secretValue = secretClient.getSecret(string).getValue();
    return secretValue;
  }

}
