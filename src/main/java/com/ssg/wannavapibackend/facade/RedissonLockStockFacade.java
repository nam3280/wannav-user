package com.ssg.wannavapibackend.facade;

import com.ssg.wannavapibackend.dto.request.ProductPurchaseRequestDTO;
import com.ssg.wannavapibackend.service.PaymentService;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Lazy;

@Log4j2
@Component
public class RedissonLockStockFacade {

    private final RedissonClient redissonClient;  // Redisson 클라이언트를 사용하여 Redis의 락을 관리
    private final PaymentService paymentService;  // PaymentService 통해 재고 감소 작업을 처리
//    private ProductRepository productRepository;

    // 생성자, RedissonClient와 StockService를 주입받음
    public RedissonLockStockFacade(RedissonClient redissonClient,
        @Lazy PaymentService paymentService) {
        this.redissonClient = redissonClient;
        this.paymentService = paymentService;
    }

    public void decreaseProductStock(List<ProductPurchaseRequestDTO> requestDTOList) {
        for (ProductPurchaseRequestDTO item : requestDTOList) {
            String lockName = "lock-" + item.getProductId(); // 상품 ID로 고유 락 이름 생성
            RLock lock = redissonClient.getLock(lockName);

            try {
                boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);

                if (!available) {
                    continue; // 락을 획득하지 못한 경우에는 넘어가서 다음 상품 처리
                }

                // 재고 감소 처리
                paymentService.decrease(item.getProductId(), item.getQuantity());
            } catch (InterruptedException e) {
                log.error("Error while acquiring lock for productId: {}", item.getProductId(), e);
                Thread.currentThread().interrupt(); // 현재 쓰레드의 인터럽트를 복원
            } catch (Exception e) {
                log.error("Unexpected error during stock decrease", e);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock(); // 현재 쓰레드가 획득한 락만 해제
                }
            }
        }
    }

        // service에 decrease가 존재하면 service와 Facade가 서로 의존
//    private void decrease(Long productId, int quantity) {
//        Product product = productRepository.findById(productId)
//            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
//        product.decrease(quantity);
//
//        productRepository.saveAndFlush(product);
//    }

    }
