package com.vickikbt.bebabeba.data.model

data class DriversInfo(
    val Username: String,
    val Email: String,
    val Phone: String,
    val UID: String,
    val CustomerRideID: String
) {
    constructor() : this("", "", "", "", "")
}