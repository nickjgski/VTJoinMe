package com.nickjgski.vtjoinme

import com.google.firebase.firestore.GeoPoint

data class Pin(val building: String,
               val totalSeats: Int,
               val availSeats: Int,
               val timestamp: String,
               val study: Boolean,
               val quiet: Boolean,
               val desc: String,
               val public: Boolean,
               val location: GeoPoint)