package solutions.doubts.activities.createdoubt;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;

import com.commonsware.cwac.camera.CameraFragment;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.google.common.base.Function;
import com.koushikdutta.ion.ProgressCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import solutions.doubts.api.S3Upload;
import solutions.doubts.api.models.S3Image;

public class DoubtsCameraFragment extends CameraFragment {
    public abstract class Callback {
        public abstract void call(S3Image image, Exception ex);
    }
    protected class DoubtsCameraHost extends SimpleCameraHost {
        private static final String TAG = "DoubtsCameraFragment";
        private static final int MAX_DIMENSION = 1024;
        private static final int COMPRESS_QUALITY = 85;

        public DoubtsCameraHost(Context _ctxt) {
            super(_ctxt);
        }

        @Override
        public void saveImage(PictureTransaction xact, Bitmap bitmap) {
            final CreateDoubtActivity cda = (CreateDoubtActivity) DoubtsCameraFragment.this.getActivity();
            Log.d(TAG, "saveImage");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            float h = bitmap.getHeight(), w = bitmap.getWidth();
            if(h > w) {
                w = w * (MAX_DIMENSION/h);
                h = MAX_DIMENSION;
            } else {
                h = h * (MAX_DIMENSION/w);
                w = MAX_DIMENSION;
            }
            Bitmap b = Bitmap.createScaledBitmap(bitmap, (int)w, (int)h, true);
            b.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, baos);
            bitmap.recycle();
            b.recycle();
            S3Upload s3u = new S3Upload(cda, baos.toByteArray(), "image/jpeg");
            s3u.upload(new ProgressCallback() {
                @Override
                public void onProgress(final long downloaded, final long total) {
                    (new Handler(Looper.getMainLooper())).post(new Runnable() {
                        @Override
                        public void run() {
                            cda.mCreateDoubtButton.setProgress(downloaded / (float) total);
                        }
                    });
                }
            }, new Callback() {
                @Override
                public void call(S3Image image, Exception ex) {
                    if (ex != null) {
                        ex.printStackTrace();
                    } else {
                        cda.createDoubt(image);
                    }
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHost(new DoubtsCameraHost(this.getActivity().getApplicationContext()));
    }
}
