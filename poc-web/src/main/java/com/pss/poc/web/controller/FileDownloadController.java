package com.pss.poc.web.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.pss.poc.web.util.PocWebHelper;

@ManagedBean(name = "fileDownloadView")
@SessionScoped
@SuppressWarnings("unchecked")
public class FileDownloadController implements Serializable {

	private static final long serialVersionUID = 936148808349484797L;
	private StreamedContent file;
	private String fileId;
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("poc-web");
	private static final Logger LOGGER = Logger.getLogger(FileUploadController.class);
	private static final String BASE_URL = "http://" + BUNDLE.getString("ws.host") + ":" + BUNDLE.getString("ws.port") + "/poc-ws/pocupload/FileUploadService/";

	public void fileDownloadViewListener() {
		InputStream stream = null;
		try {
			stream = new ByteArrayInputStream(fileId.getBytes());
			fileId = "";
			WebClient client = WebClient.create(BASE_URL + "downloadfile");
			client.type("multipart/form-data").accept(MediaType.MULTIPART_FORM_DATA);
			List<Attachment> attachments = new ArrayList<Attachment>();
			ContentDisposition cd = new ContentDisposition("attachment");
			Attachment attachment = new Attachment("id", stream, cd);
			attachments.add(attachment);
			attachments = (List<Attachment>) client.postAndGetCollection(new MultipartBody(attachments), Attachment.class);
			file = new DefaultStreamedContent(attachments.get(0).getDataHandler().getInputStream(), attachments.get(0).getContentDisposition().getParameter("filetype"), attachments.get(0).getContentDisposition().getParameter("filename"));

			client.close();
			PocWebHelper.addMessage("File Downloaded successfully", FacesMessage.SEVERITY_INFO);
		} catch (Exception e) {
			LOGGER.error(e, e);
			PocWebHelper.addMessage("File Downloading error :: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
		}

	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getFileId() {
		System.err.println("entering doGet");
		try {
			// get code

			HttpClient client = new DefaultHttpClient();
			HttpGet post = new HttpGet("http://localhost:8080/poc-ws/oauth/authorize?client_id=12345&response_code=code&scope=readData&redirect_uri=http://localhost:8080/iclub-www");

			List<NameValuePair> arguments = new ArrayList<>(3);
			arguments.add(new BasicNameValuePair("redirect_uri", "http://localhost:8080/iclub-www"));
			arguments.add(new BasicNameValuePair("client_secret", "123456"));
			arguments.add(new BasicNameValuePair("client_id", "12345"));
			arguments.add(new BasicNameValuePair("scope", "readData"));
			arguments.add(new BasicNameValuePair("response_type", "code"));
			try {
				Base64.Encoder encoder = Base64.getEncoder();
				String normalString = "12345" + ":" + "123456";
				String encodedValue = encoder.encodeToString(normalString.getBytes(StandardCharsets.UTF_8));
				post.setHeader("Authorization", "Basic " + encodedValue);
				post.setHeader("Content-Type", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				// post.setEntity(new UrlEncodedFormEntity(arguments));
				HttpResponse response1 = client.execute(post);
				String outputString = EntityUtils.toString(response1.getEntity());
				System.out.println(outputString);

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("leaving doGet");
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

}
