package com.pebel.authentication.dto.response

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val errors: Map<String, String>? = null
) {
    companion object {
        fun <T> success(data: T, message: String = "Success"): ApiResponse<T> {
            return ApiResponse(success = true, message = message, data = data)
        }
        
        fun <T> success(message: String = "Success"): ApiResponse<T> {
            return ApiResponse(success = true, message = message, data = null)
        }
        
        fun <T> error(message: String, errors: Map<String, String>? = null): ApiResponse<T> {
            return ApiResponse(success = false, message = message, data = null, errors = errors)
        }
    }
}