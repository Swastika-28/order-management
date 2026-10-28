package com.project.order_management.repo;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Map;

@Repository

public class OrderProcessingRepo {
    private final SimpleJdbcCall simpleJdbcCall;

    public OrderProcessingRepo(DataSource dataSource) {
        this.simpleJdbcCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_process_order")
                .declareParameters(
                         new SqlParameter("p_customer_id", Types.BIGINT),
                        new SqlParameter("p_product_id", Types.BIGINT),
                        new SqlParameter("p_quantity", Types.INTEGER),
                        new SqlOutParameter("p_order_id", Types.BIGINT),
                        new SqlOutParameter("p_total_amount", Types.DECIMAL),
                        new SqlOutParameter("p_status_code", Types.VARCHAR),
                        new SqlOutParameter("p_status_message", Types.VARCHAR)
                );

    }

    public Map<String, Object> executeProcessOrder(Long customerId, Long productId, Integer quantity) {
        SqlParameterSource inputParams=new MapSqlParameterSource()
                .addValue("p_customer_id",customerId)
                .addValue("p_product_id",productId)
                .addValue("p_quantity",quantity);
        return simpleJdbcCall.execute(inputParams);

    }


}
