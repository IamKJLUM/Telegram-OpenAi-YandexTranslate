package api;

import Utils.ParsingJson;
import main.DataExplorer;

import org.jsoup.Jsoup;
import org.json.JSONException;
import org.jsoup.nodes.Document;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Objects;
import java.io.IOException;

public class YandexTranslate implements Runnable {

    private final        DataExplorer dataExplorer;
    private static final String       O_AUTH_TOKEN       = "";
    private static final String       FOLDER_ID          = "";
    private static final String       LANGUAGE_TRANSLATE = "";

    public YandexTranslate(DataExplorer dataExplorer) {
        this.dataExplorer = dataExplorer;
        System.out.println("YandexTranslate successful initialization");
    }

    @Override
    public void run() {

        while (true) {

            if (dataExplorer.getDataTranslate().size() > 0) {

                for (Map.Entry<Long, String> entry : dataExplorer.getDataTranslate().entrySet()) {

                    Long chatId = entry.getKey();
                    dataExplorer.removeDataTranslate(chatId);
                    String text = entry.getValue();

                    if (text.matches(DataExplorer.EnglishRegex)) {

//OpenAi en -> Translate ru -> Main.Postman

                        dataExplorer.putPostman(
                                chatId,
                                connectionToYandex(text, LANGUAGE_TRANSLATE));

                    } else {

//OpenAi ru -> translate en -> OpenAi"
                        dataExplorer.putDataOpenAi(
                                chatId,
                                connectionToYandex(text, "en"));
                    }
                }
            }
        }
    }

    private String connectionToYandex(String text, String langTrans) {

        String iAmToken = ParsingJson.findValueByKey(Objects.requireNonNull(requestToken()), "iamToken");
        Document request = requestToYandex(iAmToken, text, langTrans);

        return ParsingJson.findValueByKey(request, "text");
    }

    private Document requestToYandex(String iAmToken, String text, String langTrans) {

        JSONObject body = new JSONObject();
        Document response = null;

        try {
            body.put("targetLanguageCode", langTrans);
            body.put("texts", text);
            body.put("folderId", FOLDER_ID);

            response = Jsoup.connect("https://translate.api.cloud.yandex.net/translate/v2/translate")
                    .header("Authorization", "Bearer " + iAmToken)
                    .header("Content-Type", "application/json")
                    .requestBody(body.toString())
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .post();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return response;
    }

    private Document requestToken() {

        JSONObject jsonGetToken = new JSONObject();
        try {

            jsonGetToken.put("yandexPassportOauthToken", O_AUTH_TOKEN);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            return Jsoup.connect("https://iam.api.cloud.yandex.net/iam/v1/tokens")
                    .header("ContentType", "Application/json")
                    .requestBody(jsonGetToken.toString())
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .post();

        } catch (IOException  e) {
            e.printStackTrace();
        }
        return null;
    }
}