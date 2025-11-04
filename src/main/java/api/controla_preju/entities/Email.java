package api.controla_preju.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Email {
    private final String to;
    private final String subject;
    private final String body;

}
