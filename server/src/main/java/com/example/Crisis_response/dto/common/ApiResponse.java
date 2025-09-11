package com.example.Crisis_response.dto.common;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String path;
    private int status;
    private String timestamp;

    public ApiResponse() {
        this.timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public ApiResponse(boolean success, String message, T data, String path, int status) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.path = path;
        this.status = status;
        this.timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static <T> ApiResponse<T> success(String message, T data, String path, int status) {
        return new ApiResponse<T>(true, message, data, path, status);
    }

    public static <T> ApiResponse<T> error(String message, T data, String path, int status) {
        return new ApiResponse<T>(false, message, data, path, status);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}


