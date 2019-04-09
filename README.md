# Alerts Common API
The following API is intended to provide a common service to expose Alerting functionallity. Having a custom/controlable Alerts API is a common requirement during any implementation, because of many reasons:

- Use of your own SMTP server to send alerts
- Send alerts on demand (from any application / Mule-flow)
- Use specific email templates (HTML)
- Send images (attached, embedded or links)
- Anypoint Platform alerts cover Platform related alerts and a limited number of events, these alerts use the MuleSoft-hosted SMTP server and use specific MuleSoft's templates.

## Features
- RAML providing a simple Alerts API Spec.
- SMTP, Gmail implementation with TLS enabled as an example; can be changed to any SMTP server.
- Image bytes retrieval from a url (including the logic for decomposing the URL in parts in order to have a dynamic invocation HTTP/HTTPS)
- Addition of the image as an inline attachment. The SMTP Transport doesn't support this by default, 2 Java classes were used to override this behaviour
- HTML (using a template) as email body (including the inline image)


## Configuration
Default configurations defined in `/src/main/resources/common.properties`:

```
smtp.host=smtp.gmail.com
smtp.port=587
smtp.user=include_your_gmail_user
smtp.password=include_your_gmail_application_password
smtp.from=include_any_from_address

```

Note: An application password is needed if using Gmail SMTP

## Run in Anypoint Studio
1. Clone the project from GitHub `git clone git@github.com:mulesoft-consulting/alerts-common-api.git`

2. Run the project and test it - go to your browser and open `http://localhost:8081/console/`

## Deploy through Runtime Manager
1. Deploy the app from Studio or from the Runtime Manager UI

## Final Note
Enjoy and provide feedback / contribute :)