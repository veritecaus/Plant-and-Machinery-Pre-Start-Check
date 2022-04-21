package com.mb.prestartcheck.console;

import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.mb.prestartcheck.R;

import java.util.regex.Pattern;

public class UIValidator {

    private static Pattern pattern_integer = Pattern.compile("^[0-9]+$");
    private static Pattern pattern_decimal = Pattern.compile("^[0-9,\\.]+$");


    @Nullable
    public  static  Integer safeGetInteger(EditText editText)
    {
        String str = editText.getText().toString().trim();
        return pattern_integer.matcher(str).find() ? Integer.parseInt(str) : null;

    }

    @Nullable
    public  static  Integer safeGetInteger(String number)
    {
        return pattern_integer.matcher(number).find() ? Integer.parseInt(number) : null;
    }


    public static void checkNumberEntry(EditText editText, View view, int errResId) throws UIFormatException
    {
        if (safeGetInteger(editText) == null)
        {
            AlertManager.showDialog(view.getContext(), errResId);
            throw new UIFormatException(editText, "EditText string was not a number.");
        }

    }


    /**
     * For the edit text entry, validate number entry and that the value is between min
     * and max parameters.
     * @param editText Reference to the EditText view containing the text entry.
     * @param view Parent Fragment containing the text entry
     * @param errResId The resource identifer of the error message to display if the text entry is invalid.
     * @param min Lowest permissible value
     * @param max Highest permissible value
     * @throws UIFormatException
     */    public static void checkNumberRange(EditText editText, View view, int errResId, int max, int min) throws UIFormatException
    {
        Integer itmp = safeGetInteger(editText);
        if (itmp != null)
        {
            if (itmp < min || itmp > max)
            {
                AlertManager.showDialog(view.getContext(), errResId);
                throw new UIFormatException(editText, "EditText number out of range.");
            }
        }
        else
            throw new UIFormatException(editText, "EditText string was not a number.");
    }

    public static void checkTextEntry(EditText editText, View view, int errResId) throws UIFormatException
    {
        String str =editText.getText().toString().trim();
        if (str.isEmpty())
        {
            AlertManager.showDialog(view.getContext(), errResId);
            throw new UIFormatException(editText, "EditText string was empty.");
        }

    }

    public static void checkIpEntry(EditText editText, View view, int errResId) throws UIFormatException
    {
        String str =editText.getText().toString().trim();
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        if (!str.matches(PATTERN))
        {
            AlertManager.showDialog(view.getContext(), errResId);
            throw new UIFormatException(editText, "Invalid tcp address entered.");
        }

    }


    public static void checkDuplicateEntries(EditText editText,  EditText editText2,View view, int errResId) throws UIFormatException
    {
        if (editText.getText().toString().trim().compareToIgnoreCase(editText2.getText().toString().trim()) == 0)
        {
            AlertManager.showDialog(view.getContext(), errResId);
            throw new UIFormatException(editText,  "Duplicate entries.");
        }

    }
}
