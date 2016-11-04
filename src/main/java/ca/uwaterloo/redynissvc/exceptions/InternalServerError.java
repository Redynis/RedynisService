package ca.uwaterloo.redynissvc.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PRIVATE)
@ToString()
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalServerError
{
    private String errorMessage;
    private String sourceClass;
}
