package solutions.doubts;

public class Constants {

    public final static String VERSION = "0.1";

    public final static String DB_NAME = "solutions.doubts.DATASTORE_CORE";

    // subject to change in the future
    public final static String FIREBASE_URL = "https://doubts.firebaseio.com";

    private static final String PROFILE_ENDPOINT = "/users";

    public static final String GLOBAL_DOUBTS_UNSOLVED_ENDPOINT = "/doubts/unsolved";

    public static final String GLOBAL_DOUBTS_SOLVED_ENDPOINT = "/doubts/solved";

    public static String getProfileEndpoint(String uid) {
        return PROFILE_ENDPOINT + uid;
    }

    public static String getUserDoubtsEndpoint(String uid) {
        return PROFILE_ENDPOINT + uid + "/doubts";
    }

    public static String getUserAnswersEndpoint(String uid) {
        return PROFILE_ENDPOINT + uid + "/answers";
    }

}
