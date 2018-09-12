package com.example.jtc.controller;

import com.example.jtc.JtcApplication;
import com.example.jtc.model.Client;
import com.example.jtc.repository.ClientRepository;
import com.example.jtc.response.ResponseUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.json.Json;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JtcApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RestApiControllerIntegrationTest {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    ClientRepository clientRepository;

    @LocalServerPort
    private int port;

    private static final String SENDER_KEY = "senderId";
    private static final String RECIPIENT_KEY = "recipientId";
    private static final String AMOUNT_KEY = "amount";

    private static final long SENDER_ID = 1L;
    private static final long RECIPIENT_ID = 2L;
    private static final double SENDER_ACCOUNT = 1000;
    private static final double RECIPIENT_ACCOUNT = 2000;

    private static final double AMOUNT_FOR_SUCCESSFUL_TRANSACTION = 500.4564;
    private static final double AMOUNT_FOR_FAIL_TRANSACTION = 1500.4564;

    private static final int HTTP_STATUS_OK = 200;

    @Test
    public void successfulTransaction() {
        String requestBody = Json.createObjectBuilder()
                .add(SENDER_KEY, SENDER_ID)
                .add(RECIPIENT_KEY, RECIPIENT_ID)
                .add(AMOUNT_KEY, AMOUNT_FOR_SUCCESSFUL_TRANSACTION)
                .build().toString();

        ResponseEntity<String> rs = restTemplate.exchange(createURL(), HttpMethod.POST, createHttpEntity(requestBody),
                String.class);

        Assert.assertEquals(HTTP_STATUS_OK, rs.getStatusCodeValue());
        Assert.assertEquals(ResponseUtils.createSuccessfulRs(), rs.getBody());

        Optional<Client> sender = clientRepository.findById(SENDER_ID);
        Optional<Client> recipient = clientRepository.findById(RECIPIENT_ID);

        Assert.assertTrue(sender.isPresent());
        Assert.assertTrue(recipient.isPresent());
        Assert.assertEquals("499.54", sender.get().getAccount().toString());
        Assert.assertEquals("2500.46", recipient.get().getAccount().toString());
    }

    @Test
    public void totalAmountNotChanged() {
        String requestBody = Json.createObjectBuilder()
                .add(SENDER_KEY, SENDER_ID)
                .add(RECIPIENT_KEY, RECIPIENT_ID)
                .add(AMOUNT_KEY, AMOUNT_FOR_SUCCESSFUL_TRANSACTION)
                .build().toString();

        ResponseEntity<String> rs = restTemplate.exchange(createURL(), HttpMethod.POST, createHttpEntity(requestBody),
                String.class);

        Assert.assertEquals(HTTP_STATUS_OK, rs.getStatusCodeValue());
        Assert.assertEquals(ResponseUtils.createSuccessfulRs(), rs.getBody());

        Optional<Client> sender = clientRepository.findById(SENDER_ID);
        Optional<Client> recipient = clientRepository.findById(RECIPIENT_ID);

        Assert.assertTrue(sender.isPresent());
        Assert.assertTrue(recipient.isPresent());
        Assert.assertEquals(
                new BigDecimal(SENDER_ACCOUNT + RECIPIENT_ACCOUNT).setScale(2, RoundingMode.HALF_UP).toString(),
                sender.get().getAccount().add(recipient.get().getAccount()).toString());
    }

    @Test
    public void failTransactionNoMoney() {
        String requestBody = Json.createObjectBuilder()
                .add(SENDER_KEY, SENDER_ID)
                .add(RECIPIENT_KEY, RECIPIENT_ID)
                .add(AMOUNT_KEY, AMOUNT_FOR_FAIL_TRANSACTION)
                .build().toString();

        ResponseEntity<String> rs = restTemplate.exchange(createURL(), HttpMethod.POST, createHttpEntity(requestBody),
                String.class);

        Assert.assertEquals(HTTP_STATUS_OK, rs.getStatusCodeValue());
        Assert.assertEquals(ResponseUtils.createErrorRs(), rs.getBody());
        accountNotChanged();
    }

    @Test
    public void failTransactionSenderNotFound() {
        String requestBody = Json.createObjectBuilder()
                .add(SENDER_KEY, 345345L)
                .add(RECIPIENT_KEY, RECIPIENT_ID)
                .add(AMOUNT_KEY, AMOUNT_FOR_FAIL_TRANSACTION)
                .build().toString();

        ResponseEntity<String> rs = restTemplate.exchange(createURL(), HttpMethod.POST, createHttpEntity(requestBody),
                String.class);

        Assert.assertEquals(HTTP_STATUS_OK, rs.getStatusCodeValue());
        Assert.assertEquals(ResponseUtils.createErrorRs(), rs.getBody());
        accountNotChanged();
    }

    @Test
    public void failTransactionRecipientNotFound() {
        String requestBody = Json.createObjectBuilder()
                .add(SENDER_KEY, SENDER_ID)
                .add(RECIPIENT_KEY, 456456L)
                .add(AMOUNT_KEY, AMOUNT_FOR_FAIL_TRANSACTION)
                .build().toString();

        ResponseEntity<String> rs = restTemplate.exchange(createURL(), HttpMethod.POST, createHttpEntity(requestBody),
                String.class);

        Assert.assertEquals(HTTP_STATUS_OK, rs.getStatusCodeValue());
        Assert.assertEquals(ResponseUtils.createErrorRs(), rs.getBody());
        accountNotChanged();
    }

    private void accountNotChanged() {
        Optional<Client> sender = clientRepository.findById(SENDER_ID);
        Optional<Client> recipient = clientRepository.findById(RECIPIENT_ID);
        Assert.assertTrue(sender.isPresent());
        Assert.assertTrue(recipient.isPresent());
        Assert.assertEquals(BigDecimal.valueOf(SENDER_ACCOUNT).setScale(2, RoundingMode.HALF_UP)
                .toString(), sender.get().getAccount().toString());
        Assert.assertEquals(BigDecimal.valueOf(RECIPIENT_ACCOUNT).setScale(2, RoundingMode.HALF_UP)
                .toString(), recipient.get().getAccount().toString());
    }

    private HttpEntity<String> createHttpEntity(String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(requestBody, headers);
    }

    private String createURL() {
        return "http://127.0.0.1:" + port + "/transfer";
    }
}
