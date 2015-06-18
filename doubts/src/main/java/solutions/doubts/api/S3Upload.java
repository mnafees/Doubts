package solutions.doubts.api;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import com.google.common.base.Function;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import solutions.doubts.activities.createdoubt.DoubtsCameraFragment;
import solutions.doubts.api.models.S3Image;
import solutions.doubts.internal.ApiConstants;

public class S3Upload {
    private static final Uri S3_SIGN_URI = Uri.parse(ApiConstants.API_ENDPOINT)
            .buildUpon()
            .appendPath("s3")
            .appendPath("sign")
            .build();
    private static final String TAG = "S3Upload";

    Context mContext;
    private byte[] mInputStream;
    private String mMimeType;

    public S3Upload(Context context, byte[] inputStream, String mimeType) {
        mContext = context;
        mInputStream = inputStream;
        mMimeType = mimeType;
    }

    public void upload(final ProgressCallback progressCallback, final DoubtsCameraFragment.Callback callback) {
        // TODO Fix this here after it's fixed in backend.
        // Should not require filename, should accept headers and mimetype
        Ion.with(mContext)
                .load(S3_SIGN_URI.buildUpon().appendPath("test.jpeg").build().toString())
                .setLogging(TAG, Log.DEBUG)
                .asJsonObject()
                .then(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null || result == null) {
                            return;
                        }

                        String status = result.getAsJsonPrimitive("status").getAsString();
                        if (!status.equals("success")) {
                            return;
                        }

                        String url = result.getAsJsonPrimitive("url").getAsString();
                        String key = result.getAsJsonPrimitive("key").getAsString();
                        Exception ex = null;
                        try {
                            String s = Ion.with(mContext)
                                    .load("PUT", url)
                                    .setHeader("Content-Type", mMimeType)
                                    .followRedirect(true)
                                    .uploadProgress(progressCallback)
                                    .setByteArrayBody(mInputStream)
                                    .asString()
                                    .get();
                        } catch (InterruptedException | ExecutionException e1) {
                            ex = e1;
                            e1.printStackTrace();
                        }
                        S3Image s3i = new S3Image();
                        s3i.setKey(key);
                        s3i.setUrl(url.split("\\?")[0]);
                        callback.call(s3i, ex);
                    }
                });
    }
}
