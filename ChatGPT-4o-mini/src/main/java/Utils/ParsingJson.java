package Utils;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ParsingJson {

    public ParsingJson() {}

    public static String findValueByKey(Document doc, String key) {

        Element body = doc.body();
        String jsonContent = body.text();

        ObjectMapper mapper = new ObjectMapper();
        try {

            JsonNode rootNode = mapper.readTree(jsonContent);
            return findValue(rootNode, key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String findValue(JsonNode node, String key) {

        if (node.has(key)) {

            return node.get(key).asText();
        }

        if (node.isArray()) {
            for (JsonNode element : node) {

                String result = findValue(element, key);
                if (result != null) {
                    return result;
                }
            }
        }

        for (JsonNode child : node) {

            String result = findValue(child, key);
            if (result != null) {

                return result;
            }
        }
        return null;
    }

    public static String parsingResponseOpenAi(String text) {

        String start = ", content=";
        return text.substring(
                (text.indexOf(start) + start.length()),
                text.indexOf(", name=")) ;
    }
}