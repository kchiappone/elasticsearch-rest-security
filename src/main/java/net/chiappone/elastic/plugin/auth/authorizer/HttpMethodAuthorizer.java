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
public class HttpMethodAuthorizer implements Authorizer {

    private ESLogger logger = null;
    private ConfigurationHelper configuration = null;

    public HttpMethodAuthorizer( ConfigurationHelper configuration, ESLogger logger ) {

        this.configuration = configuration;
        this.logger = logger;

    }

    @Override
    public AuthorizationResult authorize( AuthorizationCriteria criteria ) {

        Set<String> whitelist = getConfiguration().getProperties().getHttpMethodWhitelist();

        if ( whitelist != null && whitelist.size() > 0 ) {

            for ( String s : whitelist ) {

                if ( s.equalsIgnoreCase( criteria.getHttpMethod() ) ) {

                    logger.debug( "HTTP {} is whitelisted", criteria.getHttpMethod() );
                    return new AuthorizationResult( AccessLevel.WHITELIST, true );

                }

            }

        }

        logger.debug( "HTTP {} not whitelisted", criteria.getHttpMethod() );
        return new AuthorizationResult( false );

    }

    public ConfigurationHelper getConfiguration() {

        return configuration;
    }

}
