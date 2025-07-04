package com.test.mpr.service

import com.test.mpr.dto.InitiateTransactionRequest
import com.test.mpr.entity.Merchant
import com.test.mpr.entity.Transaction
import com.test.mpr.entity.enums.MerchantStatus
import com.test.mpr.entity.enums.TransactionStatus
import com.test.mpr.repository.TransactionRepository
import com.test.mpr.utils.TransactionLockManager
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.math.BigDecimal
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class TransactionServiceTest {
    
    @Mock
    private lateinit var transactionRepository: TransactionRepository
    
    @Mock
    private lateinit var merchantService: MerchantService
    
    @Mock
    private lateinit var redisTemplate: RedisTemplate<String, String>
    
    @Mock
    private lateinit var valueOperations: ValueOperations<String, String>
    
    @Mock
    private lateinit var transactionLockManager: TransactionLockManager
    
    @InjectMocks
    private lateinit var transactionService: TransactionService
    
    private lateinit var initiateTransactionRequest: InitiateTransactionRequest
    private lateinit var merchant: Merchant
    private lateinit var transaction: Transaction
    
    @BeforeEach
    fun setUp() {
        initiateTransactionRequest = InitiateTransactionRequest(
            amount = BigDecimal("100.00"),
            currency = "USD",
            merchantId = 1L,
            merchantRef = "MERCH123456789"
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
        
        transaction = Transaction(
            id = 1L,
            amount = BigDecimal("100.00"),
            currency = "USD",
            status = TransactionStatus.SUCCESS,
            merchantRef = "MERCH123456789",
            internalRef = "TXN2024120112000012345678",
            fee = BigDecimal("1.50"),
            merchant = merchant,
            createdAt = LocalDateTime.now()
        )
    }
    
    @Test
    fun `initiateTransaction should create transaction successfully`() {
        // Given
        `when`(transactionRepository.findByMerchantRef(anyString())).thenReturn(null)
        `when`(merchantService.getActiveMerchantById(anyLong())).thenReturn(merchant)
        `when`(transactionRepository.save(any(Transaction::class.java))).thenReturn(transaction)
        `when`(transactionLockManager.executeWithLockAndCleanup(anyString(), any())).thenAnswer { invocation ->
            val block = invocation.getArgument<() -> com.test.mpr.dto.TransactionResponse>(1)
            block.invoke()
        }
        
        // When
        val result = transactionService.initiateTransaction(initiateTransactionRequest)
        
        // Then
        assertNotNull(result)
        assertEquals(transaction.id, result.id)
        assertEquals(transaction.amount, result.amount)
        assertEquals(transaction.currency, result.currency)
        assertEquals(transaction.status, result.status)
        assertEquals(transaction.merchantRef, result.merchantRef)
        assertEquals(transaction.internalRef, result.internalRef)
        assertEquals(transaction.fee, result.fee)
        
        verify(transactionRepository).findByMerchantRef(anyString())
        verify(merchantService).getActiveMerchantById(anyLong())
        verify(transactionRepository).save(any(Transaction::class.java))
        verify(transactionLockManager).executeWithLockAndCleanup(anyString(), any())
    }
    
    @Test
    fun `initiateTransaction should throw exception when transaction in progress`() {
        // Given
        `when`(transactionLockManager.executeWithLockAndCleanup(anyString(), any())).thenThrow(IllegalStateException("Transaction with merchant reference ${initiateTransactionRequest.merchantRef} is already in progress"))
        
        // When & Then
        val exception = assertThrows(IllegalStateException::class.java) {
            transactionService.initiateTransaction(initiateTransactionRequest)
        }
        
        assertEquals("Transaction with merchant reference ${initiateTransactionRequest.merchantRef} is already in progress", exception.message)
        verify(transactionLockManager).executeWithLockAndCleanup(anyString(), any())
        verify(transactionRepository, never()).save(any(Transaction::class.java))
    }
    
    @Test
    fun `initiateTransaction should throw exception when merchant reference already exists`() {
        // Given
        `when`(transactionRepository.findByMerchantRef(anyString())).thenReturn(transaction)
        
        // When & Then
        val exception = assertThrows(com.test.mpr.exception.TransactionAlreadyExistsException::class.java) {
            transactionService.initiateTransaction(initiateTransactionRequest)
        }
        
        assertEquals("Transaction with merchant reference ${initiateTransactionRequest.merchantRef} already exists", exception.message)
        verify(transactionRepository).findByMerchantRef(anyString())
        verify(transactionRepository, never()).save(any(Transaction::class.java))
    }
} 