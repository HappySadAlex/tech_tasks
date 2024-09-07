package com.example.techtask.service.impl;

import com.example.techtask.model.Order;
import com.example.techtask.model.enumiration.OrderStatus;
import com.example.techtask.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {

    private final JdbcTemplate template;

    @Autowired
    public OrderServiceImpl(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Order findOrder() throws SQLException {
        String sql = "SELECT * FROM techtask.orders Where quantity > 1 ORDER BY created_at DESC limit 1";
        List<Order> orders = template.query(sql, new OrderRowMapper());
        if (orders.isEmpty()) {
            return null;
        }
        return orders.get(0);
    }

    @Override
    public List<Order> findOrders() {
        String sql = "SELECT o.* FROM Orders o JOIN Users u ON o.user_id = u.id WHERE u.user_status = 'ACTIVE' ORDER BY o.created_at;";
        return template.query(sql, new OrderRowMapper());
    }

    public static class OrderRowMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                Order order = Order.class.getDeclaredConstructor().newInstance();

                setField(order, "id", rs.getInt("id"));
                setField(order, "productName", rs.getString("product_name"));
                setField(order, "price", rs.getDouble("price"));
                setField(order, "quantity", rs.getInt("quantity"));
                setField(order, "userId", rs.getInt("user_id"));
                setField(order, "createdAt", rs.getTimestamp("created_at").toLocalDateTime());
                setField(order, "orderStatus", OrderStatus.valueOf(rs.getString("order_status")));

                return order;
            } catch (Exception e) {
                throw new SQLException("Error mapping row to Order", e);
            }
        }
    }

    private static void setField(Object target, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        if (value instanceof Integer) {
            field.setInt(target, (Integer) value);
        } else if (value instanceof Double) {
            field.setDouble(target, (Double) value);
        } else if (value instanceof String) {
            field.set(target, value);
        } else if (value instanceof LocalDateTime) {
            field.set(target, value);
        } else if (value instanceof OrderStatus) {
            field.set(target, value);
        } else {
            throw new IllegalArgumentException("Unsupported field type");
        }
    }

}
