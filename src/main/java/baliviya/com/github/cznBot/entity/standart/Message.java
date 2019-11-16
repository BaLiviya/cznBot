package baliviya.com.github.cznBot.entity.standart;

import baliviya.com.github.cznBot.util.FileType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor
@ToString
public class Message {
    private long     id;
    private String   name;
    private String   photo;
    private long     keyboardMarkUpId;
    private String   file;
    private FileType fileType;
    private Language language;

    public void setFile(String file, FileType fileType) {
        this.file = file;
        this.fileType = fileType;
    }
}
