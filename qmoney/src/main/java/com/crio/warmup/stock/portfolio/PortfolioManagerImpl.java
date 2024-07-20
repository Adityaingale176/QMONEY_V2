
package com.crio.warmup.stock.portfolio;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  public String token = "fed3acf3bd538c62e9bf8140692cb5413b31a7a1";

  public String getToken(){
    return token;
  }

  private RestTemplate restTemplate;

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF




  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
        if (from.isAfter(to)){
          throw new RuntimeException();
        }
      String url = buildUri(symbol, from ,to);
      TiingoCandle[] candles= this.restTemplate.getForObject(url, TiingoCandle[].class);
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


  private Double getOpeningPriceOnStartDate(List<Candle> candles) {
    return candles.get(0).getOpen();
 }


 private static Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size()-1).getClose();
 }
  private static AnnualizedReturn calculateAnnualizedReturnsForATrade(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
      Double totalReturns = (sellPrice*trade.getQuantity() - buyPrice*trade.getQuantity())/(buyPrice * trade.getQuantity());
      
      long numOfDays = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
      Double numbOfYears = numOfDays *1.0 /365;
      Double AnnualizedReturn = Math.pow((1+ totalReturns), (1/numbOfYears)) - 1.0;
      
       return new AnnualizedReturn(trade.getSymbol(), AnnualizedReturn, totalReturns);

  }
  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) {
        List <AnnualizedReturn> annualizedReturns = new ArrayList<>();
        for (PortfolioTrade trade : portfolioTrades){
          List<Candle> candles = new ArrayList<>();
          try {
            candles = getStockQuote(trade.getSymbol(), trade.getPurchaseDate(), endDate);
          } catch (JsonProcessingException e) {
            e.printStackTrace();
          }
          AnnualizedReturn annualizedReturn = calculateAnnualizedReturnsForATrade(endDate, trade, getOpeningPriceOnStartDate(candles),getClosingPriceOnEndDate(candles));
          annualizedReturns.add(annualizedReturn);
        }
        Collections.sort(annualizedReturns, getComparator());
    return annualizedReturns;
  }
}
