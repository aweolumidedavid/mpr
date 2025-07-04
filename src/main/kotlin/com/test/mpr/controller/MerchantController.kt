package com.test.mpr.controller

import com.test.mpr.dto.ApiResponse
import com.test.mpr.dto.CreateMerchantRequest
import com.test.mpr.dto.MerchantResponse
import com.test.mpr.service.MerchantService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/merchants")
@Tag(name = "Merchant Management", description = "APIs for managing merchants")
class MerchantController(
    private val merchantService: MerchantService
) {
    
    @PostMapping
    @Operation(summary = "Create a new merchant", description = "Creates a new merchant with the provided details")
    fun createMerchant(@Valid @RequestBody request: CreateMerchantRequest): ResponseEntity<ApiResponse<MerchantResponse>> {
        val merchant = merchantService.createMerchant(request)
        return ResponseEntity.ok(ApiResponse.success(merchant, "Merchant created successfully"))
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get merchant by ID", description = "Retrieves merchant details by their ID")
    fun getMerchant(@PathVariable id: Long): ResponseEntity<ApiResponse<MerchantResponse>> {
        val merchant = merchantService.getMerchantById(id)
        return ResponseEntity.ok(ApiResponse.success(merchant, "Merchant retrieved successfully"))
    }
} 