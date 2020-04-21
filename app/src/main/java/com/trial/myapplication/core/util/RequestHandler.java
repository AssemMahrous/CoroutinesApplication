package com.trial.myapplication.core.util;

import com.trial.myapplication.BuildConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import static com.trial.myapplication.core.util.Constants.AUTHORIZATION;
import static com.trial.myapplication.core.util.Constants.AUTHORIZATION_HEADER;
import static com.trial.myapplication.core.util.Constants.HEADER_KEY;

public class RequestHandler {
    public static String sendPost(String r_url) throws Exception {
        URL url = new URL(r_url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty(HEADER_KEY, BuildConfig.API_KEY);

        conn.setReadTimeout(20000);
        conn.setConnectTimeout(20000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        int responseCode = conn.getResponseCode(); // To Check for 200
        if (responseCode == HttpsURLConnection.HTTP_OK) {

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            while ((line = in.readLine()) != null) {
                sb.append(line);
                break;
            }
            in.close();
            return sb.toString();
        }
        return null;
    }

    public  String sendGet(String url, String token, String[] urlParameters) throws Exception {
        URI baseUri = new URI(url);
        URI uri = applyParameters(baseUri, urlParameters);
        HttpURLConnection con =
                (HttpURLConnection) uri.toURL().openConnection();
        con.setRequestProperty(HEADER_KEY, BuildConfig.API_KEY);
        con.setRequestProperty(AUTHORIZATION, String.format(Locale.getDefault(), AUTHORIZATION_HEADER, token));
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        System.out.println("Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // connection ok
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            return "";
        }
    }

    private URI applyParameters(URI baseUri, String[] urlParameters) {
        StringBuilder query = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < urlParameters.length; i += 2) {
            if (first) {
                first = false;
            } else {
                query.append("&");
            }
            try {
                query.append(urlParameters[i]).append("=")
                        .append(URLEncoder.encode(urlParameters[i + 1], "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                /* As URLEncoder are always correct, this exception
                 * should never be thrown. */
                throw new RuntimeException(ex);
            }
        }
        try {
            return new URI(baseUri.getScheme(), baseUri.getAuthority(),
                    baseUri.getPath(), query.toString(), null);
        } catch (URISyntaxException ex) {
            /* As baseUri and query are correct, this exception
             * should never be thrown. */
            throw new RuntimeException(ex);
        }
    }
}
