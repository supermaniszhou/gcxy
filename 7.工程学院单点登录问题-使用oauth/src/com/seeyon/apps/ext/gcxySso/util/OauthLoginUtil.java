package com.seeyon.apps.ext.gcxySso.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

public class OauthLoginUtil {
    public static String togetToken(String code, PropUtils prop) {
        String loginName = "";
        String client_id = prop.getSSO_ClientId();
        String client_secret = prop.getSSO_ClientSecret();
        StringBuffer sb = new StringBuffer();
        sb.append(prop.getSSO_OAuthAccess_token());
        sb.append("?client_id=" + client_id);
        sb.append("&client_secret=" + client_secret);
        sb.append("&code=" + code);
        sb.append("&redirect_uri=" + prop.getApplicationUrl());
        String url = sb.toString();
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = null;
        HttpResponse response = null;
        httpGet = new HttpGet(url);

        try {
            response = client.execute(httpGet);
            response.setHeader("Cache-Control", "no-cache");
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String resultString = EntityUtils.toString(response.getEntity(), "utf-8").replaceAll(" ", "");
                Map<String, Object> m = (Map<String, Object>) JSONObject.parse(resultString);
                loginName = toGet((String) m.get("access_token"), prop);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loginName;
    }

    public static String toGet(String accessToken, PropUtils propUtils) throws IOException {
        String userName = "";
        CloseableHttpClient client = HttpClients.createDefault();
        StringBuffer sb = new StringBuffer();
        sb.append(propUtils.getSSO_UserInfo());
        sb.append("?access_token=" + accessToken);
        HttpGet get = new HttpGet(sb.toString());
        CloseableHttpResponse response = client.execute(get);
        response.setHeader("Cache-Control", "no-cache");
        String resultString = "";
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            resultString = EntityUtils.toString(response.getEntity(), "utf-8").replaceAll(" ", "");
            Map<String, Object> map = (Map<String, Object>) JSONObject.parse(resultString);
            Map<String, Object> attrMap = (Map<String, Object>) map.get("attributes");
            userName = (String) attrMap.get("CODE");
        }
        return userName;
    }
}
