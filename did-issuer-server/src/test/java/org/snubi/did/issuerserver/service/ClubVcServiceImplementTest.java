//package org.snubi.did.issuerserver.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.snubi.did.issuerserver.config.CustomConfig;
//import org.snubi.did.issuerserver.converter.JsonConverter;
//import org.snubi.did.issuerserver.dto.ClubVcDto;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@SpringBootTest
//@Slf4j
//class ClubVcServiceImplementTest {
//
//    @Test
//    public void createClubHolderVcDtoTest() {
//        String issuerDid = "did:issuer:ebfeb1f712ebc6f1c276e12ec21";
//        List<String> context = CustomConfig.context;
//        String credentialId = CustomConfig.credentialId + issuerDid;
//        List<String> type = CustomConfig.clubType;
//        String issuer = issuerDid;
//        String name = "test name";
//        String description = "test description";
//        String issuanceDate = String.valueOf(LocalDateTime.now());
//
//        ClubVcDto.ClubRole issuerClubRole = ClubVcDto.ClubRole
//                .builder()
//                .clubMemberRoleSeq(1L)
//                .RoleType("ISSUER")
//                .build();
//
//        String clubRoleJson = null;
//        try {
//            clubRoleJson = JsonConverter.ObjectToJson(issuerClubRole);
//
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//        ClubVcDto.CredentialSubject credentialSubject = ClubVcDto.CredentialSubject
//                .builder()
//                .id("issuer or holder did")
//                .clubId(1L)
//                .clubName("레알마드리드")
//                .startDate(String.valueOf(LocalDateTime.now()))
//                .endDate(String.valueOf(LocalDateTime.now()))
//                .memberDataJson("excel data")
//                .clubRole(clubRoleJson)
//                .build();
//
//        ClubVcDto forSignatureDocumentDto = ClubVcDto.createForSignatureDocumentOf(context, credentialId, type, issuer,
//                name, description, issuanceDate, credentialSubject);
//
//        String forSignatureDocument = null;
//        try {
//            forSignatureDocument = JsonConverter.ObjectToJson(forSignatureDocumentDto);
//
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//        log.info("### forSignatureJson : " + forSignatureDocument);
//
//        // then
//        Assertions.assertEquals(context, forSignatureDocumentDto.getContext());
//        Assertions.assertEquals(credentialId, forSignatureDocumentDto.getId());
//        Assertions.assertEquals(type, forSignatureDocumentDto.getType());
//        Assertions.assertEquals(issuer, forSignatureDocumentDto.getIssuer());
//        Assertions.assertEquals(name, forSignatureDocumentDto.getName());
//        Assertions.assertEquals(description, forSignatureDocumentDto.getDescription());
//        Assertions.assertEquals(issuanceDate, forSignatureDocumentDto.getIssuanceDate());
//        Assertions.assertEquals(credentialSubject, forSignatureDocumentDto.getCredentialSubject());
//    }
//}