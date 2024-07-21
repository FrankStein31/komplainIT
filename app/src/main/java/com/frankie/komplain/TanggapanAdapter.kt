package com.frankie.komplain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TanggapanAdapter(private val tanggapanList: List<Tanggapan>) : RecyclerView.Adapter<TanggapanAdapter.TanggapanViewHolder>() {

    class TanggapanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tanggalTextView: TextView = itemView.findViewById(R.id.tanggalTextView)
        val progressTextView: TextView = itemView.findViewById(R.id.progressTextView)
        val pukulTextView: TextView = itemView.findViewById(R.id.pukulTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TanggapanViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_tanggapan, parent, false)
        return TanggapanViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: TanggapanViewHolder, position: Int) {
        val tanggapan = tanggapanList[position]

        // Memisahkan bagian tanggal (yyyy-MM-dd)
        val tanggal = tanggapan.created_at.substring(0, 10)
        holder.tanggalTextView.text = formatDate(tanggal)

        // Memisahkan bagian pukul (HH:mm:ss)
        val pukul = tanggapan.created_at.substring(11, 16)
        holder.pukulTextView.text = formatPukul(pukul)

        // Untuk progressTextView, menggunakan tanggapan langsung
        holder.progressTextView.text = tanggapan.tanggapan
    }
    private fun sortTanggapanByLatest() {
        tanggapanList.sortedByDescending { it.created_at }
    }
    // Fungsi untuk memformat pukul ke dalam format 24 jam
    private fun formatPukul(pukul: String): String {
        // Parsing string pukul untuk mendapatkan jam dan menit
        val jamMenit = pukul.split(":")
        val jam = jamMenit[0].toInt()
        val menit = jamMenit[1]

        // Format jam ke dalam format 24 jam
        val format24Jam = String.format("%02d", jam)

        // Mengembalikan waktu dalam format yang diinginkan (HH:mm)
        return "$format24Jam:$menit WIB"
    }

    private fun formatDate(dateString: String): String {
        return try {
            // Parsing string tanggal ke dalam format yang diinginkan (yyyy-MM-dd)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(dateString)

            // Format tanggal ke dalam format yang baru (dd MMMM yyyy)
            val newDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            newDateFormat.format(date!!)
        } catch (e: ParseException) {
            "Not Found"
        }
    }


    override fun getItemCount(): Int {
        return tanggapanList.size
    }
}