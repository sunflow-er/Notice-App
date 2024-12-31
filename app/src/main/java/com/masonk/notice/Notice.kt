package com.masonk.notice

import com.google.gson.annotations.SerializedName

data class Notice(
    // 난독화하여 보안성을 높이기 위해 사용
    // JSON 필드 이름과 Kotlin 데이터 클래스의 속성 이름을 매핑
    // JSON 데이터의 "message" 필드가 Kotlin 데이터 클래스의 "x" 속성에 매핑
    @SerializedName("message")
    val x: String
)
