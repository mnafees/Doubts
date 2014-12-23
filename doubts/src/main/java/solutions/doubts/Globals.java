package solutions.doubts;

import android.content.res.Resources;
import com.firebase.client.Firebase;

public class Globals {

    private static final Firebase _firebase = new Firebase(Constants.FIREBASE_URL);

    public static Firebase getFirebase() {
        return _firebase;
    }

    public static int pixelsFromDp(Resources resources, int sizeInDp) {
        final float scale = resources.getDisplayMetrics().density;
        int pixels = (int) (sizeInDp * scale + 0.5f);
        return pixels;
    }

}
