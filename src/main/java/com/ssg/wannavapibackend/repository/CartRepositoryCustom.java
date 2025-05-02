package com.ssg.wannavapibackend.repository;

import java.util.List;

public interface CartRepositoryCustom {
    void deleteCartItems(List<Long> cartIds);
}
