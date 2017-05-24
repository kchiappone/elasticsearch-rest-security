package net.chiappone.elastic.plugin.auth;

/**
 * @author Kurtis Chiappone
 */
public class AuthorizationResult {

    private AccessLevel accessLevel = AccessLevel.READ;
    private boolean authorized = false;

    public AuthorizationResult( boolean authorized ) {

        this.authorized = authorized;
    }

    public AuthorizationResult( AccessLevel accessLevel, boolean authorized ) {

        this.accessLevel = accessLevel;
        this.authorized = authorized;
    }

    public AccessLevel getAccessLevel() {

        return accessLevel;
    }

    public void setAccessLevel( AccessLevel accessLevel ) {

        this.accessLevel = accessLevel;
    }

    public boolean isAuthorized() {

        return authorized;
    }

    public void setAuthorized( boolean authorized ) {

        this.authorized = authorized;
    }

}
