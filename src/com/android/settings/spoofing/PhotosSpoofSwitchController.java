package com.android.settings.spoofing;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.os.Handler;
import android.os.SystemProperties;
import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.internal.util.arrow.SystemRebootUtils;
import androidx.preference.Preference;

import com.arrow.support.preferences.SystemSettingSwitchPreference;

public class PhotosSpoofSwitchController extends AbstractPreferenceController 
implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener  {

    private static final String KEY_JSON_SPOOFING_ENABLED = "gphotos_spoof_switch";
    private static final String PI_HOOKS_GPHOTOS = "persist.sys.pihooks_gphotos";
    public PhotosSpoofSwitchController(Context context) {
        super(context);
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
        SystemProperties.set(PI_HOOKS_GPHOTOS, enabled ? "true" : "false");
        updateState(preference);
        SystemRebootUtils.restartProcess(mContext, "com.google.android.apps.photos");
        return true;
    }

    @Override
    public void updateState(Preference preference) {
        ((SystemSettingSwitchPreference) preference).setChecked(isEnabled());
        preference.setSummary("Google photos unlimited backup " + (isEnabled() ? "enabled" : "disabled"));
    }

    public boolean isEnabled() {
        String value = SystemProperties.get(PI_HOOKS_GPHOTOS, "true");
        return Boolean.parseBoolean(value);
    }
}

