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
public class HostAuthorizer implements Authorizer {

    private ESLogger logger = null;
    private ConfigurationHelper configuration = null;

    public HostAuthorizer( ConfigurationHelper configuration, ESLogger logger ) {

        this.configuration = configuration;
        this.logger = logger;

    }

    @Override
    public AuthorizationResult authorize( AuthorizationCriteria criteria ) {

        Set<String> whitelist = getConfiguration().getProperties().getHostWhitelist();

        if ( whitelist != null && whitelist.size() > 0 ) {

            for ( String s : whitelist ) {

                String regex = "[/]?" + s.replaceAll( "\\.", "\\\\." ) + "(:\\d+)?";

                if ( criteria.getHost().matches( regex ) ) {

                    logger.debug( "Host is whitelisted: {}", criteria.getHost() );
                    return new AuthorizationResult( AccessLevel.WHITELIST, true );

                }

            }

        }

        return new AuthorizationResult( false );
    }

    public ConfigurationHelper getConfiguration() {

        return configuration;
    }

}
