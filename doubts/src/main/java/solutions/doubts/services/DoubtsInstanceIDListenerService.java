package solutions.doubts.services;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class DoubtsInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        Intent i = new Intent(this, GCMRegistrationService.class);
        startService(i);
    }
}
