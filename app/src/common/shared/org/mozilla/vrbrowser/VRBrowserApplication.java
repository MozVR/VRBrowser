/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.vrbrowser;

import android.app.Application;

import org.mozilla.vrbrowser.browser.Places;
import org.mozilla.vrbrowser.browser.Services;
import org.mozilla.vrbrowser.db.AppDatabase;
import org.mozilla.vrbrowser.telemetry.TelemetryWrapper;

public class VRBrowserApplication extends Application {

    private AppExecutors mAppExecutors;
    private Services mServices;
    private Places mPlaces;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppExecutors = new AppExecutors();
        mPlaces = new Places(this);
        mServices = new Services(this, mPlaces);

        TelemetryWrapper.init(this);
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, mAppExecutors);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase(), mAppExecutors);
    }

    public Services getServices() {
        return mServices;
    }

    public Places getPlaces() {
        return mPlaces;
    }
}
