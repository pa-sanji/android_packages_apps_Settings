package com.android.settings.spoofing;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.os.Handler;
import android.os.SystemProperties;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.internal.util.arrow.SystemRebootUtils;
import androidx.preference.Preference;

import com.arrow.support.preferences.SystemSettingSwitchPreference;

public class JsonSpoofingEnabledController extends AbstractPreferenceController
        implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {

    private static final String KEY_JSON_SPOOFING_ENABLED = "json_spoofing_enabled";
    private final UpdatePreferenceStatusCallback mCallback;

    public JsonSpoofingEnabledController(Context context, UpdatePreferenceStatusCallback callback) {
        super(context);
        mCallback = callback;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_JSON_SPOOFING_ENABLED;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean enabled = (Boolean) newValue;
        Log.d(getPreferenceKey(), "Spoofing enabled: " + enabled);
        SystemProperties.set("persist.sys.custom_pif", enabled ? "true" : "false");
        mCallback.updatePreferenceStatus(mContext);
        SystemRebootUtils.showSystemRebootDialog(mContext);
        return true;
    }

    @Override
    public void updateState(Preference preference) {
        ((SystemSettingSwitchPreference) preference).setChecked(isEnabled());

        String value = SystemProperties.get("persist.sys.custom_pif", "false");
        preference.setSummary("Custom PIF " + (Boolean.parseBoolean(value) ? "enabled" : "disabled"));

    }

    public boolean isEnabled() {
        String value = SystemProperties.get("persist.sys.custom_pif", "false");
        return Boolean.parseBoolean(value);
    }
}
