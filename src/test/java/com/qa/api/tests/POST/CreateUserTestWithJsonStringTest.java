package com.qa.api.tests.POST;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;

public class CreateUserTestWithJsonStringTest {


    Playwright playwright;
    APIRequest request;
    APIRequestContext requestContext;

    static String emailId;

    @BeforeTest
    public void setup(){
        playwright = Playwright.create();
        request =  playwright.request();
        requestContext = request.newContext();
    }

    @AfterTest
    public void tearDown(){
        playwright.close();
    }


    public static String getRandomEmail(){
        emailId = "testpwautomation"+ System.currentTimeMillis() + "@gmail.com";
        return emailId;
    }


    @Test
    public void createUserTest() throws IOException {

        //String json:
        String reqJsonBody = "{\n" +
                "  \"name\" : \"testingAPI\",\n" +
                "  \"email\" : \"" + getRandomEmail() +"\",\n" +
                "  \"gender\" : \"male\",\n" +
                "  \"status\" : \"active\"\n" +
                "}";
        
        System.out.println(reqJsonBody);

        //POST Call: create a user
        APIResponse apiPostResponse = requestContext.post("https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Authorization", "Bearer 24776a1f7f77ab5ba47bce648ed06fe8833ac65c23f9919fa16b76d8ed8094c9")
                        .setData(reqJsonBody));

        System.out.println(apiPostResponse.status());
        Assert.assertEquals(apiPostResponse.status(), 201);
        Assert.assertEquals(apiPostResponse.statusText(), "Created");

        System.out.println(apiPostResponse.text());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode postJsonResponse = objectMapper.readTree(apiPostResponse.body());
        System.out.println(postJsonResponse.toPrettyString());

        //capture id from the post json response:
        String userId = postJsonResponse.get("id").asText();
        System.out.println("user id : " + userId);

        //GET Call: Fetch the same user by id:

        System.out.println("===============get call response============");

        APIResponse apiGetResponse =
                requestContext.get("https://gorest.co.in/public/v2/users/"+ userId,
                        RequestOptions.create()
                                .setHeader("Authorization", "Bearer 24776a1f7f77ab5ba47bce648ed06fe8833ac65c23f9919fa16b76d8ed8094c9"));

        Assert.assertEquals(apiGetResponse.status(), 200);
        Assert.assertEquals(apiGetResponse.statusText(), "OK");
        System.out.println(apiGetResponse.text());
        Assert.assertTrue(apiGetResponse.text().contains(userId));
        Assert.assertTrue(apiGetResponse.text().contains("testingAPI"));

       // Assert.assertTrue(apiGetResponse.text().contains(emailId));


    }






}
