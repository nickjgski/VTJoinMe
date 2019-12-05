package com.nickjgski.vtjoinme

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.util.*

data class Pin(val uid: String = "",
               val building: String = "",
               val totalSeats: Int = -1,
               val availSeats: Int = -1,
               val timestamp: Date = Date(),
               val study: Boolean = false,
               val quiet: Boolean = false,
               val desc: String = "",
               val public: Boolean = false,
               val location: GeoPoint = GeoPoint(0.0, 0.0)
)