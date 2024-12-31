package com.masonk.notice

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket

fun main() {
    // 새로운 스레드를 생성하여 실행
    Thread {
        // 클라이어트 연결을 수락할 포트 번호 지정
        val port = 8080
        
        // 지정된 포트 번호로 서버 소켓을 생성하고, 해당 포트에서 들어오는 연결 요청을 수락할 준비
        val server = ServerSocket(port)

        // 무한 루프를 통해 클라이언트 연결을 지속적으로 수락
        while (true) {
            // 클라리언트가 연결을 요청하면, 클라이언트와의 통신을 위한 Socket 객체 반환
            // 이 Socket 객체를 사용하여 클라이언트와 데이터를 주고 받음
            val socket = server.accept()

            // 클라이언트로부터 데이터를 읽기 위한 input 스트림
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            
            // 클라이언트로 데이터를 보내기 위한 output 스트림
            val printer = PrintWriter(socket.getOutputStream())

            // 클라이언트로부터 데이터를 한 줄씩 읽고 터미널에 출력
            var input: String? = "-1"
            while (input != null && input != "") {
                input = reader.readLine()
                println("READ DATA $input")
            }

            // HTTP 응답 HEADER
            // 응답의 상태와 속성 전달 (메타데이터)
            // 클라이언트가 응답을 올바르게 처리하는 데 필요한 정보를 제공
            printer.println("HTTP/1.1 200 OK") // HTTP 상태 코드 200 OK를 클라이언트에 전송
            printer.println("Content-Type: text/html\r\n") // 응답의 콘텐츠 타입을 HTML로 설정

            // HTTP 응답 BODY
            // 실제 데이터
            printer.println("{\"message\": \"Today is the last day of 2024\"}") // JSON 메시지
            printer.println("\r\n") // 완전한 줄바꿈, HTTP 프로토콜에서 각 헤더 필드의 끝을 표시하는데 사용
            printer.flush()

            // 리소스 해제
            printer.close()
            reader.close()
            socket.close()
        }
    }.start()
}