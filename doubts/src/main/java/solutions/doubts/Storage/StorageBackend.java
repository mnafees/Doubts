package solutions.doubts.Storage;

/*import android.content.res.Resources;
import android.util.Log;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import solutions.doubts.R;

import java.io.*;
import java.security.*;
import java.util.Collections; */

public class StorageBackend {

    /*GoogleCredential credential;

    public StorageBackend(Resources resources) {
        try {
            JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            KeyStore keystore = SecurityUtils.getPkcs12KeyStore();
            keystore.load(resources.openRawResource(R.raw.secret),
                    "notasecret".toCharArray());

            PrivateKey key = (PrivateKey)keystore.getKey("privatekey", "notasecret".toCharArray());

            credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(new JacksonFactory())
                .setServiceAccountPrivateKey(key)
                .setServiceAccountId("53640968525-1jr2uucjktirtspv38j3deshggl0v63l@developer.gserviceaccount.com")
                .setServiceAccountScopes(Collections.singleton(StorageScopes.DEVSTORAGE_READ_WRITE))
                .build();

            credential.refreshToken();

            String URI = "https://storage.googleapis.com/";
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory(credential);
            GenericUrl url = new GenericUrl(URI);
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse response = request.execute();
            String content = response.parseAsString();
            Log.d("testing", "response content is: " + content);
            new Storage.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName("Doubts").build();
        } catch (IOException ioe) {
            Log.e("Storage Backend: ", ioe.toString());
        } catch (NoSuchAlgorithmException nsae) {
            Log.e("Storage Backend: ", nsae.toString());
        } catch (GeneralSecurityException gse) {
            Log.e("Storage Backend: ", gse.toString());
        } /*catch (JSONException jsone) {
            Log.e("Storage Backend: ", jsone.getMessage());
        }*/
    //} */

}
