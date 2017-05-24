package net.chiappone.elastic.plugin.auth;

/**
 * @author Kurtis Chiappone
 */
public class AuthorizationCriteria {

    private String host = null;
    private String httpMethod = null;
    private String url = null;
    private String authorizationHeader = null;
    private User user = null;

    public AuthorizationCriteria( String host, String httpMethod, String url,
            String authorizationHeader ) {

        this.host = host;
        this.httpMethod = httpMethod;
        this.url = url;
        this.authorizationHeader = authorizationHeader;

        if ( authorizationHeader != null ) {

            this.user = new User( authorizationHeader );

        }

    }

    public String getHost() {

        return host;
    }

    public void setHost( String host ) {

        this.host = host;
    }

    public String getHttpMethod() {

        return httpMethod;
    }

    public void setHttpMethod( String httpMethod ) {

        this.httpMethod = httpMethod;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl( String url ) {

        this.url = url;
    }

    public String getAuthorizationHeader() {

        return authorizationHeader;
    }

    public void setAuthorizationHeader( String authorizationHeader ) {

        this.authorizationHeader = authorizationHeader;
    }

    public User getUser() {

        return user;
    }

    public void setUser( User user ) {

        this.user = user;
    }

}
