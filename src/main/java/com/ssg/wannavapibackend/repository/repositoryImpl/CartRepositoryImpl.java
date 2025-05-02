package com.ssg.wannavapibackend.repository.repositoryImpl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssg.wannavapibackend.common.ErrorCode;
import com.ssg.wannavapibackend.domain.QCart;
import com.ssg.wannavapibackend.exception.CustomException;
import com.ssg.wannavapibackend.repository.CartRepositoryCustom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QCart cart = QCart.cart;

    @Transactional
    public void deleteCartItems(List<Long> cartIds) {
        long deletedCount =  queryFactory
            .delete(cart)
            .where(cart.id.in(cartIds))
            .execute();

        if (deletedCount != cartIds.size()) {
            throw new CustomException(ErrorCode.CART_ITEM_DELETE_FAILED);
        }
    }
}
