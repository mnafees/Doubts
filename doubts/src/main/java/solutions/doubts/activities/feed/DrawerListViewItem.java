/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.feed;

public class DrawerListViewItem {

    private int iconId;
    private String title;

    public DrawerListViewItem(final int iconId, final String title) {
        this.iconId = iconId;
        this.title = title;
    }

    public int getIconId() {
        return this.iconId;
    }

    public String getTitle() {
        return this.title;
    }

}
