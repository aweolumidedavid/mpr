package com.test.mpr.service

import com.test.mpr.dto.CreateMerchantRequest
import com.test.mpr.entity.Merchant
import com.test.mpr.entity.enums.MerchantStatus
import com.test.mpr.repository.MerchantRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class MerchantServiceTest {
    
    @Mock
    private lateinit var merchantRepository: MerchantRepository
    
    @InjectMocks
    private lateinit var merchantService: MerchantService
    
    private lateinit var createMerchantRequest: CreateMerchantRequest
    private lateinit var merchant: Merchant
    
    @BeforeEach
    fun setUp() {
        createMerchantRequest = CreateMerchantRequest(
            businessName = "Test Business",
            email = "test@example.com",
            settlementAccount = "1234567890"
        )
        
        merchant = Merchant(
            id = 1L,
            businessName = "Test Business",
            email = "test@example.com",
            settlementAccount = "1234567890",
            merchantRef = "TEST_MERCH_001",
            status = MerchantStatus.ACTIVE,
            createdAt = LocalDateTime.now()
        )
    }
    
    @Test
    fun `createMerchant should create merchant successfully`() {
        `when`(merchantRepository.findByEmail(createMerchantRequest.email)).thenReturn(null)
        `when`(merchantRepository.save(any(Merchant::class.java))).thenReturn(merchant)
        
        val result = merchantService.createMerchant(createMerchantRequest)
        
        assertNotNull(result)
        assertEquals(merchant.id, result.id)
        assertEquals(merchant.businessName, result.businessName)
        assertEquals(merchant.email, result.email)
        assertEquals(merchant.settlementAccount, result.settlementAccount)
        assertEquals(merchant.status, result.status)
        
        verify(merchantRepository).findByEmail(createMerchantRequest.email)
        verify(merchantRepository).save(any(Merchant::class.java))
    }
    
    @Test
    fun `createMerchant should throw exception when email already exists`() {
        `when`(merchantRepository.findByEmail(createMerchantRequest.email)).thenReturn(merchant)
        
        val exception = assertThrows(com.test.mpr.exception.MerchantEmailAlreadyExistsException::class.java) {
            merchantService.createMerchant(createMerchantRequest)
        }
        
        assertEquals("Merchant with email ${createMerchantRequest.email} already exists", exception.message)
        verify(merchantRepository).findByEmail(createMerchantRequest.email)
        verify(merchantRepository, never()).save(any(Merchant::class.java))
    }
    
    @Test
    fun `getMerchantById should return merchant when exists`() {
        `when`(merchantRepository.findById(1L)).thenReturn(java.util.Optional.of(merchant))
        
        val result = merchantService.getMerchantById(1L)

        assertNotNull(result)
        assertEquals(merchant.id, result.id)
        assertEquals(merchant.businessName, result.businessName)
        verify(merchantRepository).findById(1L)
    }
    
    @Test
    fun `getMerchantById should throw exception when merchant not found`() {
        `when`(merchantRepository.findById(1L)).thenReturn(java.util.Optional.empty())
        
        val exception = assertThrows(com.test.mpr.exception.MerchantNotFoundException::class.java) {
            merchantService.getMerchantById(1L)
        }
        
        assertEquals("Merchant with id 1 not found", exception.message)
        verify(merchantRepository).findById(1L)
    }
} 