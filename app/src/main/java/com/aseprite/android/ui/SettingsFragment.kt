package com.aseprite.android.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.aseprite.android.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}