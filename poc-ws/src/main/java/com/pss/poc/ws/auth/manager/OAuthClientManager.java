package com.pss.poc.ws.auth.manager;

import java.net.URI;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils.Consumer;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;

public class OAuthClientManager {

	private static final String DEFAULT_CLIENT_ID = "123456789";
	private static final String DEFAULT_CLIENT_SECRET = "987654321";
	
	private WebClient accessTokenService;
    private String authorizationServiceURI;
    private Consumer consumer = new Consumer(DEFAULT_CLIENT_ID, DEFAULT_CLIENT_SECRET);
    
	public OAuthClientManager() {
		
	}
	
	public URI getAuthorizationServiceURI(String name,URI redirectUri, String reservationRequestKey) {
		String scope = OAuthConstants.AUTHORIZATION_CODE_GRANT;
	    return OAuthClientUtils.getAuthorizationURI(authorizationServiceURI, 
	    		                                    consumer.getKey(),
	    		                                    redirectUri.toString(),
	    		                                    reservationRequestKey,
	    		                                    scope);
	}
	public ClientAccessToken getAccessToken(AuthorizationCodeGrant codeGrant) {
	    try {
	        return OAuthClientUtils.getAccessToken(accessTokenService, consumer, codeGrant);
	    } catch (OAuthServiceException ex) {
	        return null;
	    }
	}
	
	public String createAuthorizationHeader(ClientAccessToken token) {
		return OAuthClientUtils.createAuthorizationHeader(consumer, token);
	}
	
	public void setAccessTokenService(WebClient ats) {
		// The timeout is set to simplify debugging on the server side;
		// otherwise the connection may time-out too early
		WebClient.getConfig(ats).getHttpConduit().getClient().setReceiveTimeout(1000000);
		this.accessTokenService = ats;
	}
    
    public void setAuthorizationURI(String uri) {
		this.authorizationServiceURI = uri;
	}
	
}
