package baliviya.com.github.cznBot.entity.standart;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
public class LanguageUser {
    private long     chatId;
    private Language language;
}
