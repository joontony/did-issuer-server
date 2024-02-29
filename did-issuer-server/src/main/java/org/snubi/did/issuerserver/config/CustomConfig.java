package org.snubi.did.issuerserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class CustomConfig {

	public static String strTokenPrefix;
	@Value("${http.response.auth.token}")
	public void setStrTokenPrefix(String prop) {
		CustomConfig.strTokenPrefix = prop + " ";
	}

	public static String strResponseAuthHeader;
	@Value("${http.response.auth.header}")
	public void setStrResponseAuthHeader(String prop) {
		CustomConfig.strResponseAuthHeader = prop;
	}

	public static String strResponseAuthClaimIssue;
	@Value("${http.response.auth.claims.issue}")
	public void setStrResponseAuthClaimIssue(String prop) {
		CustomConfig.strResponseAuthClaimIssue = prop;
	}

	public static String strSecrete;
	@Value("${security.oauth2.resource.jwt.key-value}")
	public void setStrSecrete(String prop) {
		CustomConfig.strSecrete = prop;
	}

	public static String strKubernetesServerUrl;
	@Value("${kubernetes.server.url}")
	public void setStrKubernetesServerUrl(String prop) {
		CustomConfig.strKubernetesServerUrl = prop;
	}

	public static String strKubernetesServerIp;
	@Value("${kubernetes.server.ip}")
	public void setStrKubernetesServerIp(String prop) {
		CustomConfig.strKubernetesServerIp = prop;
	}

	public static List<String> context;
	@Value("#{'${vc.context}'.split(',')}")
	public void setContext(List<String> prop) {
		CustomConfig.context = prop;
	}

	public static String credentialId;
	@Value("${vc.credential_id}")
	public void setCredentialId(String prop) {
		CustomConfig.credentialId = prop;
	}

	public static List<String> avChainType;
	@Value("#{'${vc.type.avChain}'.split(',')}")
	public void setAvChainType(List<String> prop) {
		CustomConfig.avChainType = prop;
	}

	public static List<String> clubType;
	@Value("#{'${vc.type.club}'.split(',')}")
	public void setClubType(List<String> prop) {
		CustomConfig.clubType = prop;
	}

	public static String proofType;
	@Value("${vc.type.proof}")
	public void setProofType(String prop) {
		CustomConfig.proofType = prop;
	}

	public static String avChainVerificationMethod;
	@Value("${vc.verification.method.avChain}")
	public void setAvChainVerificationMethod(String prop) {
		CustomConfig.avChainVerificationMethod = prop;
	}

	public static String clubVerificationMethod;
	@Value("${vc.verification.method.club}")
	public void setClubVerificationMethod(String prop) {
		CustomConfig.clubVerificationMethod = prop;
	}

	public static String proofPurpose;
	@Value("${vc.proof_purpose}")
	public void setProofPurpose(String prop) {
		CustomConfig.proofPurpose = prop;
	}

	public static String avChainSecretKey;
	@Value("${vc.avChainSecretKey}")

	public void setAvChainSecretKey(String prop) {
		CustomConfig.avChainSecretKey = prop;
	}

	public static String didSeverUrl;
	@Value("${did.server.url}")
	public void setDidSeverUrl(String prop) { CustomConfig.didSeverUrl = prop; }

	public static String didSeverClubAfterCreateIssuer;
	@Value("${did.server.club.after.create.issuer}")
	public void setDidSeverClubAfterCreateIssuer(String prop) { CustomConfig.didSeverClubAfterCreateIssuer = prop; }

	public static String didSeverClubAfterExcelIssuer;
	@Value("${did.server.club.after.excel.issuer}")
	public void setDidSeverClubAfterExcelIssuer(String prop) { CustomConfig.didSeverClubAfterExcelIssuer = prop; }

	public static String didSeverClubAfterMobileExcelIssuer;
	@Value("${did.server.club.after.mobile.excel.issuer}")
	public void setDidSeverClubAfterMobileExcelIssuer(String prop) { CustomConfig.didSeverClubAfterMobileExcelIssuer = prop; }

	public static String didSeverClubAfterExcelReInvite;
	@Value("${did.server.club.after.excel.reinvite}")
	public void setDidSeverClubAfterExcelReInvite(String prop) { CustomConfig.didSeverClubAfterExcelReInvite = prop; }

	public static String didSeverClubQrcodeComplete;
	@Value("${did.server.club.qrcode.complete}")
	public void setDidSeverClubQrcodeComplete(String prop) { CustomConfig.didSeverClubQrcodeComplete = prop; }

	public static String didServerClubPersonalMessageEntityListener;
	@Value("${did.server.club.personal.message.entity.listener}")
	public void setDidServerClubPersonalMessageEntityListener(String prop) { CustomConfig.didServerClubPersonalMessageEntityListener = prop; }

	public static String didResolverSeverUrl;
	@Value("${did.resolver.server.url}")
	public void setDidResolverSeverUrl(String prop) { CustomConfig.didResolverSeverUrl = prop; }

	public static String didResolverServerSignatureCreate;
	@Value("${did.resolver.server.signature.create}")
	public void setDidResolverSeverSignatureCreate(String prop) { CustomConfig.didResolverServerSignatureCreate = prop; }

	public static String didIssuerPodUrl;
	@Value("${did.issuer.pod.url}")
	public void setDidIssuerPodUrl(String prop) { CustomConfig.didIssuerPodUrl = prop; }

	public static String didIssuerPodCredentialClubSignature;
	@Value("${did.issuer.pod.credential.club.signature}")
	public void setDidIssuerPodCredentialClubSignature(String prop) { CustomConfig.didIssuerPodCredentialClubSignature = prop; }

	public static String didIssuerPodPresentationVerificationClub;
	@Value("${did.issuer.pod.presentation.verification.club}")
	public void setDidIssuerPodPresentationVerificationClub(String prop) { CustomConfig.didIssuerPodPresentationVerificationClub = prop; }

	public static String didIssuerServerUrl;
	@Value("${did.issuer.server.url}")
	public void setDidIssuerServerUrl(String prop) { CustomConfig.didIssuerServerUrl = prop; }

	public static List<String> allowedOrigins;
	@Value("${allowed.origins}")
	public void setAllowedOrigins(List<String> prop) { CustomConfig.allowedOrigins = prop; }

	public static int batchSize;
	@Value("${batch.size}")
	public void batchSize(int prop) { CustomConfig.batchSize = prop; }
}
