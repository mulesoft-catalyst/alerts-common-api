package com.mulesoft.services;

import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.codec.binary.Base64;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.api.transport.PropertyScope;

public class ImageAttacher implements Callable {

	public static final String ATTACHMENT_NAME = "imageAttachment";
	private static final String UNIQUE_CID = "imageCID";
	private static final String OUTBOUND_PROPERTIES_MAP_KEY = ATTACHMENT_NAME + "Headers";

	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {

		Map<String, Object> outboundPropertiesMap = new HashMap<>();
		Map<String, Object> cid = new HashMap<>();
		cid.put("Content-ID", UNIQUE_CID);
		outboundPropertiesMap.put(OUTBOUND_PROPERTIES_MAP_KEY, cid);
		MuleMessage message = eventContext.getMessage();

		byte[] imgBytes = Base64.decodeBase64((String)message.getInvocationProperty("imageData"));
		ByteArrayDataSource dSource = new ByteArrayDataSource(imgBytes, "image/*");
		message.addOutboundAttachment(ATTACHMENT_NAME, new DataHandler(dSource));

		message.addProperties(outboundPropertiesMap, PropertyScope.OUTBOUND);
		return message;
	}

}
