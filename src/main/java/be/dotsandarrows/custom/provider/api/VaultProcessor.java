package be.dotsandarrows.custom.provider.api;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Properties;

public class VaultProcessor {
	
	private final Log logger = LogFactory.getLog(getClass());
	
    private String address;
    private String token;
    private String path;
    private Integer maxRetries = 10;
    private Integer interval = 5000;
        
    private VaultConfig config;

    private Properties props = new Properties();

    protected void getProperties() {
        logger.warn("Getting properties from Vault");
        try {
            config = new VaultConfig()
                    .address(address)
                    .token(token)
                    .engineVersion(2)
                    .build();
            final Vault vault = new Vault(config);
            Map<String, String> data = vault
            		.withRetries(maxRetries, interval)
            		.logical()
                    .read(path)
                    .getData();
            props.putAll(data);
        } catch (VaultException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public void setAddress(String address) {
    	logger.debug("Setting address to " + address);
        this.address = address;
    }

    public void setToken(String token) {
    	logger.debug("Setting token to " + token);
        this.token = token;
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
