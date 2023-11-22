package com.sws.danggeun.service;

import com.sws.danggeun.dto.OrderDto;
import com.sws.danggeun.entity.*;
import com.sws.danggeun.repository.OrderRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final OrderRepository orderRepository;
    //상품조회

    //상품검색

    //상품구매 - n개의 cart받기

    //상품판매

    //장바구니 구매

    //단일상품 구매


}
