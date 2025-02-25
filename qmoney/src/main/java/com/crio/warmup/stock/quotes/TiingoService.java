
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

private RestTemplate restTemplate;

public String token = "fed3acf3bd538c62e9bf8140692cb5413b31a7a1";

  public String getToken(){
    return token;
  }

protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
        if (from.isAfter(to)){
          throw new RuntimeException("Purchase date cant be after sell date");
        }
      String url = buildUri(symbol, from ,to);
      String candlesString = this.restTemplate.getForObject(url, String.class);
      TiingoCandle[] candles = getObjectMapper().readValue(candlesString, TiingoCandle[].class);
     return List.of(candles);
  }
    

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
      //  String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
      //       + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
      String url = "https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?startDate="+startDate+"&endDate="+endDate+"&token="+token;
     //System.out.print("url by aditya:"+ url);
      return url;
            //return uriTemplate;
  }

  private ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }
  
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.

}
