package tyrant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TyrantClient {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TyrantClient.class);

    public static String urlTyrantApi = "https://mobile.tyrantonline.com/api.php?message=init&user_id=";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:49.0) Gecko/20100101 Firefox/49.0";
    private static final String TIME_HASH = "fgjk380vf34078oi37890ioj43";

    private MessageDigest md_;
    private String userId_ = "4201263";
    private String passwordHash_ = "6bf2febd3e1ed60833c8bcddfe00eed1";
    private String syncode_ = "c9f48d49539e8b4ef5d065d383c37bdd942b6f37ad08cfe90cac10492e38f5e1";

    public TyrantClient() {
        try {
            md_ = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getUserAccount() {
        return getResponse(urlTyrantApi, "getUserAccount");
    }

    private String getResponse(String urlString, String message) {
        LOGGER.info(
                "Retrieving data from " + urlString + ", message: " + message);

        StringBuffer lineBuffer = new StringBuffer();

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(urlString);

        // add header
        post.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("timestamp",
                Long.toString(new Date().getTime())));
        urlParameters.add(new BasicNameValuePair("syncode", syncode_));
        urlParameters.add(new BasicNameValuePair("user_id", userId_));
        urlParameters.add(new BasicNameValuePair("api_stat_name", message));
        urlParameters.add(new BasicNameValuePair("password", passwordHash_));
        urlParameters.add(new BasicNameValuePair("hash", getHash(message)));

        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        HttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (ClientProtocolException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        LOGGER.info(
                "Response Code : " + response.getStatusLine().getStatusCode());

        BufferedReader rd = null;
        try {
            rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
        } catch (UnsupportedOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String line;

        try {
            while ((line = rd.readLine()) != null) {
                lineBuffer.append(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LOGGER.info("Read " + lineBuffer.length() + " bytes.");

        return lineBuffer.toString();
    }

    private String getHash(String message) {
        String stringToHash = message
                + String.valueOf(new Date().getTime() / (60 * 15)) + TIME_HASH;
        byte[] digest = md_.digest(stringToHash.getBytes());

        // convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            String hex = Integer.toHexString(0xff & digest[i]);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public void setUserId(String userId) {
        userId_ = userId;
    }

    public void setPasswordHash(String passwordHash) {
        passwordHash_ = passwordHash;
    }

    public String getUserId() {
        return userId_;
    }

    public String getPasswordHash() {
        return passwordHash_;
    }
}
