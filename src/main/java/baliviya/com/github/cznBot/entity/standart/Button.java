package baliviya.com.github.cznBot.entity.standart;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class Button {
    private int      id;
    private String   name;
    private int      commandId;
    private String   url;
    private Language language;
    private boolean  requestContact;
    private int      messageId;
}
