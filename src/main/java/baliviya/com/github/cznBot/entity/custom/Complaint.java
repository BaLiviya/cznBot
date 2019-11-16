package baliviya.com.github.cznBot.entity.custom;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class Complaint {
    private int    id;
    private String FullName;
    private String phoneNumber;
    private String location;
    private String text;
    private Date   postDate;
}
