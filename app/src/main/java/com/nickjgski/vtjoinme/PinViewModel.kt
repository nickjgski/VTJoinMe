package com.nickjgski.vtjoinme

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class PinViewModel: ViewModel() {

    val mFirestore = FirebaseFirestore.getInstance()

}