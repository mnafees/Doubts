package solutions.doubts.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.ConvertFuture;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import solutions.doubts.DoubtsApplication;
import solutions.doubts.R;
import solutions.doubts.api.models.Device;
import solutions.doubts.api.models.User;
import solutions.doubts.api.query.Query;
import solutions.doubts.internal.Session;
import solutions.doubts.internal.StringConstants;
import solutions.doubts.thirdparty.Devices;

public class GCMRegistrationService extends IntentService {
    private static final String TAG = "GCMRegistrationService";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GCMRegistrationService() {
        super(TAG);
    }

    private String getInstallId() {
        SharedPreferences sp = getSharedPreferences(StringConstants.PREFERENCES_NAME, MODE_PRIVATE);
        String installId = sp.getString("installId", Long.toHexString(Double.doubleToLongBits(Math.random())));
        SharedPreferences.Editor spe = sp.edit();
        spe.putString("installId", installId);
        spe.apply();
        return installId;
    }

    private String getDeviceName() {
        return String.format("%s (%s)", Devices.getDeviceName(), getInstallId());
    }

    // TODO XXX Handle failures, retry, etc.
    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getString(R.string.gcm_sender_id);
        try {
            String token = instanceID.getToken(senderId,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                    null);

            Device device = new Device();
            device.setName(getDeviceName());
            device.setRegistrationId(token);
            Session session = ((DoubtsApplication)getApplication()).getSession();
            if(session == null) {
                //todo reschedule
                return;
            }
            User user = session.getLoggedInUser();
            if(user == null) {
                //todo reschedule
                return;
            }

            try {
                Query.with(this)
                        .remote(Device.class)
                        .resource("users", user.getId(), user.getUsername())
                        .resource("devices")
                        .create(device, new ProgressCallback() {
                            @Override
                            public void onProgress(long downloaded, long total) {

                            }
                        }, new FutureCallback<Response<JsonObject>>() {
                            @Override
                            public void onCompleted(Exception e, Response<JsonObject> result) {

                            }
                        }).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
