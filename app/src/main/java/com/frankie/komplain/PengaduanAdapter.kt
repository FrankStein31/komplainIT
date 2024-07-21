package com.frankie.komplain

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PengaduanAdapter(private var pengaduanList: ArrayList<ListPengadaan>, private val context: Context) :

    RecyclerView.Adapter<PengaduanAdapter.PengaduanViewHolder>() {
    inner class PengaduanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name_value)
        val statusTextView: TextView = itemView.findViewById(R.id.status_value)
        val jenisTextView: TextView = itemView.findViewById(R.id.jenis_value)
        val ratingTextView: TextView = itemView.findViewById(R.id.rating_value)
        val image: ImageView = itemView.findViewById(R.id.image_list)
        val button2: Button = itemView.findViewById(R.id.button2)

        init {
            button2.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentItem = pengaduanList[position]

                    val intent = Intent(context, Detail_laporan::class.java)
                    intent.putExtra("id", currentItem.id)
                    Log.d("id", "ID saya adalah: ${currentItem.id}")
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PengaduanViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.listitem, parent, false)
        return PengaduanViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PengaduanViewHolder, position: Int) {
        val currentItem = pengaduanList[position]
        holder.nameTextView.text = currentItem.name
        holder.statusTextView.text = currentItem.status
        holder.jenisTextView.text = currentItem.jenis_pengaduan
        holder.ratingTextView.text = currentItem.rating.toString()
        Glide.with(holder.itemView.context)
            .load("http://192.168.21.169:8000/storage/${currentItem.image}")
            .into(holder.image)
    }

    override fun getItemCount() = pengaduanList.size

    fun setData(data: List<ListPengadaan>) {
        pengaduanList.clear()
        pengaduanList.addAll(data)
        notifyDataSetChanged()
    }
}
