package ca.uwaterloo.redynissvc.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PRIVATE)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceConfig
{
    private String applicatonName;
    private Integer dataLayerPort;
    private Integer metadataLayerPort;
    private String masterPropagator;
}
