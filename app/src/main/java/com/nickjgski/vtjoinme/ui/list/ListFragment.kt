package com.nickjgski.vtjoinme.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nickjgski.vtjoinme.Pin
import com.nickjgski.vtjoinme.R


class ListFragment : Fragment() {

    val mFirestore = FirebaseFirestore.getInstance()
    var mQuery: Query = mFirestore.collection("pins")

    var options: FirestoreRecyclerOptions<Pin> = FirestoreRecyclerOptions.Builder<Pin>()
        .setQuery(mQuery, Pin::class.java)
        .build()

    var adapter: PinAdapter = PinAdapter(options)

    private lateinit var pinList: RecyclerView

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        pinList = view.findViewById(R.id.pinlist)
        pinList.layoutManager = LinearLayoutManager(context)
        pinList.adapter = adapter
        return view
    }


    inner class PinAdapter(options: FirestoreRecyclerOptions<Pin>): FirestoreRecyclerAdapter<Pin, PinAdapter.PinViewHolder>(options) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_view, parent, false)
            return PinViewHolder(v)
        }

        override fun onBindViewHolder(holder: PinViewHolder, position: Int, pin: Pin) {
            var use = if(pin.study) {
                "Study"
            } else {
                "Eat"
            }
            holder.view.findViewById<TextView>(R.id.title).text = "${pin.building}, $use"
            holder.view.findViewById<TextView>(R.id.seats).text = "${pin.availSeats}/${pin.totalSeats}"
            holder.view.findViewById<TextView>(R.id.extra_info).text = pin.desc
        }

        inner class PinViewHolder(val view: View): RecyclerView.ViewHolder(view),View.OnClickListener {
            override fun onClick(view: View?) {

            }

        }

    }
}