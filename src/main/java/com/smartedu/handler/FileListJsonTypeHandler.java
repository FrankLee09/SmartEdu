package com.smartedu.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartedu.entity.File;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.List;

public class FileListJsonTypeHandler extends BaseTypeHandler<List<File>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<File> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting List<File> to JSON", e);
        }
    }

    @Override
    public List<File> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        try {
            return json == null ? null : objectMapper.readValue(json, new TypeReference<List<File>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error reading JSON to List<File>", e);
        }
    }

    @Override
    public List<File> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        try {
            return json == null ? null : objectMapper.readValue(json, new TypeReference<List<File>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error reading JSON to List<File>", e);
        }
    }

    @Override
    public List<File> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        try {
            return json == null ? null : objectMapper.readValue(json, new TypeReference<List<File>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error reading JSON to List<File>", e);
        }
    }
}
