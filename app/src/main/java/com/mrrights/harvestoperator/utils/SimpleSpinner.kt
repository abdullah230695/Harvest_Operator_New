package com.mrrights.harvestoperator.utils

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter

class SimpleSpinner(val context: Context) {

    //SIMPLE SPINNER ARRAY
    fun listSpinner(arraySpinner: Array<String>): SpinnerAdapter {
        val adapterMachineCondition = ArrayAdapter(context, android.R.layout.simple_spinner_item, arraySpinner)
        adapterMachineCondition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapterMachineCondition
    }

}