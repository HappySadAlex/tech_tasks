package com.example.techtask.controller;

import com.example.techtask.model.Order;
import com.example.techtask.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

/**
 * Attention! Only DI and service interaction applicable in this class. Each controller method
 * should only contain a call to the corresponding service method
 */
@RestController
@RequestMapping("api/v1/orders")
public class OrderController {

  // DI here
  private final OrderService orderService;

  @Autowired
  public OrderController(OrderService orderService){
    this.orderService = orderService;
  }

  @GetMapping("desired-order")
  public Order findOrder() throws SQLException {
    return orderService.findOrder();
  }

  @GetMapping("desired-orders")
  public List<Order> findOrders() {
    return orderService.findOrders();
  }
}
