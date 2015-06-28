/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.util;

import android.text.TextUtils;

import solutions.doubts.api.models.Answer;
import solutions.doubts.api.models.Question;
import solutions.doubts.api.models.User;

public class StringUtil {

    private static String md5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }


    /** Alpha */
    public static String getProfileImageUrl(User user) {
        String url;
        if (user.getImage() == null) {
            if (user.getEmail() == null) {
                url = String.format("http://www.gravatar.com/avatar/%s?s=200&d=wavatar", md5(user.getUsername()));
            } else {
                url = String.format("http://www.gravatar.com/avatar/%s?s=200&d=wavatar", md5(user.getEmail()));
            }
            return url;
        }
        url = user.getImage().getUrl();
        if (url == null || TextUtils.isEmpty(url)) {
            url = String.format("http://www.gravatar.com/avatar/%s?s=200&d=wavatar", md5(user.getEmail()));
        }
        return url;
    }

    /** Alpha */
    public static String getDoubtImageUrl(Question question) {
        String url;
        if (question.getImage() == null) {
            url = String.format("http://www.gravatar.com/avatar/%s?s=200&d=retro", md5(question.getTitle()));
            return url;
        }
        url = question.getImage().getUrl();
        if (url == null || TextUtils.isEmpty(url)) {
            url = String.format("http://www.gravatar.com/avatar/%s?s=500&d=retro", md5(question.getTitle()));
        }
        return url;
    }

    /** Alpha */
    public static String getAnswerImageUrl(Answer answer) {
        String url;
        if (answer.getImage() == null) {
            url = String.format("http://www.gravatar.com/avatar/%s?s=200&d=retro", md5(answer.getTitle()));
            return url;
        }
        url = answer.getImage().getUrl();
        if (url == null || TextUtils.isEmpty(url)) {
            url = String.format("http://www.gravatar.com/avatar/%s?s=500&d=retro", md5(answer.getTitle()));
        }
        return url;
    }

}
