package com.example.mapquiz

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class PlayerAdapterRanking(options: FirestoreRecyclerOptions<PlayerRanking?>) : FirestoreRecyclerAdapter<PlayerRanking?, PlayerAdapterRanking.PlayerHolderRanking?>(options) {
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PlayerHolderRanking, position: Int, model: PlayerRanking) {
        holder.tvUserName.text = model.getKullaniciAdi()
        holder.tvPoints.text = model.getPuan().toString() + " puan"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerHolderRanking {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.score_rows,
                parent, false)
        return PlayerHolderRanking(v)
    }

    inner class PlayerHolderRanking(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUserName: TextView = itemView.findViewById<TextView?>(R.id.txt_username)!!
        var tvPoints: TextView = itemView.findViewById<TextView?>(R.id.txt_points)!!
    }
}