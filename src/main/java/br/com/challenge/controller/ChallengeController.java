package br.com.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.challenge.helper.ResponseJson;
import br.com.challenge.helper.Score;
import br.com.challenge.service.ChallengeService;

@RestController
@RequestMapping("/challenge")
public class ChallengeController {
    @Autowired ChallengeService challengeService;

    @GetMapping("/submitSolution")
    public ResponseEntity<Score> submitSolution() {
        return this.challengeService.submitSolution();
    }

    @GetMapping("/generateData")
    public ResponseEntity<ResponseJson> generateData() {
        return this.challengeService.generateData();
    }

}