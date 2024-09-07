package com.example.techtask.service.impl;

import com.example.techtask.model.User;
import com.example.techtask.model.enumiration.UserStatus;
import com.example.techtask.service.UserService;
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
public class UserServiceImpl implements UserService {

    private final JdbcTemplate template;

    @Autowired
    public UserServiceImpl(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public User findUser() {
        String sql = "SELECT u.*\n" +
                "FROM users u\n" +
                "JOIN (\n" +
                "\tSELECT o.user_id, SUM(o.price * o.quantity) AS total_amount\n" +
                "    FROM orders o\n" +
                "    WHERE EXTRACT(YEAR FROM o.created_at) = 2003\n" +
                "      AND o.order_status = 'DELIVERED'\n" +
                "    GROUP BY o.user_id\n" +
                "    ORDER BY total_amount DESC\n" +
                "    LIMIT 1\n" +
                "\t) max_order ON u.id = max_order.user_id;";


        return template.query(sql, new UserRowMapper()).get(0);
    }

    @Override
    public List<User> findUsers() {
        String sql = "SELECT DISTINCT u.* FROM users u " +
                "JOIN orders o ON u.id = o.user_id " +
                "WHERE EXTRACT(YEAR FROM o.created_at) = 2010 AND o.order_status = 'PAID';";
        return template.query(sql, new UserRowMapper());
    }

    public static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                User user = User.class.getDeclaredConstructor().newInstance();

                setField(user, "id", rs.getInt("id"));
                setField(user, "email", rs.getString("email"));
                setField(user, "userStatus", UserStatus.valueOf(rs.getString("user_status")));

                return user;
            } catch (Exception e) {
                throw new SQLException("Error mapping row to User", e);
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
        } else if (value instanceof UserStatus) {
            field.set(target, value);
        } else {
            throw new IllegalArgumentException("Unsupported field type");
        }
    }

}
