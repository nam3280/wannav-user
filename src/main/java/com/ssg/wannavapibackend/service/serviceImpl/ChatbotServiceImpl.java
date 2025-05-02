package com.ssg.wannavapibackend.service.serviceImpl;

import com.ssg.wannavapibackend.common.ErrorCode;
import com.ssg.wannavapibackend.config.WebSocketConfig;
import com.ssg.wannavapibackend.domain.User;
import com.ssg.wannavapibackend.exception.CustomException;
import com.ssg.wannavapibackend.repository.UserRepository;
import com.ssg.wannavapibackend.service.ChatbotService;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final WebSocketConfig webSocketConfig;
    private final UserRepository userRepository;

    @Override
    public String sendMessage(Long userId, String requestMessage) {
        String responseMessage = "";
        try {
            String message = getReqMessage(userId, requestMessage);
            String encodeBase64String = makeSignature(message);

            HttpURLConnection connection = createConnection(encodeBase64String);
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());

            dos.write(message.getBytes("UTF-8"));
            dos.flush();
            dos.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "UTF-8"));

                String decodedString = null;
                String jsonString = "";

                while ((decodedString = in.readLine()) != null) {
                    jsonString = decodedString;
                }

                // 받아온 값을 세팅
                JSONParser jsonParser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) jsonParser.parse(jsonString);

                    JSONArray contentArray = (JSONArray) json.get("content");
                    JSONObject content = (JSONObject) contentArray.get(0);
                    JSONObject data = (JSONObject) content.get("data");
                    String details = "";
                    details = (String) data.get("details");
                    responseMessage = details;
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }

                in.close();
            } else {
                responseMessage = connection.getResponseMessage();
                log.error(responseMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return responseMessage;
    }


    private HttpURLConnection createConnection(String encodeBase64String) {
        try {
            URL url = new URL(webSocketConfig.getInvokeUrl());

            // api 서버 접속 (서버 -> 서버 통신)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;UTF-8");
            connection.setRequestProperty("X-NCP-CHATBOT_SIGNATURE", encodeBase64String);

            connection.setDoOutput(true);

            return connection;

        } catch (IOException e) { // 연결 생성 실패
            log.error("Error creating connection", e);
            throw new CustomException(ErrorCode.CHATBOT_CONNECTION_FAILED);
        } catch (Exception e) { // 예기치 못한 오류
            log.error("Unexpected error in connection creation", e);
            throw new CustomException(ErrorCode.CHATBOT_UNKNOWN_ERROR);
        }
    }

    /**
     * 보낼 메시지를 네이버 챗봇 포맷으로 변경해주는 메서드
     *
     * @param voiceMessage
     * @return
     */
    private String getReqMessage(Long userId, String voiceMessage) {
        String requestBody = "";
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        try {
            JSONObject obj = new JSONObject();
            long timestamp = new Date().getTime();
            obj.put("version", "v1");
            obj.put("userId", user.getChatbotCode());
            obj.put("timestamp", timestamp);

            JSONObject dataObj = new JSONObject();
            dataObj.put("details", voiceMessage);

            JSONObject contentObj = new JSONObject();
            contentObj.put("type", "text");
            contentObj.put("data", dataObj);

            JSONArray contentArr = new JSONArray();
            contentArr.add(contentObj);

            obj.put("content", contentArr);
            obj.put("event", "send");


            requestBody = obj.toString();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return requestBody;
    }

    /**
     * 보낼 메시지를 네이버에서 제공해준 암호화로 변경해주는 메서드
     *
     * @param message
     * @return
     */
    private String makeSignature(String message) {
        String encodeBase64String = "";

        try {
            String secretKey = webSocketConfig.getSecretKey();
            byte[] secretKeyBytes = secretKey.getBytes("UTF-8");

            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);

            byte[] signature = mac.doFinal(message.getBytes("UTF-8"));
            encodeBase64String = Base64.encodeBase64String(signature);

            return encodeBase64String;

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return encodeBase64String;
    }
}