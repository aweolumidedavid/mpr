package com.test.mpr.controller

import com.test.mpr.dto.*
import com.test.mpr.service.SettlementService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/settlements")
@Tag(name = "Settlement Management", description = "APIs for managing settlements")
class SettlementController(
    private val settlementService: SettlementService
) {
    
    @PostMapping("/process/{merchantId}")
    @Operation(summary = "Process settlements for merchant", description = "Processes settlements for a specific merchant")
    fun processSettlements(@PathVariable merchantId: Long): ResponseEntity<ApiResponse<String>> {
        settlementService.processSettlementsForMerchant(merchantId)
        return ResponseEntity.ok(ApiResponse.success("Settlements processed successfully", "Settlements processed successfully"))
    }
    
    @GetMapping("/summary/{merchantId}")
    @Operation(summary = "Get settlement summary", description = "Retrieves settlement summary for a specific merchant")
    fun getSettlementSummary(@PathVariable merchantId: Long): ResponseEntity<ApiResponse<SettlementSummaryResponse>> {
        val summary = settlementService.getSettlementSummary(merchantId)
        return ResponseEntity.ok(ApiResponse.success(summary, "Settlement summary retrieved successfully"))
    }
    
    @GetMapping("/batches/{merchantId}")
    @Operation(summary = "Get settlement batches", description = "Retrieves all settlement batches for a specific merchant")
    fun getSettlementBatches(@PathVariable merchantId: Long): ResponseEntity<ApiResponse<List<SettlementBatchResponse>>> {
        val batches = settlementService.getSettlementBatches(merchantId)
        return ResponseEntity.ok(ApiResponse.success(batches, "Settlement batches retrieved successfully"))
    }
} 