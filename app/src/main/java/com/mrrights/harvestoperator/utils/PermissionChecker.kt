package com.mrrights.harvestoperator.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.mrrights.harvestoperator.utils.Constants.Companion.DENIED
import com.mrrights.harvestoperator.utils.Constants.Companion.GRANTED

class PermissionChecker(val context: Context) {

    fun check(permissions: Array<String>): Int {

        var permitted = DENIED

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                permitted = GRANTED
            } else if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                permitted = DENIED
            }
        }
        return permitted
    }

}