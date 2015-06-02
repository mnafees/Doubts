/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.query;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import solutions.doubts.DoubtsApplication;
import solutions.doubts.internal.RestConstants;

public class ImageUploadQuery {

    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

    private String mFilename;
    private Drawable mDrawable;

    private OkHttpClient mOkHttpClient = new OkHttpClient();

    private ImageUploadQuery(final String filename,
                             final Drawable drawable) {
        mFilename = filename;
        mDrawable = drawable;

        getKey();
    }

    private void getKey() {
        final Request req = new Request.Builder()
                .url(RestConstants.API_ENDPOINT + "/s3/sign/" + mFilename)
                .get()
                .addHeader(RestConstants.HEADER_AUTHORIZATION, DoubtsApplication
                        .getInstance().getAuthToken().toString())
                .build();
        mOkHttpClient.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                final JsonObject jsonObject = new Gson().fromJson(response.body().string(),
                        JsonObject.class);
                uploadImageToS3(jsonObject.get("url").getAsString());
            }
        });
    }

    private void uploadImageToS3(final String url) {
        final Bitmap bitmap = ((BitmapDrawable)mDrawable).getBitmap();
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();
        final Request req = new Request.Builder()
                .url(url)
                .put(RequestBody.create(MEDIA_TYPE_JPEG, data))
                .build();
        mOkHttpClient.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        });
    }

    public static ImageUploadQuery upload(final String filename,
                                          final Drawable drawable) {
        return new ImageUploadQuery(filename, drawable);
    }

}
