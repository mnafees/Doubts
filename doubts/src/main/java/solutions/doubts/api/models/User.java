package solutions.doubts.api.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User {
    @DatabaseField(id = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String email, username;

    @DatabaseField
    private String name;
}
