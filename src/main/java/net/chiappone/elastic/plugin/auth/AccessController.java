package net.chiappone.elastic.plugin.auth;

import net.chiappone.elastic.plugin.auth.authorizer.*;
import net.chiappone.elastic.plugin.util.ConfigurationHelper;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.rest.RestRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kurtis Chiappone
 */
public class AccessController {

    private List<Authorizer> authorizers = null;
    private ConfigurationHelper configuration = null;
    private ESLogger logger = null;

    public AccessController( ConfigurationHelper configuration, ESLogger logger ) {

        this.configuration = configuration;
        this.logger = logger;

    }

    public List<Authorizer> getAuthorizers() {

        if ( authorizers == null ) {

            this.authorizers = new ArrayList<Authorizer>();
            this.authorizers.add( new HttpMethodAuthorizer( getConfiguration(), logger ) );
            this.authorizers.add( new UrlAuthorizer( getConfiguration(), logger ) );
            this.authorizers.add( new HostAuthorizer( getConfiguration(), logger ) );
            this.authorizers.add( new UserAuthorizer( getConfiguration(), logger ) );

        }

        return authorizers;
    }

    public ConfigurationHelper getConfiguration() {

        return configuration;
    }

    public boolean isAuthorized( RestRequest request ) {

        // If this somehow happens to be a null request, deny authorization

        if ( request == null ) {

            logger.trace( "isAuthorized() request null" );
            return false;

        }

        // Otherwise build the authorization criteria

        String host = request.getRemoteAddress().toString();
        String httpMethod = request.method().toString();
        String url = request.uri();
        String authorizationHeader = request.header( "Authorization" );

        AuthorizationCriteria criteria = new AuthorizationCriteria( host, httpMethod, url,
                authorizationHeader );

        // And loop through each of the authorizers to find a match

        AuthorizationResult result = new AuthorizationResult( false );

        for ( Authorizer authorizer : getAuthorizers() ) {

            result = authorizer.authorize( criteria );

            // Found a match, so stop looking

            if ( result.isAuthorized() ) {

                break;

            }

        }

        // Log the result for auditing purposes

        if ( result.isAuthorized() ) {

            logger.debug( "IP[{}] USER[{}] ACCESS[{}] HTTP[{}] URL[{}] authorized request",
                    criteria.getHost(), criteria.getUser(), result.getAccessLevel(),
                    criteria.getHttpMethod(), criteria.getUrl() );

        } else {

            logger.warn( "IP[{}] USER[{}] ACCESS[{}] HTTP[{}] URL[{}] unauthorized request",
                    criteria.getHost(), criteria.getUser(), result.getAccessLevel(),
                    criteria.getHttpMethod(), criteria.getUrl() );

        }

        return result.isAuthorized();

    }

}
