package solutions.doubts.api.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Device extends RealmObject {
    private String registrationId;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
}
