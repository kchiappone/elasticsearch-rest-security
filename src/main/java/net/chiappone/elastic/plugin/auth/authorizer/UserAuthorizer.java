package net.chiappone.elastic.plugin.auth.authorizer;

import net.chiappone.elastic.plugin.auth.AccessLevel;
import net.chiappone.elastic.plugin.auth.AuthorizationCriteria;
import net.chiappone.elastic.plugin.auth.AuthorizationResult;
import net.chiappone.elastic.plugin.auth.User;
import net.chiappone.elastic.plugin.auth.ldap.LdapHelper;
import net.chiappone.elastic.plugin.util.ConfigurationHelper;
import org.elasticsearch.common.logging.ESLogger;

import java.util.Set;

/**
 * @author Kurtis Chiappone
 */
public class UserAuthorizer implements Authorizer {

    private ConfigurationHelper configuration = null;
    private LdapHelper ldapHelper = null;
    private ESLogger logger = null;

    public UserAuthorizer( ConfigurationHelper configuration, ESLogger logger ) {

        this.configuration = configuration;
        this.logger = logger;

    }

    @Override
    public AuthorizationResult authorize( AuthorizationCriteria criteria ) {

        AuthorizationResult result = new AuthorizationResult( false );

        if ( criteria.getAuthorizationHeader() != null ) {

            User user = criteria.getUser();
            logger.trace( "Received HTTP Basic Authorization header for {}", user );
            AccessLevel accessLevel = determineAccessLevel( user );

            switch ( accessLevel ) {

                // Authorize all ADMIN requests

                case ADMIN:
                    result.setAccessLevel( AccessLevel.ADMIN );
                    result.setAuthorized( true );
                    break;

                // If the user has WRITE access, determine which indices they have access to.
                // Prohibit DELETE for all users but the admin.

                case WRITE:

                    if ( !"DELETE".equalsIgnoreCase( criteria.getHttpMethod() ) ) {

                        if ( checkIndexAccess( criteria, result, user ) ) {

                            result.setAccessLevel( AccessLevel.WRITE );
                            result.setAuthorized( true );
                            break;

                        }

                    }

                default:
                    break;

            }

        }

        return result;

    }

    private boolean checkIndexAccess( AuthorizationCriteria criteria, AuthorizationResult result,
            User user ) {

        // Check user specific access, and default index access

        return traverseIndices( criteria, user,
                getConfiguration().getProperties().getUserIndexAccess( user.getId() ) )
                || traverseIndices( criteria, user,
                getConfiguration().getProperties().getDefaultIndices() );

    }

    private boolean traverseIndices( AuthorizationCriteria criteria, User user,
            Set<String> indices ) {

        logger.debug( "User ({}) has access to {} indices", user, indices );

        if ( indices != null ) {

            for ( String index : indices ) {

                if ( "all".equalsIgnoreCase( index ) || criteria.getUrl().matches( index ) ) {

                    return true;

                }

            }

        }

        return false;
    }

    private AccessLevel determineAccessLevel( User user ) {

        // First check HTTP basic

        if ( isHttpBasicAdminUser( user ) ) {

            return AccessLevel.ADMIN;

        } else if ( isHttpBasicWriteUser( user ) ) {

            return AccessLevel.WRITE;

        }

        logger.trace( "User ({}) not found in any HTTP Basic user lists", user );

        // Then check LDAP, which returns READ access by default if the user is not found

        return getLdapHelper().determineAccessLevel( user );

    }

    public ConfigurationHelper getConfiguration() {

        return configuration;
    }

    public LdapHelper getLdapHelper() {

        if ( ldapHelper == null ) {

            this.ldapHelper = new LdapHelper( getConfiguration(), logger );

        }

        return ldapHelper;

    }

    private boolean isHttpBasicAdminUser( User user ) {

        if ( user != null ) {

            if ( getConfiguration().getProperties().getBasicAdminUsers()
                    .contains( user.getAuthorizationHeader() ) ) {

                logger.debug( "HTTP Basic ADMIN user ({}) is authenticated", user );
                return true;

            }

        }

        return false;

    }

    private boolean isHttpBasicWriteUser( User user ) {

        if ( user != null ) {

            if ( getConfiguration().getProperties().getBasicWriteUsers()
                    .contains( user.getAuthorizationHeader() ) ) {

                logger.debug( "HTTP Basic WRITE user ({}) is authenticated", user );
                return true;

            }

        }

        return false;

    }

}
