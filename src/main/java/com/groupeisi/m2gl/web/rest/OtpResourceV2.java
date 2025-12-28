//package com.groupeisi.m2gl.web.rest;
//
//import com.groupeisi.m2gl.service.dto.OtpResponseV2DTO;
//import com.groupeisi.m2gl.service.dto.VerifyOtpDTO;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//public class OtpResourceV2 {
//
//    private final OtpServiceV2 otpService;
//
//    public OtpResourceV2(OtpServiceV2 otpService) {
//        this.otpService = otpService;
//    }
//
//    @PostMapping("/send-otp")
//    public ResponseEntity<OtpResponseV2DTO> sendOtp(@RequestParam String phone) {
//        return ResponseEntity.ok(otpService.generateOtp(phone));
//    }
//
//    @PostMapping("/verify-otp")
//    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpDTO dto) {
//
//        if (otpService.verifyOtp(dto)) {
//            return ResponseEntity.ok("OTP valide");
//        }
//        return ResponseEntity.badRequest().body("OTP invalide ou expiré");
//    }
//}
