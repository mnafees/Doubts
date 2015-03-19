/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.util;

import com.google.gson.JsonObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QueryClassReflector {

    private static QueryClassReflector INSTANCE;

    private String mPackagePath = "solutions/doubts/api/models";
    private final List<Class<?>> mClassList = new ArrayList<>();

    public static QueryClassReflector getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new QueryClassReflector();
        }
        return INSTANCE;
    }

    private QueryClassReflector() {
        URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(mPackagePath);
        if (scannedUrl == null) {
            //throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage));
        }
        File scannedDir = new File(scannedUrl.getFile());
        for (File file : scannedDir.listFiles()) {
            mClassList.addAll(find(file, mPackagePath));
        }

    }

    private static List<Class<?>> find(File file, String scannedPackage) {
        List<Class<?>> classes = new ArrayList<>();
        String resource = scannedPackage + "." + file.getName();
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                classes.addAll(find(child, resource));
            }
        } else if (resource.endsWith(".class")) {
            int endIndex = resource.length() - ".class".length();
            String className = resource.substring(0, endIndex);
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException ignore) {
            }
        }
        return classes;
    }

    public Class getClassFor(final JsonObject json) {
        for (Class clazz : mClassList) {
            if (json.has(clazz.getName().toLowerCase() + "s")) {
                return clazz;
            }
        }
        return null;
    }

}
