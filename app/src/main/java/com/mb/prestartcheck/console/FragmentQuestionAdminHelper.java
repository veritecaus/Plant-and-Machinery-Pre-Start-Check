package com.mb.prestartcheck.console;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.R;

public class FragmentQuestionAdminHelper {

    public final static String DIALOG_TAG_PERMISSION_NOTICE = "dialog_tag_permission_notice";


    @Nullable
   public static ActivityResultLauncher<String> registerForPermissionGrants(final Fragment fragment, final Runnable callback)
    {
        try {

            ActivityResultLauncher<String> contract  =  fragment.registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            if (result)
                                callback.run();
                        }
                    });
            return contract;

        }
        catch(Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
        }

        return null;
    }

    public static  boolean checkPermissions(final Fragment fragment,  ActivityResultLauncher<String> contract)
    {
        if (ContextCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (fragment.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))  {
            String message = fragment.getString(R.string.message_rationale_permission_read_external);
            DialogQuestion dialogQuestion = new DialogQuestion(message, "OK", "No thanks", fragment);
            dialogQuestion.show(fragment.getParentFragmentManager(), DIALOG_TAG_PERMISSION_NOTICE);
        }
        else {
            contract.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        return false;
    }
}
