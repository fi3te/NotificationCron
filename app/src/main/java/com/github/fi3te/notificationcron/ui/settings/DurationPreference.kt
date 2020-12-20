package com.github.fi3te.notificationcron.ui.settings

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.widget.Button
import androidx.core.widget.doAfterTextChanged
import androidx.preference.EditTextPreference
import com.github.fi3te.notificationcron.R
import java.util.regex.Pattern

class DurationPreference : EditTextPreference {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        initEditText()
    }

    private fun initEditText() = setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        editText.doAfterTextChanged { editable ->
            requireNotNull(editable)
            val isPositiveInteger = Pattern.matches("^[1-9]+[0-9]*$", editable.toString())
            editText.error =
                if (isPositiveInteger) null else editText.context.getString(R.string.invalid_duration)
            editText.rootView.findViewById<Button>(android.R.id.button1).isEnabled = isPositiveInteger;
        }
    }
}