package solutions.doubts.activities.createquestion;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.commonsware.cwac.camera.CameraFragment;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.koushikdutta.ion.ProgressCallback;

import java.io.ByteArrayOutputStream;

import solutions.doubts.activities.createanswer.CreateAnswerActivity;
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
            final Context context = DoubtsCameraFragment.this.getActivity();
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
            S3Upload s3u = new S3Upload(context, baos.toByteArray(), "image/jpeg");
            s3u.upload(new ProgressCallback() {
                @Override
                public void onProgress(final long downloaded, final long total) {
                    // TODO: show some progress while uploading
                }
            }, new Callback() {
                @Override
                public void call(S3Image image, Exception ex) {
                    if (ex != null) {
                        ex.printStackTrace();
                    } else {
                        // FIX: Temporary
                        if (DoubtsCameraFragment.this.getActivity().getClass()
                                .equals(CreateQuestionActivity.class)) {
                            ((CreateQuestionActivity)DoubtsCameraFragment.this.getActivity()).createDoubt(image);
                        } else {
                            ((CreateAnswerActivity)DoubtsCameraFragment.this.getActivity()).createAnswer(image);
                        }
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
