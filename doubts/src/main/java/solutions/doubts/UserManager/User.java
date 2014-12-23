package solutions.doubts.UserManager;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class User {

    private Firebase _firebase;
    private boolean _loggedIn;
    private String _uid;

    public void User(Firebase firebase) {
        _firebase = firebase;
        _loggedIn = false;
        _uid = null;
    }

    void registerNewUser(String email, String password) {
        _firebase.createUser(email, password, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(FirebaseError firebaseError) {

            }
        });
    }

    void authenticateUser(String email, String password, final AuthResult handler) {
        _firebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                _uid = authData.getUid();
                _loggedIn = true;
                handler.onSuccess();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                handler.onError(firebaseError);
            }
        });
    }

    public interface AuthResult {
        void onSuccess();
        void onError(FirebaseError error);
    }

}
