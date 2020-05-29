package com.vickikbt.bebabeba.model

data class Riders(
    val Username: String,
    val Email: String,
    val Phone: String,
    val UID: String
) {
    constructor() : this("", "", "", "")
}

