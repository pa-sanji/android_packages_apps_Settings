package com.android.settings.spoofing;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import java.util.Iterator;


import android.os.SystemProperties;
import com.android.internal.util.arrow.SystemRestartUtils;
import android.os.Handler;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

public class PifJsonLoaderController extends AbstractPreferenceController
        implements PreferenceControllerMixin {

    public interface FileSelector {
        void openFileSelector();
    }

    private static final String KEY_PIF_JSON_LOADER = "pif_json_loader";
    private static final String TAG = "PifJsonLoaderController";
    public FileSelector mFileSelector;
    public JsonSpoofingEnabledController mJsonSpoofingEnabledController;
    public Handler mHandler;

    public PifJsonLoaderController(Context context, FileSelector fileSelector, JsonSpoofingEnabledController jsonSpoofingEnabledController) {
        super(context);
        mFileSelector = fileSelector;
        mHandler = ((SpoofingSettings) fileSelector).getHandler();
        mJsonSpoofingEnabledController = jsonSpoofingEnabledController;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    public String getLogTag() {
        return TAG;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_PIF_JSON_LOADER;
    }

    @Override
    public void updateState(Preference preference) {
        preference.setEnabled(mJsonSpoofingEnabledController.isEnabled());
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (KEY_PIF_JSON_LOADER.equals(preference.getKey())) {
            mFileSelector.openFileSelector();
            return true;
        }
        return false;
    }

    public void loadPifJson(Uri uri) {
        Log.d(getLogTag(), "Loading PIF JSON from URI: " + uri.toString());
        try (InputStream inputStream = mContext.getContentResolver().openInputStream(uri)) {
            if (inputStream != null) {
                String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Log.d(getLogTag(), "PIF JSON data: " + json);
                JSONObject jsonObject = new JSONObject(json);
                for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                    String key = it.next();
                    String value = jsonObject.getString(key);
                    Log.d(getLogTag(), "Setting PIF property: persist.sys.pihooks_" + key + " = " + value);
                    SystemProperties.set("persist.sys.pihooks_" + key, value);
                }
                Toast.makeText(mContext, "PIF JSON loaded successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(getLogTag(), "Error reading PIF JSON or setting properties", e);
            Toast.makeText(mContext, "Error loading PIF JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        mHandler.postDelayed(() -> {
            SystemRestartUtils.restartSystem(mContext);
        }, 1250);
    }
}
