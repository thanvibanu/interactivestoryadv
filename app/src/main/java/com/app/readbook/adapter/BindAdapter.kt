package com.app.readbook.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BindAdapter<VB : ViewBinding, Data> : RecyclerView.Adapter<BindHolder<VB>>() {
    val data: ArrayList<Data> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindHolder<VB> {
        return BindHolder(createHolder(parent))
    }

    abstract fun createHolder(parent: ViewGroup?): VB
    override fun onBindViewHolder(holder: BindHolder<VB>, position: Int) {
        val d = data[position]
        bind(holder.vb, d, position)
    }

    abstract fun bind(vb: VB, data: Data, position: Int)
    override fun getItemCount(): Int {
        return data.size
    }
}