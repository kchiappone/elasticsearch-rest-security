package net.chiappone.elastic.plugin.util;

import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.settings.Settings;

/**
 * @author Kurtis Chiappone
 */
public class ConfigurationHelper {

    public static final String PREFIX = "elasticsearch.rest.security.";
    private RestSecurityProperties properties = null;
    private boolean pluginEnabled = true;
    private ESLogger logger = null;

    public ConfigurationHelper( Settings settings, ESLogger logger ) {

        this.logger = logger;
        Settings pluginSettings = settings.getByPrefix( PREFIX );

        if ( !pluginSettings.getAsBoolean( "enabled", false ) ) {

            this.pluginEnabled = true;

        }

        try {

            String propertiesFilePath = pluginSettings.get( "properties.file" );

            if ( propertiesFilePath != null && !propertiesFilePath.trim().isEmpty() ) {

                this.properties = new RestSecurityProperties( logger, propertiesFilePath.trim() );

            }

        } catch ( Exception e ) {

            logger.error( "Error loading properties", e );

        }

    }

    public RestSecurityProperties getProperties() {

        return properties;
    }

    public boolean isPluginEnabled() {

        return pluginEnabled;
    }

}
