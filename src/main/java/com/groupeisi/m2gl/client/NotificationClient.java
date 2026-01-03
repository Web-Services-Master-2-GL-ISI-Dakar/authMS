package com.groupeisi.m2gl.client;

import com.groupeisi.m2gl.notif.wsdl.SendOtpRequest;
import com.groupeisi.m2gl.notif.wsdl.OtpResponse;
import com.groupeisi.m2gl.notif.wsdl.OtpInfo;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import static com.groupeisi.m2gl.config.Constants.NOTIF_WS_ENDPOINT;

public class NotificationClient extends WebServiceGatewaySupport {

    public OtpResponse sendOtp(String phoneNumber, String code, int expiry) {
        SendOtpRequest request = new SendOtpRequest();
        request.setEventRef("AUTH-OTP-" + System.currentTimeMillis());
        request.setLanguage("FR");

        OtpInfo otp = new OtpInfo();
        otp.setPhoneNumber(phoneNumber);
        otp.setVerificationCode(code);
        otp.setExpiryInMinutes(expiry);

        request.setOtp(otp);

        // Execute the call to the NOTIF service
        return (OtpResponse) getWebServiceTemplate()
                .marshalSendAndReceive(NOTIF_WS_ENDPOINT, request);
    }
}
