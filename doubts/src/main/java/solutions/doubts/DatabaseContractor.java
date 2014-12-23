package solutions.doubts;

import android.provider.BaseColumns;

public class DatabaseContractor {

    public DatabaseContractor() {}

    public static abstract class NewDoubtPostingError implements BaseColumns {
        public static final String TABLE_NAME = "server_sync_error";
        public static final String COLUMN_NAME_UID = "uid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_IMAGE_URL = "imageUrl";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_ANSWERED = "answered";
    }

}
