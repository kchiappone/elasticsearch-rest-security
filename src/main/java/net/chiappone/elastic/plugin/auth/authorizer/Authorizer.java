package net.chiappone.elastic.plugin.auth.authorizer;

import net.chiappone.elastic.plugin.auth.AuthorizationCriteria;
import net.chiappone.elastic.plugin.auth.AuthorizationResult;

/**
 * @author Kurtis Chiappone
 */
public interface Authorizer {

    AuthorizationResult authorize( AuthorizationCriteria criteria );

}
