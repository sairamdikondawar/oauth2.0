package com.pss.poc.ws.auth.manager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.pss.poc.orm.bean.ClientDetails;
import com.pss.poc.orm.dao.ClientDetailsDAO;
import com.pss.poc.ws.model.ConsumerRegistration;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.cxf.rs.security.oauth2.common.Client;

@Path(value = "/registerProvider")
public class ThirdPartyRegistrationService {
	
	@Context
	private UriInfo uriInfo;
	private OAuthManager manager;
	private ClientDetailsDAO clientDetailsDAO;
	private Map<String, CachedOutputStream> appLogos = 
	    new HashMap<String, CachedOutputStream>();
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/")
	public ConsumerRegistration register(MultipartBody body) {
	    String appName = body.getAttachmentObject("appName", String.class);
	    String appURI = body.getAttachmentObject("appURI", String.class);
	    String appRedirectURI = body.getAttachmentObject("appRedirectURI", String.class);
	    String appDesc = body.getAttachmentObject("appDescription", String.class);
	    

	    URI logoURI = null;
	    
	    Attachment att = body.getAttachment("appLogo");
	    if (att != null) {
	        InputStream logoStream = att.getObject(InputStream.class);
	        CachedOutputStream cos = new CachedOutputStream();
	        try {
	            IOUtils.copy(logoStream, cos);
	            appLogos.put(appName.toLowerCase(), cos);
	            
	            UriBuilder ub = uriInfo.getAbsolutePathBuilder();
	            ub.path("logo").path(appName.toLowerCase());
	            
	            ContentDisposition cd = att.getContentDisposition();
	            if (cd != null && cd.getParameter("filename") != null) {
	               ub.path(cd.getParameter("filename"));
	            }
	            logoURI = ub.build();
	            
	        } catch (IOException ex) {
	            // ignore   
	        }
	        
	        
	    }
	    
		String clientId = generateClientId(appName, appURI);
		String clientSecret = generateClientSecret();
	
		Client newClient = new Client(clientId, clientSecret, true, appName, appURI);
		
		newClient.setApplicationDescription(appDesc);
		newClient.setApplicationLogoUri(logoURI.toString());
		newClient.setRedirectUris(Collections.singletonList(appRedirectURI));
		
		clientDetailsDAO.save(createClientDetails(clientId, clientSecret, appName, appURI, appRedirectURI));
		manager.registerClient(newClient);
		return new ConsumerRegistration(clientId, clientSecret);
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/")
	public ConsumerRegistration registerForm(@FormParam("appName") String appName,
			@FormParam("appURI") String appURI,
			@FormParam("appRedirectURI") String appRedirectURI) {
	    String clientId = generateClientId(appName, appURI);
		String clientSecret = generateClientSecret();
	
		Client newClient = new Client(clientId, clientSecret, true, appName, appURI);
		newClient.setRedirectUris(Collections.singletonList(appRedirectURI));
		manager.registerClient(newClient);
		return new ConsumerRegistration(clientId, clientSecret);
	}
	
	@GET
	@Path("logo/{appName}/{image}")
	@Produces({"image/png", "image/jpeg", "image/gif" })
	public byte[] getApplicationLogo(@PathParam("appName") String appName) {
	    CachedOutputStream cos = appLogos.get(appName);
	    try {
	        return cos == null ? null : cos.getBytes();
	    } catch (IOException ex) {
	        return null;
	    }
	}
	
	public String generateClientId(String appName, String appURI) {
	    // if appURI is not allowed to contain paths, example, it can only be
	    // www.mycompany.com, then appURI can be used as a consumer key
		return System.currentTimeMillis()+"";
	}
	
	public ClientDetails createClientDetails(String clientId,String clientScrt,String appName, String appURI, String redirectURI)
	{
		ClientDetails clientDetails=new ClientDetails();
		clientDetails.setClientAppName(appName);
		clientDetails.setClientid(clientId);
		clientDetails.setClientUri(appURI);
		clientDetails.setClientScrt(clientScrt);
		clientDetails.setClientRedirectUri(redirectURI); 
		
		return clientDetails;
	}
	
	public String generateClientSecret() {
        return System.currentTimeMillis()+"";
    }
	
	public void setDataProvider(OAuthManager manager) {
		this.manager = manager;
	}

	public ClientDetailsDAO getClientDetailsDAO() {
		return clientDetailsDAO;
	}

	public void setClientDetailsDAO(ClientDetailsDAO clientDetailsDAO) {
		this.clientDetailsDAO = clientDetailsDAO;
	}
}

