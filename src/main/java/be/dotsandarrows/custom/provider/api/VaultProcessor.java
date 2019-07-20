package be.dotsandarrows.custom.provider.api;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.api.Auth;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Properties;

public class VaultProcessor {
	
	private final Log logger = LogFactory.getLog(getClass());
	
    private String address;
    private String appRoleId;
    private String appRoleSecret;
    private String path;
    private Integer maxRetries = 10;
    private Integer interval = 5000;
        
    private VaultConfig config;
    private Vault vault;
    private Properties props = new Properties();

    public VaultProcessor(String address, String appRoleId, String appRoleSecret, String path, String maxRetries, String interval) throws VaultException {
        // Set all relevant properties
        this.address = address;
        this.appRoleId = appRoleId;
        this.appRoleSecret = appRoleSecret;
        this.path = path;
        this.setMaxRetries(maxRetries);
        this.setInterval(interval);

        // Initialize the vault config
        this.config = this.initializeConfig();

        // Initialize the Vault object
        this.vault = new Vault(this.config);

        // Authenticate with Vault
        this.authenticate();

        // Get all properties and store them locally
        this.getAllProperties();
    }

    private VaultConfig initializeConfig() throws VaultException {
        logger.debug("Initializing Vault config");
        return new VaultConfig()
                .address(address)
                .engineVersion(2)
                .build();
    }

    private void authenticate() throws VaultException {
        this.vault.auth().loginByAppRole(appRoleId, appRoleSecret);
    }

    private void getAllProperties() throws VaultException {
        Map<String, String> data = vault
                .withRetries(this.maxRetries, this.interval)
                .logical()
                .read(path)
                .getData();
        props.putAll(data);
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public void setAddress(String address) {
    	logger.debug("Setting address to " + address);
        this.address = address;
    }

    public void setAppRoleId(String appRoleId) {
        this.appRoleId = appRoleId;
    }

    public void setAppRoleSecret(String appRoleSecret) {
        this.appRoleSecret = appRoleSecret;
    }

    public void setPath(String path) {
    	logger.debug("Setting path to " + path);
        this.path = path;
    }

    public void setMaxRetries(String maxRetries) {
        try {
            if (maxRetries != null && !maxRetries.isEmpty())
                this.maxRetries = Integer.parseInt(maxRetries);
        } catch (NumberFormatException e) {
            logger.debug("No maxRetries value was provided. Defaulting to " + this.maxRetries);
        }
    }

    public void setInterval(String interval) {
        try {
        if (interval != null && !interval.isEmpty())
            this.interval = Integer.parseInt(interval);
        } catch (NumberFormatException e) {
            logger.debug("No interval value was provided. Defaulting to " + this.interval);
        }
    }
}
