package api;

import main.DataExplorer;

import Utils.ParsingJson;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;

import java.util.Map;
import java.util.List;
import java.time.Duration;

public class OpenAi implements Runnable {

    static final   String        TOKEN_OPENAI = "";
    private        DataExplorer  dataExplorer;
    private static OpenAiService service;
    private static String        MODEL;

    public OpenAi() {}

    public OpenAi(DataExplorer dataExplorer, ModelEnum model) {

        this.dataExplorer = dataExplorer;
        service = SingletonService.getInstance().getService();
        MODEL = model.getName();
        System.out.println("OpenAi successful initialization");
    }

    @Override
    public void run() {

        while (true) {

            if (dataExplorer.getDataOpenAi().size() > 0) {
                boolean fastOrLow = whatModel(MODEL);

                for (Map.Entry<Long, String> entry : dataExplorer.getDataOpenAi().entrySet()) {

                    long chatId = entry.getKey();
                    dataExplorer.removeDataOpenAi(chatId);
                    String text = entry.getValue();

                    if (fastOrLow) {
                        fastGPT(chatId, text);
                    } else {
                        lowGPT(chatId, text);
                    }
                }
            }
        }
    }

    public ChatCompletionRequest responseOpenAi(String text) {

        ChatMessage message = new ChatMessage("assistant", text);
        ChatCompletionRequest response = new ChatCompletionRequest();

        response.setModel(MODEL);
        response.setMaxTokens(2500);
        response.setMessages(List.of(message));

        return response;
    }

    public String requestOpenAi(ChatCompletionRequest response) {

        return ParsingJson.parsingResponseOpenAi(
                OpenAi.service.createChatCompletion(response)
                        .getChoices()
                        .toString());
    }

    private void fastGPT(long chatId, String text) {
        dataExplorer.putPostman(
                chatId,
                requestOpenAi(responseOpenAi(text)));
    }

    private void lowGPT(long chatId, String text) {

        if (text.matches(DataExplorer.EnglishRegex)) {
// ? -> ResponseOpenAi en-> Translate ru
            dataExplorer.putTranslate(
                    chatId,
                    requestOpenAi(responseOpenAi(text)));

        } else {
// Bot ru -> OpenAi ru -> Translate en
            dataExplorer.putTranslate(chatId, text);
        }
    }

    private static class SingletonService {

        static OpenAi.SingletonService instance;
        private final OpenAiService service;

        public OpenAiService getService() {
            return service;
        }
        private SingletonService() {

            service = new OpenAiService(TOKEN_OPENAI, Duration.ofSeconds(420));
        }
        protected static SingletonService getInstance() {

            if(instance == null) return new SingletonService();
            return instance;
        }
    }

    public enum ModelEnum {
        DALL_E_3("dall-e-3"),
        WHISPER_1("whisper-1"),
        DALL_E_2("dall-e-2"),
        TTS_1_HD_1106("tts-1-hd-1106"),
        TTS_1_HD("tts-1-hd"),
        TTS_1("tts-1"),
        BABBAGE_002("babbage-002"),
        TEXT_EMBEDDING_3_SMALL("text-embedding-3-small"),
        TEXT_EMBEDDING_3_LARGE("text-embedding-3-large"),
        TTS_1_1106("tts-1-1106"),
        GPT_4O_MINI_2024_07_18("gpt-4o-mini-2024-07-18"),
        GPT_3_5_TURBO("gpt-3.5-turbo"),
        GPT_3_5_TURBO_INSTRUCT("gpt-3.5-turbo-instruct"),
        GPT_3_5_TURBO_INSTRUCT_0914("gpt-3.5-turbo-instruct-0914"),
        TEXT_EMBEDDING_ADA_002("text-embedding-ada-002"),
        GPT_3_5_TURBO_16K("gpt-3.5-turbo-16k"),
        DAVINCI_002("davinci-002"),
        GPT_4O_MINI("gpt-4o-mini"),
        GPT_3_5_TURBO_0125("gpt-3.5-turbo-0125"),
        GPT_3_5_TURBO_1106("gpt-3.5-turbo-1106"),
        GPT_4("gpt-4");

        private final String name;

        ModelEnum(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private boolean whatModel(String model) {
        return model.matches("(gpt-4o-mini-2024-07-18|gpt-4|gpt-4o-mini|gpt-3.5-turbo-instruct-0914|gpt-3.5-turbo-0125|gpt-3.5-turbo-1106|gpt-3.5-turbo-16k)");
    }
}