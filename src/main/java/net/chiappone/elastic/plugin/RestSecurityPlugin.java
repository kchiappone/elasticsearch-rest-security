package net.chiappone.elastic.plugin;

import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestModule;

/**
 * @author Kurtis Chiappone
 */
public class RestSecurityPlugin extends Plugin {

    @Override
    public String description() {

        return "Elasticsearch REST security";

    }

    @Override
    public String name() {

        return "elasticsearch-rest-security";

    }

    public void onModule( RestModule module ) {

        module.addRestAction( RestSecurityHandler.class );

    }

}
