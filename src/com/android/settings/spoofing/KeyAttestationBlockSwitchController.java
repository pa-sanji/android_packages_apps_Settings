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
import androidx.preference.Preference;

import com.arrow.support.preferences.SystemSettingSwitchPreference;

public class KeyAttestationBlockSwitchController extends AbstractPreferenceController 
implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener  {

    private static final String KEY_ATTESTATION = "key_attestation_switch";
    private static final String PI_HOOKS_KEY_ATTESTATION = "persist.sys.pihooks.disable_attestation_block";
    public KeyAttestationBlockSwitchController(Context context) {
        super(context);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_ATTESTATION;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean enabled = (Boolean) newValue;
        Log.d(getPreferenceKey(), "Key attestation block: " + enabled);
        SystemProperties.set(PI_HOOKS_KEY_ATTESTATION, enabled ? "true" : "false");
        updateState(preference);
        return true;
    }

    @Override
    public void updateState(Preference preference) {
        ((SystemSettingSwitchPreference) preference).setChecked(isEnabled());
    }

    public boolean isEnabled() {
        String value = SystemProperties.get(PI_HOOKS_KEY_ATTESTATION, "false");
        return Boolean.parseBoolean(value);
    }
}
