package com.example.mapquiz

class PlayerMP {
    private var userName: String? = null
    private var point: Int? = null
    private var totalPoint: Int? = null
    private var userId: String? = null
    private var status: String? = null

    fun getUserName(): String?{
        return userName
    }

    fun getPoint(): Int?{
        return this.point
    }

    fun getTotalPoint(): Int?{
        return this.totalPoint
    }

    fun getUserId(): String?{
        return this.userId
    }

    fun getStatus(): String?{
        return this.status
    }

    constructor() {
    }
    constructor(userId: String?, userName: String?, point: Int?, totalPoint: Int?, status: String?) {
        this.userId = userId
        this.userName = userName
        this.totalPoint = totalPoint
        this.point = point
        this.status = status
    }
}