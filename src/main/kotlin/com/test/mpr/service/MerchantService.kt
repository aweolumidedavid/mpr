package com.test.mpr.service

import com.test.mpr.dto.CreateMerchantRequest
import com.test.mpr.dto.MerchantResponse
import com.test.mpr.entity.Merchant
import com.test.mpr.entity.enums.MerchantStatus
import com.test.mpr.exception.MerchantEmailAlreadyExistsException
import com.test.mpr.exception.MerchantInactiveException
import com.test.mpr.exception.MerchantNotFoundException
import com.test.mpr.repository.MerchantRepository
import com.test.mpr.utils.ReferenceGenerator

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MerchantService(
    private val merchantRepository: MerchantRepository
) {
    
    @Transactional
    fun createMerchant(request: CreateMerchantRequest): MerchantResponse {
        // check if email already exists
        merchantRepository.findByEmail(request.email)?.let {
            throw MerchantEmailAlreadyExistsException(request.email)
        }
        
        val merchantRef = ReferenceGenerator.generateMerchantRef()
        
        val merchant = Merchant(
            businessName = request.businessName,
            email = request.email,
            settlementAccount = request.settlementAccount,
            merchantRef = merchantRef,
            status = MerchantStatus.ACTIVE
        )
        
        val savedMerchant = merchantRepository.save(merchant)
        return mapToResponse(savedMerchant)
    }
    
    fun getMerchantById(id: Long): MerchantResponse {
        val merchant = merchantRepository.findById(id)
            .orElseThrow { MerchantNotFoundException(id) }
        return mapToResponse(merchant)
    }
    
    fun getActiveMerchantById(id: Long): Merchant {
        return merchantRepository.findByStatusAndId(MerchantStatus.ACTIVE, id)
            ?: throw MerchantInactiveException(id)
    }
    
    fun getAllActiveMerchants(): List<Merchant> {
        return merchantRepository.findByStatus(MerchantStatus.ACTIVE)
    }
    
    private fun mapToResponse(merchant: Merchant): MerchantResponse {
        return MerchantResponse(
            id = merchant.id!!,
            businessName = merchant.businessName,
            email = merchant.email,
            settlementAccount = merchant.settlementAccount,
            merchantRef = merchant.merchantRef,
            status = merchant.status,
            createdAt = merchant.createdAt,
            updatedAt = merchant.updatedAt
        )
    }
} 