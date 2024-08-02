package database;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "CHAT_BY_ID")
@Entity
public class EntityUserChat implements Serializable {

    @Id
    @Column(name = "CHAT_ID",nullable = false, columnDefinition = "int unsigned")
    private Long chatId;

    @Column(name = "USER_NAME", nullable = false)
    private String name;

    @Column
    private String text;

    @Column(name = "MESSAGE_COUNT", columnDefinition = "int unsigned")
    private Integer countMessage;

    @Version
    private Integer version;

    public EntityUserChat() {}

    public String getName() {
        return name == null? "": name;
    }
    public String getText() {
        if (text == null) {
            setCountMessage(0);
            return "";
        }
        return text;
    }
    public long getChatId() {
        return chatId == null? 0: chatId;
    }
    public Integer getVersion() {
        return version;
    }
    public int getCountMessage() {
        return countMessage == null? 0: countMessage;
    }

    public void setText(String text) {
        this.text = text;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setChatId(long chatId) {
        this.chatId = chatId;
    }
    public void setCountMessage(int countMessage) {
        this.countMessage = countMessage;
    }
    public void setVersion(Integer version) {
        this.version = version;
    }
}
