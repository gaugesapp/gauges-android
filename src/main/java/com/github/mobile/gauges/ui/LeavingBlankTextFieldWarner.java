package com.github.mobile.gauges.ui;

import android.view.View;
import android.widget.EditText;

import com.github.mobile.gauges.R.string;

import roboguice.inject.InjectResource;

/**
 * Helper to set an error message on a {@link EditText} when the field is empty and the view loses focus
 */
public class LeavingBlankTextFieldWarner implements View.OnFocusChangeListener {

    @InjectResource(string.blank_field_warning)
    private String warning;

    @Override
    public void onFocusChange(final View view, final boolean hasFocus) {
        EditText editText = (EditText) view;
        if (editText.length() == 0 && !hasFocus)
            editText.setError(warning);
    }
}
