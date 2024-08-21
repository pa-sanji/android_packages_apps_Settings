package com.android.settings.spoofing;

import android.content.Context;
import android.util.Log;

import com.android.settings.R;
import android.app.AlertDialog; 
import android.os.SystemProperties;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

import org.json.JSONException;
import org.json.JSONObject;

public class AppliedJsonViewerController extends AbstractPreferenceController
        implements PreferenceControllerMixin {

    private static final String KEY_VIEW_APPLIED_JSON_VIEWER = "applied_json_viewer";
    private static final String TAG = "AppliedJsonViewerController";

    public JsonSpoofingEnabledController mJsonSpoofingEnabledController;
    public AppliedJsonViewerController(Context context, JsonSpoofingEnabledController jsonSpoofingEnabledController) {
        super(context);
        mJsonSpoofingEnabledController = jsonSpoofingEnabledController;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_VIEW_APPLIED_JSON_VIEWER;
    }

    @Override
    public void updateState(Preference preference) {
        preference.setEnabled(mJsonSpoofingEnabledController.isEnabled());
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (KEY_VIEW_APPLIED_JSON_VIEWER.equals(preference.getKey())) {
            showPropertiesDialog();
            return true;
        }
        return false;
    }

    private void showPropertiesDialog() {
        StringBuilder properties = new StringBuilder();
        try {
            JSONObject jsonObject = new JSONObject();
            String[] keys = {
                "persist.sys.pihooks_ID",
                "persist.sys.pihooks_BRAND",
                "persist.sys.pihooks_DEVICE",
                "persist.sys.pihooks_FINGERPRINT",
                "persist.sys.pihooks_MANUFACTURER",
                "persist.sys.pihooks_MODEL",
                "persist.sys.pihooks_PRODUCT",
                "persist.sys.pihooks_SECURITY_PATCH",
                "persist.sys.pihooks_DEVICE_INITIAL_SDK_INT"
            };
            for (String key : keys) {
                String value = SystemProperties.get(key, null);
                if (value != null) {
                    String buildKey = key.replace("persist.sys.pihooks_", "");
                    jsonObject.put(buildKey, value);
                }
            }
            properties.append(jsonObject.toString(4));
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON from properties", e);
            properties.append(mContext.getString(R.string.error_loading_properties));
        }
        new AlertDialog.Builder(mContext)
            .setTitle(R.string.show_pif_properties_title)
            .setMessage(properties.toString())
            .setPositiveButton(R.string.okay, null)
            .show();
    }
}
