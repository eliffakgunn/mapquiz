package com.example.mapquiz

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class PlayerAdapterWR(options: FirestoreRecyclerOptions<Player?>) : FirestoreRecyclerAdapter<Player?, PlayerAdapterWR.PlayerHolder?>(options) {
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PlayerHolder, position: Int, model: Player) {
        holder.tvUserName.text = model.getUserName()
        holder.tvPoints.text = model.getTotalPoint().toString() + " puan"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.row_players_yedek,
                parent, false)
        return PlayerHolder(v)
    }

    inner class PlayerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUserName: TextView = itemView.findViewById<TextView?>(R.id.txt_username)!!
        var tvPoints: TextView = itemView.findViewById<TextView?>(R.id.txt_points)!!
    }
}