package solutions.doubts.DataManager;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import solutions.doubts.Constants;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class DoubtsHandler {

    private Firebase _firebase;

    public DoubtsHandler(Firebase firebase) {
        _firebase = firebase;
    }

    void commit(String title, String imageUrl, String descriptionText) {
        final int uid = Math.abs(new SecureRandom().nextInt());
        Doubt doubt = new Doubt(uid, title, imageUrl, descriptionText);
        Map<Integer, Doubt> map = new HashMap();
        map.put(uid, doubt);
        _firebase.child(Constants.getUserDoubtsEndpoint(_firebase.getAuth().getUid()))
                .setValue(map, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            // there's an error
                            if (firebaseError.getCode() == FirebaseError.DISCONNECTED) {

                            }
                        } else {
                            // successfully committed new data
                            firebase.child(Constants.GLOBAL_DOUBTS_UNSOLVED_ENDPOINT)
                                    .setValue(uid, new Firebase.CompletionListener() {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                            if (firebaseError != null) {
                                                // there's an error, ouch!
                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private class Doubt {

        private int _uid;
        private String _title;
        private String _imageUrl;
        private String _descriptionText;
        private boolean _answered;

        public Doubt(int uid, String title, String imageUrl, String descriptionText) {
            _uid = uid;
            _title = title;
            _imageUrl = imageUrl;
            _descriptionText = descriptionText;
            _answered = false;
        }

        public int getUid() {
            return _uid;
        }

        public String getTitle() {
            return _title;
        }

        public String getImageUrl() {
            return _imageUrl;
        }

        public String getDescriptionText() {
            return _descriptionText;
        }

        public boolean getAnswered() {
            return _answered;
        }
    }

}
