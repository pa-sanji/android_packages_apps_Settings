package com.android.settings.spoofing;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.net.Uri;

import android.content.Context;
import android.content.Intent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.core.PreferenceControllerMixin;

import com.android.settingslib.core.AbstractPreferenceController;
import com.android.internal.logging.nano.MetricsProto;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class SpoofingSettings extends DashboardFragment implements PreferenceControllerMixin,
        UpdatePreferenceStatusCallback, PifJsonLoaderController.FileSelector {

    private static final String TAG = "SpoofingSettings";
    private PifJsonLoaderController pifJsonLoaderController;

    @Override
    protected String getLogTag() {
        return TAG;
    }

    public PifJsonLoaderController getPifJsonLoaderController() {
        return pifJsonLoaderController;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ARROW;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.spoofing_prefs;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        final Activity activity = getActivity();

        final JsonSpoofingEnabledController jsonSpoofingEnabledController = new JsonSpoofingEnabledController(
                activity, this /* UpdatePreferenceStatusCallback */);
        pifJsonLoaderController = new PifJsonLoaderController(
                activity, this /* FileSelector */, jsonSpoofingEnabledController);
    
        controllers.add(jsonSpoofingEnabledController);
    
        controllers.add(pifJsonLoaderController);
        controllers.add(new AppliedJsonViewerController(activity, jsonSpoofingEnabledController));
        return controllers;
    }

    @Override
    public void updatePreferenceStatus(Context context) {
        updatePreferenceStates();
    }

    @Override
    public void openFileSelector() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 10001);
    
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            // Delegate the result back to the controller.
            getPifJsonLoaderController().loadPifJson(uri);
        }
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.spoofing_prefs);
}
