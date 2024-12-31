package com.masonk.notice

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.gson.Gson
import com.masonk.notice.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

// 클라이언트
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // OkHttp 클라이언트
        // HTTP 요청을 보내고 응답을 받기 위한 클라이언트 객체를 생성
        // 이 객체를 사용하여 HTTP 요청을 구성하고 실행
        val client = OkHttpClient()

        // 입력받은 서버 호스트 주소를 저장할 변수
        var serverHost = ""

        // EditText에 텍스트 변경 리스너를 추가, 입력된 서버 호스트 주소를 저장
        binding.serverHostEditText.addTextChangedListener {
            serverHost = it.toString()
        }

        // 서버에 연결 요청을 보냄
        binding.contactButton.setOnClickListener {
            // 객체를 생성하여 지정된 URL로 HTTP 요청을 설정
            val request: Request = Request.Builder()
                .url("http://${serverHost}:8080")
                .build()

            // OkHttp로 비동기 요청을 보내기 위한 콜백 객체 생성
            // 콜백 메서드는 OkHttp 내부 스레드에서 실행됨
            val callback = object : Callback {
                // 요청 실패 시
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "수신 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                // 요청 성공 시
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        // 응답의 BODY를 문자열로 변환
                        val response = response.body?.string()
                        
                        // GSON을 사용하여 JSON 응답을 Notice 객체로 변환
                        val notice = Gson().fromJson(response, Notice::class.java)

                        runOnUiThread {
                            // 응답 메시지(공지) 표시
                            binding.noticeText.apply {
                                isVisible = true
                                text = notice.x
                            }
                            
                            // 서버호스트 입력 필드와 연결 버튼 숨김
                            binding.serverHostEditText.isVisible = false
                            binding.contactButton.isVisible = false
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "수신 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

           
            // 네트워크 요청이 완료될 때까지 메인 스레드가 차단되지 않도록 하기 위해 (앱의 성능과 사용자 경험 향상)
            // 네트워크 요청이 백그라운드에서 처리되고, 요청이 완료되면 콜백 메서드를 통해 결과 처리
            client
                .newCall(request) // Request 객체를 인자로 받아 새로운 Call 객체 생성, 이 Call 객체는 HTTP 요청을 나타냄
                .enqueue(callback) // 비동기적으로 HTTP 요청 실행,  Callback 객체를 인자로 받아, 요청이 안료되었을 때 콜백 메서드 호출
        }
    }
}