## <p align="center">수학문제 타이머2</p>
<p align="center">🛠︎ <a href="https://github.com/haechan29/Problem-Timer">수학문제 타이머</a>를 리팩토링한 프로젝트</p>
<p align="center"><img src="https://github.com/haechan29/Problem-Timer-2/assets/63138511/d92c82d7-5553-4062-b6cf-cdd307f34f9a" width=700></p>
<br/>

## 비효율적인 통신 과정을 개선

__[기존 코드]__
```
// 수업 리스트가 null인지 0.00n초간 확인한다.
private boolean isLecturesNullForNms(long n) {
    for (int i = 0; i < n / 10L; i++) {
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lectures == null) continue;
        return false;
    }
    return true;
}
```
➡️ Coroutine의 ``withTimeout()``을 사용하여 __가독성__, __처리 속도 증가__ <br/><br/>

## 콜백 지옥 탈출

__[기존 코드]__
```
threadPool.execute(new Runnable() {             // Callback 1

    @Override
    public void run() {
        // 단원별 점수 리스트를 얻는다.
        getScoreByUnit2List();
      
        threadPool.execute(new Runnable() {     // Callback 2

            @Override
            public void run() {
                ...

                handler.post(new Runnable() {   // Callback 3

                   @Override
                    public void run() {
                       ...
                    }
                });
            }
        });
    }
}
```
➡️ Coroutine을 통한 ``동시성 프로그래밍``으로 __콜백 지옥 삭제__ <br/><br/>

## 개발 기간 감소

__[기존 방식]__ <br/>
뷰와 레이아웃이 분리되어 있어(xml) 작업이 복잡하고, 가독성이 떨어짐<br/><br/>
➡️ Compose를 통해 ``선언적 UI``를 구성하여 __가독성 증가__, __부작용 감소__ <br/><br/>

__[기존 방식]__ <br/>
MVC 아키텍처를 적용하여 Activity가 비대해짐<br/><br/>
➡️ ``MVVM`` 아키텍처를 적용하여 __관심사 분리__, __가독성 증가__ <br/><br/>
