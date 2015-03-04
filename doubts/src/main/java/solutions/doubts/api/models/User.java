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

    private S3Image image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public S3Image getImage() {
        return image;
    }

    public void setImage(S3Image image) {
        this.image = image;
    }
}
