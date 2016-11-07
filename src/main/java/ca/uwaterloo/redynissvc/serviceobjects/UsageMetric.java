package ca.uwaterloo.redynissvc.serviceobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PRIVATE)
@ToString()
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsageMetric
{
    private Integer totalAccessCount;
    private List<String> hosts;
    private Map<String, Integer> hostAccesses;
}
