package com.example.mapquiz

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class PlayerAdapterScore(options: FirestoreRecyclerOptions<PlayerMP?>) : FirestoreRecyclerAdapter<PlayerMP?, PlayerAdapterScore.PlayerHolderScore?>(options) {
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PlayerHolderScore, position: Int, model: PlayerMP) {
        holder.tvUserName.text = model.getUserName()
        holder.tvPoints.text = model.getPoint().toString() + " puan"
        holder.tvStatus.text = model.getStatus()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerHolderScore {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.multi_score_rows,
            parent, false)
        return PlayerHolderScore(v)
    }

    inner class PlayerHolderScore(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUserName: TextView = itemView.findViewById<TextView?>(R.id.txt_username)!!
        var tvPoints: TextView = itemView.findViewById<TextView?>(R.id.txt_points)!!
        var tvStatus: TextView = itemView.findViewById<TextView?>(R.id.txtStatus)!!
    }
}