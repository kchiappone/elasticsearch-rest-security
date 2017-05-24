package net.chiappone.elastic.plugin.auth.authorizer;

import net.chiappone.elastic.plugin.auth.AccessLevel;
import net.chiappone.elastic.plugin.auth.AuthorizationCriteria;
import net.chiappone.elastic.plugin.auth.AuthorizationResult;
import net.chiappone.elastic.plugin.util.ConfigurationHelper;
import org.elasticsearch.common.logging.ESLogger;

import java.util.Set;

/**
 * @author Kurtis Chiappone
 */
public class UrlAuthorizer implements Authorizer {

    private ESLogger logger = null;
    private ConfigurationHelper configuration = null;

    public UrlAuthorizer( ConfigurationHelper configuration, ESLogger logger ) {

        this.configuration = configuration;
        this.logger = logger;

    }

    @Override
    public AuthorizationResult authorize( AuthorizationCriteria criteria ) {

        Set<String> whitelist = getConfiguration().getProperties().getUrlWhitelist();

        if ( whitelist != null && whitelist.size() > 0 ) {

            for ( String s : whitelist ) {

                // As long as it's not a DELETE request

                if ( criteria.getUrl().matches( s ) && !"DELETE"
                        .equalsIgnoreCase( criteria.getHttpMethod() ) ) {

                    logger.debug( "URL is whitelisted: {}", criteria.getUrl() );
                    return new AuthorizationResult( AccessLevel.WHITELIST, true );

                }

            }

        }

        logger.debug( "URL not whitelisted: {}", criteria.getUrl() );
        return new AuthorizationResult( false );

    }

    public ConfigurationHelper getConfiguration() {

        return configuration;
    }

}
