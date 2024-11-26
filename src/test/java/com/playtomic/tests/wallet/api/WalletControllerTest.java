package com.playtomic.tests.wallet.api;

import com.playtomic.tests.wallet.service.ResourceNotFoundException;
import com.playtomic.tests.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(WalletController.class)
class WalletControllerTest {

    public static final long WALLET_ID = 1L;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;


    @Test
    void getWallet_whenWalletExists_shouldReturnWalletDTO() throws Exception {

        when(walletService.getWallet(WALLET_ID))
                .thenReturn(new WalletDTO("10.0 EUR"));

        mockMvc.perform(get("/wallet/{id}", WALLET_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("balance").value("10.0 EUR"));
    }

    @Test
    void getWallet_whenWalletDoesNotExist_shouldReturn404() throws Exception {
        when(walletService.getWallet(WALLET_ID)).thenThrow(new ResourceNotFoundException("Wallet not found with id: " + WALLET_ID));

        mockMvc.perform(get("/wallet/{id}", WALLET_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}