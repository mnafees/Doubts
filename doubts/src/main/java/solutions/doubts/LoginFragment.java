package solutions.doubts;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.ImageView;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

public class LoginFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private boolean _google_intentInProgress;
    private boolean _google_signInClicked;
    private ConnectionResult _google_connectionResult;
    private GoogleApiClient _googleApiClient;

    private UiLifecycleHelper _fb_uiHelper;

    private final Firebase _firebase = Globals.getFirebase();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _fb_uiHelper = new UiLifecycleHelper(getActivity(), fb_callback);
        _fb_uiHelper.onCreate(savedInstanceState);

        Plus.PlusOptions options = new Plus.PlusOptions.Builder()
                .setServerClientId("53640968525-79lin04strl91d76j0f8k0cha28k8v91.apps.googleusercontent.com")
                .build();

        _googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API, options)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    @Override
    public void onStart() {
        super.onStart();
        _googleApiClient.connect();
    }

    @Override
    public void onStop() {
        if (_googleApiClient.isConnected()) _googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        _fb_uiHelper.onResume();
        if (!_googleApiClient.isConnected()) _googleApiClient.connect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        _fb_uiHelper.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode != Activity.RESULT_OK) {
                _google_signInClicked = false;
            }

            _google_intentInProgress = false;

            if (!_googleApiClient.isConnecting()) {
                _googleApiClient.connect();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        _fb_uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _fb_uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        _fb_uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_login, container, false);

        LoginButton fbLogin = (LoginButton) view.findViewById(R.id.fbLogin);
        fbLogin.setBackgroundResource(R.drawable.login_view_paper_fb);
        fbLogin.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        ImageView gplusLogin = (ImageView) view.findViewById(R.id.gplusLogin);
        gplusLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        return view;
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), getActivity(),
                    0).show();
            return;
        }

        if (!_google_intentInProgress) {
            // Store the ConnectionResult for later usage
            _google_connectionResult = result;

            if (_google_signInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                google_resolveSignInError();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int code) {
        _googleApiClient.connect();
    }

    private Session.StatusCallback fb_callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            fb_onSessionStateChange(session, state, exception);
        }
    };

    private void fb_onSessionStateChange(Session session,
                                      SessionState state,
                                      Exception exception) {
        if (state.isOpened()) {
            _firebase.authWithOAuthToken("facebook", session.getAccessToken(),
                    new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            // The Facebook user is now authenticated with Firebase
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            // there was an error
                        }
                    });
        } else if (state.isClosed()) {
            _firebase.unauth();
        }
    }

    private void google_resolveSignInError() {
        if (_google_connectionResult.hasResolution()) {
            try {
                _google_intentInProgress = true;
                _google_connectionResult.startResolutionForResult(getActivity(), 0);
            } catch (IntentSender.SendIntentException e) {
                _google_intentInProgress = false;
                _googleApiClient.connect();
            }
        }
    }

    private void signInWithGoogle() {
        if (!_googleApiClient.isConnecting()) {
            _google_signInClicked = true;
            google_resolveSignInError();
        }
    }

}
