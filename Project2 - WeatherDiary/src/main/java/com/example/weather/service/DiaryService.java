package com.example.weather.service;

import com.example.weather.Repository.DiaryRepository;
import com.example.weather.domain.Diary;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
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
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;

    @Value("${openweathermap.key}")
    private String apiKey;

    public void createDiary(LocalDate date,String text){
        // API로 날씨 데이터 가져오기
        String weatherData = getWeatherString();
        // 받아온 날씨 데이터 파싱
        Map<String, Object> parsedData = parseWeather(weatherData);
        // 파싱된 데이터 + 일기 DB 삽입
        Diary nowDiary = new Diary();
        nowDiary.setDate(date);
        nowDiary.setWeather(parsedData.get("main").toString());
        nowDiary.setIcon(parsedData.get("icon").toString());
        nowDiary.setTemperature((Double)parsedData.get("temp"));
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }

    public List<Diary> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date);
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
        // 대괄호로 시작하면, JSON Array!
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main",weatherData.get("main"));
        resultMap.put("icon",weatherData.get("icon"));

        return resultMap;
    }


}
