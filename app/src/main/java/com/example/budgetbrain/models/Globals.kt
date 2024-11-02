package com.example.budgetbrain.models

object Globals {
    var SessionUser: SessionUser? = null;
    fun clear() {
        SessionUser = null
    }
    var biometricsAvailable: Boolean = false;
}