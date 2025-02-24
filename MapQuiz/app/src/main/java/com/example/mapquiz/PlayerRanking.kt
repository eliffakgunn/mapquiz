package com.example.mapquiz

class PlayerRanking {
    private var kullaniciAdi: String? = null
    private var puan: Long? = null
    private var email: String? = null

    fun getKullaniciAdi(): String?{
        return this.kullaniciAdi
    }

    fun getPuan(): Long?{
        return this.puan
    }

    fun getEmail(): String?{
        return this.email
    }

    constructor() {
    }

    constructor(kullaniciAdi: String?, puan: Long?, email: String?) {
        this.kullaniciAdi = kullaniciAdi
        this.puan = puan
        this.email = email
    }
}