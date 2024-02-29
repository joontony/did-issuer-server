package org.snubi.did.issuerserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.snubi.did.issuerserver.common.ErrorCode;
import org.snubi.did.issuerserver.config.CustomConfig;
import org.snubi.did.issuerserver.exception.CustomException;
import org.snubi.did.issuerserver.repository.MemberDidRepository;
import org.snubi.lib.jwt.JTWClaimsUtil;
import org.snubi.lib.jwt.JWTClaims;
import org.snubi.lib.misc.Misc;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class WithAuthController {
	
	
	@Autowired
	MemberDidRepository memberDidRepository;
	
	//@Autowired
	protected HttpServletRequest request;
	@Autowired
	public void setHttpServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	protected JWTClaims clsJWTClaims = new JWTClaims();		
	
	protected boolean isOnlyOwn(String strId)  {
		try {
			JWTClaims clsJWTClaims = this.getClaims();
			return clsJWTClaims.getId().equals(strId);
		} catch(Exception Ex) {			
			log.error("## WithAuthController ## isOnlyOwn");			
		}
		return false;
	}
	
	protected String getToken() throws Exception {
		String token = "";
		try {
			token = request.getHeader("Authorization").replace(CustomConfig.strTokenPrefix, "");
		} catch(Exception Ex) {
			log.info("Exception {}", Ex.getMessage());
		}
		return token;
	}
	
	protected JWTClaims getClaims() throws Exception {
		try {
			String strHTTPHeader = request.getHeader("Authorization").replace(CustomConfig.strTokenPrefix, "");	
			//log.info("strHTTPHeader {}", strHTTPHeader);			
			String strKey = CustomConfig.strSecrete;			
			this.clsJWTClaims = (new JTWClaimsUtil()).getClaims(strHTTPHeader,strKey);	
			log.info("this.clsJWTClaims.getId() {}", this.clsJWTClaims.getId());				
			if(Misc.isEmtyString(this.clsJWTClaims.getId()) == true) {
				throw new CustomException(ErrorCode.UNAUTHORIZED);
			}
		} catch(Exception Ex) {
			log.info("Exception {}", Ex.getMessage());
			clsJWTClaims.setId("not-login");
		}
		return clsJWTClaims;
	}
    
}
