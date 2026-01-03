package com.groupeisi.m2gl.config;

import com.groupeisi.m2gl.client.NotificationClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import static com.groupeisi.m2gl.config.Constants.NOTIF_WS_ENDPOINT;

@Configuration
public class SoapClientConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // Package where classes were generated in step 2
        marshaller.setContextPath("com.groupeisi.m2gl.notif.wsdl");
        return marshaller;
    }

    @Bean
    public NotificationClient notificationClient(Jaxb2Marshaller marshaller) {
        NotificationClient client = new NotificationClient();
        client.setDefaultUri(NOTIF_WS_ENDPOINT);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
