package com.mrrights.harvestoperator.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.mrrights.harvestoperator.R
import com.mrrights.harvestoperator.models.ModelMachine

class AdapterMachines(private var context: Context, private var listMachines: List<ModelMachine>) :
    RecyclerView.Adapter<AdapterMachines.MachineDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineDetailsViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.recyclerview_machines, parent, false)
        return MachineDetailsViewHolder(itemView)
    }

    class MachineDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewMachineType: TextView = itemView.findViewById(R.id.textViewMachineType)
        var textViewMachineNo: TextView = itemView.findViewById(R.id.textViewMachineNo)
        var textViewInsuranceNo: TextView = itemView.findViewById(R.id.textViewInsuranceNo)
        var textViewMachineStatus: TextView = itemView.findViewById(R.id.textViewMachineStatus)
        var imageViewMachine: ImageView = itemView.findViewById(R.id.imageViewMachine)
        var imageViewMachineStatus: ShapeableImageView =
            itemView.findViewById(R.id.imageViewMachineStatus)
    }

    override fun getItemCount(): Int {
        return listMachines.size
    }


    override fun onBindViewHolder(holder: MachineDetailsViewHolder, position: Int) {
        if (listMachines[position].condition == 1) {
            holder.textViewMachineStatus.text = "Bad"
            holder.imageViewMachineStatus.setBackgroundColor(Color.RED)
        } else if (listMachines[position].condition == 2) {
            holder.textViewMachineStatus.text = "Good"
            holder.imageViewMachineStatus.setBackgroundColor(Color.BLUE)
        } else if (listMachines[position].condition == 3) {
            holder.textViewMachineStatus.text = "Excellent"
            holder.imageViewMachineStatus.setBackgroundColor(Color.GREEN)
        }

        if (listMachines[position].type == 1) {
            holder.textViewMachineType.text = "TOT"
            holder.imageViewMachine.setImageResource(R.drawable.icons8_tractor_100px)
        } else if (listMachines[position].type == 2) {
            holder.textViewMachineType.text = "Belt"
            holder.imageViewMachine.setImageResource(R.drawable.icons8_crane_100px)
        } else if (listMachines[position].type == 3) {
            holder.textViewMachineType.text = "Combined"
            holder.imageViewMachine.setImageResource(R.drawable.icons8_harvester_100px)
        }

        holder.textViewMachineNo.text = listMachines[position].number
        holder.textViewInsuranceNo.text = listMachines[position].insuranceNo

    }


}