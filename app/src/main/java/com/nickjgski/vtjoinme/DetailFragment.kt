package com.nickjgski.vtjoinme


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {

    val mFirestore = FirebaseFirestore.getInstance()

    private lateinit var titleText: TextView
    private lateinit var seatsText: TextView
    private lateinit var descText: TextView
    private lateinit var boolText: TextView
    private lateinit var tableButton: Button

    private var model: PinViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        model = activity?.let { ViewModelProviders.of(it).get(PinViewModel::class.java)}
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleText = view.findViewById(R.id.table_title)
        seatsText = view.findViewById(R.id.table_seats)
        descText = view.findViewById(R.id.table_desc)
        boolText = view.findViewById(R.id.table_bool)
        tableButton = view.findViewById(R.id.table_button)

        val id: String? = this.arguments?.getString("id").toString()
        Log.d("Detail", "$id")
        var table: Pin? = Pin()
        val doc = mFirestore.collection("pins").document(id!!)
        doc.get().addOnSuccessListener { documentSnapshot ->
            table = documentSnapshot.toObject(Pin::class.java)
            table?.uid = id
            Log.d("Detail", "Success")
            titleText.text = table?.building
            seatsText.text = "${table?.availSeats}/${table?.totalSeats}"
            descText.text = table?.desc
            var study = if(table != null && table!!.study) {
                "studying"
            } else {
                "dining"
            }
            var quiet = if(table != null && table!!.quiet) {
                "Quietly"
            } else {
                "Socially"
            }
            boolText.text = "$quiet $study"
        }
        if (model?.currTable != "" && model?.currTable == id) {
            tableButton.text = "Leave"
        } else {
            tableButton.text = "Join"
        }

        tableButton.setOnClickListener {
            if (model?.currTable != "" && model?.currTable == id) {
                doc.update("availSeats", FieldValue.increment(-1))
                model?.currTable = ""
                tableButton.text = "Join"
                table?.availSeats = table?.availSeats!! + 1
            } else {
                doc.update("availSeats", FieldValue.increment(1))
                model?.currTable = id
                tableButton.text = "Leave"
                table?.availSeats = table?.availSeats!! - 1
            }
            seatsText.text = "${table?.availSeats}/${table?.totalSeats}"
        }

    }


}
