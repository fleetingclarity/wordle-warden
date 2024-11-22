package dev.fleetingclarity.wordlewarden.commands.groupstats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserStats {
    private final String username;
    private final double averageScore;
    private final double groupAverage;
    private final double differenceFromGroup;
}
