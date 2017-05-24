package net.chiappone.elastic.plugin.auth;

import javax.xml.bind.DatatypeConverter;

/**
 * @author Kurtis Chiappone
 */
public class User {

    private String id = null;
    private boolean authenticated = false;
    private String authorizationHeader = null;
    private String password = null;

    public User( String base64Auth ) {

        if ( base64Auth != null && !base64Auth.trim().isEmpty() ) {

            base64Auth = base64Auth.replace( "Basic ", "" ).trim();

            setAuthorizationHeader(
                    new String( DatatypeConverter.parseBase64Binary( base64Auth ) ) );

            if ( getAuthorizationHeader().contains( ":" ) ) {

                String[] split = getAuthorizationHeader().split( ":" );

                if ( split != null && split.length > 0 ) {

                    this.id = split[ 0 ];
                    this.password = split[ 1 ];

                }

            }

        }

    }

    @Override
    public boolean equals( Object obj ) {

        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        User other = (User) obj;
        if ( id == null ) {
            if ( other.id != null )
                return false;
        } else if ( !id.equals( other.id ) )
            return false;
        if ( authenticated != other.authenticated )
            return false;
        if ( authorizationHeader == null ) {
            if ( other.authorizationHeader != null )
                return false;
        } else if ( !authorizationHeader.equals( other.authorizationHeader ) )
            return false;
        if ( password == null ) {
            if ( other.password != null )
                return false;
        } else if ( !password.equals( other.password ) )
            return false;
        return true;
    }

    public String getId() {

        return id;
    }

    public void setId( String id ) {

        this.id = id;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword( String password ) {

        this.password = password;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
        result = prime * result + ( authenticated ? 1231 : 1237 );
        result = prime * result + ( ( authorizationHeader == null ) ?
                0 :
                authorizationHeader.hashCode() );
        result = prime * result + ( ( password == null ) ? 0 : password.hashCode() );
        return result;
    }

    public boolean isAuthenticated() {

        return authenticated;
    }

    public void setAuthenticated( boolean isAuthenticated ) {

        this.authenticated = isAuthenticated;
    }

    @Override
    public String toString() {

        return id;
    }

    public String getAuthorizationHeader() {

        return authorizationHeader;
    }

    public void setAuthorizationHeader( String authorization ) {

        this.authorizationHeader = authorization;
    }

}
