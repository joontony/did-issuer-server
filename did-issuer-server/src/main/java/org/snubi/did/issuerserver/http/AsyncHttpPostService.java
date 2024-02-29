package org.snubi.did.issuerserver.http;

import lombok.extern.slf4j.Slf4j;
import org.snubi.did.issuerserver.config.CustomConfig;
import org.snubi.did.issuerserver.dto.Response;
import org.snubi.lib.http.HttpUtilPost;
import org.snubi.lib.response.SnubiResponse;
import org.springframework.stereotype.Service;

@Service
public interface AsyncHttpPostService {

    @Slf4j
    class HttpService {

        public static SnubiResponse postToDidSeverClubAfterExcelIssuer(String json)   {
            try {
                HttpUtilPost<Response.AfterExcelSaveResponse> clsHttpUtilPost = new HttpUtilPost<Response.AfterExcelSaveResponse>();
                clsHttpUtilPost.setStrUrl(CustomConfig.didSeverClubAfterExcelIssuer);
                clsHttpUtilPost.setStrAuth(null);
                clsHttpUtilPost.setStrTokenType("Bearer");
                clsHttpUtilPost.setStrCharset("UTF-8");
                clsHttpUtilPost.setStrType("application/json");
                SnubiResponse clsSnubiResponse = clsHttpUtilPost.post(json);
                return clsSnubiResponse;
            } catch (Exception Ex) {
                Ex.printStackTrace();
            }
            return null;
        }

        public static SnubiResponse postToDidSeverClubAfterMobileExcelIssuer(String json)   {
            try {
                HttpUtilPost<Response.AfterExcelSaveResponse> clsHttpUtilPost = new HttpUtilPost<Response.AfterExcelSaveResponse>();
                clsHttpUtilPost.setStrUrl(CustomConfig.didSeverClubAfterMobileExcelIssuer);
                clsHttpUtilPost.setStrAuth(null);
                clsHttpUtilPost.setStrTokenType("Bearer");
                clsHttpUtilPost.setStrCharset("UTF-8");
                clsHttpUtilPost.setStrType("application/json");
                SnubiResponse clsSnubiResponse = clsHttpUtilPost.post(json);
                return clsSnubiResponse;
            } catch (Exception Ex) {
                Ex.printStackTrace();
            }
            return null;
        }

        public static SnubiResponse postToDidSeverClubAfterExcelReInvite(String json)   {
            try {
                HttpUtilPost<Response.AfterExcelSaveResponse> clsHttpUtilPost = new HttpUtilPost<Response.AfterExcelSaveResponse>();
                clsHttpUtilPost.setStrUrl(CustomConfig.didSeverClubAfterExcelReInvite);
                clsHttpUtilPost.setStrAuth(null);
                clsHttpUtilPost.setStrTokenType("Bearer");
                clsHttpUtilPost.setStrCharset("UTF-8");
                clsHttpUtilPost.setStrType("application/json");
                SnubiResponse clsSnubiResponse = clsHttpUtilPost.post(json);
                return clsSnubiResponse;
            } catch (Exception Ex) {
                Ex.printStackTrace();
            }
            return null;
        }
    }
}
