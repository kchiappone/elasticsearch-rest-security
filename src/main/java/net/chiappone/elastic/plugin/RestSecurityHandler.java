package net.chiappone.elastic.plugin;

import net.chiappone.elastic.plugin.util.ConfigurationHelper;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;

/**
 * @author Kurtis Chiappone
 */
public class RestSecurityHandler extends BaseRestHandler {

    @Inject
    public RestSecurityHandler( Settings settings, RestController controller, Client client ) {

        super( settings, controller, client );

        ConfigurationHelper configuration = new ConfigurationHelper( settings, logger );

        if ( !configuration.isPluginEnabled() ) {

            logger.info( "rest-security plugin disabled" );

        } else {

            logger.debug( "rest-security plugin enabled" );
            controller.registerFilter( new RestSecurityFilter( logger, configuration ) );

        }

    }

    @Override
    protected void handleRequest( RestRequest request, RestChannel channel, Client client )
            throws Exception {

    }

}
