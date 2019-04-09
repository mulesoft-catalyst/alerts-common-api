package com.mulesoft.services;

import java.io.IOException;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transport.email.AbstractMailConnector;
import org.mule.transport.email.transformers.ObjectToMimeMessage;

public class InlineAttachmentTransformer extends ObjectToMimeMessage {

	@Override
	protected void setContent(Object payload, Message msg, String contentType, MuleMessage message) throws Exception {
		boolean transformInboundAttachments = message.getInboundAttachmentNames().size() > 0;
		boolean transformOutboundAttachments = message.getOutboundAttachmentNames().size() > 0;

		if (transformInboundAttachments || transformOutboundAttachments) {
			MimeMultipart multipart = new MimeMultipart("related");
			multipart.addBodyPart(getPayloadBodyPart(message.getPayload(), contentType));

			if (transformInboundAttachments) {
				for (String name : message.getInboundAttachmentNames()) {
					// Let outbound attachments override inbound ones
					if (!transformOutboundAttachments || message.getOutboundAttachment(name) == null) {
						addAttachment(multipart, msg, message, message.getInboundAttachment(name), name);
					}
				}
			}

			if (transformOutboundAttachments) {
				for (String name : message.getOutboundAttachmentNames()) {
					addAttachment(multipart, msg, message, message.getOutboundAttachment(name), name);
				}
			}

			// the payload must be set to the constructed MimeMultipart message
			payload = multipart;
			// the ContentType of the message to be sent, must be the multipart content type
			contentType = multipart.getContentType();

			// now the message will contain the multipart payload, and the multipart
			// contentType
			msg.setContent(payload, contentType);
		}
	}

	@Override
	protected BodyPart getPayloadBodyPart(Object payload, String contentType)
			throws MessagingException, TransformerException, IOException {
		BodyPart part = new MimeBodyPart();
		part.setContent(payload, contentType);
		return part;
	}

	@Override
	protected BodyPart getBodyPartForAttachment(DataHandler handler, String name) throws MessagingException {
		BodyPart part = new MimeBodyPart();
		part.setDataHandler(handler);
		part.setDescription(name);
		return part;
	}

	@Override
	protected void addBodyPartHeaders(BodyPart part, String name, MuleMessage message) {
		Map<String, String> headers = message.getOutboundProperty(
				name + AbstractMailConnector.ATTACHMENT_HEADERS_PROPERTY_POSTFIX);

		if (null != headers) {
			for (String key : headers.keySet()) {
				try {
					part.setHeader(key, "<" + headers.get(key) + ">");
				}
				catch (MessagingException me) {
					throw new RuntimeException("Failed to set bodypart header", me);
				}
			}
		}
	}

	private void addAttachment(MimeMultipart multipart, Message msg, MuleMessage message, DataHandler dataHandler, String name) throws MessagingException {
		BodyPart part = getBodyPartForAttachment(dataHandler, name);
		// Check message props for extra headers
		addBodyPartHeaders(part, name, message);
		multipart.addBodyPart(part);
	}
}
