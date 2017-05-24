package net.chiappone.elastic.plugin.util;

import org.elasticsearch.common.logging.ESLogger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author Kurtis Chiappone
 */
public class RestSecurityProperties {

    private Properties properties = null;
    private Set<String> hostWhitelist = null;
    private Set<String> httpMethodWhitelist = null;
    private Set<String> urlWhitelist = null;
    private Set<String> basicAdminUsers = null;
    private Set<String> basicWriteUsers = null;
    private Set<String> defaultIndices = null;
    private boolean ldapEnabled = false;
    private String ldapBaseDn = null;
    private String ldapPassword = null;
    private String ldapUrl = null;
    private String ldapUser = null;
    private Set<String> ldapAdminUsers = null;
    private Set<String> ldapAdminGroups = null;
    private Set<String> ldapWriteUsers = null;
    private Set<String> ldapWriteGroups = null;
    private ESLogger logger = null;

    public RestSecurityProperties( final ESLogger logger, final String path ) throws Exception {

        this.logger = logger;
        this.properties = new Properties();
        File propertiesFile = new File( path );

        if ( propertiesFile != null && propertiesFile.exists() ) {

            loadProperties( path );
            TimerTask t = new FileModifiedMonitor( propertiesFile ) {

                @Override
                protected void onFileChange() {

                    try {

                        logger.info( "Properties file updated. Reloading..." );
                        loadProperties( path );

                    } catch ( Exception e ) {

                        logger.error( "Error reloading properties", e );

                    }

                }

            };

            Timer timer = new Timer();
            timer.scheduleAtFixedRate( t, 0, 60000 );

        }

    }

    public Set<String> getDefaultIndices() {

        return defaultIndices;
    }

    public void setDefaultIndices( Set<String> defaultIndices ) {

        this.defaultIndices = defaultIndices;
    }

    public Set<String> getUserIndexAccess( String user ) {

        return keyToSet( "auth.indices.user." + user );

    }

    private void loadProperties( String path ) throws IOException {

        properties.clear();
        properties.load( new FileReader( new File( path ) ) );
        setPropertyValues();

    }

    private Boolean getAsBoolean( String key, boolean defaultValue ) {

        String property = getProperty( key, String.valueOf( defaultValue ) );

        if ( property != null ) {

            property = property.trim();

            if ( "true".equalsIgnoreCase( property ) || "false".equalsIgnoreCase( property ) ) {

                return Boolean.valueOf( property.trim() );

            }

        }

        return defaultValue;

    }

    private void setPropertyValues() {

        setHostWhitelist( keyToSet( "whitelist.host" ) );
        setBasicAdminUsers( keyToSet( "auth.basic.admin" ) );
        setBasicWriteUsers( keyToSet( "auth.basic.write" ) );
        setHttpMethodWhitelist( keyToSet( "whitelist.method" ) );
        setUrlWhitelist( keyToSet( "whitelist.url" ) );
        setLdapEnabled( getAsBoolean( "auth.ldap.config.enabled", false ) );
        setLdapUrl( getProperty( "auth.ldap.config.url" ) );
        setLdapUser( getProperty( "auth.ldap.config.user" ) );
        setLdapPassword( getProperty( "auth.ldap.config.password" ) );
        setLdapBaseDn( getProperty( "auth.ldap.config.base.dn" ) );
        setLdapAdminUsers( keyToSet( "auth.ldap.admin.users" ) );
        setLdapAdminGroups( keyToSet( "auth.ldap.admin.groups" ) );
        setLdapWriteUsers( keyToSet( "auth.ldap.write.users" ) );
        setLdapWriteGroups( keyToSet( "auth.ldap.write.groups" ) );
        setDefaultIndices( keyToSet( "auth.indices.default" ) );

    }

    private String[] getAsArray( String key ) {

        String property = getProperty( key );

        if ( property != null && !property.trim().isEmpty() ) {

            return property.trim().split( "," );

        }

        return new String[] {};

    }

    private Set<String> keyToSet( String key ) {

        Set<String> set = new TreeSet<String>();
        String[] array = getAsArray( key );

        if ( array != null && array.length > 0 ) {

            for ( String s : array ) {

                set.add( s.trim() );

            }

        }

        return set;

    }

    public String getProperty( String key ) {

        return properties.getProperty( ConfigurationHelper.PREFIX + key );
    }

    public String getProperty( String key, String defaultValue ) {

        return properties.getProperty( ConfigurationHelper.PREFIX + key, defaultValue );
    }

    public Set<String> getHostWhitelist() {

        return hostWhitelist;
    }

    public void setHostWhitelist( Set<String> hosts ) {

        this.hostWhitelist = hosts;
    }

    public Set<String> getHttpMethodWhitelist() {

        return httpMethodWhitelist;
    }

    public void setHttpMethodWhitelist( Set<String> methods ) {

        this.httpMethodWhitelist = methods;
    }

    public Set<String> getUrlWhitelist() {

        return urlWhitelist;
    }

    public void setUrlWhitelist( Set<String> urls ) {

        this.urlWhitelist = urls;
    }

    public Set<String> getBasicAdminUsers() {

        return basicAdminUsers;
    }

    public void setBasicAdminUsers( Set<String> users ) {

        this.basicAdminUsers = users;
    }

    public String getLdapBaseDn() {

        return ldapBaseDn;
    }

    public void setLdapBaseDn( String baseDn ) {

        this.ldapBaseDn = baseDn;
    }

    public String getLdapPassword() {

        return ldapPassword;
    }

    public void setLdapPassword( String password ) {

        this.ldapPassword = password;
    }

    public String getLdapUrl() {

        return ldapUrl;
    }

    public void setLdapUrl( String url ) {

        this.ldapUrl = url;
    }

    public String getLdapUser() {

        return ldapUser;
    }

    public void setLdapUser( String user ) {

        this.ldapUser = user;
    }

    public boolean isLdapEnabled() {

        return ldapEnabled;
    }

    public void setLdapEnabled( boolean enabled ) {

        this.ldapEnabled = enabled;
    }

    public Set<String> getLdapAdminUsers() {

        return ldapAdminUsers;
    }

    public void setLdapAdminUsers( Set<String> users ) {

        this.ldapAdminUsers = users;
    }

    public Set<String> getLdapAdminGroups() {

        return ldapAdminGroups;
    }

    public void setLdapAdminGroups( Set<String> groups ) {

        this.ldapAdminGroups = groups;
    }

    public Set<String> getLdapWriteUsers() {

        return ldapWriteUsers;
    }

    public void setLdapWriteUsers( Set<String> ldapWriteUsers ) {

        this.ldapWriteUsers = ldapWriteUsers;
    }

    public Set<String> getLdapWriteGroups() {

        return ldapWriteGroups;
    }

    public void setLdapWriteGroups( Set<String> ldapWriteGroups ) {

        this.ldapWriteGroups = ldapWriteGroups;
    }

    public Set<String> getBasicWriteUsers() {

        return basicWriteUsers;
    }

    public void setBasicWriteUsers( Set<String> users ) {

        this.basicWriteUsers = users;
    }

}
