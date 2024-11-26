package com.playtomic.tests.wallet.api;

import com.playtomic.tests.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WalletController {
    private Logger log = LoggerFactory.getLogger(WalletController.class);
    @Autowired
    private WalletService walletService;

    @RequestMapping("/")
    void log() {
        log.info("Logging from /");
    }

    @GetMapping("/wallet/{id}")
    public ResponseEntity<WalletDTO> getWallet(@PathVariable long id) {

        return ResponseEntity.ok(walletService.getWallet(id));
    }


    @PostMapping("/wallet/{id}/top-up")
    public ResponseEntity<Void> topUp(@PathVariable long id, @RequestBody TopUpRequest requestBody) {
        walletService.topUp(id, requestBody);
        return ResponseEntity.noContent().build();
    }
}
