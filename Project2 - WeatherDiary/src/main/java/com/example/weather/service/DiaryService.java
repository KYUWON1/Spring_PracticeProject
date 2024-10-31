package com.example.weather.service;

import com.example.weather.Repository.DateWeatherRepository;
import com.example.weather.Repository.DiaryRepository;
import com.example.weather.WeatherApplication;
import com.example.weather.domain.DateWeather;
import com.example.weather.domain.Diary;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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

    private final DateWeatherRepository dateWeatherRepository;

    private static final Logger logger =
            LoggerFactory.getLogger(WeatherApplication.class);

    @Value("${openweathermap.key}")
    private String apiKey;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date,String text){
        logger.info("started to create diary");

        DateWeather dateWeather = getDateWeather(date);

        // 파싱된 데이터 + 일기 DB 삽입
        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
        logger.info("end to create diary");
    }

    private DateWeather getDateWeather(LocalDate date){
        List<DateWeather> dateWeatherListFromDB =
                dateWeatherRepository.findAllByDate(date);
        if(dateWeatherListFromDB.size() == 0){
            // 새로 api에서 날씨를 가져오거나
            // 정책상 날씨없이 일기 쓰거나
            // 우선 날씨데이터 불러오도록 실시
            return getWeatherFromApi();
        }else{
            // 데이터 존재시 데이터 반환
            return dateWeatherListFromDB.get(0);
        }

    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        logger.debug("read diary");
        return diaryRepository.findAllByDate(date);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate,endDate);
    }

    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        // id 값이 그래도이기 때문에 db 자체에거 덥어쓰기 진행
        diaryRepository.save(nowDiary); 
    }

    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }

    @Transactional
    @Scheduled(cron="0 0 1 * * *") // 0초 0분 1시 매일매일
    public void saveWeatherDate(){
        logger.info("날씨 데이터 스케줄러 동작");
        dateWeatherRepository.save(getWeatherFromApi());
    }

    private DateWeather getWeatherFromApi(){
        String weatherData = getWeatherString();
        Map<String,Object> parsedWeather = parseWeather(weatherData);

        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double)parsedWeather.get("temp"));
        return dateWeather;
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
