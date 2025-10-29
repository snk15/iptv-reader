package com.example.iptvreader.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.iptvreader.R
import com.example.iptvreader.model.IPTVItem
import com.squareup.picasso.Picasso

class M3UAdapter(
    private var items: List<IPTVItem>,
    private val onClick: (IPTVItem) -> Unit
) : RecyclerView.Adapter<M3UAdapter.VH>() {

    fun update(newItems: List<IPTVItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_channel, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val it = items[position]
        holder.title.text = it.title
        holder.group.text = it.groupTitle ?: ""
        if (!it.tvgLogo.isNullOrEmpty()) {
            holder.logo.visibility = View.VISIBLE
            Picasso.get().load(it.tvgLogo).fit().centerCrop().into(holder.logo)
        } else {
            holder.logo.visibility = View.GONE
        }
        holder.itemView.setOnClickListener { onClick(it) }
    }

    override fun getItemCount(): Int = items.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.channel_title)
        val group: TextView = view.findViewById(R.id.channel_group)
        val logo: ImageView = view.findViewById(R.id.channel_logo)
    }
}