package com.example.weather.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class DiaryService {

    @Value("${openweathermap.key}")
    private String apiKey;

    public void createDiary(LocalDate date,String text){
        // API로 날씨 데이터 가져오기
        String weatherData = getWeatherString();
        // 받아온 날씨 데이터 파싱
        Map<String, Object> parsedData = parseWeather(weatherData);
        // 파싱된 데이터 + 일기 DB 삽입

    }

    private String getWeatherString(){
        String apiUrl = "https://api.openweathermap.org/data/2" +
            ".5/weather?q=seoul&appid=" + apiKey;
        try{
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            BufferedReader br;
            if(responseCode == 200){
                br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
            }else {
                br =
                        new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while((inputLine = br.readLine()) != null){
                response.append(inputLine);
            }
            br.close();

            return response.toString();
        }catch (Exception e){
            return "Failed to get response";
        }
    }

    private Map<String, Object> parseWeather(String jsonString){
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        // json의 중괄호가 안닫히는등 파싱 에러 처리
        try{
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        }catch(ParseException e){
            throw new RuntimeException(e);
        }
        Map<String,Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp",mainData.get("temp"));
        JSONObject weatherData = (JSONObject) jsonObject.get("weather");
        resultMap.put("main",weatherData.get("main"));
        resultMap.put("icon",weatherData.get("icon"));

        return resultMap;
    }
}
