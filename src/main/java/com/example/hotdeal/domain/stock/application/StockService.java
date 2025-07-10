package com.example.hotdeal.domain.stock.application;

import com.example.hotdeal.domain.stock.domain.Stock;
import com.example.hotdeal.domain.stock.infra.StockRepository;

import com.example.hotdeal.global.lock.LockService;
import com.example.hotdeal.global.lock.RedissonLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockRepository repository;
    //private final LockService lockService;
    private final RedissonLockService lockService;
    private final StockTransactionService stockTransactionService;

    // 재고 증가 (관리자용)
    @Transactional
    public void increase(Long stockId, int qty) {
        Stock s = repository.findById(stockId).orElseThrow();
        s.increase(qty);
    }

    // 재고 초기화 (관리자용)
    @Transactional
    public void reset(Long stockId, int qty) {
        Stock s = repository.findById(stockId).orElseThrow();
        s.reset(qty);
    }

    // 남은 수량 조회
    @Transactional(readOnly = true)
    public int available(Long stockId) {
        return repository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("stock"))
                .getQuantity();
    }

    //재고 감소 로직
    @Transactional
    public void decrease(Long stockId, int quantity) {
        Stock stock = repository.findById(stockId).orElseThrow();
        stock.decrease(quantity);   // dirty checking 후 flush
    }

  // @Transactional 제거 - Lock 밖에서 트랜잭션이 시작되면 안됨
   public void decreaseWithLock(Long stockId, int quantity) {
       String lockKey = String.valueOf(stockId);
       lockService.executeWithLock(lockKey, () -> {
           // Lock 내부에서 새로운 트랜잭션 시작
          stockTransactionService.decreaseInNewTransaction(stockId, quantity);
          return null;
       });
   }

   //Mysql 행 락 사용
   @Transactional
    public void decreaseWithMysqlLock(Long stockId, int quantity) {
       Stock stock = repository.findByIdWithPessimisticLock(stockId)
               .orElseThrow(()-> new IllegalArgumentException("재고 없음"));

       if (stock.getQuantity() < quantity)
           throw new IllegalStateException("재고 부족");

       stock.decrease(quantity);  // dirty checking → UPDATE

   }

}
