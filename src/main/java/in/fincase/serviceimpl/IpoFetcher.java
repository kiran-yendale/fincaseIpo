package in.fincase.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import org.springframework.stereotype.Component;

import java.util.List;

import org.asynchttpclient.*;
import com.fasterxml.jackson.databind.*;

import in.fincase.entity.UserEntity;
import in.fincase.repository.UserRepository;
@Component
public class IpoFetcher {

	@Autowired
	EmailService emailService;
	
	@Autowired
	UserRepository userRepository;
	
	  @Scheduled(fixedRate = 36000000)  
	    public void fetchIpoData() {
	        try (AsyncHttpClient client = new DefaultAsyncHttpClient()) {
	            // Fetch IPO details using the API
	            client.prepare("GET", "https://indian-stock-exchange-api2.p.rapidapi.com/ipo")
	                .setHeader("x-rapidapi-key", "a2c436126ad7p18a259b9f") // Use your RapidAPI key here
	                .setHeader("x-rapidapi-host", "indian-stock-exchange-api2.p.rapidapi.com")
	                .execute()
	                .toCompletableFuture()
	                .thenAccept(response -> {
	                    try {
	                        ObjectMapper objectMapper = new ObjectMapper();
	                        JsonNode rootNode = objectMapper.readTree(response.getResponseBody());
	                        JsonNode ipos = rootNode.path("ipos");

	                        ipos.forEach(ipo -> {
	                            String ipoName = ipo.path("name").asText();
	                            String ipoDate = ipo.path("date").asText();
	                            String ipoStatus = ipo.path("status").asText();
	                            String ipoPriceBand = ipo.path("priceBand").asText();
	                            sendIpoEmails(ipoName, ipoDate, ipoStatus, ipoPriceBand);
	                        });
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }
	                }).join();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	
	  private void sendIpoEmails(String ipoName, String ipoDate, String ipoStatus, String ipoPriceBand) {
	         List<UserEntity> users = userRepository.findAll();  
	        for (UserEntity user : users) {
	            String emailSubject = "New IPO Launched: " + ipoName;
	            String emailContent = String.format("Hello %s,\n\nThere is a new IPO available!\n\n" +
	                    "IPO: %s\nDate: %s\nStatus: %s\nPrice Band: %s\n\nBest regards,\nYour Company",
	                    user.getEmail(), ipoName, ipoDate, ipoStatus, ipoPriceBand);
  emailService.sendEmail(user.getEmail(), emailSubject, emailContent);
	        }
	    }
}
