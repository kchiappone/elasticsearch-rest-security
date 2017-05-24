package net.chiappone.elastic.plugin;

import net.chiappone.elastic.plugin.auth.AccessController;
import net.chiappone.elastic.plugin.util.ConfigurationHelper;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.rest.*;

/**
 * @author Kurtis Chiappone
 */
public class RestSecurityFilter extends RestFilter {

    private AccessController accessController = null;
    private ConfigurationHelper configuration = null;
    private ESLogger logger = null;

    public RestSecurityFilter( ESLogger logger, ConfigurationHelper configuration ) {

        this.logger = logger;
        this.configuration = configuration;

    }

    public AccessController getAccessController() {

        if ( accessController == null ) {

            this.accessController = new AccessController( getConfiguration(), logger );

        }

        return accessController;

    }

    public ConfigurationHelper getConfiguration() {

        return configuration;
    }

    @Override
    public void process( RestRequest request, RestChannel channel, RestFilterChain filterChain )
            throws Exception {

        if ( getAccessController().isAuthorized( request ) ) {

            sendAuthorizedResponse( request, filterChain, channel );

        } else {

            sendUnauthorizedResponse( channel, "Authorization required" );

        }

    }

    public void sendAuthorizedResponse( RestRequest request, RestFilterChain filterChain,
            RestChannel channel ) {

        filterChain.continueProcessing( request, channel );
    }

    public void sendUnauthorizedResponse( RestChannel channel, String reason ) {

        RestResponse response = new BytesRestResponse( RestStatus.UNAUTHORIZED, reason );
        response.addHeader( "WWW-Authenticate", "Basic" );
        channel.sendResponse( response );
    }

}
