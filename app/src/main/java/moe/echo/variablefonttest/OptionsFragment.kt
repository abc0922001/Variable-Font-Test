package moe.echo.variablefonttest

import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import androidx.preference.*
import rikka.preference.SimpleMenuPreference

class OptionsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.options, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.overScrollMode = View.OVER_SCROLL_NEVER

        val fontVariationSettings = mutableMapOf<String, String>()

        val fontFamilyValues = resources.getStringArray(R.array.font_families_value)
        val fontFamilyList = arrayOf(
            Typeface.DEFAULT, Typeface.DEFAULT_BOLD, Typeface.MONOSPACE,
            Typeface.SANS_SERIF, Typeface.SERIF
        )
        val valueToTypeface = fontFamilyValues.zip(fontFamilyList).toMap()

        val previewContent: EditText =
            requireParentFragment().requireView().findViewById(R.id.preview_content)
        val textSize: EditTextPreference? = findPreference(Constants.PREF_TEXT_SIZE)
        val fontFamilies: SimpleMenuPreference? = findPreference(Constants.PREF_FONT_FAMILIES)

        val ital: SwitchPreferenceCompat? = findPreference(Constants.PREF_VARIATION_ITALIC)
        val opsz: EditTextPreference? = findPreference(Constants.PREF_VARIATION_OPTICAL_SIZE)
        val slnt: EditTextPreference? = findPreference(Constants.PREF_VARIATION_SLANT)
        val wdth: EditTextPreference? = findPreference(Constants.PREF_VARIATION_WIDTH)
        val wght: SeekBarPreference? = findPreference(Constants.PREF_VARIATION_WEIGHT)
        val chws: SwitchPreferenceCompat? = findPreference(Constants.PREF_FEATURE_CHWS)

        textSize?.apply {
            setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().toFloatOrNull() != null) {
                    previewContent.textSize = newValue.toString().toFloat()
                    true
                } else false
            }
        }

        fontFamilies?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                if (valueToTypeface.contains(newValue)) {
                    previewContent.typeface = valueToTypeface[newValue]
                    wght?.value = 400
                    true
                } else false
            }
        }

        ital?.apply {
            setOnPreferenceChangeListener { _, _ ->
                fontVariationSettings[Constants.VARIATION_AXIS_ITALIC] =
                    if (!isChecked) "1" else "0"
                previewContent.fontVariationSettings = fontVariationSettings.toFeatures()
                true
            }
        }

        opsz?.apply {
            setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
            }
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().toDoubleOrNull() != null) {
                    fontVariationSettings[Constants.VARIATION_AXIS_OPTICAL_SIZE] =
                        newValue.toString()
                    previewContent.fontVariationSettings = fontVariationSettings.toFeatures()
                    true
                } else false
            }
        }

        slnt?.apply {
            setOnBindEditTextListener { editText ->
                editText.inputType =
                    InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            }
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().toDoubleOrNull() != null) {
                    fontVariationSettings[Constants.VARIATION_AXIS_SLANT] = newValue.toString()
                    previewContent.fontVariationSettings = fontVariationSettings.toFeatures()
                    true
                } else false
            }
        }

        wdth?.apply {
            setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
            }
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().toDoubleOrNull() != null) {
                    fontVariationSettings[Constants.VARIATION_AXIS_WIDTH] = newValue.toString()
                    previewContent.fontVariationSettings = fontVariationSettings.toFeatures()
                    true
                } else false
            }
        }

        wght?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().toFloatOrNull() != null) {
                    fontVariationSettings[Constants.VARIATION_AXIS_WEIGHT] = newValue.toString()
                    previewContent.fontVariationSettings = fontVariationSettings.toFeatures()
                    true
                } else false
            }
        }

        chws?.apply {
            setOnPreferenceChangeListener { _, _ ->
                // TODO: replace with list
                previewContent.fontFeatureSettings = if (!isChecked) "'chws' 1" else ""
                true
            }
        }
    }

    private fun MutableMap<String, String>.toFeatures(): String =
        this.toList().joinToString { "'${it.first}' ${it.second}" }
}