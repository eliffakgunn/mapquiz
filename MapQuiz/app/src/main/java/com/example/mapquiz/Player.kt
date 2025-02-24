package com.example.mapquiz

class Player {
    private var userName: String? = null
    private var point: Int? = null
    private var totalPoint: Int? = null
    private var userId: String? = null

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

    constructor() {
    }
    constructor(userId: String?, userName: String?, point: Int?, totalPoint: Int?) {
        this.userId = userId
        this.userName = userName
        this.totalPoint = totalPoint
        this.point = point
    }
}