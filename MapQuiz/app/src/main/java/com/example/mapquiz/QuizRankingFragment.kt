package com.example.mapquiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class QuizRankingFragment:Fragment(){
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var adapter: PlayerAdapterRanking? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ranking, container,false)

        setUpRecyclerView(view)

        return view
    }

    private fun setUpRecyclerView(view: View) {
        val collectionRef = db.collection("Kullanıcılar")
        val query = collectionRef.orderBy("puan", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<PlayerRanking>()
                .setQuery(query, PlayerRanking::class.java)
                .build()

        adapter = PlayerAdapterRanking(options)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }
}