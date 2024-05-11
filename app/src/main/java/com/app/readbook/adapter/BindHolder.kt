package com.app.readbook.adapter

import androidx.viewbinding.ViewBinding
import androidx.recyclerview.widget.RecyclerView

class BindHolder<VB : ViewBinding>(val vb: VB) : RecyclerView.ViewHolder(vb!!.root)