package com.vickikbt.bebabeba.utils.permission

/*
*  This is a Listener to Listen when a user gets permissions dialog
* */
interface RequestPermissionResultListener {
    fun onPermissionGranted()
    fun onPermissionDenied()
    fun onPermissionPermanentlyDenied()
}