package net.chiappone.elastic.plugin.auth.ldap;

import net.chiappone.elastic.plugin.auth.AccessLevel;
import net.chiappone.elastic.plugin.auth.User;
import net.chiappone.elastic.plugin.util.ConfigurationHelper;
import org.elasticsearch.common.logging.ESLogger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Kurtis Chiappone
 */
public class LdapHelper {

    private ConfigurationHelper configuration = null;
    private ESLogger logger = null;

    public LdapHelper( ConfigurationHelper configuration, ESLogger logger ) {

        this.configuration = configuration;
        this.logger = logger;

    }

    private boolean authenticate( SearchResult result, User user ) {

        logger.trace( "Authenticating LDAP user {}...", user );

        if ( result != null ) {

            String userPrincipal = result.getNameInNamespace();

            try {

                connectToLdap( userPrincipal, user.getPassword() );

            } catch ( NamingException e ) {

                logger.warn( "Unable to authenticate LDAP user: {}", user );
                return false;

            }

        }

        logger.debug( "LDAP user authenticated: {}", user );
        return true;

    }

    private InitialDirContext connectToLdap( String user, String password ) throws NamingException {

        Properties props = new Properties();
        props.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
        props.put( Context.PROVIDER_URL, getConfiguration().getProperties().getLdapUrl() );
        props.put( Context.SECURITY_PRINCIPAL, user );
        props.put( Context.SECURITY_CREDENTIALS, password );
        return new InitialDirContext( props );

    }

    public AccessLevel determineAccessLevel( User user ) {

        if ( user != null && getConfiguration().getProperties().isLdapEnabled() ) {

            SearchResult result = searchUser( user );

            if ( result != null ) {

                boolean authenticated = authenticate( result, user );

                if ( authenticated ) {

                    if ( isAdminUser( user ) || isAdminGroup( result, user ) ) {

                        return AccessLevel.ADMIN;

                    } else if ( isWriteUser( user ) || isWriteGroup( result, user ) ) {

                        return AccessLevel.WRITE;

                    }

                }

            } else {

                logger.warn( "LDAP user not found: {}", user );

            }

        } else {

            logger.trace( "User null? {} LDAP enabled? {}", ( user == null ),
                    getConfiguration().getProperties().isLdapEnabled() );

        }

        return AccessLevel.READ;

    }

    public ConfigurationHelper getConfiguration() {

        return configuration;
    }

    private Set<String> getGroups( SearchResult result ) throws NamingException {

        Set<String> groups = new TreeSet<String>();

        if ( result != null ) {

            Attribute memberAttribute = result.getAttributes().get( "memberOf" );

            if ( memberAttribute != null ) {

                for ( int i = 0; i < memberAttribute.size(); i++ ) {

                    String group = (String) memberAttribute.get( i );

                    if ( group != null ) {

                        String[] groupSplit = group.split( "," );

                        if ( groupSplit != null && groupSplit.length > 0 ) {

                            // CN=abc, OU=xyz, ...

                            String cn = groupSplit[ 0 ];

                            if ( cn != null ) {

                                String[] cnSplit = cn.split( "=" );

                                if ( cnSplit != null && cnSplit.length > 0 ) {

                                    groups.add( cnSplit[ 1 ] );

                                }

                            }

                        }

                    }

                }

            }

        }

        return groups;

    }

    private boolean isAdminGroup( SearchResult result, User user ) {

        logger.trace( "Searching LDAP groups..." );

        try {

            Set<String> groups = getGroups( result );

            for ( String group : getConfiguration().getProperties().getLdapAdminGroups() ) {

                if ( groups.contains( group ) ) {

                    logger.debug( "LDAP user {} is in the ADMIN group: {}", user, group );
                    return true;

                }

            }

        } catch ( Exception e ) {

            logger.warn( "Error checking LDAP groups for user: {}", user, e );

        }

        logger.trace( "User not found in any LDAP ADMIN groups: {}", user );
        return false;

    }

    private boolean isAdminUser( User user ) {

        if ( getConfiguration().getProperties().getLdapAdminUsers().contains( user.getId() ) ) {

            logger.debug( "LDAP user is in the ADMIN list: {}", user );
            return true;

        }

        logger.trace( "LDAP user is not in the ADMIN list: {}", user );
        return false;

    }

    private boolean isWriteGroup( SearchResult result, User user ) {

        logger.trace( "Searching LDAP groups..." );

        try {

            Set<String> groups = getGroups( result );

            for ( String group : getConfiguration().getProperties().getLdapWriteGroups() ) {

                if ( groups.contains( group ) ) {

                    logger.debug( "LDAP user {} is in the WRITE group: {}", user, group );
                    return true;

                }

            }

        } catch ( Exception e ) {

            logger.warn( "Error checking LDAP groups for user: {}", user, e );

        }

        logger.trace( "User not found in any LDAP WRITE groups: {}", user );
        return false;

    }

    private boolean isWriteUser( User user ) {

        if ( getConfiguration().getProperties().getLdapWriteUsers().contains( user.getId() ) ) {

            logger.debug( "LDAP user is in the WRITE list: {}", user );
            return true;

        }

        logger.trace( "LDAP user is not in the WRITE list: {}", user );
        return false;

    }

    private SearchResult searchUser( User user ) {

        try {

            InitialDirContext context = connectToLdap(
                    getConfiguration().getProperties().getLdapUser(),
                    getConfiguration().getProperties().getLdapPassword() );
            SearchControls search = new SearchControls();
            search.setReturningAttributes( new String[] { "memberOf" } );
            search.setSearchScope( SearchControls.SUBTREE_SCOPE );

            NamingEnumeration<SearchResult> enumeration = context
                    .search( getConfiguration().getProperties().getLdapBaseDn(),
                            "(cn=" + user + ")", search );

            if ( enumeration != null && enumeration.hasMoreElements() ) {

                return enumeration.nextElement();

            }

        } catch ( Exception e ) {

            logger.warn( "Error searching for LDAP user: {}", user, e );

        }

        return null;

    }

}
