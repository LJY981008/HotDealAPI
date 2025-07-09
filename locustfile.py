from locust import HttpUser, task, between
import json
import random
import time

# 실행과정
# brew install python3
# brew install locust
# cd /Users/ljy/IdeaProjects/test/HotDeal // 터미널을 통해 이 파일이 있는 경로로 이동
# python3 -m locust -f locustfile.py --host=http://localhost:8080 // 이 파일 실행
# localhost:8089 로 접속

class EventCreateUser(HttpUser):
    wait_time = between(2, 5)  # 요청 간 2-5초 대기 (부하 감소)
    
    def on_start(self):
        """사용자가 시작할 때 실행되는 메서드"""
        # 필요시 로그인 등의 초기 설정
        pass
    
    @task
    def create_event(self):
        """이벤트 생성 API 테스트"""
        headers = {"Content-Type": "application/json"}
        
        # 랜덤한 이벤트 데이터 생성
        event_data = {
            "eventType": "HOT_DEAL",
            "eventDiscount": random.randint(5, 50),
            "eventDuration": random.randint(1,7),
            "startEventTime": "2025-07-09T00:00:00",
            "productIds" : [1,2,3,4,5,6,7,8,9,10]
        }
        
        # API 호출
        with self.client.post(
            "/api/event/create",
            json=event_data,
            headers=headers,
            catch_response=True
        ) as response:
            if response.status_code == 201:
                response.success()
                print(f"이벤트 생성 성공: {response.json()}")
            else:
                response.failure(f"이벤트 생성 실패 - 상태코드: {response.status_code}, 응답: {response.text}")
                print(f"이벤트 생성 실패: {response.status_code} - {response.text}") 